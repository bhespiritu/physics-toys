package particle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ParticleLife extends JPanel{

	int ppm = 25;
	
	float particleMass = 1;
	float minSize = 0.1f, maxSize = 2;
	float maxForce = 5;
	
	float dt = 0.05f;
	
	int numParticleTypes = 4;
	int numParticles = 700;
	
	float worldSize = 30;
	
	float fricCoef = .9f;
	
	private class ParticleRelationship
	{
		float strength;
		float minDist; 
		float maxDist;
	}
	
	private class Particle
	{
		int type;
		float px, py;
		float vx = 0, vy = 0;
	}
	
	ParticleRelationship[][] relationships;
	
	ArrayList<Particle> particles = new ArrayList<Particle>();
	
	public ParticleLife() {
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension((int)(ppm*worldSize), (int)(ppm*worldSize)));
		randomizeRelationships();
		init();
	}
	
	public void randomizeRelationships()
	{
		relationships = new ParticleRelationship[numParticleTypes][numParticleTypes];
		for(int i = 0; i < numParticleTypes; i++)
		{
			for(int j = 0; j < numParticleTypes; j++)
			{
				ParticleRelationship pr = new ParticleRelationship();
				
				float distA = (float) (Math.random()*(maxSize-minSize) + minSize);
				float distB = (float) (Math.random()*(maxSize-minSize) + minSize);
				if(distA < distB)
				{
					pr.minDist = distA;
					pr.maxDist = distB;
				} else
				{
					pr.minDist = distB;
					pr.maxDist = distA;
				}
				
				pr.strength = (float) (Math.random()*maxForce * (Math.random() < 0.5f ? -1 : 1));
				relationships[i][j] = pr;
			}
		}
	}
	
	public void init()
	{
		particles.clear();
		for(int i = 0; i < numParticles; i++)
		{
			Particle p = new Particle();
			p.type = (int) Math.floor(numParticleTypes*Math.random());
			p.px = (float) (Math.random()*worldSize);
			p.py = (float) (Math.random()*worldSize);
			particles.add(p);
		}
	}
	
	public void timeStep()
	{
		for(int i = 0; i < numParticles; i++)
		{
			for(int j = 0; j < numParticles; j++)
			{
				if(i != j)
				{
					Particle a = particles.get(i);
					Particle b = particles.get(j);
					
					
					ParticleRelationship pr = relationships[a.type][b.type];
					
					
					float dx = Math.min(a.px - b.px, a.px - b.px + worldSize);
					float dy = Math.min(a.py - b.py, a.py - b.py + worldSize);
					
					
					float dist2 = ((dx)*(dx)+(dy)*(dy)); 
					float dist = (float) Math.sqrt(dist2);
					
					dx /= dist;
					dy /= dist;
					
					float forceStrength = calculateForceStrength(dist, pr);
					
					
					
					a.vx += dx*forceStrength*dt;
					a.vy += dy*forceStrength*dt;
					
				}
			}
		}
		for(int i = 0; i < numParticles; i++)
		{
			Particle p = particles.get(i);
			p.vx *= fricCoef;
			p.vy *= fricCoef;
			p.px += p.vx * dt / particleMass;
			p.py += p.vy * dt / particleMass;
			while(p.px < 0) p.px += worldSize;
			while(p.py < 0) p.py += worldSize;
			p.px %= worldSize;
			p.py %= worldSize;
			
		}
	}
	
	private float calculateForceStrength(float dist, ParticleRelationship pr)
	{
		if(dist*dist < pr.minDist*pr.minDist)
		{
			return (pr.minDist*pr.minDist) - dist*dist;
		} else
		{
			float out = Math.max(Math.min(dist - pr.minDist, pr.maxDist - dist), 0);
			out /= (pr.maxDist-pr.minDist)/2;
			out *= pr.strength;
			return out;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int i = 0; i < numParticles; i++)
		{
			Particle p = particles.get(i);
			int px = (int) (p.px * ppm);
			int py = (int) (p.py * ppm);
			
			Color c = Color.getHSBColor((float)p.type/numParticleTypes, 1, 1);
			g.setColor(c);
			g.fillOval(px - 2, py - 2, 4, 4);
		}
	}
	
	public static void main(String[] args) {
		ParticleLife pl = new ParticleLife();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pl);
		frame.pack();
		frame.setVisible(true);
		int i = 0;
		for(;;)
		{
			if(i == 3000) 
			{
				pl.randomizeRelationships();
				pl.init();
				i = 0;
			}
			i++;
			pl.timeStep();
			pl.repaint();
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
