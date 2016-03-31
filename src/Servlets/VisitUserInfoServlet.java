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
 * Servlet implementation class VisitUserInfoServlet
 */
@SuppressWarnings("deprecation")
public class VisitUserInfoServlet extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VisitUserInfoServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request username to visit
	 * @param response all information about the user
	 * @return	all information about the user in json
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
try {
	    	
        	
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
    		Connection conn = ds.getConnection();

    		Collection<User> UserToVisitObject = new ArrayList<User>(); 

    		//get username from uri
    		String uri = request.getRequestURI();
    		if (uri.indexOf(AppConstants.USERNAME) != -1){
    			String usertoVisist = uri.substring(uri.indexOf(AppConstants.USERNAME) + AppConstants.USERNAME.length() + 1);
    			usertoVisist = usertoVisist.replaceAll("\\%20", " ");
    			
    			try {
	
    					PreparedStatement stmt = conn.prepareStatement(AppConstants.SELECT_USERS_BY_NAME_STMT);
        				
        				stmt.setString(1, usertoVisist);
        				ResultSet rs = stmt.executeQuery();
        				while (rs.next()){
        					UserToVisitObject.add(new User(rs.getString(1) , rs.getString(2) , rs.getString(3) , rs.getString(4) , 
        							rs.getString(5) , rs.getDouble(6) , rs.getInt(7) ,rs.getInt(8)));
        				}
        				
        				rs.close();
        				stmt.close();
        					
    				
    			} catch (SQLException e) {
    				getServletContext().log("Error while querying for customers", e);
    	    		response.sendError(500);//internal server error
    			}
    		}

    		conn.close();
    		
    		Gson gson = new Gson();
        	String FollowJsonResult = gson.toJson(UserToVisitObject, AppConstants.USER_COLLECTION);

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
	 * @param request username to visit
	 * @param response all information about the user
	 * @return	all information about the user in json
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
