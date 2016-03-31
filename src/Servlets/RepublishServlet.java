package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.google.gson.Gson;

import First.AppConstants;
import Messages.ConvertDate;
import Messages.Message;


/**
 * Servlet implementation class RepublishServlet
 */
@SuppressWarnings("deprecation")
public class RepublishServlet extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RepublishServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request - the information of the republished message
	 * @param response - 
	 * @return response - the last 10 message of the user
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
	    	
        	//obtain CustomerDB data source from Tomcat's context
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
    		Connection conn = ds.getConnection();
    		 
    		ArrayList<Message> ReMsg = new ArrayList<Message>();
    		//ArrayList<Message> Msgs = new ArrayList<Message>();
    		//get the message Id from the url
    		String uri = request.getRequestURI();
    		if (uri.indexOf(AppConstants.MSGID) != -1){
    			String msgid = uri.substring(uri.indexOf(AppConstants.MSGID) + AppConstants.MSGID.length() + 1);
    			
    			try {
    				
    				//Get message text and add RE: to the begining
    				PreparedStatement stmt = conn.prepareStatement(AppConstants.GET_MSG_BY_ID);
    				int IntMsgID = Integer.parseInt(msgid);
    				stmt.setInt(1,IntMsgID);
    				ResultSet rs = stmt.executeQuery();
    				rs.next();
 
    				String msgtext = "RE: " + rs.getString(2);
    				
    				
    				//update republished column of the message the had been republished.
    				PreparedStatement stmt2 = conn.prepareStatement(AppConstants.UPDATE_REPUBLISHED);
    				msgid = msgid.replaceAll("\\%20", " ");
    				stmt2.setInt(1,IntMsgID);
    			    stmt2.executeUpdate();
    				
    				// add msg to the table with this username
    				HttpSession session = request.getSession(false);
    				String ThisNickname = session.getAttribute("nickname").toString();
    				String ThisName = session.getAttribute("username").toString();
    				
    				Date d = new Date();
    				SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    				PreparedStatement pstmt = conn.prepareStatement(AppConstants.INSERT_MESSAGE_STMT);
    				
    				String ReDate = date.format(d);
    				pstmt.setInt(1,Message.MsgCounter);
    				pstmt.setString(2,msgtext);
    				pstmt.setString(3,ThisNickname);
    				pstmt.setString(4, ReDate);
    				pstmt.setInt(5,0);
    				
    				pstmt.executeUpdate();
    				
    				
    				//check if the message includes # then add it to the topic table
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
    				
    				//String timeToDisplay = ConvertDate.GetDuration(ReDate);
    				
    				pstmt = conn.prepareStatement(AppConstants.SELECT_MSGS_BY_USER);
    				pstmt.setString(1,ThisName);
    				rs = pstmt.executeQuery();
    				
    				int counter = 0;
    				while(rs.next() && counter < 10){
    					String timeToDisplay = ConvertDate.GetDuration(rs.getString(4));
    					ReMsg.add(new Message(Message.MsgCounter,msgtext,ThisNickname,timeToDisplay,0));
    					counter++;
    				}
    				
    				//ReMsg.add(new Message(Message.MsgCounter,msgtext,ThisNickname,timeToDisplay,0));
    				
    				Message.MsgCounter++;
    				
    				conn.commit();
    				pstmt.close();
    				
    				rs.close();
    				stmt.close();
    				
    				Gson gson = new Gson();
    	        	//convert from messages collection to json
    	        	String MsgsJsonResult = gson.toJson(ReMsg, AppConstants.MSGS_COLLECTION);
    	        	
    	        	PrintWriter writer = response.getWriter();
    	        	writer.println(MsgsJsonResult);
    	        	writer.close();
    	        	
    				
    				
    			} catch (SQLException e) {
    				getServletContext().log("Error while querying for customers", e);
    	    		response.sendError(500);//internal server error
    			} catch (ParseException e) {
					e.printStackTrace();
				}
    		}

    		conn.close();
    		
    	} catch (SQLException | NamingException e) {
    		getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
    	}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request - the information of the republished message
	 * @param response - 
	 * @return response - the last 10 message of the user
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
