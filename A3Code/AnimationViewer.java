/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 * YOUR UPI: nyip406
 * ==========================================================================================
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListDataListener;
import java.lang.reflect.Field;

class AnimationViewer extends JComponent implements Runnable, TreeModel {
	private Thread animationThread = null; // the thread for animation
	private static int DELAY = 120; // the current animation speed
	private ShapeType currentShapeType = Shape.DEFAULT_SHAPETYPE; // the current shape type,
	private PathType currentPathType = Shape.DEFAULT_PATHTYPE; // the current path type
	private Color currentColor = Shape.DEFAULT_COLOR; // the current fill colour of a shape
	private Color currentBorderColor = Shape.DEFAULT_BORDER_COLOR;
	private int currentPanelWidth = Shape.DEFAULT_PANEL_WIDTH, currentPanelHeight = Shape.DEFAULT_PANEL_HEIGHT,currentWidth = Shape.DEFAULT_WIDTH, currentHeight = Shape.DEFAULT_HEIGHT;
	private String currentLabel = Shape.DEFAULT_LABEL;

	protected NestedShape root;
	protected DefaultListModel<Shape> listModel;
	ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
	
public NestedShape getRoot(){
    return root;
 }
public boolean isLeaf(Object node){
    if (node instanceof NestedShape){
        return false;
    }
    return true;
}

public boolean isRoot(Shape selectedNode){
    if (selectedNode == root){
        return true;
    }
    return false;
}
public Shape getChild(Object parent, int index){
    if (parent instanceof NestedShape && (index < ((NestedShape)parent).getSize()) && (index >= 0)){
        return ((NestedShape)parent).getInnerShapeAt(index);
    }
    return null;
}

public int getChildCount(Object parent){
    if (parent instanceof NestedShape){
        return ((NestedShape)parent).getSize();
    }
    return 0;
}
public int getIndexOfChild(Object parent, Object child){
    if (parent instanceof NestedShape){
        return ((NestedShape)parent).indexOf((Shape)child);
    }
    return -1;
}
public void addTreeModelListener(final TreeModelListener tml) {treeModelListeners.add(tml);}
public void removeTreeModelListener(final TreeModelListener tml) {treeModelListeners.remove(tml);}
public void valueForPathChanged(TreePath path, Object newValue) {  }

	public AnimationViewer() {
		start();
		NestedShape root = new NestedShape(Shape.DEFAULT_PANEL_WIDTH, Shape.DEFAULT_PANEL_HEIGHT);
		this.root = root;
		listModel = new DefaultListModel<Shape>();
	}

	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < root.getSize(); i++) {
		    Shape currentShape = root.getInnerShapeAt(i);
			currentShape.move();
			currentShape.draw(g);
			currentShape.drawHandles(g);
			currentShape.drawString(g);
		}
	}
	public void resetMarginSize() {
		currentPanelWidth = getWidth();
		currentPanelHeight = getHeight();
		for (int i = 0; i < root.getSize(); i++) {
		    Shape currentShape = root.getInnerShapeAt(i);
			currentShape.resetPanelSize(currentPanelWidth, currentPanelHeight);
		}
	}

	public void fireTreeNodesInserted(Object source, Object[] path,int[] childIndices,Object[] children) {
		System.out.printf("Called fireTreeNodesInserted: path=%s, childIndices=%s, children=%s\n", Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));
		final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
		for (final TreeModelListener tml : treeModelListeners)
			tml.treeNodesInserted(event);
	}
  
	public void addShapeNode(NestedShape selectedNode){
		Shape child = selectedNode.createInnerShape(currentPathType, currentShapeType);
		int[] idx = {getIndexOfChild(selectedNode, child)};
		Object[] newchild = {child};
		fireTreeNodesInserted(this, selectedNode.getPath(), idx, newchild);
		listModel.add(getIndexOfChild(selectedNode, child), child);
}
	
	protected void fireTreeNodesRemoved(Object source, Object[] path,int[] childIndices,Object[] children) {
    System.out.printf("Called fireTreeNodesRemoved: path=%s, childIndices=%s, children=%s\n", Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));
    final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
    for (final TreeModelListener tml : treeModelListeners)
      tml.treeNodesRemoved(event);
}

public void removeNodeFromParent(Shape selectedNode){
        Shape parent = selectedNode.getParent();
        int[] idx = {((NestedShape)parent).indexOf(selectedNode)};
        ((NestedShape)parent).removeInnerShape(selectedNode);
        Object[] child = {selectedNode};
        fireTreeNodesRemoved(this, parent.getPath(),idx, child);
}
public void reload(Shape selectedNode){
    if (selectedNode instanceof NestedShape){
        listModel.clear();
        for (int i = 0; i < ((NestedShape)selectedNode).getSize(); i++) {
		    Shape s = ((NestedShape)selectedNode).getInnerShapeAt(i);
		    listModel.add(i, s);
    }
}
}
	// you don't need to make any changes after this line ______________
	public String getCurrentLabel() {return currentLabel;}
	public int getCurrentHeight() { return currentHeight; }
	public int getCurrentWidth() { return currentWidth; }
	public Color getCurrentColor() { return currentColor; }
	public Color getCurrentBorderColor() { return currentBorderColor; }
	public void setCurrentShapeType(ShapeType value) {currentShapeType = value;}
	public void setCurrentPathType(PathType value) {currentPathType = value;}
	public ShapeType getCurrentShapeType() {return currentShapeType;}
	public PathType getCurrentPathType() {return currentPathType;}
	public void update(Graphics g) {
		paint(g);
	}
	public void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}
	public void stop() {
		if (animationThread != null) {
			animationThread = null;
		}
	}
	public void run() {
		Thread myThread = Thread.currentThread();
		while (animationThread == myThread) {
			repaint();
			pause(DELAY);
		}
	}
	private void pause(int milliseconds) {
		try {
			Thread.sleep((long) milliseconds);
		} catch (InterruptedException ie) {}
	}
}
