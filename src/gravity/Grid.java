package gravity;

public class Grid {

	private float[] current;

	
	int width, height;
	
	public Grid(int w, int h) {
		width = w;
		height = h;
		
		current = new float[w*h];
	}
	
	public int getIndex(int x, int y)
	{
		while(x<0) x += width;
		while(y<0) y += height;
		x %= width;
		y %= height;
		return y*width + x;
	}
	
	public float get(int x, int y)
	{
		return current[getIndex(x,y)];
	}
	
	public float get(int i) {return current[i];}
	
	public float get(float x, float y)
	{
		if((int) x == x && (int) y == y) return get((int)x,(int)y);
		
		return (float) bilin_interp(this, x, y);
		
	}
	
	public void set(float value, int x, int y)
	{
		current[getIndex(x,y)] = value;
	}
	
	public void set(float value, int i)
	{
		current[i] = value;
	}
	
	public void add(float value, int x, int y)
	{
		current[getIndex(x,y)] += value;
	}
	
	public void add(float value, int i)
	{
		current[i] += value;
	}
	

	
	public float[] getCurrent()
	{
		return current;
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
