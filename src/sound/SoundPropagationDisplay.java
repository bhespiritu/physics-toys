package sound;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gravity.Grid;

public class SoundPropagationDisplay extends JPanel{

	public static void main(String[] args) {
		JFrame frame = new JFrame("Sound");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SoundPropagationDisplay gravity = new SoundPropagationDisplay();
		frame.setContentPane(gravity);
		frame.pack();
		frame.setVisible(true);
		for(;;)
		{
			//System.out.println("PING");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gravity.timeStep();
			frame.repaint();
		}
	}
	
	float jacobiconstant = 0.1f;//= (float) (4*Math.PI*6.67408e-11);
	
	float dt = 1/24f;
	int gridSize = 250;
	static int screenSize = 500;
	float screenToGrid;
	float time = 0;
	
	float particleMass = 1;
	
	private static final Dimension defaultDimension = new Dimension(screenSize,screenSize);

	Grid sound, soundOther;
	Grid isWall;
	
	int emitterX = 10, emitterY = 10;
	float emitterFrequency = 0.5f;
	float emitterAmplitude = 1;
	private Grid soundVel;
	private Grid soundVelOther;
	
	public SoundPropagationDisplay() {
		screenToGrid = (float)screenSize/gridSize;
		setPreferredSize(defaultDimension);
		setBackground(Color.BLACK);
		sound = new Grid(gridSize, gridSize);
		soundOther = new Grid(gridSize, gridSize);
		soundVel = new Grid(gridSize, gridSize);
		soundVelOther = new Grid(gridSize, gridSize);
		isWall = new Grid(gridSize, gridSize);
		for(int i = 0; i < gridSize*gridSize; i++)
		{
			sound.set(0,i);
			soundVel.set(0, i);
			soundVelOther.set(0, i);
			soundOther.set(0, i);
			isWall.set(0.1f,i);
		}
		
		for(int y = 0; y < gridSize; y++)
		{
			isWall.set(0, 30, y);
		}
		isWall.set(0.1f, 30,10);
		isWall.set(0.1f, 30,20);
	}
	
	public float attack = 10f;
	
	public void timeStep()
	{
		time += dt;
		for(int y = 0; y < gridSize; y++)
			sound.set((float) (emitterAmplitude*Math.sin(2*Math.PI*time*emitterFrequency)), 0, y);
		for(int x = 0; x < gridSize; x++)
		{
			for(int y = 0; y < gridSize;y++)
			{
				float delta = 0;
				delta += sound.get(x, y-1);
				delta += sound.get(x, y+1);
				delta += sound.get(x-1, y);
				delta += sound.get(x+1, y);
				delta -= 4*sound.get(x, y);
				soundVelOther.set(soundVel.get(x, y) + delta*attack*dt, x,y);
			}
		}
		for(int x = 0; x < gridSize; x++)
		{
			for(int y = 0; y < gridSize;y++)
			{
				soundOther.set(sound.get(x,y) + soundVelOther.get(x, y)*dt*isWall.get(x, y), x, y);
			}
		}
		Grid temp = sound;
		sound = soundOther;
		soundOther = temp;
		
		temp = soundVel;
		soundVel = soundVelOther;
		soundVelOther = temp;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int x = 0; x < gridSize; x++)
		{
			for(int y = 0; y < gridSize; y++)
			{
				float emitter = (sound.get(x, y));
				
				if(emitter < -1) emitter = -1;
				if(emitter > 1) emitter = 1;
				if(emitter > 0) 
					g.setColor(new Color(0,emitter,0));
				else
					g.setColor(new Color(-emitter,0,0));
				g.fillRect(x*10, y*10, 10, 10);
			}
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
