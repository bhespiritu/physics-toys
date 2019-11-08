package gravity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gravity2DDisplay extends JPanel{

	public static void main(String[] args) {
		JFrame frame = new JFrame("Gravity");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Gravity2DDisplay gravity = new Gravity2DDisplay();
		frame.setContentPane(gravity);
		frame.pack();
		frame.setVisible(true);
		for(;;) 
		{
			//System.out.println("PING");
			gravity.timeStep();
			frame.repaint();
		}
	}
	
	float jacobiconstant = (float) (4*Math.PI*6.67408e-11);
	
	private class Particle
	{
		public float px, py;
		public float vx = 0, vy = 0;
	}
	
	
	
	int ppm = 1;//pixels per meter
	float dt = 1000f;
	int particleCount = 100000;
	float particleMass = 10;
	int gridSize = 250;
	static int screenSize = 500;
	float screenToGrid;
	
	private static final Dimension defaultDimension = new Dimension(screenSize,screenSize);
	
	Particle[] particles;
	Grid potential, density, velocityX, velocityY, delPX, delPY;
	
	public Gravity2DDisplay() {
		screenToGrid = (float)screenSize/gridSize;
		setPreferredSize(defaultDimension);
		setBackground(Color.BLACK);
		particles = new Particle[particleCount];
		for(int i = 0; i < particleCount; i++)
		{
			Particle p = new Particle();
			p.px = (float) (screenSize*Math.random());
			p.py = (float) (screenSize*Math.random());
			particles[i] = p;
		}
		potential = new Grid(gridSize,gridSize);
		density = new Grid(gridSize,gridSize);
		velocityX = new Grid(gridSize,gridSize);
		velocityY = new Grid(gridSize,gridSize);
		velocityY = new Grid(gridSize,gridSize);
		delPX = new Grid(gridSize,gridSize);
		delPY = new Grid(gridSize,gridSize);
	}
	
	public void timeStep()
	{
		for(int i = 0; i < gridSize*gridSize; i++)
		{
			density.set(0,i);
			velocityX.set(0, i);
			velocityY.set(0,i);
		}
		
		//calculate density
		for(int i = 0; i < particleCount; i++)
		{
			Particle p = particles[i];
			density.add(particleMass, (int)(p.px/screenToGrid), (int)(p.py/screenToGrid));
			velocityX.add(p.vx, (int)p.px, (int)p.py);
			velocityY.add(p.vy, (int)p.px, (int)p.py);
		}
		
		for(int k = 0; k < 20; k++)
		{
			for(int x = 0; x < gridSize; x++)
			{
				for(int y = 0; y < gridSize; y++)
				{
					float dc = density.get(x,y);
					float du = potential.get(x,y+1);
					float dr = potential.get(x+1,y);
					float dd = potential.get(x,y-1);
					float dl = potential.get(x-1,y);
					
					float value = (du+dr+dd+dl-(jacobiconstant*dc))/4;
					potential.set(value, x,y);
				}
			}
		}
		
		for(int x = 0; x < gridSize; x++)
		{
			for(int y = 0; y < gridSize; y++)
			{
				delPX.set((potential.get(x+1,y)-potential.get(x-1, y))/2, x, y);
				delPY.set((potential.get(x,y+1)-potential.get(x, y-1))/2, x, y);
			}
		}
		
		for(int i = 0; i < particleCount; i++)
		{
			Particle p = particles[i];
			p.vx -= bilin_interp(delPX, p.px/screenToGrid, p.py/screenToGrid)*dt;
			p.vy -= bilin_interp(delPY, p.px/screenToGrid, p.py/screenToGrid)*dt;
			
			//p.vx -= p.vx*0.01f;
			//p.vy -= p.vy*0.01f;
			
			p.px += p.vx*dt;
			p.py += p.vy*dt;
			
			while(p.px < 0)p.px += screenSize;
			while(p.py < 0)p.py += screenSize;
			p.px %= screenSize;
			p.py %= screenSize;
			
			
			
			particles[i] = p;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		float avgPotential = 0;
		for(int x = 0; x < gridSize; x ++)
		{
			for(int y = 0; y < gridSize; y++)
			{
				avgPotential += potential.get(x,y);
			}
		}
		avgPotential/=gridSize*gridSize;
		for(int x = 0; x < gridSize; x ++)
		{
			for(int y = 0; y < gridSize; y++)
			{
				//System.out.println(potential.get(x,y)/1e-7);
				float scale = (float) ((potential.get(x, y)-avgPotential)*1e5);
				if(scale > 1) scale = 1;
				if(scale < 0) scale = 0;
				g.setColor(new Color(0,0,.5f*(1-scale)));
				//System.out.println(scale);
				g.fillRect(x*2, y*2, 2, 2);
				
				float vX = delPX.get(x,y);
				float vY = delPY.get(x,y);
				float magnitude = (float) Math.sqrt(vX*vX + vY*vY);
				
				vX /= magnitude;
				vY /= magnitude;
				g.setColor(Color.red);
				//g.drawLine(x*2, y*2, (int)(x+vX*2), (int)(y*vY*2));
				
			}
		}
		for(int i = 0; i < particleCount; i++)
		{
			Particle p = particles[i];
			float scale = Math.abs((float) (density.get((int)(p.px/2), (int)(p.py/2))/particleCount));
			scale = (float) lerp(0.1, 1, scale*1000);
			if(scale > 1) scale = 1;
			g.setColor(new Color(0,scale,0));
			//g.setColor(Color.CYAN);
			g.drawLine((int)(p.px), (int)(p.py),(int)(p.px), (int)(p.py));
		}
		
				
	}
	
	private static double lerp(double s, double e, double t) {
        return s + (e - s) * t;
    }
 
    private static double blerp(double c00, double c10, double c01, double c11, double tx, double ty) {
        return lerp(lerp(c00, c10, tx), lerp(c01, c11, tx), ty);
    }
	
	public double bilin_interp(Grid grid, float x, float y)
	{
		
		//assuming the coordinates are in gridspace coords
		x+=.5; y +=.5;
		int x1 = (int) x;
		int y1 = (int) y;
		int x2 = x1 + 1;
		int y2 = y1 + 1;
		
		float dx = x - x1, idx = 1-dx;
		float dy = y - y1, idy = 1-dy;
		
		float q11 = grid.get(x1,y1);
		float q21 = grid.get(x2,y1);
		float q12 = grid.get(x1,y2);
		float q22 = grid.get(x2,y2);
		
		//float fxy1 = idx*q11 + dx*q21;
		//float fxy2 = idx*q12 + dx*q22;
		
		return blerp(q11,q21,q12,q22,dx,dy);
		
		//return idy*fxy1 + dy*fxy2;
	}
	
}
