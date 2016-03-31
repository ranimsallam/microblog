package Servlets;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;





import First.AppConstants;
import Messages.Message;

/**
 * Servlet implementation class PostMsgServlet
 */
@SuppressWarnings("deprecation")
public class PostMsgServlet extends HttpServlet implements SingleThreadModel {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostMsgServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request new meesage text
	 * @param response 
	 * @return 
	 */
    //servlet to post new messag.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Context context;
		try {
			context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
			Connection conn = ds.getConnection();
			
			
			
			//if the message posted from discover page or profile page or it is reply message
			String msgtext = request.getParameter("msgText");
			if(request.getParameter("msgText") != null){
				msgtext = request.getParameter("msgText");
			}else if(request.getParameter("msgTxtFromDisc") != null){
				msgtext = request.getParameter("msgTxtFromDisc");
			}else{
				msgtext = request.getParameter("replymsg");
			}

			
			PreparedStatement pstmt = conn.prepareStatement(AppConstants.INSERT_MESSAGE_STMT);
			PreparedStatement pstmt2 = conn.prepareStatement(AppConstants.SELECT_USERS_BY_NICKNAME_STMT);
			
			
			//get from the session the nickname of the user that posted the message 
			HttpSession session = request.getSession(false);
			String thisNickname = (String) session.getAttribute("nickname");
					
			pstmt2.setString(1,thisNickname);
			ResultSet rs = pstmt2.executeQuery();
			
			rs.next();
			
			Date d = new Date();
			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			//insert the message with its information to MESSAGES Table.
			pstmt.setInt(1,Message.MsgCounter);
			
			if(request.getParameter("msgText") != null){
				pstmt.setString(2,request.getParameter("msgText"));
			}else if(request.getParameter("msgTxtFromDisc") != null){
				pstmt.setString(2,request.getParameter("msgTxtFromDisc"));
			}else{
				pstmt.setString(2,request.getParameter("replymsg"));
			}
			
			pstmt.setString(3,thisNickname);
			pstmt.setString(4, date.format(d));
			pstmt.setInt(5,0);
			
			pstmt.executeUpdate();
			
			
			//check if there is # and insert the message to TOPIC table with the word after #
			int index = msgtext.indexOf('#'); 
			if(index != -1){
				
				String topicSave[];
				String[] txtSplit = msgtext.split("#");
				
				for(int j=1; j < txtSplit.length; j++){
					topicSave = txtSplit[j].split("\\s");
					pstmt = conn.prepareStatement(AppConstants.INSERT_INTO_TOPICS);
					pstmt.setString(1,topicSave[0]);
					pstmt.setInt(2,Message.MsgCounter);
					pstmt.executeUpdate();
				}	
			}
			
			//commit update
			conn.commit();
	
			//close statements
			pstmt.close();
			conn.close();
			
			Message.MsgCounter++;
			
			
		} catch (SQLException | NamingException e) {
			getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
		}
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response
	 * @param request new meesage text
	 * @param response 
	 * @return 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
