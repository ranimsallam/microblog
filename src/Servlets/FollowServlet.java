package Servlets;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;




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

import First.AppConstants;






/**
 * Servlet implementation class FollowServlet
 */
@SuppressWarnings("deprecation")
public class FollowServlet extends HttpServlet implements SingleThreadModel{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request user to follow
	 * @param response
	 * @return 
	 */
	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
	    	
        	
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
    		Connection conn = ds.getConnection();

    		String CommandDone = "";
    		String uri = request.getRequestURI();
    		if (uri.indexOf(AppConstants.NICKNAME) != -1){
    			String nicknameToFollow = uri.substring(uri.indexOf(AppConstants.NICKNAME) + AppConstants.NICKNAME.length() + 1);
    			nicknameToFollow = nicknameToFollow.replaceAll("\\%20", " ");
    			
    			try {
    				
    				HttpSession session = request.getSession(false);
    				String thisNickname = (String) session.getAttribute("nickname");
    				Double newPopularity;
    				
    				if(thisNickname.equals(nicknameToFollow))
    					response.setStatus(404);
    				else{
    			    
		    			    //Get number of followers of nicknameToFollow
		    				PreparedStatement stmt = conn.prepareStatement(AppConstants.SELECT_USERS_BY_NICKNAME_STMT);
		    				stmt.setString(1,nicknameToFollow);
		    				ResultSet rs = stmt.executeQuery();
		    				rs.next();
		    				int numberOfFollowers = rs.getInt(8);

		    				
		    				//check if the user is already following the other user
		    				stmt = conn.prepareStatement(AppConstants.SELECT_FOLLOW_PAIR);
		    				stmt.setString(1,thisNickname);
		    				stmt.setString(2,nicknameToFollow);
		    				rs = stmt.executeQuery();
		    			    //rs.next();
		    			    
		    			    //the the user osn't following the other user - do follow request
		    			    if(!rs.next()){
		    				
			    				// add 1 to followingCounter to thisNickname
			    			    stmt = conn.prepareStatement(AppConstants.INC_FOLLOWING_BY_ONE);
			    				stmt.setString(1,thisNickname);
			    			    stmt.executeUpdate();
			    				
			    				
			    				//add 1 to followersCounter to nicknameToFollow
			    				stmt = conn.prepareStatement(AppConstants.INC_FOLLOWERS_BY_ONE);
			    				//nicknameToFollow = nicknameToFollow.replaceAll("\\%20", " ");
			    				stmt.setString(1,nicknameToFollow);
			    			    stmt.executeUpdate();
			    			    
			    			    	
			  
			    			    newPopularity = Math.log10(2+numberOfFollowers+1);	
			    				
			    			    //update popularity to nicknameToFollow
			    			    stmt = conn.prepareStatement(AppConstants.UPDATE_POPULARITY);
			    				stmt.setDouble(1,newPopularity);
			    				stmt.setString(2,nicknameToFollow);
			    			    stmt.executeUpdate();    
			    			    
			    			    //insert into FOLLOWING TABLE
			    			    stmt = conn.prepareStatement(AppConstants.INSER_FOLLOWING_STMT);
			    				stmt.setString(1,thisNickname);
			    				stmt.setString(2,nicknameToFollow);
			    				stmt.executeUpdate();
			    				
			    				CommandDone = "FOLLOW";
		    			    }
		    			    //if the user is already following the other user - do unfollow request
		    			    else{
		    			    	
		    			    	//delete row from FOLLOWING TABLE (unfollow)
		    			    	stmt = conn.prepareStatement(AppConstants.DELETE_ROW_FROM_FOLLOWING);
			    				stmt.setString(1,thisNickname);
			    				stmt.setString(2,nicknameToFollow);
			    			    stmt.executeUpdate();
			    			    
			    			    //decrease following by 1
			    			    stmt = conn.prepareStatement(AppConstants.DEC_FOLLOWING_BY_ONE);
			    				stmt.setString(1,thisNickname);
			    			    stmt.executeUpdate();
			    			    
			    			    //decrease followers by 1
			    			    stmt = conn.prepareStatement(AppConstants.DEC_FOLLOWERS_BY_ONE);
			    				stmt.setString(1,nicknameToFollow);
			    			    stmt.executeUpdate();
			    			    
			    			    
			    			    newPopularity = Math.log10(2+numberOfFollowers-1);
			    			    
			    			    //update popularity to user 
			    			    stmt = conn.prepareStatement(AppConstants.UPDATE_POPULARITY);
			    				stmt.setDouble(1,newPopularity);
			    				stmt.setString(2,nicknameToFollow);
			    			    stmt.executeUpdate();
		    			    	
			    			    CommandDone = "UNFOLLOW";
		    			    }
		    				
		    				
		    				conn.commit();
		    				
		    				
		    				rs.close();
		    				stmt.close();
    				}
    				
    			} catch (SQLException e) {
    				getServletContext().log("Error while querying for customers", e);
    	    		response.sendError(500);//internal server error
    			}
    		}

    		conn.close();
    		
    		
    	} catch (SQLException | NamingException e) {
    		getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
    	}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request user to follow
	 * @param response
	 * @return 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
