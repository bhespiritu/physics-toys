package cloth;

import base.Vector3;

public abstract class Obstacle extends Object3D{

	public float frictionCoef = 0f;
	
	public abstract boolean intersect(Vector3 point);
	public abstract float penDepth(Vector3 point);
	public abstract Vector3 normal(Vector3 point);
	
}
