package edu.wmich.demo;

public class Test {

	public static int[] main(int m, int n) {    
	       int i = m -1; // s1
	       int j = n;    // s2
	       int a = i + 1;  // s3
	       while(a < 2)
	       {
	           i = i + 1; // s4
	           j = j - 1 + a; // s5
	           if( i+j +a < 4) // s6
	           {
	               a = i + a; // s7
	               i = i + j; // s8
	           }
	           i = a + j; // s9
	           break;
	       }
	       return (new int[] {i,j,a});
	   }
}
