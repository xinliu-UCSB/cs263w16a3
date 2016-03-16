
/*
 * Xin Liu
 * Created on Mar 1, 2016
 * The Grades servlet should be mapped to the "/grade/update_enqueue" URL.
 * forward to /grade_comment.jsp
 * check the authentication of the current user
 * whether the current user is an instructor of the course
 * accept gradeKeyname, score, attribute
 */
package cs263w16.grade;

import java.util.Arrays;
import java.io.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UpdateEnqueue extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	GradesServlet gs = new GradesServlet();

	String gradeKeyname = req.getParameter("gradeKeyname");
        String score = req.getParameter("score");
        String reason = req.getParameter("reason");

	if (user == null) {
		System.err.println( "User has not logged in, but try to add grade" );
		response.sendRedirect("/welcome.jsp");
	}

	// find the corresponding "Instructor" Entity of current user
	// if the instructorID is in the instructorID property of the course
	// the course will be used for searching the grade
	String instructorID =  gs.getInstructorID ( user.getUserId() );
	if (instructorID == null) {
		System.err.println( "got instructorID == null" );
		forwardWithWarning(req, response, "Error: current user is not an instructor.", gradeKeyname);
		return;
	}

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/grade/update_worker")
			.param("gradeKeyname",gradeKeyname).param("instructorID", instructorID)
			.param("score", score).param("reason", reason) ) ;

			
        response.sendRedirect("/done.html");

    }


    private void forwardWithWarning (HttpServletRequest req, HttpServletResponse resp, String warningMessage, String gradeKeyname)
            throws ServletException, IOException {
        String nextJSP = "/grade/grade_comment.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("warningMessage", warningMessage);
        req.setAttribute("gradeKeyname", gradeKeyname);
        dispatcher.forward(req, resp);
    } 

}
