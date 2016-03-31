package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import First.AppConstants;
import Messages.ConvertDate;
import Messages.Message;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetTopicMsgsServlet
 */
@SuppressWarnings("deprecation")
public class GetTopicMsgsServlet extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTopicMsgsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request topic name
	 * @param response all messages that include the topic name
	 * @return json arraylist of last 10 the messages that include the topic name
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
try {
	    	
        	//obtain CustomerDB data source from Tomcat's context
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
    		Connection conn = ds.getConnection();

    		Collection<Message> LastTenTopics = new ArrayList<Message>(); 
    		
    		
    		//get the topic name from the url
    		String uri = request.getRequestURI();
    		if (uri.indexOf(AppConstants.TOPIC) != -1){
    			String topic = uri.substring(uri.indexOf(AppConstants.TOPIC) + AppConstants.TOPIC.length() + 1);
    			topic = topic.replaceAll("\\%20", " ");
    		
    			
    			
    			try {
    				/*
    				 * select all messages that includes topic name :
    				 *	get all the messages from MESSAGES table that has the same MsgID of the MsgID of TOPICS table
    				 * 	and has TopicName = topic and orderd by MsgID
    				 *	(inner join between MESSAGES Table and TOPICS Table)
    				 */
    				PreparedStatement stmt = conn.prepareStatement(AppConstants.GET_MSGS_FROM_TOPICS);
    				stmt.setString(1,topic);
    				ResultSet rs = stmt.executeQuery();
    				
		
        			
        			/*
        			 * when the user posted a message the MsgID increases by one so we can get the messages ordered by MsgId
        			 * after selection the messages that include the topic , insert all messages to collection.
        			 */
        			while (rs.next() ){
        				String timeToDisplay = ConvertDate.GetDuration(rs.getString(4));
        				LastTenTopics.add(new Message(rs.getInt(1),rs.getString(2),rs.getString(3) , 
        						timeToDisplay, rs.getInt(5)));
        				
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
    		
    		//convert from collection to json.
    		Gson gson = new Gson();
        	String TopicsJsonResult = gson.toJson(LastTenTopics, AppConstants.MSGS_COLLECTION);

        	PrintWriter writer = response.getWriter();
        	writer.println(TopicsJsonResult);
        	writer.close();
    	} catch (SQLException | NamingException e) {
    		getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
    	}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request topic name
	 * @param response all messages that include the topic name
	 * @return json arraylist of all the messages that include the topic name
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
