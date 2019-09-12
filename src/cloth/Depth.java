package cloth;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Depth {

	public static void main(String[] args) throws IOException {
		int[] time = new int[1000];
		for(int i = 0; i < 1000; i++)
		{
			double j = i;
			int k = 0;
			while(j > 2)
			{
				j = Math.pow(j, 0.5);
				k++;
			}
			time[i] = k;
			
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("out.csv"));
		for(int i = 0; i < 1000; i++)
		{
			System.out.println(i +"," + time[i]);
			writer.write(i +"," + time[i]);
			writer.newLine();
		}
	
		writer.close();
	}
	
}
