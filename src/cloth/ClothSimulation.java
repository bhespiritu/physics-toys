package cloth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import base.Vector3;

public class ClothSimulation extends JPanel {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Rubik's Cube");
		ClothSimulation clothPane = new ClothSimulation();
		frame.setContentPane(clothPane);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		int i = 0;
		for (;;) {
			clothPane.timestep();
			clothPane.repaint();
			
			if (i % 2 == 0 && i < 2888 && false) {
				BufferedImage image = new BufferedImage(clothPane.getWidth(), clothPane.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				clothPane.printAll(g);
				g.dispose();
				try {
					ImageIO.write(image, "png", new File("frames/" + i/2 + ".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			i++;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public float dt = 0.01f;
	public Vector3 gravity = Vector3.UP.mult(9.81f);
	private Camera cam;
	public float airDrag = 0.99f;

	private Cloth cloth;

	Sphere s = new Sphere();

	public ClothSimulation() {
		setPreferredSize(new Dimension(500, 500));
		cam = new Camera();
		cam.position = Vector3.ONE.mult(5);
		cam.forward = cam.position.mult(-1).normalize();

		if (cam instanceof PerspectiveCamera) {
			PerspectiveCamera pc = (PerspectiveCamera) cam;
			pc.yaw = (float) Math.PI;
		}

		cloth = new Cloth();

		for (int x = -3; x < 3; x++) {
			for (int y = -3; y < 3; y++) {
				Node n = new Node();
				n.position = new Vector3(x * Node.restLength, -2, y * Node.restLength);
				cloth.nodes.add(n);
			}
		}
		for (int i = 0; i < cloth.nodes.size(); i++) {
			Node n = cloth.nodes.get(i);
			if (cloth.nodes.size() > i + 1 && i % 6 != 5)
				Node.connect(n, cloth.nodes.get(i + 1));
			if (cloth.nodes.size() > i + 6)
				Node.connect(n, cloth.nodes.get(i + 6));

		}
	}

	private float time = 0;

	public void timestep() {
		time += dt;
		//cam.position.x = (float) (5 * Math.cos(time));
		//cam.position.z = (float) (5 * Math.sin(time));
		//cam.forward = cam.position.mult(-1).normalize();
		cloth.timeStep(this);
		cloth.resolveCollision(s);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.translate(getWidth() / 2, getHeight() / 2);
		cloth.draw(g, cam);
		s.draw(g, cam);

	}

}
