package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;




import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import Messages.ConvertDate;
import Messages.Message;

import com.google.gson.Gson;

import First.AppConstants;

/**
 * Servlet implementation class GetUserMsgsServlet
 */
@SuppressWarnings("deprecation")
public class GetUserMsgsServlet extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserMsgsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request nickname of user
	 * @param response last 10 messages that the user posted
	 * @return json arraylist of last 10 messages that the user posted
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
    	
        	//obtain CustomerDB data source from Tomcat's context
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
    		Connection conn = ds.getConnection();

    		
    		ArrayList<Message> retMsgs = new ArrayList<Message>();
    		
    		
    		//get the nickname from the url
    		String uri = request.getRequestURI();
    		if (uri.indexOf(AppConstants.NICKNAME) != -1){
    			String nickname = uri.substring(uri.indexOf(AppConstants.NICKNAME) + AppConstants.NICKNAME.length() + 1);
    			PreparedStatement stmt;
    			try {
    				//select all the messages if the user that has nickname = nickname ordered by msgID
    				stmt = conn.prepareStatement(AppConstants.SELECT_MSGS_BY_USER);
    				nickname = nickname.replaceAll("\\%20", " ");
    				stmt.setString(1, nickname);
    				ResultSet rs = stmt.executeQuery();
    				
    				
    				int counter = 0;
    				//get the last 10 messages
    				while (rs.next() && counter < 10){
    					
    					String timeToDisplay = ConvertDate.GetDuration(rs.getString(4));
    					retMsgs.add(new Message(rs.getInt(1),rs.getString(2), rs.getString(3),
    							timeToDisplay , rs.getInt(5)));
    					
    					counter++;
    				}
    				
    				
    				rs.close();
    				stmt.close();
    			} catch (SQLException e) {
    				getServletContext().log("Error while querying for customers", e);
    	    		response.sendError(500);//internal server error
    			} catch (ParseException e) {
					e.printStackTrace();
				}
    		}

    		conn.close();
    		
    		Gson gson = new Gson();
        	//convert from customers collection to json
        	String MsgsJsonResult = gson.toJson(retMsgs, AppConstants.MSGS_COLLECTION);

        	PrintWriter writer = response.getWriter();
        	writer.println(MsgsJsonResult);
        	writer.close();
    	} catch (SQLException | NamingException e) {
    		getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
    	}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request nickname of user
	 * @param response last 10 messages that the user posted
	 * @return json arraylist of last 10 messages that the user posted
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
