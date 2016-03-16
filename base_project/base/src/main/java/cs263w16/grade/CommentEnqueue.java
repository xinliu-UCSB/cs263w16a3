// The Enqueue servlet should be mapped to the "/comment/enqueue" URL.
package cs263w16.grade;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.*;
import java.util.logging.*;
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
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


public class CommentEnqueue extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String gradeKeyname = req.getParameter("gradeKeyname");
	String email = req.getParameter("email");
        String userID = req.getParameter("userID");
        String userType;
	String[] senderInfo = getUserInfo(email, userID, gradeKeyname);
        String name = req.getParameter("name");
        String content = req.getParameter("content");

        // Add the task to the default queue.
	if(senderInfo != null) {
//		String sender = userInfo[0];
//		userType = userInfo[1];
        	Queue queue = QueueFactory.getDefaultQueue();
       		queue.add(TaskOptions.Builder.withUrl("/comment/worker")
			.param("gradeKeyname", gradeKeyname).param("userID", senderInfo[0])
			.param("userType", senderInfo[1]).param("name", name)
			.param("content", content) ) ;

        	resp.sendRedirect("/done.html");
//		forwardGradeComment(req, resp, userID, gradeKeyname);
	} else {
		resp.sendRedirect("/welcome.jsp");
	}
    }

    private void forwardGradeComment (HttpServletRequest req, HttpServletResponse resp, 
		String userID, String gradeKeyname)
           	throws ServletException, IOException {
        String nextJSP = "/grade/grade_comment.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
	req.setAttribute("userID", userID);
        req.setAttribute("gradeKeyname", gradeKeyname);
        dispatcher.forward(req, resp);
    }

    public String[] getUserInfo( String email, String userID, String gradeKeyname) {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	Filter UserIDFilter = new FilterPredicate("userId", FilterOperator.EQUAL, userID);
	Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);

	//check whether the userID is the student of the corresponding grade
	Query q = new Query("Student").setFilter(emailFilter);
	PreparedQuery pq = datastore.prepare(q);	
	try{
		Entity st = datastore.prepare(q).asSingleEntity();
		if (st == null) {
			System.out.println( "CommentEnqueue: user is not a student.");
		} else {
			System.out.println( "CommentEnqueue: user is a student.");
			String perm = (String) st.getProperty("perm");
	    		try{
				Key gradeKey = KeyFactory.stringToKey(gradeKeyname);
				Entity grade = datastore.get(gradeKey);
				String studentID = (String) grade.getProperty("studentID");
				if( perm.equals(studentID) )	 {
					String[] userInfo = new String[2];
					userInfo[0] = perm;
					userInfo[1] = "student";
					System.out.println( "userInfo: " + userInfo[0] + ", "+ userInfo[1]);
					return userInfo;
				}	
			} catch(Exception e) {
				System.out.println( "The grade is not found or user is not the student." );
			}
		}
	} catch (Exception e) {
		System.out.println( "Too many students are found for this user");
	}		


	//check whether the userID is an instructor of the corresponding course
	q = new Query("Instructor").setFilter(UserIDFilter);
	pq = datastore.prepare(q);
	try{
		Entity instructor = datastore.prepare(q).asSingleEntity();
		if (instructor == null) {
			System.out.println( "CommentEnqueue: user is not an instructor.");
		} else {
			System.out.println( "CommentEnqueue: user is an instructor.");
			String instructorID = (String) instructor.getProperty("instructorID");
	    		try{
				Key gradeKey = KeyFactory.stringToKey(gradeKeyname);
				Key courseKey = gradeKey.getParent();
				Entity course = datastore.get(courseKey);
				List<String> instructorIDList = (List<String>) course.getProperty("instructorID");
				for(String str: instructorIDList) {
    					if(str.trim().contains(instructorID)) {
						String[] userInfo = new String[2];
						userInfo[0] = instructorID;
						userInfo[1] = "instructor";
						System.out.println( "userInfo: " + userInfo[0] + ", "+ userInfo[1]);
						return userInfo;
					}
				}
	    		} catch(Exception e) {
				System.out.println( "The grade is not found or instructorID error." );
				return null;
			}
		}
	} catch (Exception e) {
		System.out.println( "Too many instructors are found for this user");
		return null;
	}

	return null;
    }
}
