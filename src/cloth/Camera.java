package cloth;

import java.awt.Point;

import base.Vector3;

public class Camera extends Object3D{

	public Vector3 forward = Vector3.FORWARD;
	public Vector3 up = Vector3.UP;
	public float zoom = 200; 
	
	public Point projectPoint(Vector3 point) //converts a 3D point to screen coords
	{
		Vector3 relative = point.sub(position);
		//Vector3 camForward = applyRotation(Vector3.FORWARD);
		Vector3 camForward = forward;
		float dist = camForward.dot(relative);
		
		Vector3 project = point.sub(camForward.mult(dist));
		Vector3 projRelative = project.sub(position);
		
		Vector3 camRight = up.cross(forward).normalize();
		Vector3 camUp = forward.cross(camRight).normalize();
		//if(forward.dot(Vector3.FORWARD) > 0) camUp = camUp.mult(-1);
		camRight = forward.cross(camUp).normalize();

		float x = camRight.dot(projRelative);
		float y = camUp.dot(projRelative);
		
		float dx = zoom;//scale factor for converting from units to pixels

		return new Point((int)(x*dx),(int)(y*dx));
	}
	
	public Vector3 screenToWorld(Point screenPoint, int screenWidth, int screenHeight)
	{
		screenPoint.x -= screenWidth/2;
		screenPoint.y -= screenHeight/2;
		
		Vector3 out = position;
		Vector3 camRight = up.cross(forward).normalize();
		Vector3 camUp = forward.cross(camRight).normalize();
		
		out = out.add(camRight.mult(-screenPoint.x/zoom));
		out = out.add(camUp.mult(screenPoint.y/zoom));
		
		return out;
	}
	
}
