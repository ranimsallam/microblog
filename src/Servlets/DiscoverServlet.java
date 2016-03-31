package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

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
import Messages.ConvertDate;
import Messages.Message;

/**
 * Servlet implementation class DiscoverServlet
 */
@SuppressWarnings("deprecation")
public class DiscoverServlet extends HttpServlet implements SingleThreadModel {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DiscoverServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request 
	 * @param response ArrayList of Json Message
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Context context;
		PreparedStatement pstmt = null;
		
		try {
			context = new InitialContext();
			BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
			Connection conn = ds.getConnection();
			
			HttpSession session = request.getSession(false);
			String myNickname = (String) session.getAttribute("nickname");
			ResultSet rs3 = null;
			ArrayList<Message> TopTenMsgs = new ArrayList<Message>();
			
			
			//select the top 10 public messages.
			if(Message.ShowMsgsIFollow == false){
				pstmt = conn.prepareStatement(AppConstants.SELECT_OTHERS_MSGS);
				pstmt.setString(1,myNickname);
				ResultSet rs = pstmt.executeQuery();
			
				
				while(rs.next()){
					pstmt = conn.prepareStatement(AppConstants.SELECT_USERS_BY_NICKNAME_STMT);
					pstmt.setString(1,rs.getString(3));
					rs3 = pstmt.executeQuery();
					
					rs3.next(); 
					int followers = rs3.getInt(8);
					int republished = rs.getInt(5);
					double popu = (Math.log10(followers+2)/Math.log10(2)) * (Math.log10(republished+2)/Math.log10(2));
					String timeToDisplay = ConvertDate.GetDuration(rs.getString(4));
					
					TopTenMsgs.add(new Message(rs.getInt(1),rs.getString(2), rs.getString(3), timeToDisplay,
							rs.getInt(5),popu));
					
				}
				
				Message.ReturnSorted(TopTenMsgs);
				
				if(TopTenMsgs.size() > 10){
					TopTenMsgs = new ArrayList<Message>(TopTenMsgs.subList(0, 10));
				}
				
				Collections.sort(TopTenMsgs,Message.COMPAREBYDATE);
				
			
			
				rs.close();
			}
			//if ShowMsgsIFollow == true , the DiscoverServlet will select the top 10 messages that the user is following
			else{
				pstmt = conn.prepareStatement(AppConstants.GET_FOLLOWING);
				pstmt.setString(1, myNickname);
				ResultSet rs2 = pstmt.executeQuery();
				
				
				while(rs2.next()){
					pstmt = conn.prepareStatement(AppConstants.SELECT_MSGS_BY_USER);
					pstmt.setString(1,rs2.getString(3));
					rs3 = pstmt.executeQuery();
					rs3.next();
							
					int followers = rs2.getInt(8);
					int republished = rs3.getInt(5);
					double popu = (Math.log10(followers+2)/Math.log10(2)) * (Math.log10(republished+2)/Math.log10(2));
					String timeToDisplay = ConvertDate.GetDuration(rs3.getString(4));
					
					TopTenMsgs.add(new Message(rs3.getInt(1),rs3.getString(2), rs3.getString(3), timeToDisplay,
							rs3.getInt(5),popu));
					
				}
				
				Message.ReturnSorted(TopTenMsgs);
				
				if(TopTenMsgs.size() > 10){
					TopTenMsgs = new ArrayList<Message>(TopTenMsgs.subList(0, 10));
				}
				
				Collections.sort(TopTenMsgs,Message.COMPAREBYDATE);
			
				
				rs2.close();
			}
			
			//commit update
			conn.commit();
			
			
			//close statements
			pstmt.close();
			conn.close();
			
			
			Gson gson = new Gson();
        	//convert from messages collection to json
        	String MsgsJsonResult = gson.toJson(TopTenMsgs, AppConstants.MSGS_COLLECTION);
        	
        	PrintWriter writer = response.getWriter();
        	writer.println(MsgsJsonResult);
        	writer.close();
			
		} catch (SQLException | NamingException e) {
			getServletContext().log("Error while closing connection", e);
    		response.sendError(500);//internal server error
		} catch (ParseException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @request
	 * @param Arraylist of Json Message
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
