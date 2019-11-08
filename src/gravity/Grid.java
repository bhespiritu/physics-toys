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
	

	
}
