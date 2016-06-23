package Train;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import ICTCLAS.I3S.AC.ICTCLAS50;
import Process.ThreadCut;
import Process.ThreadDetag;
import Process.ThreadStartProbSequence;
import Process.ThreadTransEmitProb;

public class TrainClass {
	private String directory;
	private String corpus_name;
	
	public TrainClass(String directory){
		if(directory.charAt(directory.length()-1)=='\\')
			directory = directory.substring(0, directory.length()-1);
		this.directory = directory;
		this.corpus_name = directory.substring(directory.lastIndexOf('\\') + 1);
	}
	private LinkedList<String> filelist = new LinkedList<>();
	
	public void train_with_cutted(){

		filelist.clear();
		LinkedList<ThreadStartProbSequence> startthreadlist= new LinkedList<>();
		File index = new File(directory);
		for(int i=0; i<index.listFiles().length;i++){
			if(index.listFiles()[i].isDirectory() == false){
				String name = index.listFiles()[i].getName();
				filelist.add(name);
				startthreadlist.add(new ThreadStartProbSequence(directory, name));
			}
		}
		
		for(ThreadStartProbSequence thread:startthreadlist){
			thread.start();
		}
		while(true){
			boolean start_status = true;
			for(ThreadStartProbSequence thread : startthreadlist){
				start_status = start_status && thread.isFinished();
			}
			if(start_status == true){
				System.out.println("All start prob and sequence finished...");
				break;
			}
		}
		
		long total=0l,total_B=0l,total_S=0l;
		try{
			File startproblist = new File(directory + "\\StartProb");
			for(int i=0; i<startproblist.listFiles().length;i++){
				if(startproblist.listFiles()[i].isDirectory() == false){
					FileInputStream startprobfile = new FileInputStream(startproblist.listFiles()[i].getAbsolutePath());
					InputStreamReader starprobin = new InputStreamReader(startprobfile,"UTF-8");
					BufferedReader startprobuf = new BufferedReader(starprobin);
					
					total += Integer.parseInt(startprobuf.readLine());
					total_S += Integer.parseInt(startprobuf.readLine());
					total_B += Integer.parseInt(startprobuf.readLine());
					
					startprobuf.close();
				}
			}	
			File rs =new File(directory + "\\Result"); 
			if  (!rs.exists() && !rs.isDirectory())      
			{
				rs.mkdir();    
			}
			
			FileOutputStream starprobout = new FileOutputStream(directory + "\\result\\" + corpus_name + "_prob_start.py");
			OutputStreamWriter startprowriter = new OutputStreamWriter(starprobout, "UTF-8");
			startprowriter.write("P={");
			startprowriter.write("\'B\':" + String.valueOf(Math.log((double)total_B/total))+",\r\n");
			startprowriter.write("\'E\':-3.14e+100,\r\n\'M\':-3.14e+100,\r\n");
			startprowriter.write("\'S\':" + String.valueOf(Math.log((double)total_S/total))+"}\r\n");
			startprowriter.flush();
			startprowriter.close();
			System.out.println("Start prob matrix finished...");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		LinkedList<ThreadTransEmitProb> transemitthreadlist = new LinkedList<>();
		for(String name : filelist){
			transemitthreadlist.add(new ThreadTransEmitProb(directory, name));
		}
		for(ThreadTransEmitProb thread : transemitthreadlist){
			thread.start();
		}
		
		while(true){
			boolean transemit_status = true;
			for(ThreadTransEmitProb thread : transemitthreadlist){
				transemit_status = transemit_status && thread.isFinished();
			}
			if(transemit_status == true){
				System.out.println("All trans prob and emit prob finished...");
				break;
			}
		}
		try {
			FileOutputStream emitout = new FileOutputStream( directory + "\\Result\\" + corpus_name + "_prob_emit.py");
			OutputStreamWriter emitwriter = new OutputStreamWriter(emitout, "UTF-8");

			emitwriter.write("from __future__ import unicode_literals\r\nP={");
			Set<Map.Entry<Character, Hashtable<Character, Integer>>> set = ThreadTransEmitProb.total.entrySet();

			for (Map.Entry<Character, Hashtable<Character, Integer>> p : set) {
				emitwriter.write("\'" + p.getKey() + "\'" + ":{");
				Set<Map.Entry<Character, Integer>> base_set = p.getValue().entrySet();
				for (Map.Entry<Character, Integer> base : base_set) {
					emitwriter.write("\'"+ ThreadTransEmitProb.chineseToUnicode(base.getKey()) + "\':" + String.valueOf(Math.log((double) base.getValue()/ ThreadTransEmitProb.count_S.get(p.getKey()))+ ",\r\n"));
				}
				emitwriter.write("},\r\n");
			}
			emitwriter.write("}\r\n");
			emitwriter.flush();
			emitwriter.close();
			System.out.println("Emit prob matrix finished...");			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		try {
			HashMap<String, Long> trans_map = new HashMap<>();

			for (String name : filelist) {
				FileInputStream fileInputStream = new FileInputStream(directory + "\\TransProb\\" + name);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String temp = null;

				while ((temp = bufferedReader.readLine()) != null) {
					String statusname = temp.substring(temp.indexOf('(') + 1, temp.indexOf(')'));
					String value = temp.substring(temp.indexOf('=') + 1);
					addHashMap(statusname, value, trans_map);
				}
				bufferedReader.close();
			}
			
			FileOutputStream fosTrans = new FileOutputStream(directory + "\\Result\\" + corpus_name +"_prob_trans_hash.py");
			OutputStreamWriter oswTrans = new OutputStreamWriter(fosTrans, "UTF-8");
			oswTrans.write("P={");
			oswTrans.write("\'B\': {\'E\':" + String.valueOf(Math.log((double) trans_map.get("EB") / trans_map.get("B"))) + ",\'M\':" + String.valueOf(Math.log((double) trans_map.get("MB") / trans_map.get("B"))) + "},\r\n");
			oswTrans.write("\'E\': {\'B\':" + String.valueOf(Math.log((double) trans_map.get("BE") / trans_map.get("E"))) + ",\'S\':" + String.valueOf(Math.log((double) trans_map.get("SE") / trans_map.get("E"))) + "},\r\n");
			oswTrans.write("\'M\': {\'E\':" + String.valueOf(Math.log((double) trans_map.get("EM") / trans_map.get("M"))) + ",\'M\':" + String.valueOf(Math.log((double) trans_map.get("MM") / trans_map.get("M"))) + "},\r\n");
			oswTrans.write("\'S\': {\'B\':" + String.valueOf(Math.log((double) trans_map.get("BS") / trans_map.get("S"))) + ",\'S\':" + String.valueOf(Math.log((double) trans_map.get("SS") / trans_map.get("S"))) + "}}\r\n");
			oswTrans.flush();
			oswTrans.close();
			System.out.println("Trans prob matrix finished...");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();			
		}
		System.out.println("All finished, plaese check the result in direcotry " + directory + "\\Result\\ !");	
	}
	
	public void train_with_tagged(){

		File dt =new File(directory + "\\Detag"); 
		if(!dt.exists() && !dt.isDirectory())      
		{
			dt.mkdir();    
		}
		
		File index = new File(directory);
		LinkedList<ThreadDetag> detagthreadlist = new LinkedList<>();

		for (int i = 0; i < index.listFiles().length; i++) {
			if (index.listFiles()[i].isDirectory() == false) {
				String name = index.listFiles()[i].getName();
				detagthreadlist.add(new ThreadDetag(directory, name));
			}
		}
		for(ThreadDetag thread : detagthreadlist){
			thread.start();
		}
		while(true){
			boolean detag_status = true;
			for(ThreadDetag thread : detagthreadlist){
				detag_status = detag_status && thread.isFinished();
			}
			if(detag_status == true){
				System.out.println("All detag finished...");
				break;
			}
		}
		this.directory = directory + "\\Detag";
		train_with_cutted();
		
	}
	
	public void train_with_raw(){	
		File dt =new File(directory + "\\Cut"); 
		if(!dt.exists() && !dt.isDirectory())      
		{
			dt.mkdir();    
		}
		
		File index = new File(directory);
		LinkedList<ThreadCut> cutthreadlist = new LinkedList<>();
		
		for (int i = 0; i < index.listFiles().length; i++) {
			if (index.listFiles()[i].isDirectory() == false) {
				String name = index.listFiles()[i].getName();
				cutthreadlist.add(new ThreadCut(directory, name));
			}
		}
		for(ThreadCut thread : cutthreadlist){
			thread.start();
		}
		while(true){
			boolean detag_status = true;
			for(ThreadCut thread : cutthreadlist){
				detag_status = detag_status && thread.isFinished();
			}
			if(detag_status == true){
				System.out.println("All cut finished...");
				break;
			}
		}
		this.directory = directory + "\\Cut";
		train_with_cutted();
			
	}
	
	private static void addHashMap(String name, String value,HashMap<String,Long> map){
		if(map.containsKey(name)){
			map.put(name, map.get(name)+Long.parseLong(value));	
		}
		else{
			map.put(name, Long.parseLong(value));
		}
	}
}
