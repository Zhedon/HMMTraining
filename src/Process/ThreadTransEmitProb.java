package Process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class ThreadTransEmitProb extends Thread {
	public String name;
	public String directory;
	
	public boolean finished = false;
	
	public static Hashtable<Character, Integer> mapB = new Hashtable<>();
	public static Hashtable<Character, Integer> mapM = new Hashtable<>();
	public static Hashtable<Character, Integer> mapE = new Hashtable<>();
	public static Hashtable<Character, Integer> mapS = new Hashtable<>();
	public static HashMap<Character, Hashtable<Character, Integer>> total= new HashMap<>();
	
	public static Hashtable<Character, Long> count_S = new Hashtable<>();
	static
	{
		total.put('B', mapB);
		total.put('M', mapM);
		total.put('E', mapE);
		total.put('S', mapS);
		
		count_S.put('B', 0l);
		count_S.put('M', 0l);
		count_S.put('E', 0l);
		count_S.put('S', 0l);
	}

	public boolean isFinished(){
		return finished;
	}
	
	public ThreadTransEmitProb(String directory, String name) {
		// TODO Auto-generated constructor stub
		this.directory = directory;
		this.name = name;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 
		try {
			System.out.println("Thread " + name + " step2 started...");
			
			FileInputStream fisStatus = new FileInputStream(directory + "\\StatusSequence\\" + name);
			InputStreamReader isrStatus = new InputStreamReader(fisStatus,"UTF-8");
			BufferedReader brStatus = new BufferedReader(isrStatus);
			
			FileInputStream fisSource = new FileInputStream(directory + "\\" + name);
			InputStreamReader isrSource = new InputStreamReader(fisSource,"UTF-8");
			BufferedReader brSource = new BufferedReader(isrSource);
				
			String temp = null;
			String source = null;
			long PB=0l,PE=0l,PM=0l,PS=0l,Peb=0l,Pmb=0l,Pbe=0l,Pse=0l,Pem=0l,Pmm=0l,Pbs=0l,Pss=0l;
			while((temp = brStatus.readLine()) != null &&(source = brSource.readLine()) != null){		
				int i,j;
				if(temp.length()>0){
				if(temp.charAt(0)=='B'){
					addHashMap(source.charAt(0), mapB);
				}
				else if(temp.charAt(0)=='M'){
					addHashMap(source.charAt(0), mapM);
				}
				else if(temp.charAt(0)=='E'){
					addHashMap(source.charAt(0), mapE);
				}
				else if(temp.charAt(0)=='S'){
					addHashMap(source.charAt(0), mapS);
				}
				for(i=1,j=1;i<temp.length()&&j<temp.length();i++,j++){
					while(source.charAt(j)==' '){
						j++;
					}
					//System.out.println("Thread:" + name + ";Index:" + String.valueOf(i)+";Charactor:" + source.charAt(j));
					
					if(temp.charAt(i-1)=='B'){
						PB++;
						addHashMap(source.charAt(j), mapB);
						if(temp.charAt(i)=='E'){
							Peb++;
						}
						else{
							Pmb++;
						}
					}
					else if(temp.charAt(i-1)=='E'){
						PE++;
						addHashMap(source.charAt(j), mapE);
						if(temp.charAt(i)=='B'){
							Pbe++;
						}
						else{
							Pse++;
						}
					}
					else if(temp.charAt(i-1)=='M'){
						PM++;
						addHashMap(source.charAt(j), mapM);
						if(temp.charAt(i)=='E'){
							Pem++;
						}
						else{
							Pmm++;
						}
					}
					else{
						PS++;
						addHashMap(source.charAt(j), mapS);
						if(temp.charAt(i)=='B'){
							Pbs++;
						}
						else{
							Pss++;
						}
					}
				}
				}
			}
			brSource.close();
			brStatus.close();
			
			File ts =new File(directory + "\\TransProb"); 
			if  (!ts.exists() && !ts.isDirectory())      
			{
				ts.mkdir();    
			}
			FileOutputStream fosTrans = new FileOutputStream(directory + "\\TransProb\\" + name);
			OutputStreamWriter oswTrans = new OutputStreamWriter(fosTrans, "UTF-8");
//			oswTrans.write("P={");
//			oswTrans.write("\'B\': {\'E\':"+ String.valueOf(Math.log((double)Peb/PB))+",\'M\':"+String.valueOf(Math.log((double)Pmb/PB))+"},\r\n");
//			oswTrans.write("\'E\': {\'B\':"+ String.valueOf(Math.log((double)Pbe/PE))+",\'S\':"+String.valueOf(Math.log((double)Pse/PE))+"},\r\n");
//			oswTrans.write("\'M\': {\'E\':"+ String.valueOf(Math.log((double)Pem/PM))+",\'M\':"+String.valueOf(Math.log((double)Pmm/PM))+"},\r\n");
//			oswTrans.write("\'S\': {\'B\':"+ String.valueOf(Math.log((double)Pbs/PS))+",\'S\':"+String.valueOf(Math.log((double)Pss/PS))+"}}\r\n");
			oswTrans.write("Frequecy(B)="+PB+"\r\nFrequecy(EB)=" + Peb + "\r\nFrequecy(MB)=" + Pmb + "\r\nFrequecy(E)=" + PE + "\r\nFrequecy(BE)=" + Pbe + "\r\nFrequecy(SE)=" + Pse + "\r\nFrequecy(M)=" + PM +"\r\nFrequecy(EM)=" + Pem + "\r\nFrequecy(MM)=" + Pmm + "\r\nFrequecy(S)=" + PS + "\r\nFrequecy(BS)=" + Pbs + "\r\nFrequecy(SS)=" + Pss);
			oswTrans.flush();
			oswTrans.close();			
			
			count_S.put('B', count_S.get('B') + PB);
			count_S.put('M', count_S.get('M') + PM);
			count_S.put('E', count_S.get('E') + PE);
			count_S.put('S', count_S.get('S') + PS);
			
			System.out.println("Thread: " + name + " generate trans prob and emit prob finished...");
			finished = true;

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void addHashMap(Character c,Hashtable<Character,Integer> map){
		if(map.containsKey(c)){
			map.put(c, map.get(c)+1);
		}
		else{
			map.put(c, 1);
		}
	}	
	public static String chineseToUnicode(Character c){  
        String result="";
        String code = Integer.toHexString(c);
        
        while(code.length()<4){
			code = "0" + code;
		}
        
        result+="\\u" + code;  
        return result;  
    }

}
