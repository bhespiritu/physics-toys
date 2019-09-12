package cloth;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import base.Vector3;

public class Cloth implements IDrawable{

	public ArrayList<Node> nodes = new ArrayList<Node>();

//	private ArrayList<Point> projected = new ArrayList<Point>();
	
	public void timeStep(ClothSimulation env)
	{
		float dt = env.dt;
		Vector3 diff = new Vector3(0,0,0);
		float diffMag;
		float diffRest;
		for(int i = 0; i < nodes.size(); i++)
		{
			Node n = nodes.get(i);
			for(int j = 0; j < 4; j++)
			{
				if(n.neighbors[j] == null) continue;
				diff = n.neighbors[j].position.sub(n.position);
				diffMag = diff.magnitude();
				diffRest = n.restLength - diffMag;
				diff = diff.normalize().mult(diffRest);
				n.velocity = n.velocity.sub(diff.mult(dt*Node.elasticity));
			}
		}
		for(int i = 0; i < nodes.size(); i++)
		{
			Node n = nodes.get(i);
			for(int j = 0; j < 4; j++)
			{
				if(n.neighbors[j] == null) continue;
				diff = n.neighbors[j].position.sub(n.position).normalize();
				diffMag = n.velocity.sub(n.neighbors[j].velocity).magnitude();
				n.velocity = n.velocity.add(diff.mult(diffMag*Node.damping));
			}
		}
		
		for(int i = 0; i < nodes.size(); i++)
		{
			Node n = nodes.get(i);
			n.velocity = n.velocity.add(env.gravity.mult(dt*Node.mass));
			n.velocity = n.velocity.mult(env.airDrag);
			n.position = n.position.add(n.velocity.mult(dt));
			
			if(n.position.y >= 2) 
				{
				n.position.y = 2;
				n.velocity = n.velocity.mult(0.99f);
				}
			//System.out.println(n.position + " " + n.velocity);
		}
	}
	
	public void resolveCollision(Obstacle b)
	{
		float penDepth;
		Vector3 normal = new Vector3(0,0,0);
		for(int i = 0; i < nodes.size(); i++)
		{
			Node n = nodes.get(i);
			if(b.intersect(n.position))
			{
				penDepth = b.penDepth(n.position);
				normal = b.normal(n.position);
				n.position = n.position.add(normal.mult(-penDepth));
				n.velocity = n.velocity.sub(Vector3.project(normal, n.velocity));
				n.velocity = n.velocity.mult(b.frictionCoef);
			}
		}
	}
	
	@Override
	public void draw(Graphics g, Camera c) {
		
		for(int i = 0; i < nodes.size(); i++)
		{
			Node n = nodes.get(i);
			Node right = n.neighbors[0];
			Node down = n.neighbors[1];
			
			Point base = c.projectPoint(n.position);
			if (right != null) {
				Point PRight = c.projectPoint(right.position);
				g.drawLine(base.x, base.y, PRight.x, PRight.y);
			}
			if (down != null) {
				Point PDown = c.projectPoint(down.position);
				g.drawLine(base.x, base.y, PDown.x, PDown.y);
			}
			
			g.drawOval(base.x-5, base.y-5, 10, 10);
			//System.out.println(base);
		}
		
	}
	
	
	
}
