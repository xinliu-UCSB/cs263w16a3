/*
 * Xin Liu
 * Last modified on Feb 12, 2016
 * The Worker servlet should be mapped to the "/grade/worker" URL.
 * create a single "Grade" Entity and put it into datastore
 * use a "Course" Entity as the parent
 * may need to check for this course, studentID and name, whether the "Grade" Entity already exists 
 */

package cs263w16.grade;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;


public class Worker extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        String courseKeyStr = req.getParameter("courseKeyStr");

    try {
	Key courseKey = KeyFactory.stringToKey(courseKeyStr);

	Entity course = datastore.get(courseKey);
	ArrayList<String> instructors = (ArrayList<String>) course.getProperty("instructors");

	// check whether current user is authoenticated to add grade for this course
	UserService userService = UserServiceFactory.getUserService();
	User userName = userService.getCurrentUser();
	if (userName == null) {
		System.err.println( "User has not logged in, but try to add grade" );
		response.sendRedirect("/welcome.jsp");
	}
	//need to know what is stored in coures property "instructors"
	if(!instructors.contains( userName.toString() )) {
		System.err.println( "User " + userName.toString() + " is not authenticated to add grade for this course" );
		response.sendRedirect("/welcome.jsp");		
	}

        String studentID = req.getParameter("studentID");
        String name = req.getParameter("name");
        int score = Integer.parseInt( req.getParameter("score") );

	// grader should be the current user, who must be one of the course.instructors
        String grader =  userName.toString();
	String attribute = req.getParameter("attribute");
	Date date = new Date();

	//create an Entity of kind TaskData and put it into Datastore.
	// the corresponding course is set as the parent
	Entity tne = new Entity("Grade", courseKey);
	tne.setProperty("studentID", studentID);
	tne.setProperty("name", name);
	tne.setProperty("score", score);
	tne.setProperty("grader", grader);
	tne.setProperty("date", date);
	tne.setProperty("attribute", attribute);

	datastore.put(tne);

        Key gradeKey = tne.getKey();
	String gradeKeyStr = KeyFactory.keyToString(gradeKey);
	Grade gd = new Grade(gradeKeyStr, studentID, name, score, grader, date, attribute);

	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	//keyName in Memcache is used to search whether a grade 
	// of a particular "courseKeyStr + studentID + name" exists
	String keyName = courseKeyStr + studentID + name;
	syncCache.put(keyName, gd );

	System.out.println( "Stored " + studentID + " / " + name + " in Datastore and Memcache" );

    } catch (Exception e) {
		System.out.println( "Key of " + courseKeyStr + " is not found." );
		response.sendRedirect("/welcome.jsp");		
    }

    }
}
