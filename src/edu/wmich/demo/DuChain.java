package edu.wmich.demo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DuChain {
	List<CBlock> blocks;
	Map<Pair<String, String>, Integer> mapping,defMap;
	Map<Pair<String, String>, Vector<String>> duchain;
	int count,size;
	long in1[],in2[],out1[],out2[];
	
	public DuChain() {
		
		blocks = Demo.blocks;
		mapping = new HashMap<Pair<String,String>, Integer>();
		defMap = new HashMap<Pair<String,String>, Integer>();
		duchain = new HashMap<Pair<String,String>, Vector<String>>();
		size = blocks.size();
		
		//Setting the Boolean Array mapping for later reference
		count = 0;
		for(int i=0;i < size;i++){
			CBlock b = blocks.get(i);
			Iterator<Pair<String, String>> it2 = b.use.iterator();
			while(it2.hasNext()){
				Pair<String, String> temp = it2.next();
				if(!check(mapping,temp))
					mapping.put(temp, count++);
			}
			
			Iterator<Pair<String, String>> it3 = b.def.iterator();
			while(it3.hasNext()){
				Pair<String, String> temp = it3.next();
				if(!check(mapping,temp))
					mapping.put(temp, count++);
			}
		}
		
		/*
		Iterator<Map.Entry<Pair<String,String>, Integer>> entries = mapping.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<Pair<String,String>, Integer> entry = entries.next();
		    System.out.println(entry.getValue() + " : (" + entry.getKey().getLeft() + "," +  entry.getKey().getRight() + ")");
		}
		*/
		
		count--;
		System.out.println("Count : " + count);
		//Obtain use and negation-def for each block in Binary Notation
		for(int i=0;i < size;i++){
			CBlock b = blocks.get(i);
			Iterator<Pair<String, String>> u = b.use.iterator();
			while(u.hasNext())
				b.use_num = ((1 << (count - search(u.next()))) | b.use_num);
			
			Iterator<Pair<String, String>> d = b.def.iterator();
			while(d.hasNext())
				b.def_num = ((1 << (count - search(d.next()))) | b.def_num);
			b.ndef_num = ~b.def_num;
		}
		
		/*
		for(int i=0;i < size;i++){
			CBlock cb = blocks.get(i);
			System.out.println("Block : " + i + " " + Integer.toBinaryString((int)cb.use_num) + " " + Integer.toBinaryString((int)cb.def_num) + " " + Integer.toBinaryString((int)cb.ndef_num));
		}
		*/
		
		//Producing Iterations
		in1 = new long[size];out1 = new long[size];
		int iter  = 0;
		while(true){
			iter++;
			System.out.println("Iteration : " + iter);
			in2 = new long[size];
			out2 = new long[size];
			//Calculating IN using formula IN[B] = USE[B] U (OUT[B] – DEF[B])
			for(int i=0;i < size;i++)
				in2[i] = blocks.get(i).use_num | (out1[i] & blocks.get(i).ndef_num);
			
			//If any two iterations have same value of IN[B] then break
			if(condition(in1,in2))
				break;
			
			//Calculating OUT based on relations of blocks
			for(int i=0;i < size;i++){	
				Iterator<String> itr = blocks.get(i).next.iterator();
				long temp = 0;
				while(itr.hasNext())
					temp = temp | in2[Demo.connection.get(itr.next())];
				out2[i] = temp;
			}
			
			in1 = in2.clone();
			out1 = out2.clone();
		}
		
		//obtaining IN[Bi] ans OUT[Bi]
		for(int i=0;i < size;i++){
			CBlock cb = blocks.get(i);
			
			long num1 = in1[i];
			int pos = 0;
			while(num1 > 0){
				if((num1 & 1) == 1)
					cb.in.add(findPairFromId(pos));
				pos++;
				num1 >>= 1;
			}
			
			long num2 = out1[i];
			pos = 0;
			while(num2 > 0){
				if((num2 & 1) == 1)
					cb.out.add(findPairFromId(pos));
				pos++;
				num2 >>= 1;
			}
		}
		
		//Form the DU Chain
		//Obtain map<defined-pair,block no> and for each pair , serach out[block no] 
		for(int i=0;i < size;i++){
			CBlock b = blocks.get(i);
			count = 0;
			Iterator<Pair<String, String>> itd = b.defHelp.iterator();
			while(itd.hasNext()){
				Pair<String,String> p = itd.next();
				Vector<String> vs = findVectorFromOut(i,p.getLeft());
				duchain.put(p, vs);
				System.out.println("Adding to Du Chain" + "(" + p.getLeft() + "," + p.getRight() + ")");
			}
		}
		
		printDuChain();
		
	}
	
	//prints the DU chain
	private void printDuChain() {
		System.out.println("Size Of Du Chain : " + duchain.size());
		System.out.println("=========DU CHAIN=========");
		Iterator<Map.Entry<Pair<String,String>, Vector<String > > > entries = duchain.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<Pair<String,String>, Vector<String>> entry = entries.next();
		    System.out.print("(" + entry.getKey().getLeft() + "," + entry.getKey().getRight() + ")" + '\t');
		    Iterator<String> i = entry.getValue().iterator();
		    while(i.hasNext())
		    	System.out.print(i.next() + ",");
		    System.out.println("");
		}
	}

	private Vector<String> findVectorFromOut(int i, String left) {
		Vector<String> ans = new Vector<String>();
		List<Pair<String, String>> out = blocks.get(i).out;
		Iterator<Pair<String,String>> it = out.iterator();
		while(it.hasNext()){
			Pair<String,String> p = it.next();
			if(left.equals(p.getLeft()))
				ans.add(p.getRight());
		}
		return ans;
	}

	
	private Pair<String,String> findPairFromId(int n){
		Iterator<Map.Entry<Pair<String,String>, Integer>> entries = mapping.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<Pair<String,String>, Integer> entry = entries.next();
		    if(n == entry.getValue())
		    	return entry.getKey();
		}
		return null;
	}
	
	private boolean condition(long a[],long b[]){
		for(int i=0;i < size;i++)
			if(a[i] != b[i])
				return false;
		return true;
	}
	
	private boolean check(Map<Pair<String, String>, Integer> map ,Pair<String, String > p){
		for (Pair key : map.keySet()) 
		    if(p.equals(key))
		    	return true;
		return false;    
	}
	
	private long search(Pair<String,String> p){
		Iterator<Map.Entry<Pair<String,String>, Integer>> entries = mapping.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<Pair<String,String>, Integer> entry = entries.next();
		    if(p.equals(entry.getKey())){
		    	return entry.getValue();
		    }
		}
		System.out.println("Not Found");
		return -1;
	}
}
