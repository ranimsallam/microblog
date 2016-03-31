package Servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Messages.Message;

/**
 * Servlet implementation class ChangeDisplayServlet
 */
@SuppressWarnings("deprecation")
public class ChangeDisplayServlet extends HttpServlet implements SingleThreadModel {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeDisplayServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request
	 * @param response
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*changes the ShowMsgsIFollow from true to false or from false to true to let the DiscoverServlet to know
		wich messages to select from the MESSAGES table 
		if ShowMsgsIFollow == true , the DiscoverServlet will select the top 10 messages that the user is following
		else it'll select the top 10 public messages. */
		Message.ShowMsgsIFollow = !(Message.ShowMsgsIFollow);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param request
	 * @param response
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
