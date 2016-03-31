package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import Users.User;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetFollowUsersServlet
 */
@SuppressWarnings("deprecation")
public class GetFollowUsersServlet extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFollowUsersServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 *  @param request name of user
	 * @param response users that the user is following
	 * @return json arraylist includes the followers with their information
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
	    	
        	//obtain CustomerDB data source from Tomcat's context
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
    		Connection conn = ds.getConnection();

    		Collection<User> TenFollowing = new ArrayList<User>(); 

    		
    		String uri = request.getRequestURI();
    		if (uri.indexOf(AppConstants.USERNAME) != -1){
    			String username = uri.substring(uri.indexOf(AppConstants.USERNAME) + AppConstants.USERNAME.length() + 1);
    			username = username.replaceAll("\\%20", " ");
    			
    			try {
    				//get the nickname of the username
    				PreparedStatement stmt = conn.prepareStatement(AppConstants.SELECT_USERS_BY_NAME_STMT);
    				stmt.setString(1,username);
    				ResultSet rs = stmt.executeQuery();
    				rs.next();
    				String nickname = rs.getString(3);
    				
    				//get the users that username is following ordered by users popularity	
    				stmt = conn.prepareStatement(AppConstants.GET_FOLLOWING);
        				
        			stmt.setString(1, nickname);
        			rs = stmt.executeQuery();
        			int followingCounter = 0;
        			//insert into collection the top 10 users that username is following orderd by user popularity.
        			while (rs.next() && followingCounter < 10){
        				TenFollowing.add(new User(rs.getString(1) , rs.getString(2) , rs.getString(3) , rs.getString(4) , 
        						rs.getString(5) , rs.getDouble(6) , rs.getInt(7) ,rs.getInt(8)));
        				followingCounter++;
        			}
        			rs.close();
        			stmt.close();
    				
    			
    				
    			} catch (SQLException e) {
    				getServletContext().log("Error while querying for customers", e);
    	    		response.sendError(500);//internal server error
    			}
    		}

    		conn.close();
    		//convert from collection to json
    		Gson gson = new Gson();
        	
        	String FollowJsonResult = gson.toJson(TenFollowing, AppConstants.USER_COLLECTION);

        	PrintWriter writer = response.getWriter();
        	writer.println(FollowJsonResult);
        	writer.close();
    	} catch (SQLException | NamingException e) {
    		getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
    	}
	
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 *  @param request name of user
	 * @param response users that the user is following
	 * @return json arraylist includes the followers with their information
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
