package cloth;

import java.awt.Graphics;
import java.awt.Point;

import base.Vector3;

public class Sphere extends Obstacle implements IDrawable {

	public float radius = .3f;

	@Override
	public void draw(Graphics g, Camera c) {
		if (c instanceof PerspectiveCamera) {
			Point pixPos = c.projectPoint(position);
			Point pixRad = c.projectPoint(position.add(Vector3.UP.mult(radius)));
		} else {
			int pixRad = (int) (radius * c.zoom);
			Point pixPos = c.projectPoint(position);
			g.drawOval(pixPos.x - pixRad, pixPos.y - pixRad, pixRad * 2, pixRad * 2);
		}
	}

	@Override
	public boolean intersect(Vector3 point) {
		return (point.sub(position).sqrMagnitude() <= radius * radius);
	}

	@Override
	public float penDepth(Vector3 point) {
		return point.sub(position).magnitude() - radius;
	}

	@Override
	public Vector3 normal(Vector3 point) {

		return point.sub(position).normalize();
	}

}
