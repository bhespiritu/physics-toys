package base;

public class Vector2 {
	public float x;
	public float y;
	
	public Vector2(float _x, float _y) {
		x = _x;
		y = _y; 
	}
	
	public Vector2 add(Vector2 b)
	{
		return new Vector2(x+b.x,y+b.y);
	}
	
	public Vector2 subtract(Vector2 b)
	{
		return new Vector2(x-b.x,y-b.y);
	}
	
	public Vector2 multiply(float b)
	{
		return new Vector2(x*b,y*b);
	}

	
	public static float dot(Vector2 a, Vector2 b)
	{
		return a.x*b.x + a.y*b.y;
	}
	
	public Vector2 divide(float b)
	{
		return new Vector2(x/b,y/b);
	}
	
	public Vector2 rot90()
	{
		return new Vector2(-y,x);
	}
	
	public float sqrMagnitude()
	{
		return (x*x + y*y);
	}
	
	public float magnitude()
	{
		return (float) Math.sqrt(sqrMagnitude());
	}
	
	public Vector2 normalized()
	{
		return divide(magnitude());
	}
	
	@Override
	public String toString() {
		return "{" + x + "," + y + "}";
	}
	
}
