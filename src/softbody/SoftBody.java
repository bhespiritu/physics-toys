package softbody;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import base.Vector2;

public class SoftBody extends JPanel implements MouseListener{

	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame("Softbody");
		SoftBody sb = new SoftBody();
		frame.addMouseListener(sb);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(sb);
		frame.pack();
		frame.setVisible(true);
		for(;;)
		{
			sb.timeStep();
			sb.repaint();
			Thread.sleep(1000/60);
		}
	}
	
	public float dt = 1/30f;
	public int ppm = 5;
	
	public int NUMP = 15;
	public int NUMS = NUMP + 1;
	private float BALLRADIUS = 5;
	public float MASS = 1f;
	public float SPRING = 10f;
	public float DAMP = 0f;
	public float pressure = 50*3;
	
	public boolean useGravity = true;
	public float gravityStrength = 9.8f;
	
	public boolean isClicked = false;
	public Node currentNode = null;
	
	public Node[] nodes = new Node[NUMP];
	public Spring[] springs = new Spring[NUMS];
	
	public SoftBody() {
		setPreferredSize(new Dimension(500,500));
		for(int i = 0; i < NUMP; i++) nodes[i] = new Node();
		for(int i = 0; i < NUMS; i++) springs[i] = new Spring();		
		makeCircle();
	}
	
	public void makeCircle()
	{
		
		for(int i = 0; i < NUMP; ++i)
		{
			nodes[i].position.x = (float) (BALLRADIUS*Math.sin(Math.PI*2*i/NUMP));
			nodes[i].position.y = (float) (BALLRADIUS*Math.cos(Math.PI*2*i/NUMP));
		}
		
		for(int i = 1; i < NUMP; ++i)
		{
			AddSpring(i,i,i-1);
			//AddSpring(i-1,i-1,i);
		}
		AddSpring(NUMP, NUMP-1, 0);
	}

	void AddSpring(int pi, int i, int j) {
		springs[pi].p1 = i;
		springs[pi].p2 = j;
		springs[pi].length = nodes[i].position.subtract(nodes[j].position).magnitude();
	}
	
	public void timeStep()
	{
		for(int i = 0; i < NUMP; i++)
		{
			nodes[i].force.x = 0;
			if(useGravity)
				nodes[i].force.y = gravityStrength*MASS;
		}
		
		for(int i = 0; i < NUMS; i++)
		{
			Spring s = springs[i];
			Node a = nodes[s.p1];
			Node b = nodes[s.p2];
			Vector2 diff = a.position.subtract(b.position);
			float dist = diff.magnitude();
			if(dist  != 0)
			{
				Vector2 relVel = a.velocity.subtract(b.velocity);
				float f = (dist-s.length)*SPRING + (relVel.x *(diff.x) + relVel.y * (diff.y))*DAMP/dist;
				Vector2 force = diff.divide(dist).multiply(f);
				a.force = a.force.subtract(force);
				b.force = b.force.add(force);
				//System.out.println(f);
				s.normal.x = diff.y/dist;
				s.normal.y = -diff.x/dist;
			}
		}
		
		float volume = calculateVolume();
		//System.out.println(volume);
		
		for(int i = 0; i < NUMS; i++)
		{
			Spring s = springs[i];
			Node a = nodes[s.p1];
			Node b = nodes[s.p2];
			Vector2 diff = a.position.subtract(b.position);
			float dist = diff.magnitude();
			
			float pressurev = -dist * pressure * (1/volume);
			
			a.force = a.force.add(s.normal.multiply(pressurev));
			b.force = b.force.add(s.normal.multiply(pressurev));
		}
		
		if(!isClicked)
		{
			currentNode = null;
		}
		
		if(currentNode != null && isClicked)
		{
			Point p = getMousePosition();
			Vector2 nodeP = currentNode.position;
			if(p != null);
			{
				Vector2 mousePos = new Vector2((p.x - getWidth()/2)/ppm,(p.y - getHeight()/2)/ppm);
				Vector2 diff = mousePos.subtract(nodeP);
				currentNode.force = currentNode.force.add(diff.multiply(4));
			}
		}
		
		integrateEuler();
	}
	
	float calculateVolume()
	{
		float volume = 0;
		for(int i = 0; i < NUMS; i++)
		{
			Spring s = springs[i];
			Node a = nodes[s.p1];
			Node b = nodes[s.p2];
			Vector2 diff = a.position.subtract(b.position);
			float dist = diff.magnitude();
			
			volume += .5f*Math.abs(diff.x)*Math.abs(s.normal.x)*dist;
		}
		return volume;
	}
	
	void integrateEuler()
	{
		for(int i = 0; i < NUMP; i++)
		{
			Node n = nodes[i];
			n.velocity = n.velocity.add(n.force.multiply(dt/MASS));
			n.velocity = n.velocity.multiply(0.99f);
			n.position = n.position.add(n.velocity.multiply(dt));
			
			if(n.position.y > 10)
			{
				n.position.y = 10;
				n.velocity.y *= -0.1f;
				//n.velocity.x *= 0.9f;
				//n.velocity.x = 0;
			}
			
			if(n.position.x > 60)
			{
				n.position.x = 60;
				n.velocity.x *= -0.1f;
				//n.velocity.x *= 0.9f;
				//n.velocity.y = 0;
			}
			
			if(n.position.x < -60)
			{
				n.position.x = -60;
				n.velocity.x *= -0.1f;
				//n.velocity.x *= 0.9f;
				//n.velocity.y = 0;
			}
			
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.translate(getWidth()/2, getHeight()/2);
		
		for(int i = 0; i < NUMS; i++)
		{
			Spring s = springs[i];
			Vector2 p1 = nodes[s.p1].position;
			Vector2 p2 = nodes[s.p2].position;
			Vector2 mid = p1.add(p2).divide(2);
			
			int p1x = (int) (p1.x * ppm);
			int p1y = (int) (p1.y * ppm);
			
			int p2x = (int) (p2.x * ppm);
			int p2y = (int) (p2.y * ppm);
			
			int mx = (int) (mid.x * ppm);
			int my = (int) (mid.y * ppm);
			
			g.drawLine(p1x, p1y, p2x, p2y);
			//g.drawLine(p1x, p1y, (int)(p1x + s.normal.x*10), (int)(p1y + s.normal.y*10));
			//g.drawLine(p2x, p2y, (int)(p2x + s.normal.x*10), (int)(p2y + s.normal.y*10));
			g.drawLine(mx, my, (int)(mx + s.normal.x*10), (int)(my + s.normal.y*10));
		}
		
		if(isClicked)
		{
			Point mousePos = getMousePosition();
			Vector2 pos = currentNode.position;
			
			g.drawLine((int)(pos.x * ppm), (int)(pos.y*ppm), mousePos.x - getWidth()/2, mousePos.y - getHeight()/2);
		}
	}
	
	Node findNearestNode(Vector2 pos)
	{
		
		Node minNode = nodes[0];
		float minDist = pos.subtract(minNode.position).sqrMagnitude();
		for(int i = 1; i < NUMP; i++)
		{
			Node n = nodes[i];
			float dist = pos.subtract(n.position).sqrMagnitude();
			if(dist < minDist)
			{
				minDist = dist;
				minNode = n;
			}
		}
		return minNode;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		isClicked = true;
		Point p = getMousePosition();
		Vector2 mousePos = new Vector2((p.x - getWidth()/2)/ppm,(p.y - getHeight()/2)/ppm);
		currentNode = findNearestNode(mousePos);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		isClicked = false;
		//currentNode = null;
	}
	
}
