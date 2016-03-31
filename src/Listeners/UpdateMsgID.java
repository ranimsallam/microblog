package Listeners;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import First.AppConstants;

import Messages.Message;

/**
 * An example listener that reads the customer json file and populates the data into a Derby database
 */
public class UpdateMsgID implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public UpdateMsgID() {
        // TODO Auto-generated constructor stub
    }
    
    
	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event)  { 
    	ServletContext cntx = event.getServletContext();
    	
    	try{
    		
    		//obtain CustomerDB data source from Tomcat's context
    		Context context = new InitialContext();
    		BasicDataSource ds = (BasicDataSource)context.lookup(AppConstants.DB_DATASOURCE);
    		Connection conn = ds.getConnection();
    		PreparedStatement pstmt = null;
    		
    		try{
    			//create Users and Messages tables
    			//Statement stmt = conn.createStatement();
    			//ResultSet rs = stmt.executeUpdate(AppConstants.SELECT_MSG_ORDER_BY_ID);
    			pstmt = conn.prepareStatement(AppConstants.SELECT_MSG_ORDER_BY_ID);
    			ResultSet rs = pstmt.executeQuery();
    			
    			if(rs.next() == false){
    				Message.MsgCounter = 0;
    			}
    			else{
    				Message.MsgCounter = rs.getInt(1) + 1;
    			}
    			
    			
    			
    			//commit update
        		conn.commit();
        		//stmt.close();
        		pstmt.close();
        		
        		
    		}catch (SQLException e){
    			//check if exception thrown since table was already created (so we created the database already 
    			//in the past
    		}
    		
    		//close connection
    		conn.close();

    	} catch (SQLException | NamingException e) {
    		//log error 
    		cntx.log("Error during database initialization",e);
    	}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event)  { 
         //shut down database
    	 try {
			DriverManager.getConnection(AppConstants.PROTOCOL + AppConstants.DB_NAME +";shutdown=true");
		} catch (SQLException e) {
			ServletContext cntx = event.getServletContext();
			cntx.log("Error shutting down database",e);
		}

    }
	
}
