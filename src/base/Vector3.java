package base;

public class Vector3 {
	public float x;
	public float y;
	public float z;

	public static final Vector3 FORWARD = new Vector3(0, 0, 1);
	public static final Vector3 UP = new Vector3(0, 1, 0);
	public static final Vector3 RIGHT = new Vector3(1, 0, 0);
	public static final Vector3 ZERO = new Vector3(0, 0, 0);
	public static final Vector3 ONE = new Vector3(1, 1, 1).normalize();
	public static final Vector3 ARBITRARY = new Vector3(0.2f, 5f, .7f).normalize();

	public Vector3(float i, float j, float k) {
		x = i;
		y = j;
		z = k;
	}

	public Vector3 add(Vector3 other) {
		return new Vector3(x + other.x, y + other.y, z + other.z);
	}

	public Vector3 sub(Vector3 other) {
		return new Vector3(x - other.x, y - other.y, z - other.z);
	}

	public Vector3 mult(float other) {
		return new Vector3(x * other, y * other, z * other);
	}

	public Vector3 div(float other) {
		return new Vector3(x / other, y / other, z / other);
	}

	public float dot(Vector3 other) {
		return x * other.x + y * other.y + z * other.z;
	}

	public float sqrMagnitude() {
		return x * x + y * y + z * z;
	}

	public float magnitude() {
		return (float) Math.sqrt(sqrMagnitude());
	}

	public Vector3 normalize() {
		return div(magnitude());
	}

	public Vector3 cross(Vector3 other) {
		Vector3 out = Vector3.ZERO;
		out.x = y * other.z - z * other.y;
		out.y = -(x * other.z - z * other.x);
		out.z = (x * other.y - y * other.x);
		return out;
	}

	public Vector3 rotate(int axis, float angle) {
		Vector3 out = copy();
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		switch (axis) {
		case 0: // x
			out.y = (cos * y - sin * z);
			out.z = (sin * y + cos * z);
			break;
		case 1: // y
			out.x = (cos * x - sin * z);
			out.z = (sin * x + cos * z);
			break;
		case 2: // z
			out.x = (cos * x - sin * y);
			out.y = (sin * x + cos * y);
			break;
		}
		return out;
	}

	public Vector3 rotate(Vector3 axis, float angle) {
		axis = axis.normalize();
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);


		float out_x = (cos + axis.x * axis.x * (1 - cos)) * x + (axis.x * axis.y * (1 - cos) - axis.z * sin) * y
				+ (axis.x * axis.z * (1 - cos) + axis.y * sin) * z;
		float out_y = (axis.y*axis.y*(1-cos) + axis.z*sin)*x + (cos + axis.y*axis.y*(1-cos))*y + (axis.y*axis.z*(1-cos)-axis.x*sin)*z;
		float out_z = (axis.z*axis.x*(1-cos) - axis.y*sin)*x + (axis.z*axis.y*(1-cos)-axis.x*sin)*y + (cos + axis.z*axis.z*(1-cos))*z;
		return new Vector3(out_x,out_y,out_z).normalize();
	}
	//b onto a
	public static Vector3 project(Vector3 a, Vector3 b)
	{
		return a.normalize().mult((a.dot(b)/(a.magnitude())));
	}
	
	public Vector3 copy() {
		return new Vector3(x, y, z);
	}
	
	@Override
	public boolean equals(Object o) {
		Vector3 v = (Vector3) o;
		return v.x == x && v.y == y && v.z == z;
	}

	@Override
	public String toString() {
		return "{" + x + ',' + y + ',' + z + '}';
	}

}
