package Main;
//have to do email test .. date 2019 1 18
import java.io.BufferedReader;
import java.util.concurrent.Semaphore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import hihi.GoogleAuthentication;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
/**
 * Servlet implementation class NICEServlet
 */

@WebServlet("/NICEServlet")
@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory)
public class NICEServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String userDirString = "";
    private File file = null;
    private File file1 = null;
    private int thread_num = 0;
    private long end_t = 0;
    private long start_t = 0;
    private boolean operation_check = true;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NICEServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher view = request.getRequestDispatcher("NICE.html");
		view.forward(request, response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
			
		String input_type = request.getParameter("tabss");
		String emailAddress = request.getParameter("emailAddress");
		String set_num = request.getParameter("set_num"); //set the number of thread
		
		// Get Current Time for making save_folder
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		
		start_t = System.currentTimeMillis();	// for calculate working time

		// Make Directory
		userDirString = Setup.FileSaveDirectory+emailAddress+"/";
		
		File userDir=new File(userDirString); 

		NICE_METHOD.makeEmailDir(userDir,userDirString, strDate);
		
		//input file process Plink -> X
		NICE_METHOD.inputDataType(input_type, request, strDate, file, file1);
		

		
		/** NICE Instruction **/
		//path change, b_R close, 
		RequestDispatcher view = request.getRequestDispatcher("index.html");
		view.forward(request, response);
		//doGet(request, response);
		
		thread_num = NICE_METHOD.createThreadFolder(set_num, userDir);
		
		NICE_METHOD.divideX(userDirString, set_num, thread_num, input_type, file1);
		
//		
//		Process x_s = null; Process x_ss = null; Process y_r = null; Process N_combine = null; // make array later..
//		String num_x = ""; int line_x = 0;
//		try { //y -> y_rightdim, and x divide
//			BufferedReader br = new BufferedReader(new FileReader(userDirString+"/X.txt"));
//			int lines = 0;
//			while (br.readLine() != null) lines++;
//			br.close();
//			
//			
//			if(set_num.equals("5set")) {
//				line_x = (lines / thread_num) + 1; // if 10 part, divide 
//			}else {
//				line_x = (lines / 10) + 1; // if 10 part, divide 	
//			}
//			
//			x_ss = Runtime.getRuntime().exec("cmd split -d -l " + Integer.toString(line_x) + " " + userDirString +"/X.txt " + 
//												userDirString + "/x_");
//			x_ss.waitFor();
//			
//				if(input_type.equals("type2")) {
//					File rightdim_y = new File(userDirString + "/test.R");
//					FileWriter fw_y = new FileWriter(rightdim_y, true);
//					fw_y.write("Y = as.matrix(read.table(\"" + file1.getPath() + "\"))\n" + 
//							"write.table(t(Y), \"" + userDirString +"/Y_rightdim.txt\"," + "row.names = F, col.names = F, quote = F)\n");
//					System.out.println(rightdim_y.getPath());
//				
//					fw_y.flush();
//					fw_y.close();
//					y_r = Runtime.getRuntime().exec("R CMD BATCH " + rightdim_y.getPath());		
//					y_r.waitFor();
//				}
//		}catch(Exception e) {
//			e.printStackTrace();
//		}     
		
		File x_[];
		
		nice_multi part_[];
			
		Thread t_[];

		if(set_num.equals("5set")) {  //5SET CASE
			x_ = new File[thread_num];
			part_ = new nice_multi[thread_num];
			t_ = new Thread[thread_num];
			for(int i = 0; i < thread_num; i ++) {
				if(i < 10) {
					x_[i] = new File(userDirString+"/x_0" + (i));
				}
				else {
					x_[i] = new File(userDirString+"/x_" + (i));			
				}
				part_[i] = new nice_multi(x_[i], userDir_[i].getPath(), String.valueOf(i+1));
				t_[i] = new Thread(part_[i]);
			}		
			try {
				String N_num = "";
				for(int i = 0; i < thread_num; i++) {
					t_[i].start();
				}
				
				for(int i = 0; i < thread_num; i++) {
					t_[i].join();
				}
				for(int i = 0; i < thread_num; i++) {
					N_num = N_num + userDirString+"/"+String.valueOf(i+1)+"/NICE.txt ";
				}
				String[] command_s = {
						"/bin/sh",
						"-c",
						"cat " + N_num + ">" + userDirString + "/NICE.txt"
				};
	
				N_combine = Runtime.getRuntime().exec(command_s);
				N_combine.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else { //10SET CASE
			x_ = new File[10];
			part_ = new nice_multi[10];
			t_ = new Thread[10];
			for(int i = 0; i < 10; i ++) {
				x_[i] = new File(userDirString+"/x_0" + (i));
				part_[i] = new nice_multi(x_[i], userDir_[i].getPath(), String.valueOf(i+1));
				t_[i] = new Thread(part_[i]);
			}			
			try {
				String N_num = "";
				for(int i = 0; i < 10; i++) {
					t_[i].start();
				}
				for(int i = 0; i < 10; i++) {
					t_[i].join();
				}
				//combine n Nice.txt
				for(int i = 0; i < 10; i++) {
					N_num = N_num + userDirString+"/"+String.valueOf(i+1)+"/NICE.txt ";
				}
				String[] command_s = {
						"/bin/sh",
						"-c",
						"cat " + N_num + ">" + userDirString + "/NICE.txt"
				};
	
				N_combine = Runtime.getRuntime().exec(command_s);
				N_combine.waitFor();
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		end_t = System.currentTimeMillis();
		System.out.println("work time is " + (end_t - start_t)/1000.0);
		//send e_mail
			File f_check = new File(userDirString + "/NICE.txt"); 
			try {
				java.util.Properties properties = System.getProperties();
				properties.put("mail.smtp.starttls.enable", "true");
				properties.put("mail.smtp.host", "smtp.gmail.com");
				properties.put("mail.smtp.auth", "true");
				properties.put("mail.smtp.port", "587");
				
				Authenticator auth = new GoogleAuthentication();
				Session s = Session.getDefaultInstance(properties, auth);
				Message msg = new MimeMessage(s);
				Address sender_address = new InternetAddress("taegun89@gmail.com");
				Address receiver_address = new InternetAddress(emailAddress);
		
				msg.setHeader("content-type", "text/html;charset=euc-kr");
				
				msg.setFrom(sender_address);
				msg.addRecipient(Message.RecipientType.TO, receiver_address);
				if(true) {
					msg.setSubject("Nice complete");
					msg.setContent("<a href = \"http://210.94.194.52:8080/download/"+emailAddress+"/" + strDate+"/NICE.txt"+"\">download_link</a>" +"<br>Working time is " +(end_t - start_t)/1000.0 + "Second.", "text/html; charset=euc-kr");
				
				}
				else {
					msg.setSubject("Nice fail");
					msg.setContent("send email to us please", "text/html; charset=euc-kr");
							
				}
					  // messageBodyPart.setText("test");
						  
				
				Transport.send(msg);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	
	private void Make_Nice(File file, String f_path, String f_num) {
		Process a_0 = null; Process a_1 = null;Process a_2 = null;Process a_3 = null;Process a_4 = null;Process a_5 = null;Process a_6 = null;
		Process a_0_1 = null; Process a_0_2 = null;
		String num1 = ""; String num2 = ""; String num3 = ""; String num4 = "";
		
		try {
			if(a_0 == null) {
				File rightdim = new File(f_path + "/test.R");
				FileWriter fw = new FileWriter(rightdim, true);
				fw.write("X = as.matrix(read.table(\"" + file.getPath() + "\"))\n" + 
						"write.table(t(X), \"" + f_path +"/X_rightdim.txt\"," + "row.names = F, col.names = F, quote = F)\n");
				a_0 = Runtime.getRuntime().exec("R CMD BATCH " + rightdim.getPath());
				
				fw.flush();
				fw.close();
				 
			    a_0.waitFor();			
			}
			if(a_0_1 == null ) { //line_number.txt make
		
				//for pipe cmd, use "/bin/sh", "-c"
				String[] command = {
						"/bin/sh",
						"-c",
						"wc -l " + file.getPath() + " " + userDirString + "/Y.txt " + userDirString + "/Y_rightdim.txt >> " + f_path + "/line_num.txt"
				};
				a_0_1 = Runtime.getRuntime().exec(command);
				
			    a_0_1.waitFor();
			}
			if(a_0_2 == null ) {
				
				try {
				File read_file = new File(f_path+"/line_num.txt");
				FileReader f_r = new FileReader(read_file);
				BufferedReader b_r = new BufferedReader(f_r);
				num1 = b_r.readLine().replace(file.getPath(), "");
				num2 = b_r.readLine().replace(userDirString+"/Y.txt", "");
				num3 = b_r.readLine().replace(userDirString+"/Y_rightdim.txt", "");
					
				b_r.close();
				f_r.close();
				
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
				a_0_2 = Runtime.getRuntime().exec(Setup.NICEDirectory+"t_test_static "
													+ num2 + num1 + num3 + userDirString + "/Y.txt " + file.getPath() + " "   
												     + f_path + "/p_ttest.txt");
										 
			    a_0_2.waitFor();
			   
			}

			if(a_1 == null){
				a_1 = Runtime.getRuntime().exec("R CMD BATCH --args -snp=" + f_path + "/X_rightdim.txt -pheno="+userDirString +"/Y_rightdim.txt -out=" + f_path +" -- /home/ktg/NICE/inputMS.R "
													+ f_path + "/inputMS.Rout");
			    a_1.waitFor();			
			}
			if(a_2 == null){	
				//a_2 = Runtime.getRuntime().exec("java -Xmx2048m -jar /home/ktg/NICE/Metasoft.jar -input "+ f_path + "/inputMS.txt -mvalue -mvalue_method mcmc -mcmc_sample 10000 -seed 0 -mvalue_p_thres 1.0 -mvalue_prior_sigma 0.05 -mvalue_prior_beta 1 5 -pvalue_table /home/ktg/NICE/HanEskinPvalueTable.txt -output "+ f_path +"/posterior.txt");
	
			    //a_2.waitFor();
				File metasoft = new File(f_path + "/test2.sh");
				FileWriter fw = new FileWriter(metasoft, true);
				fw.write("java -Xmx2048m -jar "+Setup.NICEDirectory+"/Metasoft.jar -input "+ f_path + "/inputMS.txt -mvalue -mvalue_method mcmc -mcmc_sample 1000000 -seed 0 -mvalue_p_thres 1.0 -mvalue_prior_sigma 0.05 -mvalue_prior_beta 1 5 -pvalue_table "+Setup.NICEDirectory+"HanEskinPvalueTable.txt -output "+ f_path +"/posterior.txt");
				
				fw.flush();
				fw.close();
	
				String[] command_s = {
						"/bin/sh",
						"-c",
						"sh " + metasoft.getPath() + " &> " + f_path + "/java_log.txt"
				};
				
				a_2 = Runtime.getRuntime().exec(command_s);
				
				a_2.waitFor();	
		    }
			synchronized(this) {
				if(a_3 == null)
				{	
					a_3 = Runtime.getRuntime().exec("R CMD BATCH --args -snp="+ f_path +"/X_rightdim.txt -pheno="+userDirString+"/Y_rightdim.txt -MvalueThreshold=0.5 -Mvalue="+f_path+"/posterior.txt -minGeneNumber=10 -Pdefault="+ f_path +"/p_ttest.txt -out="+f_path+"/"+ " -st_snp_num="+ num4 + " -NICE=./ -- "+Setup.NICEDirectory+"/NICE.R" + " " + f_path + "/NICE.Rout");		 
					File n_check = new File(f_path + "/NICE.txt");
					if(!n_check.exists()){operation_check = false;}
					a_3.waitFor();	
				}
			}
				
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	/*private void MakeNICEOptionFile(String dir){
	      String ProgramDir = "-java = /bin/java #System path\r\n" + 
	            "-R = /bin/R\r\n" + 
	            "-NICE = ./home/ktg/NICE\r\n\r\n";
	      String ProgramSetting = "-mvalue_method mcmc  # mvalue options\r\n" + 
	            "-mcmc_sample 10000\r\n" + 
	            "-seed 0\r\n" + 
	            "-mvalue_p_thres 1.0\r\n" + 
	            "-mvalue_prior_sigma 0.05\r\n" + 
	            "-mvalue_prior_beta 1 5\r\n";
	      try {
	         String OutputDir = "-snp = "+dir+"/X_rightdim.txt \r\n" + 
	               "-pheno = "+dir+"/Y_rightdim.txt\r\n" + 
	               "-pvalue = "+dir+"/p_ttest.txt\r\n" + 
	               "-out = "+dir + "\r\n\r\n";
	         BufferedWriter bw = new BufferedWriter(new FileWriter(dir+"/NICE_option.txt"));
	         bw.write(ProgramDir);
	         bw.write(OutputDir);
	         bw.write(ProgramSetting);
	         bw.close();
	         
	      }catch(Exception e) {
	         e.printStackTrace();
	      }
	   }*/
	class nice_multi implements Runnable{
		public File f;
		public String f_path;
		public String f_num;
		public nice_multi(File a, String c, String d) {
			f = a;  f_path = c; f_num = d;
		}
		public void run() {
				Make_Nice(f, f_path, f_num);
		
			
		}
		
	}
	
}
