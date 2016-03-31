package Listeners;


import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;
import java.sql.Statement;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import First.AppConstants;


/**
 * An example listener that reads the customer json file and populates the data into a Derby database
 */
public class CreateMsgsTable implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public CreateMsgsTable() {
        // TODO Auto-generated constructor stub
    }
    
    //utility that checks whether the customer tables already exists
    private boolean tableAlreadyExists(SQLException e) {
        boolean exists;
        if(e.getSQLState().equals("X0Y32")) {
            exists = true;
        } else {
            exists = false;
        }
        return exists;
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
    		boolean created = false;
    		try{
    			//create Users and Messages tables
    			Statement stmt = conn.createStatement();
    			
    			//stmt.executeUpdate("DROP TABLE MESSAGES");
    			stmt.executeUpdate(AppConstants.CREATE_MESSAGES_TABLE);
    			
    			//stmt.executeUpdate(AppConstants.CREATE_FOLLOWING_TABLE);
    			//commit update
        		conn.commit();
        		stmt.close();
        		
    		}catch (SQLException e){
    			//check if exception thrown since table was already created (so we created the database already 
    			//in the past
    			created = tableAlreadyExists(e);
    			if (!created){
    				throw e; //re-throw the exception so it will be caught in the
    				//external try..catch and recorded as error in the log
    			}
    		
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
