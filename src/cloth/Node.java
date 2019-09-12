package cloth;

import base.Vector3;

public class Node {

	public static float elasticity = 50f;
	public static float damping = 0f;
	public static float mass = 0.015f;
	public static float restLength = 1/10f;
	public Node[] neighbors = new Node[4];
	public boolean isRight = true;
	
	public Vector3 position, velocity = Vector3.ZERO;
	
	
	public static void connect(Node a, Node b)
	{
		int index = a.isRight ? 0 : 1;
		a.neighbors[index] = b;
		b.neighbors[index + 2] = a;
		a.isRight = !a.isRight;
	}
	
}
