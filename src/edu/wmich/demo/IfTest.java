package edu.wmich.demo;

public class IfTest {

	public static void main(String args[]) {
		
		int x  = 1;
		int a = 3;
		
		a = x + 1;
		
		while (x < 1)
		{
			x = x + 1;
		
		}	
		if (a < 0)
		{
			if (a < 1)
				a = a * 1;
			else
			{
				while ( a < 2)
				{
					double f = 1.2;
					a = a - 1;
					a = a - 2;
				}
				
			}
		}else
		{	a = a + 1;
			a = a + 2;
			a = a + 3;
		} 
		a = a + 12; 
		double ff = 12;
	} 
	
	
	
}
