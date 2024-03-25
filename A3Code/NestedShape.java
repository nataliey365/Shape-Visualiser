/*
 *  ============================================================================================
 *  A1.java : A shape that is nested.
 *  YOUR UPI: nyip406
 *  ============================================================================================
 */

import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.awt.Color;
class NestedShape extends RectangleShape{
	
    private ArrayList<Shape> innerShapes = new ArrayList<Shape>();
    
    public Shape createInnerShape(PathType pt, ShapeType st){
        switch (st) {
			case RECTANGLE: {
				RectangleShape inner = new RectangleShape(0, 0, (width/5), (height/5), width, height, color ,borderColor, pt);
                inner.setParent(this);
                innerShapes.add(inner);
                return inner;
			} case OVAL: {
				OvalShape inner = new OvalShape(0, 0, (width/5), (height/5), width, height, color ,borderColor, pt);
				inner.setParent(this);
                innerShapes.add(inner);
                return inner;
			} case NESTED: {
				NestedShape inner = new NestedShape(0, 0, (width/5), (height/5), width, height, color ,borderColor, pt);
				inner.setParent(this);
                innerShapes.add(inner);
                return inner;
			}
			
		}
		return null;
    }
    
    public NestedShape(){
        super();
        createInnerShape(PathType.BOUNCING, ShapeType.RECTANGLE);
    }
    public NestedShape(int x, int y, int w, int h, int mw, int mh, Color c, Color bc, PathType pt) {
		super(x ,y ,w, h ,mw ,mh, c, bc, pt);
		createInnerShape(PathType.BOUNCING, ShapeType.RECTANGLE);
	}
	public NestedShape(int w, int h){
	    super(0 ,0 ,w, h ,Shape.DEFAULT_PANEL_WIDTH ,Shape.DEFAULT_PANEL_HEIGHT, Shape.DEFAULT_COLOR, Shape.DEFAULT_BORDER_COLOR, PathType.BOUNCING);
	}
	
	public Shape getInnerShapeAt(int index){
	    return innerShapes.get(index);
	}
	public int getSize(){
	    return innerShapes.size();
	}

public void draw(Graphics g){
    g.setColor(Color.black);
    g.drawRect(x, y, width, height);
    g.translate(x,y);
	for (Shape s: innerShapes){
	   s.draw(g);
	   s.drawHandles(g);
	   s.drawString(g);
	}
	g.translate(-x,-y);
	
}

public void move(){
    path.move();
    for (Shape s: innerShapes){
        s.move();
    }
}

public int indexOf(Shape s){
    return innerShapes.indexOf(s);
}
public void addInnerShape(Shape s){
    innerShapes.add(s);
    s.setParent(this);
}

public void removeInnerShape(Shape s){
    innerShapes.remove(s);
    s.setParent(null);
}
public void removeInnerShapeAt(int index){
    Shape s = innerShapes.get(index);
    s.setParent(null);
    innerShapes.remove(index);
}
public ArrayList<Shape> getAllInnerShapes(){
    return innerShapes;
}
}