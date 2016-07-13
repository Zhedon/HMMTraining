package Process;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ThreadStartProbSequence extends Thread{
	
	private String directory;
	private String name;
	private Boolean finished;
	
	public ThreadStartProbSequence(String directory, String name) {
		// TODO Auto-generated constructor stub
		finished = false;
		this.directory = directory;
		this.name = name;
	}
	
	public boolean isFinished(){
		return finished;
	}

	public void run(){
		FileInputStream fileInputStream;
		try {
			System.out.println("Thread: " + name + " step1 start...");
			fileInputStream = new FileInputStream(directory + "\\" + name);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			
			File sq =new File(directory + "\\StatusSequence"); 
			if  (!sq.exists() && !sq.isDirectory())      
			{
				sq.mkdir();    
			}						
			FileOutputStream fstatus = new FileOutputStream(directory + "\\StatusSequence\\" + name);
			OutputStreamWriter wstatus = new OutputStreamWriter(fstatus, "UTF-8");
			
			File sp =new File(directory + "\\StartProb"); 
			if  (!sp.exists() && !sp.isDirectory())      
			{
				sp.mkdir();    
			}	
			FileOutputStream fstart = new FileOutputStream(directory + "\\StartProb\\" + name);
			OutputStreamWriter wstart = new OutputStreamWriter(fstart,"UTF-8");		

			long total=0;
			long total_S=0;
			long total_B=0;			
			String temp = null;
			int i;
			
			while((temp = bufferedReader.readLine()) != null){
				
/*				Pattern p = Pattern.compile("/[a-z]*");
				Matcher m = p.matcher(temp);
				while(m.find()){
					temp = temp.replace(m.group(), "");
				}*/
				temp = temp.replaceAll(" {2,}", " ");
				temp = temp.replace("[^\u4E00-\u9FA5]", "");

				for(i=0;i<temp.length()-1;i++){					
					if(temp.charAt(i)!=' '){						
						if(i==0){
							if(temp.charAt(i+1)==' '){
								wstatus.write("S");
								total_S++;
								total++;
							}
							else{
								wstatus.write("B");
								total_B++;
								total++;
							}
						}
						else{
							if(temp.charAt(i-1)==' '){
								if(temp.charAt(i+1)!=' '){
									wstatus.write("B");
									total_B++;
									total++;
								}
								else{
									wstatus.write("S");
									total_S++;
									total++;
								}
							}
							else{
								if(temp.charAt(i+1)!=' '){
									wstatus.write("M");
								}
								else{
									wstatus.write("E");
								}
							}
						}
					}
/*					else{
						wstatus.write(temp.charAt(i));
					}	*/
				}
				wstatus.write("\r\n");
			}

			wstart.write(String.valueOf(total) + "\r\n");
			wstart.write(String.valueOf(total_S) + "\r\n");
			wstart.write(String.valueOf(total_B) + "\r\n");
			wstart.flush();
			wstart.close();
			wstatus.flush();
			wstatus.close();
			bufferedReader.close();
			System.out.println("Thread: " + name + " generate statprob and status_sequence finished");
			finished = true;
		}		
				
		 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
