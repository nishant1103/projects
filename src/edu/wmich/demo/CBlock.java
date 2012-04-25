package edu.wmich.demo;

import java.util.ArrayList;
import java.util.List;

public class CBlock {
	
	List<Pair<String, String>> use,def,in,out,defHelp;
	List<String> next;
	long use_num,def_num,ndef_num;
	public CBlock() {
		
		/** gives USE[B] for a block B
		*/
		use = new ArrayList<Pair<String, String>>();
		/** gives DEF[B] for a block B
		*/
		def = new ArrayList<Pair<String, String>>();
		/** gives IN[B] for a block B
		*/
		in = new ArrayList<Pair<String, String>>();
		/** gives OUT[B] for a block B
		*/
		out = new ArrayList<Pair<String, String>>();
		/** gives the flow relation between blocks
		*/
		next = new ArrayList<String>();
		/** gives the flow relation between blocks
		*/
		defHelp = new ArrayList<Pair<String,String>>();
		/** numbers formed on basis of Boolean Array defined
		*/	
		use_num = def_num = ndef_num = 0;
	}
	
}
