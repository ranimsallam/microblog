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
 * Servlet implementation class GetAllMsgsServlet
 */
@SuppressWarnings("deprecation")
public class GetAllMsgsServlet extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllMsgsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request
	 * @param response all messages
	 * @return json arraylist that includes all the messages with its information
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Context context;
		PreparedStatement pstmt = null;
		
		try {
			context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
			Connection conn = ds.getConnection();
			
			Collection<Message> AllMessages = new ArrayList<Message>();
			
				pstmt = conn.prepareStatement(AppConstants.SELECT_ALL_MSGS);

				ResultSet rs = pstmt.executeQuery();
				
			
				
				while (rs.next()){
					String timeToDisplay = ConvertDate.GetDuration(rs.getString(4));
					AllMessages.add(new Message(rs.getInt(1),rs.getString(2),rs.getString(3) , 
							timeToDisplay, rs.getInt(5)));
				}
				rs.close();
			
			
			//commit update
			conn.commit();
			
			
			//close statements
			pstmt.close();
			conn.close();
			
			
			Gson gson = new Gson();
        	//convert from customers collection to json
        	String MsgsJsonResult = gson.toJson(AllMessages, AppConstants.MSGS_COLLECTION);
        	
        	PrintWriter writer = response.getWriter();
        	writer.println(MsgsJsonResult);
        	writer.close();
			
		} catch (SQLException | NamingException e) {
			getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request
	 * @param response all messages
	 * @return json arraylist that includes all the messages with its information
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}	
