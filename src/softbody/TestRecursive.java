package softbody;

public class TestRecursive {
	public static void main(String[] args) {
		System.out.println(recurse(64));
	}
	
	public static int recurse(int n)
	{
		if(n <= 1) return 1;
		return n + recurse(n/2) + recurse(n/4) + recurse(n/8);
	}
}
