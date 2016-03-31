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

import Users.User;
import First.AppConstants;

/**
 * Servlet implementation class RegisterServlet
 */
@SuppressWarnings("deprecation")
public class LoginServlet extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request username and password
	 * @param response the information about the username
	 * @return information about user in json
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Context context;
		PreparedStatement pstmt;
		try {
			context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
			Connection conn = ds.getConnection();
			
			Collection<User> loginedUser = new ArrayList<User>(); 
			
			pstmt = conn.prepareStatement(AppConstants.SELECT_USERS_BY_NAME_STMT);
			
			pstmt.setString(1,request.getParameter("username"));
			ResultSet rs = pstmt.executeQuery();
			
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
	       
	      
			
			//if there is user with this username
			if (rs.next()){
				//if the username and the password are correct
				if ( (rs.getString(2).equals(request.getParameter("password")))
						&& rs.getString(1).equals(request.getParameter("username"))){
					
					
					
					String user = rs.getString(1);
					String pass = rs.getString(2);
					String nickname = rs.getString(3);
					String desc = rs.getString(4);
					String photo = rs.getString(5);
					double pop = rs.getDouble(6);
					int following = rs.getInt(7);
					int followers = rs.getInt(8);
					
					String username = request.getParameter("username");
					HttpSession session = null;
						
						//save the loged-in user information in session.
						session = request.getSession();
						session.setAttribute("username", username);
						session.setAttribute("password", pass);
						session.setAttribute("nickname", nickname);
						session.setAttribute("description", desc);
						session.setAttribute("photo", photo);
						session.setAttribute("popularity", pop);
						session.setAttribute("following", following);
						session.setAttribute("followers", followers);
						
						//insert the information of the loged-in user to collection
						loginedUser.add(new User(user,pass,nickname,desc,photo,pop,following,followers));


				} 
				
			}
			
			
			//close statements
			pstmt.close();
			conn.close();
			
			//convert from collection to user
			Gson gson = new Gson();
        	String UserJsonResult = gson.toJson(loginedUser, AppConstants.USER_COLLECTION);

        	PrintWriter writer = response.getWriter();
        	writer.println(UserJsonResult);
        	writer.close();
			
		} catch (SQLException | NamingException e) {
			getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
		}	
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request username and password
	 * @param response the information about the username
	 * @return information about user in json
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request,response);
	}

}