package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class NICE_METHOD extends HttpServlet{
	/**
	 * Make Folder for each Email
	 * @param userDir
	 * @param userDirString
	 * @param strDate
	 */
	public static void makeEmailDir(File userDir, String userDirString, String strDate) {
		if(userDir.mkdir()) {			//this part need Optimize
			Process f_chm = null;
			try {
				if(f_chm == null && Setup.isLinux) {
					f_chm = Runtime.getRuntime().exec("chmod 777 "+ userDirString);
					
					f_chm.waitFor();
				}						
			}
			catch(Exception e) {
				e.printStackTrace();
			} 
			userDirString += strDate; 
			userDir = new File(userDirString);
			userDir.mkdir();
		}
		else{
			Process f_chm = null;
			try {
				if(f_chm == null&& Setup.isLinux) {
					f_chm = Runtime.getRuntime().exec("chmod 777 "+ userDirString);
					
					f_chm.waitFor();
				}						
			}
			catch(Exception e) {
				e.printStackTrace();
			} 

			userDirString += strDate;
			userDir = new File(userDirString);
			userDir.mkdir();
		}
		Process f_chm1 = null;
		try {
			if(f_chm1 == null&& Setup.isLinux) {
				f_chm1 = Runtime.getRuntime().exec("chmod 777 "+ userDirString);
				
				f_chm1.waitFor();
			}						
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Manufacture Input Data Type for NICE
	 * @param input_type
	 * @param request
	 * @param userDirString
	 * @param file
	 * @param file1
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void inputDataType(String input_type, HttpServletRequest request,String userDirString, File file, File file1) throws ServletException, IOException {
		if(input_type.equals("type1")) {   //BIM , BED, FAM
			try {
				Part part_00 = request.getPart("BIM_file");
				Part part_01 = request.getPart("BED_file");
				Part part_02 = request.getPart("FAM_file");
				Part part_03 = request.getPart("EX_file");
				
				File file_0 = new File(userDirString+"/input_f.bim");
				File file_1 = new File(userDirString+"/input_f.bed");
				File file_2 = new File(userDirString+"/input_f.fam");
				File file_3 = new File(userDirString+"/input_y.txt");
				
				try (InputStream inputStream= part_00.getInputStream()) { // save uploaded file
					Files.copy(inputStream, file_0.toPath());
				}
				try (InputStream inputStream= part_01.getInputStream()) { // save uploaded file
					Files.copy(inputStream, file_1.toPath());
				}	
				try (InputStream inputStream= part_02.getInputStream()) { // save uploaded file
					Files.copy(inputStream, file_2.toPath());
				}
				try (InputStream inputStream= part_03.getInputStream()) { // save uploaded file
					Files.copy(inputStream, file_3.toPath());
				}	
				Process plink_data = null;
				plink_data = Runtime.getRuntime().exec(Setup.PlinkDirectory+" --bfile " + userDirString + "/input_f --recodeA --noweb --maf 0.3 --out "+userDirString+"/input_x");
				plink_data.waitFor();
				
				Process pl_to_input = null; //x data
				pl_to_input = Runtime.getRuntime().exec(Setup.NICEDirectory+"pl_to_input " + userDirString);
				pl_to_input.waitFor();
				
				Process pl_to_input_y = null; // y data
				pl_to_input_y = Runtime.getRuntime().exec(Setup.NICEDirectory+"pl_to_input_y " + userDirString);
				pl_to_input_y.waitFor();
				
				// X_rightdim -> X.txt Y_rightdim -> Y.txt
				Process xr_to_x = null;
				File XR_to_X = new File(userDirString + "/test.R");
				FileWriter fw = new FileWriter(XR_to_X, true);
				fw.write("X = as.matrix(read.table(\"" + userDirString+"/X_rightdim.txt" + "\"))\n" + 
						"write.table(t(X), \"" + userDirString +"/X.txt\"," + "row.names = F, col.names = F, quote = F)\n" +
						"Y = as.matrix(read.table(\"" + userDirString+"/Y_rightdim.txt" + "\"))\n" + 
						"write.table(t(Y), \"" + userDirString +"/Y.txt\"," + "row.names = F, col.names = F, quote = F)\n");
				System.out.println(XR_to_X.getPath());
				xr_to_x = Runtime.getRuntime().exec("R CMD BATCH " + XR_to_X.getPath());
				fw.flush();
				fw.close();
				 
				xr_to_x.waitFor();			
			
			}
			catch(IOException | InterruptedException e) {
				//
			}
		}
		else if (input_type.equals("type2")){ // input x, y
		// Upload SNP file	
			Part part = request.getPart("SNPfile");
			file = new File(userDirString+"/X.txt");
			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file.toPath());
			}		
		// Upload Phenotype file
			Part part1 = request.getPart("Phenotypefile");
			file1 = new File(userDirString+"/Y.txt");
			try (InputStream inputStream= part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file1.toPath());
			}
		}
		else if (input_type.equals("type3")){ // input x_rightdim, y_rightdim
		// Upload SNP file	
			Part part = request.getPart("SNPfile2");
			file = new File(userDirString+"/X_rightdim.txt");
			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file.toPath());
			}		
		// Upload Phenotype file
			Part part1 = request.getPart("Phenotypefile2");
			file1 = new File(userDirString+"/Y_rightdim.txt");
			try (InputStream inputStream= part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file1.toPath());
			}
			
			try {
				Process type_3_dir = null;
				File XR_to_X = new File(userDirString + "/test.R");
				FileWriter fw = new FileWriter(XR_to_X, true);
				fw.write("X = as.matrix(read.table(\"" + userDirString+"/X_rightdim.txt" + "\"))\n" + 
						"write.table(t(X), \"" + userDirString +"/X.txt\"," + "row.names = F, col.names = F, quote = F)\n" +
						"Y = as.matrix(read.table(\"" + userDirString+"/Y_rightdim.txt" + "\"))\n" + 
						"write.table(t(Y), \"" + userDirString +"/Y.txt\"," + "row.names = F, col.names = F, quote = F)\n");
				System.out.println(XR_to_X.getPath());
				type_3_dir = Runtime.getRuntime().exec("R CMD BATCH " + XR_to_X.getPath());
				
				System.out.println("here it is");
				
				fw.flush();
				fw.close();
				 
				type_3_dir.waitFor();			
			}
			catch(IOException | InterruptedException e) {
				//
			}
		}// end of if
	}
	/**
	 * Create Folder for each Thread
	 * @param set_num
	 * @param userDir
	 * @return
	 */
	public static int createThreadFolder(String set_num, File userDir) {
		int thread_num;
		// need input_thread number by users
		//file separate //10part later
		File userDir_[];
		if(set_num.equals("5set")) {
			thread_num = 5;
			userDir_ = new File[thread_num];
			for(int i = 0; i < thread_num; i++) {
				userDir_[i] = new File(userDir.getPath()+"/" + String.valueOf(i+1));
				userDir_[i].mkdirs();
				Process f_chm = null;
				try {
					if(f_chm == null && Setup.isLinux) {
						f_chm = Runtime.getRuntime().exec("chmod 777 "+ userDir_[i]);
						
						f_chm.waitFor();
					}						
				}
				catch(Exception e) {
					e.printStackTrace();
				}     
			}// end of for
		}
		else {
			thread_num = 10;
			userDir_ = new File[thread_num];
			for(int i = 0; i < thread_num; i++) {
				userDir_[i] = new File(userDir.getPath()+"/" + String.valueOf(i+1));
				userDir_[i].mkdirs();
				Process f_chm = null;
				try {
					if(f_chm == null&& Setup.isLinux) {
						f_chm = Runtime.getRuntime().exec("chmod 777 "+ userDir_[i]);
						
						f_chm.waitFor();
					}						
				}
				catch(Exception e) {
					e.printStackTrace();
				}  
			}// end of for
		}
		return thread_num;
	}
	
	public static void divideX(String userDirString, String set_num, int thread_num, String input_type, File file1) {
		// Get X Line Number
		Process y_r;
		int lines = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(userDirString+"/X.txt"));
			while (br.readLine() != null) lines++;
			br.close();	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int line_x=0;
		if(set_num.equals("5set")) {
			line_x = (lines / thread_num) + 1; // if 10 part, divide 
		}else {
			line_x = (lines / 10) + 1; // if 10 part, divide 	
		}
		try {
			FileReader fr= new FileReader(userDirString+"/X.txt");
			BufferedReader br = new BufferedReader(fr);
			String str="";
			for(int i=0;i<thread_num-1;i++) {
				FileWriter fw;
				if(i<10) 
					fw = new FileWriter(userDirString+"/x_0"+i);
				else
					fw = new FileWriter(userDirString+"/x_"+i);
				BufferedWriter bw = new BufferedWriter(fw);
				for(int j=0;j<line_x;j++) {
					str = br.readLine();
					if(str == null){
						break;
					}
					bw.write(str+"\n");
				}
				bw.close(); fw.close();
			}
			br.close(); fr.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		// For Input Type 2
		try {
			if(input_type.equals("type2")) {
				File rightdim_y = new File(userDirString + "/test.R");
				FileWriter fw_y = new FileWriter(rightdim_y, true);
				fw_y.write("Y = as.matrix(read.table(\"" + file1.getPath() + "\"))\n" + 
						"write.table(t(Y), \"" + userDirString +"/Y_rightdim.txt\"," + "row.names = F, col.names = F, quote = F)\n");
				System.out.println(rightdim_y.getPath());
			
				fw_y.flush();
				fw_y.close();
				y_r = Runtime.getRuntime().exec("R CMD BATCH " + rightdim_y.getPath());		
				y_r.waitFor();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


}
