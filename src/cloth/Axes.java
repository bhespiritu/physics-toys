package cloth;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import base.Vector3;

public class Axes extends Object3D{

	public Vector3 forward = new Vector3(6,0,1).normalize(), up, right = Vector3.RIGHT;
	
	public Axes() {
		up = forward.cross(right).mult(-1).normalize();
		right = forward.cross(up).normalize();
	}
	
	public void draw(Camera c, Graphics g)
	{
		//right = forward.cross(Vector3.UP).normalize();
		up = forward.cross(right).mult(-1).normalize();
		//right = forward.cross(up).normalize();
		Point base = c.projectPoint(position);
		Point fop = c.projectPoint(position.add(forward));
		Point upp = c.projectPoint(position.add(up));
		Point rip = c.projectPoint(position.add(right));
		
		g.setColor(Color.BLUE);
		g.drawLine(base.x, base.y, fop.x, fop.y);
		g.setColor(Color.ORANGE);
		g.drawLine(base.x, base.y, upp.x, upp.y);
		g.setColor(Color.RED);
		g.drawLine(base.x, base.y, rip.x, rip.y);
	}
	
}
