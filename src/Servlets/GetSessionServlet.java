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
import javax.servlet.http.HttpSession;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.google.gson.Gson;

import First.AppConstants;
import Users.User;

/**
 * Servlet implementation class GetSessionServlet
 */
@SuppressWarnings("deprecation")
public class GetSessionServlet extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSessionServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 *  @param request 
	 * @param response 
	 * @return json - all the information of the user from the session
	 */
    //this servlet returns response that include the information of the username in the session (username that loged-in)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		HttpSession session = request.getSession(false);
		PreparedStatement pstmt = null;
		Context context;
		
		try {
			context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
			Connection conn = ds.getConnection();
			
			Collection<User> userInfo = new ArrayList<User>();
			
			
				//get the row that include information of the loged-in user (that have been saved in the session)
				pstmt = conn.prepareStatement(AppConstants.SELECT_USERS_BY_NAME_STMT);
				pstmt.setString(1,(String) session.getAttribute("username"));
				
				ResultSet rs = pstmt.executeQuery();
				
				rs.next();
					User user = new User(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),
							rs.getString(5),rs.getDouble(6),rs.getInt(7),rs.getInt(8));
					userInfo.add(user);
				
				rs.close();
				
			//commit update
			conn.commit();
			
			
			//close statements
			pstmt.close();
			conn.close();
			
			//convert from collection to json
			Gson gson = new Gson();
        	
        	String UserJson  = gson.toJson(userInfo, AppConstants.USER_COLLECTION);
        	
        	PrintWriter writer = response.getWriter();
        	writer.println(UserJson);
        	writer.close();
			
		} catch (SQLException | NamingException e) {
			getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
		}	
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 *  @param request 
	 * @param response 
	 * @return json - all the information of the user from the session
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request,response);
	}

}
