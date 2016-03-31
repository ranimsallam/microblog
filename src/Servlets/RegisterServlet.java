package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javax.servlet.http.HttpSession;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.google.gson.Gson;

import Users.User;
import First.AppConstants;

/**
 * Servlet implementation class RegisterServlet
 */
@SuppressWarnings("deprecation")
public class RegisterServlet extends HttpServlet implements SingleThreadModel {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request username , password, nickname , description , photoUrl
	 * @param response the information about the user
	 * @return information about the user in json
	 */
    //Servlet to register a new account. 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Context context;
		try {
			context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
			Connection conn = ds.getConnection();
			
			Collection<User> loginedUser = new ArrayList<User>();
			
			/* check if there is a user with the same username*/
			Statement stmt;
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(AppConstants.SELECT_ALL_USERS_STMT);
			boolean userExist = false;
			while (rs.next()){
				if( rs.getString("Username").equals(request.getParameter("username")) ||
						rs.getString("Nickname").equals(request.getParameter("nickname")))
					userExist = true;
			}
			
			
			PreparedStatement pstmt = conn.prepareStatement(AppConstants.INSERT_USER_STMT);
			
			//if there is no user with the request username or nickname register new user with the request information
			if(!userExist){
				
				String pass = request.getParameter("password");
				String nickname = request.getParameter("nickname");
				String desc = request.getParameter("description");

				int following = 0;
				int followers = 0;
			
				pstmt.setString(1,request.getParameter("username"));
				pstmt.setString(2,request.getParameter("password"));
				pstmt.setString(3,request.getParameter("nickname"));
				pstmt.setString(4,request.getParameter("description"));
				String p = request.getParameter("photo");
				if( p == ""){
					p = "http://www.elsieman.org/image/home/twitter_logo1-Copy%20(2).png";
				}
				pstmt.setString(5,p);
				pstmt.setDouble(6,Math.log(2));
				pstmt.setInt(7,0);
				pstmt.setInt(8,0);
				
				pstmt.executeUpdate();
				
				//save the information of the new user in session.
				String username = request.getParameter("username");
				HttpSession session = null;
				session = request.getSession();
				session.setAttribute("username", username);
				session.setAttribute("password", pass);
				session.setAttribute("nickname", nickname);
				session.setAttribute("description", desc);
				session.setAttribute("photo", p);
				session.setAttribute("popularity", Math.log(2));
				session.setAttribute("following", following);
				session.setAttribute("followers", followers);
				//commit update
				
				//add the new user to collection and return it.
				loginedUser.add(new User(username,pass,nickname,desc,p,Math.log(2),following,followers));
				conn.commit();
			}
			
			//close statements
			pstmt.close();
			conn.close();
			
			//convert from collection to json
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
	 * @param request username , password, nickname , description , photoUrl
	 * @param response the information about the user
	 * @return information about the user in json
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request,response);
	}

}
