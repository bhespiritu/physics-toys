package cloth;

import base.Vector3;

public class Object3D {

	public Vector3 position;
	public Vector3 rotation;
	
	public Object3D() {
		position = new Vector3(0,0,0);
		rotation = new Vector3(0,0,0);
	}
	
	public Vector3 applyRotation(Vector3 in)//rotates around x, then y, then z
	{
		Vector3 before = in;
		Vector3 next = new Vector3(0,0,0);
		
		float cosx = (float) Math.cos(rotation.x);
		float sinx = (float) Math.sin(rotation.x);
		
		//rotation around x axis
		next.x = before.x;
		next.y = cosx*before.y + sinx*-before.z;
		next.z = sinx*before.y + cosx*before.z;
		
		before = next;
		
		float cosy = (float) Math.cos(rotation.y);
		float siny = (float) Math.sin(rotation.y);
		
		//rotation around y axis
		next.x = cosy*before.x + siny*before.z;
		next.y = before.y;
		next.z = -siny*before.x + cosy*before.z;
				
		before = next;
		
		float cosz = (float) Math.cos(rotation.z);
		float sinz = (float) Math.sin(rotation.z);
		
		//rotation around z axis
		next.x = cosz*before.x - sinz*before.y;
		next.y = sinz*before.x + cosz*before.y;
		next.z = before.z;
		
		before = next;
		
		return next;
	}
	
}
