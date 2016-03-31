package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import Following.Follow;




import com.google.gson.Gson;

/**
 * Servlet implementation class ShowFollowTable
 */
@SuppressWarnings("deprecation")
public class ShowFollowTable extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowFollowTable() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request
	 * @param response all the pairs of following users
	 * @return all the pairs of following users is json
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try{
			Context context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
			Connection conn = ds.getConnection();
		
			Collection<Follow> FollowResult = new ArrayList<Follow>();
			
			Statement stmt;
			try {
				//get all the following pairs in the database.
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(AppConstants.SELECT_ALL_FOLLOWING);
				
				//insert the following pairs to collection
				while (rs.next()){
					FollowResult.add(new Follow(rs.getString(1),rs.getString(2)));
				}
				
				conn.close();
				
			}catch (SQLException e) {
						getServletContext().log("Error while querying for customers", e);
			    		response.sendError(500);//internal server error
					}
		     conn.close();
			Gson gson = new Gson();
			
        	//convert from collection to json
        	String FollowJsonResult = gson.toJson(FollowResult, AppConstants.FOLLOW_COLLECTION);

        	PrintWriter writer = response.getWriter();
        	writer.println(FollowJsonResult);
        	writer.close();
        	
		}catch (SQLException | NamingException e) {
    		getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
    	}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request
	 * @param response all the pairs of following users
	 * @return all the pairs of following users is json
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
