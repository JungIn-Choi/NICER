package Main;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import javax.servlet.http.Part;

@MultipartConfig(maxFileSize = -1, maxRequestSize = -1, location = Setup.FileSaveDirectory_Multitrans)
public class MultiTrans {
	private File x_file; // SNP file
	private File y_file; // Pheno file
	private File threshold_file; // Threshold file
	public String email_dir;
	private int snp_num;
	private int window_size;
	private int s_num;
	private Thread thr;
	private int tl_snp_cnt = 0;
	private final int FORCE_THREAD = 0; // Force Thread Number(not to force <= 0)

	MultiTrans(String emailaddr, HttpServletRequest request) {
		// create directory
		email_dir = Setup.FileSaveDirectory_Multitrans + emailaddr + "/";
		File userDir = new File(email_dir);
		if (!userDir.exists() && userDir.mkdir()) { // this part need Optimize
			Process f_chm = null;
			try {
				if (f_chm == null) {
					f_chm = Runtime.getRuntime().exec("chmod 777 " + email_dir);

					f_chm.waitFor();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		email_dir += getCurrentStrDate() + "/";// append date within email address
//		email_dir += "2019-04-07_13-17-53/";
		userDir = new File(email_dir);
		userDir.mkdir();
	}

	public void run(String snp_num, String window_size, String s_num) {
		createThreadDir();
		createNrunThread(snp_num, window_size, s_num);
		waitThread();

		combineResults();

		return; // end of running MultiTrans
	}

	private void createThreadDir() {

		File userDir = new File(email_dir);
		userDir.mkdirs();
		Process f_chm = null;
		try {
			if (f_chm == null) {
				f_chm = Runtime.getRuntime().exec("chmod 777 " + userDir);
				f_chm.waitFor();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void combineResults() {
		try {
			FileWriter fw = new FileWriter(email_dir + "/MultiTrans.output");
			BufferedWriter bw = new BufferedWriter(fw);
			FileReader fr = new FileReader(email_dir + "/MultiTrans.output");
			
			BufferedReader br = new BufferedReader(fr);
			String tmp = br.readLine();
			while (tmp != null) {
				bw.write(tmp + "\n");
				tmp = br.readLine();
			}
			br.close();
			fr.close();

			bw.close();
			fw.close();
		} catch (Exception e) {
			printERROR("Error while combining MultiTrans.txt...!!");
			e.printStackTrace();
		}
	}

	private void waitThread() {
		try {
			thr.join();
		} catch (InterruptedException e) {
			printERROR("Error while joining thread!!");
			e.printStackTrace();
		}

	}

	private void createNrunThread(String snp_num, String window_size, String s_num) {
		thr = new Thread(new MultiTrans_Runnable(email_dir, snp_num, window_size, s_num));
		thr.start();

	}

	/*
	 * private void divideXfile(int ln_cnt) { try { int ttl_ln =
	 * countXfile(x_file.getAbsolutePath()); FileReader fr = new
	 * FileReader(x_file.getAbsoluteFile()); BufferedReader br = new
	 * BufferedReader(fr); String tmp; int thr_cnt = 1;
	 * 
	 * BufferedWriter bw = new BufferedWriter(new FileWriter(email_dir + "/" +
	 * thr_cnt + "/X.txt")); for (int i = 0; i < ttl_ln; i++) { tmp = br.readLine();
	 * if (i != 0 && i % ln_cnt == 0) { System.out.println(i); bw.close();
	 * thr_cnt++;
	 * 
	 * bw = new BufferedWriter(new FileWriter(email_dir + "/" + thr_cnt +
	 * "/X.txt")); } bw.write(tmp + "\n"); } bw.close(); // int thr_cnt = 1; //
	 * FileReader fr = new FileReader(x_file.getAbsoluteFile()); // BufferedReader
	 * br = new BufferedReader(fr); // String ln = br.readLine(); // // FileWriter
	 * fw = new FileWriter(email_dir+"/"+thr_cnt+"/"+"X.txt"); // BufferedWriter bw
	 * = new BufferedWriter(fw); // // int cnt =0; // while(ln != null) { // cnt++;
	 * // ln+="\n"; // bw.write(ln.toCharArray()); // ln = br.readLine(); // //
	 * if(cnt == ln_cnt) { // bw.close();fw.close(); // thr_cnt++; cnt = 1; // fw =
	 * new FileWriter(email_dir+"/"+thr_cnt+"/"+"X.txt"); // bw = new
	 * BufferedWriter(fw); // } // } // bw.close(); fw.close(); // br.close();
	 * fr.close(); } catch (Exception e) {
	 * printERROR("Error while dividing X file!!"); e.printStackTrace(); } }
	 */

	/**
	 * Count Number of Lines in X.txt
	 * 
	 * @return
	 */
	public static int countXfile(String str) {
		File x_file = new File(str);
		int ln_cnt = 0;
		try {
			FileReader fr = new FileReader(x_file.getAbsolutePath());
			BufferedReader br = new BufferedReader(fr);
			String tmp = br.readLine();
			while (tmp != null) {
				ln_cnt++;
				tmp = br.readLine();
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			printERROR("Error while reading " + str + " file to count lines!!");
			e.printStackTrace();
		}
		return ln_cnt;
	}

	/**
	 * Create Folder for each Threads
	 * 
	 * @param thr_num_str
	 */

	public void downloadInputData(HttpServletRequest request) {
		try {
			Part part = request.getPart("SNPfile");
			File x = new File(email_dir + "/X.txt");
			try (InputStream inputStream = part.getInputStream()){ // save uploaded file
				Files.copy(inputStream, x.toPath());
			}
			Part part1 = request.getPart("Phenotypefile");
			File y = new File(email_dir + "/Y.txt");
			try (InputStream inputStream = part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, y.toPath());
			}
			Part part2 = request.getPart("Thresholdfile");
			File x_rightdim = new File(email_dir + "/X_rightdim.txt");
			try (InputStream inputStream = part2.getInputStream()) { // save uploaded file
				Files.copy(inputStream, x_rightdim.toPath());
			}
			Part part3 = request.getPart("Thresholdfile");
			File r_file = new File(email_dir + "/r.txt");
			try (InputStream inputStream = part3.getInputStream()) { // save uploaded file
				Files.copy(inputStream, r_file.toPath());
			}
			Part part4 = request.getPart("Sortedfile");
			File sorted_file = new File(email_dir + "/sorted");
			try (InputStream inputStream = part4.getInputStream()) { // save uploaded file
				Files.copy(inputStream, sorted_file.toPath());
			}
		} catch (Exception e) {
			printERROR("Error Occured while uploading transposed XY data!!");
			e.printStackTrace();
		}
	}

	private void downloadVCF(HttpServletRequest request) {
		try {
			Part part = request.getPart("VCFfile");
			File x = new File(email_dir + "/VCF.vcf");
			try (InputStream inputStream = part.getInputStream()) {
				Files.copy(inputStream, x.toPath());
			}
			Part part1 = request.getPart("Expfile");
			File y = new File(email_dir + "/input_y.txt");
			try (InputStream inputStream = part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, y.toPath());
			}

			Process vcf_data = null;
			// --vcf test.vcf --allow-extra-chr --no-fid --no-parents --no-sex --no-pheno
			// --out test
			vcf_data = Runtime.getRuntime()
					.exec(Setup.PLINKdir + " --vcf " + email_dir
							+ "/VCF.vcf --allow-extra-chr --no-fid --no-parents --no-sex --no-pheno --out " + email_dir
							+ "/input_f");
			vcf_data.waitFor();

			Process plink_data = null;
			plink_data = Runtime.getRuntime().exec(Setup.PLINKdir + " --bfile " + email_dir
					+ "/input_f --allow-extra-chr --recodeA --noweb --maf 0.3 --out " + email_dir + "/input_x");
			plink_data.waitFor();

			Process pl_to_input = null; // x data
			pl_to_input = Runtime.getRuntime().exec(Setup.NICEdir + "/pl_to_input " + email_dir);
			pl_to_input.waitFor();

			Process pl_to_input_y = null; // y data
			pl_to_input_y = Runtime.getRuntime().exec(Setup.NICEdir + "pl_to_input_y " + email_dir);
			pl_to_input_y.waitFor();

			// X_rightdim -> X.txt Y_rightdim -> Y.txt
			transposeFile(email_dir + "/X_rightdim.txt", email_dir + "/X.txt");
			transposeFile(email_dir + "/Y_rightdim.txt", email_dir + "/Y.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadTransposedXY(HttpServletRequest request) {
		// Upload SNP file
		try {
			Part part = request.getPart("SNPfile2");
			File x = new File(email_dir + "/X_rightdim.txt");
			try (InputStream inputStream = part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, x.toPath());
			}
			// Upload Phenotype file
			Part part1 = request.getPart("Phenotypefile2");
			File y = new File(email_dir + "/Y_rightdim.txt");
			try (InputStream inputStream = part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, y.toPath());
			}
		} catch (Exception e) {
			printERROR("Error Occured while uploading transposed XY data!!");
			e.printStackTrace();
		}
		transposeFile(email_dir + "X_rightdim.txt", email_dir + "X.txt");
		transposeFile(email_dir + "Y_rightdim.txt", email_dir + "Y.txt");
		x_file = new File(email_dir + "/X.txt");
		y_file = new File(email_dir + "/Y.txt");
		System.out.println(email_dir);
	}

	private void downloadXY(HttpServletRequest request) {
		try {
			// Upload SNP file
			Part part = request.getPart("SNPfile");
			x_file = new File(email_dir + "/X.txt");
			try (InputStream inputStream = part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, x_file.toPath());
			}
			// Upload Phenotype file
			Part part1 = request.getPart("Phenotypefile");
			y_file = new File(email_dir + "/Y.txt");
			try (InputStream inputStream = part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, y_file.toPath());
			}
			transposeFile(x_file.getPath(), email_dir + "/Y_rightdim.txt");
		} catch (Exception e) {
			printERROR("Error Occurred while uploading XY data!!");
			e.printStackTrace();
		}
	}

	private void downloadPlink(HttpServletRequest request) {
		try {
			Part part_00 = request.getPart("SNPfile");
			Part part_01 = request.getPart("Phenotypefile");

			File file_0 = new File(email_dir + "/input_f.snp");
			File file_1 = new File(email_dir + "/input_f.phe");

			try (InputStream inputStream = part_00.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file_0.toPath());
			}
			try (InputStream inputStream = part_01.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file_1.toPath());
			}

			Process plink_data = null;
			plink_data = Runtime.getRuntime().exec(Setup.PLINKdir + " --bfile " + email_dir
					+ "/input_f --recodeA --noweb --maf 0.3 --out " + email_dir + "/input_x");
			plink_data.waitFor();

			Process pl_to_input = null; // x data
			pl_to_input = Runtime.getRuntime().exec(Setup.NICEdir + "/pl_to_input " + email_dir);
			pl_to_input.waitFor();

			Process pl_to_input_y = null; // y data
			pl_to_input_y = Runtime.getRuntime().exec(Setup.NICEdir + "pl_to_input_y " + email_dir);
			pl_to_input_y.waitFor();

			// X_rightdim -> X.txt Y_rightdim -> Y.txt
			transposeFile(email_dir + "/X_rightdim.txt", email_dir + "/X.txt");
			transposeFile(email_dir + "/Y_rightdim.txt", email_dir + "/Y.txt");
		} catch (Exception e) {
			printERROR("Error Occurred while uploading// converting PLINK data!!");
			e.printStackTrace();
		}
	}

	/**
	 * Error Printing Screen
	 * 
	 * @param err_str error message
	 */
	public static void printERROR(String err_msg) {
		System.err.print(err_msg + "\n");
	}

	/**
	 * Get String formatted Current Date
	 * 
	 * @return
	 */
	public String getCurrentStrDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");// dd/MM/yyyy
		Date now = new Date();
		return sdfDate.format(now);
	}

	public static void transposeFile(String inputFile, String outputFile) {
		try {
			Vector<String[]> vec = new Vector<>();

			FileReader fr = new FileReader(inputFile);
			BufferedReader br = new BufferedReader(fr);

			String ln = br.readLine();
			while (ln != null) {
				String[] list = ln.split(" ");
				vec.add(list);
				ln = br.readLine();
			}
			br.close();
			fr.close();

			int row = vec.size();
			int col = vec.get(0).length;
			String mat[][] = new String[row][col];
			for (int i = 0; i < row; i++) {
				String[] tmp = vec.get(i);
				for (int j = 0; j < col; j++) {
					mat[i][j] = tmp[j];
				}
			}
			vec.clear();

			FileWriter fw = new FileWriter(outputFile);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < col; i++) {
				for (int j = 0; j < row; j++) {
					bw.write(mat[j][i] + " ");
				}
				bw.write("\n");
			}
			bw.close();
			fw.close();

		} catch (IOException e) {
			printERROR("Error while transposing " + inputFile + "!!!");
			e.printStackTrace();
		}
	}
}
