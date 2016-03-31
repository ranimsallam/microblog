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
 * Servlet implementation class GetAllUsersServlet
 */
@SuppressWarnings("deprecation")
public class GetAllUsersServlet extends HttpServlet implements SingleThreadModel {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllUsersServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request
	 * @param response returns all users in USERS table
	 * @return 
	 */
    //this servlet return all the users in the database.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Context context;
		PreparedStatement pstmt = null;
		
		try {
			context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
			Connection conn = ds.getConnection();
			
			Collection<User> AllUsers = new ArrayList<User>();
				
				//select all the rows in USERS table.
				pstmt = conn.prepareStatement(AppConstants.SELECT_ALL_USERS_STMT);

				ResultSet rs = pstmt.executeQuery();
				
				//insert into users collection
				while (rs.next()){
					AllUsers.add( new User(rs.getString(1) , rs.getString(2) , rs.getString(3) , rs.getString(4) , 
							rs.getString(5) , rs.getDouble(6), rs.getInt(7) , rs.getInt(8)));
				}
				rs.close();
			
			
			//commit update
			conn.commit();
			
			
			//close statements
			pstmt.close();
			conn.close();
			
			
			Gson gson = new Gson();
        	//convert from users collection to json.
        	String MsgsJsonResult = gson.toJson(AllUsers, AppConstants.USER_COLLECTION);
        	
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request
	 * @param response returns all users in USERS table
	 * @return 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
