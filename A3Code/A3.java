/*
 *  ============================================================================================
 *  A1.java : Extends JFrame and contains a panel where shapes move around on the screen.
 *  YOUR UPI: nyip406
 *  ============================================================================================
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.ArrayList;
import javax.swing.tree.*;


public class A3  extends JFrame {
	private AnimationViewer panel;  // panel for bouncing area
	JButton addNodeButton, removeNodeButton;
	JComboBox<ShapeType> shapesComboBox;
	JComboBox<PathType> pathComboBox;
	JTree tree;
	JList<Shape> shapesList;
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new A3();
			}
		});
	}
	public A3() {
		super("Bouncing Application");
		JPanel mainPanel = setUpMainPanel();
		add(mainPanel, BorderLayout.CENTER);
		add(setUpToolsPanel(), BorderLayout.NORTH);
		addComponentListener(
			new ComponentAdapter() { // resize the frame and reset all margins for all shapes
				public void componentResized(ComponentEvent componentEvent) {
					panel.resetMarginSize();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(Shape.DEFAULT_PANEL_WIDTH * 2 + 100, Shape.DEFAULT_PANEL_HEIGHT + 100);
		setVisible(true);
	}
	public JPanel setUpMainPanel() {
		JPanel mainPanel = new JPanel();
		panel = new AnimationViewer();
		panel.setPreferredSize(new Dimension(Shape.DEFAULT_PANEL_WIDTH, Shape.DEFAULT_PANEL_HEIGHT));
		JPanel modelPanel = setUpModelPanel();
		modelPanel.setPreferredSize(new Dimension(Shape.DEFAULT_PANEL_WIDTH, Shape.DEFAULT_PANEL_HEIGHT));
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modelPanel, panel);
		mainSplitPane.setResizeWeight(0.5);
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setContinuousLayout(true);
		mainPanel.add(mainSplitPane);
		return mainPanel;
	}
	public JPanel setUpModelPanel() {
		JPanel treePanel = new JPanel(new BorderLayout());
		JPanel listPanel = new JPanel(new BorderLayout());
		treePanel.setPreferredSize(new Dimension(Shape.DEFAULT_PANEL_WIDTH, Shape.DEFAULT_PANEL_HEIGHT/2));
		listPanel.setPreferredSize(new Dimension(Shape.DEFAULT_PANEL_WIDTH, Shape.DEFAULT_PANEL_HEIGHT/2));
		tree = new JTree(panel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(new TreeNodeSelectionListener());
		JScrollPane treeScrollpane = new JScrollPane(tree);
		JPanel treeButtonsPanel = new JPanel();
		addNodeButton = new JButton("Add Node");
		addNodeButton.addActionListener( new AddListener());
		removeNodeButton = new JButton("Remove Node");
		removeNodeButton.addActionListener( new RemoveListener());
		treeButtonsPanel.add(addNodeButton);
		treeButtonsPanel.add(removeNodeButton);
		treePanel.add(treeButtonsPanel,BorderLayout.NORTH);
		treePanel.add(treeScrollpane,BorderLayout.CENTER);
		shapesList = new JList<Shape>(panel.listModel);
		listPanel.add(shapesList);
		JPanel modelPanel = new JPanel();
		JSplitPane modelSplitPane =  new JSplitPane(JSplitPane.VERTICAL_SPLIT, treePanel, listPanel);
		modelSplitPane.setResizeWeight(0.5);
		modelSplitPane.setOneTouchExpandable(true);
		modelSplitPane.setContinuousLayout(true);
		modelPanel.add(modelSplitPane);
		return modelPanel;
	}

	public JPanel setUpToolsPanel() {
		shapesComboBox = new JComboBox<ShapeType>(new DefaultComboBoxModel<ShapeType>(ShapeType.values()));
		shapesComboBox.addActionListener( new ShapeActionListener()) ;
		pathComboBox = new JComboBox<PathType>(new DefaultComboBoxModel<PathType>(PathType.values()));
		pathComboBox.addActionListener( new PathActionListener());
		JPanel toolsPanel = new JPanel();
		toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.X_AXIS));
		toolsPanel.add(new JLabel(" Shape: ", JLabel.RIGHT));
		toolsPanel.add(shapesComboBox);
		toolsPanel.add(new JLabel(" Path: ", JLabel.RIGHT));
		toolsPanel.add(pathComboBox);
		return toolsPanel;
	}
	class ShapeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			panel.setCurrentShapeType((ShapeType)shapesComboBox.getSelectedItem());
		}
	}
	class PathActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			panel.setCurrentPathType((PathType)pathComboBox.getSelectedItem());
		}
	}
	
	class RemoveListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
        Object last = tree.getLastSelectedPathComponent();
        if (last != null && (Shape)last != panel.getRoot()){
            panel.removeNodeFromParent((Shape)last);
        } else if (last != null && (Shape)last == panel.getRoot()){
                JOptionPane.showMessageDialog(null, "ERROR: Must not remove the root.");
        } else if (last == null){
            JOptionPane.showMessageDialog(null, "ERROR: No node selected.");
        }
    }
	}
	
	class AddListener implements ActionListener{
    public void actionPerformed(ActionEvent e){
        Object last = tree.getLastSelectedPathComponent();
        if (last != null){
            if (last instanceof NestedShape) {
                panel.addShapeNode(((NestedShape)last));
            } else {
                JOptionPane.showMessageDialog(null, "ERROR: Must select a NestedShape node.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "ERROR: No node selected.");
        }
    }
}
class TreeNodeSelectionListener implements TreeSelectionListener{
    public void valueChanged(TreeSelectionEvent e){
        Object last = tree.getLastSelectedPathComponent();
        if (last != null){
            tree.expandPath(tree.getSelectionPath());
            panel.reload((Shape)last);
        }
    }
}

}

