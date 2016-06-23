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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadDetag extends Thread {
	public String name;
	public String directory;
	
	public boolean finished = false;	
	
	public boolean isFinished(){
		return finished;
	}
	
	public ThreadDetag(String directory, String name) {
		// TODO Auto-generated constructor stub
		this.directory = directory;
		this.name = name;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 
		try {
			System.out.println("Thread " + name + " Detag started...");
			
			FileOutputStream detagout = new FileOutputStream(directory + "\\Detag\\" + name);
			OutputStreamWriter detagwriter = new OutputStreamWriter(detagout, "UTF-8");

			
			FileInputStream tagin = new FileInputStream(directory + "\\" + name);
			InputStreamReader tagreader = new InputStreamReader(tagin, "UTF-8");
			BufferedReader tagbuffer = new BufferedReader(tagreader);
			
			String temp = null;
			while((temp = tagbuffer.readLine())!= null){
				temp = temp.replaceAll(" {2,}", " ");
				if (temp.length() > 0) {
					Pattern p = Pattern.compile("/[a-z]*");
					Matcher m = p.matcher(temp);
					while (m.find()) {
						temp = temp.replace(m.group(), "");
					}
					temp = temp.replaceAll(" {2,}", " ");
					detagwriter.write(temp);
					detagwriter.write("\r\n");
				}
			}
			tagbuffer.close();
			detagwriter.flush();
			detagwriter.close();			
			finished = true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	
    }

}
