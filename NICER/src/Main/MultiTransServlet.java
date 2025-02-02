package Main;
//have to do email test .. date 2019 1 18
import java.io.*;
import java.util.Enumeration;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;

import hihi.GoogleAuthentication;
/**
 * Servlet implementation class MultiTransServlet
 */

@WebServlet("/MultiTransServlet")
@MultipartConfig
public class MultiTransServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private long end_t = 0;
    private long start_t = 0;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultiTransServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher view = request.getRequestDispatcher("MultiTrans.jsp");
		view.forward(request, response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

//		Enumeration params = request.getParameterNames();
//		System.out.println("----------------------------");
//		while (params.hasMoreElements()){
//		    String name = (String)params.nextElement();
//		    System.out.println(name + " : " +request.getParameter(name));
//		}
//		System.out.println("----------------------------");
		
//		String input_type_str = request.getParameter("tabss");
		

		String emailAddress = request.getParameter("emailAddress");
		String snp_num = request.getParameter("NumSNPs");
		String window_size = request.getParameter("windowSize");
		String s_num = request.getParameter("s_num");

				
		start_t = System.currentTimeMillis();	// for calculate working time

		MultiTrans multitrans = new MultiTrans(emailAddress,request);
		multitrans.downloadInputData(request);
		
		String someMessage = "Upload Complete!! Please Wait for Email Notification\n";
		PrintWriter out = response.getWriter();
		out.print("<html><head>");
		out.print("<script type=\"text/javascript\">alert(" + someMessage + ");</script>");
		out.print("</head><body></body></html>");
		//Feed Client the main page 
		RequestDispatcher view = request.getRequestDispatcher("index.html");
		view.forward(request, response);
		System.out.println("email : "+emailAddress + " snp_num : "+ snp_num + "window_size : "+window_size+"s_num " + s_num);

		/** Running MultiTrans **/
		multitrans.run(snp_num, window_size, s_num);
		end_t = System.currentTimeMillis();
		sendResultMail(multitrans.email_dir,"MultiTrans.output", emailAddress, end_t-start_t);
	}
	public static void sendResultMail(String file_dir,String file_name, String emailaddr, long end_start) {
		//send e_mail
			try {
				java.util.Properties prop = System.getProperties();
				prop.put("mail.smtp.starttls.enable", "true");
				prop.put("mail.smtp.host", "smtp.gmail.com");
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.port", "587");
				
				Authenticator auth = new GoogleAuthentication();
				Session s;
				try {
					s = Session.getDefaultInstance(prop, auth);
				}catch(Exception e) {
					s = Session.getInstance(prop,auth);
				}
				
				Message msg = new MimeMessage(s);
				Address sender_address = new InternetAddress("wd32777@gmail.com");
				Address receiver_address = new InternetAddress(emailaddr);
		
				msg.setHeader("content-type", "text/html;charset=euc-kr");
				msg.setFrom(sender_address);
				msg.addRecipient(Message.RecipientType.TO, receiver_address);
				File res = new File(file_dir+file_name);
				String _tmp = file_dir.replace("/", Setup.urlencode);
				if(res.exists()) {
					msg.setSubject(file_name+" complete");
					msg.setContent("<a href = \"http://"+Setup.ipAddr+"/NICER/Download?file="+_tmp+file_name+"\">download_link</a>" 
							+"<br>Working time is " +(end_start)/1000.0 + "Second.</br>", "text/html; charset=euc-kr");
				
				}
				else {
					msg.setSubject("MultiTrans fail");
					msg.setContent("Please send us an e-mail for further explanations.", "text/html; charset=euc-kr");
							
				}		  
				
				Transport.send(msg);
				prop.clear();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
	}
}
