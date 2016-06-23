package Process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ICTCLAS.I3S.AC.ICTCLAS50;

public class ThreadCut extends Thread {
	public String name;
	public String directory;
	
	public boolean finished = false;	
	
	public boolean isFinished(){
		return finished;
	}
	
	public ThreadCut(String directory, String name) {
		// TODO Auto-generated constructor stub
		this.directory = directory;
		this.name = name;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
		String argu = ".";
		try {
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false) {
				System.out.println("Init Fail!");
				throw new Exception();
			}
		} catch (UnsupportedEncodingException e1) {
			// TODOAuto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODOAuto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			System.out.println("Thread " + name + " Cut started...");
			
			FileOutputStream cutout = new FileOutputStream(directory + "\\Cut\\" + name);
			OutputStreamWriter cutwriter = new OutputStreamWriter(cutout, "UTF-8");
			
			FileInputStream rawin = new FileInputStream(directory + "\\" + name);
			InputStreamReader rawreader = new InputStreamReader(rawin, "UTF-8");
			BufferedReader rawbuffer = new BufferedReader(rawreader);
			
			String temp = null;
			while((temp = rawbuffer.readLine())!= null){
				temp = temp.replaceAll(" {2,}", " ");
				temp = temp.replace("[^\u4E00-\u9FA5]", "");
				if (temp.length() > 0) {
					temp = temp.replaceAll(" {2,}", " ");
					byte[] nativeBytes = testICTCLAS50.ICTCLAS_ParagraphProcess(temp.getBytes("UTF-8"), 0, 0);
					String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "UTF-8");
					cutwriter.write(nativeStr);
					cutwriter.write("\r\n");
				}
			}
			rawbuffer.close();
			cutwriter.flush();
			cutwriter.close();
			finished = true;
			System.out.println("");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
    }

}
