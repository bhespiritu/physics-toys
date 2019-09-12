package cloth;

import java.awt.Point;

import base.Vector3;

public class PerspectiveCamera extends Camera{

	public float yaw;
	public float pitch;
	
	public float focalLength = -50f;
	
	@Override
	public Point projectPoint(Vector3 point) {
		Vector3 relative = point.sub(position);
		Vector3 d = new Vector3(0,0,0);
		
		float sinx = (float) Math.sin(pitch);
		float cosx = (float) Math.cos(pitch);
		
		float siny = (float) Math.sin(yaw);
		float cosy = (float) Math.cos(yaw);
		
		float sinz = (float) Math.sin(0);
		float cosz = (float) Math.cos(0);
		
		d.x = cosy*(sinz*relative.y + cosz*relative.x) - siny*relative.z;
		d.y = sinx*(cosy*relative.z + siny*(sinz*relative.y + cosz*relative.x)) + cosx*(cosz*relative.y - sinz*relative.x);
		d.z = cosx*(cosy*relative.z + siny*(sinz*relative.y + cosz*relative.x)) - sinx*(cosz*relative.y - sinz*relative.x);
		
		Point out = new Point((int)((focalLength/d.z)*d.x*zoom),(int)((focalLength/d.z)*d.y*zoom));
		
		return out;
	}

}
