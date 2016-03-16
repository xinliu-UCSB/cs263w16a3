/*
 * Xin Liu
 * Last modified on Feb 18, 2016
 * The Grades servlet should be mapped to the "/grade" URL.
 * forward to /listgrade.jsp with the list of serach resultgrades
 * check the authentication of the current user
 * whether the current user is an instructor of the course
 * accept either courseKeyStr, or courseID, or courseName as a parameter
 */

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


//import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class GradesServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
  	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	UserService userService = UserServiceFactory.getUserService();

	User user = userService.getCurrentUser();
	if (user == null) {
		System.err.println( "User has not logged in, but try to add grade" );
		resp.sendRedirect("/welcome.jsp");
	}

	// find the corresponding "Instructor" Entity of current user
	// if the instructorID is in the instructorID property of the course
	// the course will be used for searching the grade
	String instructorID =  getInstructorID ( user.getUserId() );
	if (instructorID == null) {
		System.err.println( "got instructorID == null" );
		resp.sendRedirect("/welcome.jsp");
		return;
	}

        String courseKeyStr = req.getParameter("courseKeyStr");
        String courseID = req.getParameter("courseID");
        String courseName = req.getParameter("courseName");

        String studentID = req.getParameter("studentID");
        String name = req.getParameter("name");

	Key courseKey;

     try {
	if(courseKeyStr != null) {
	    try{
		courseKey = KeyFactory.stringToKey(courseKeyStr);
	    } catch(Exception e) {
		System.out.println( "The course is not found." );
		forwardGradeListWithWarning(req, resp, courseID, "Course not found, please try agian!");
		return;
	    }
	} else if(courseID != null && !courseID.equals("") ) {
		courseKey = getCourseKey( "courseID", courseID, instructorID );
	} else if (courseName != null && courseName != "") {
		courseKey = getCourseKey("courseName", courseName, instructorID );
	} else {
		//forward to "/listgrade.jsp", ask user to specify a course
		String warningMessage = "Please specify a course!";
		forwardGradeListWithWarning(req, resp, courseID, warningMessage);
		return;
	}

	if (courseKey == null) {
		forwardGradeListWithWarning(req, resp, courseID, "Error when looking for the course, please try agian!");
		return;
	}	

	//check whether instructorID is an instructorID of courseID
	// if not, return to listgrade.jsp
	// disable this first
/*	if( !isUserAuthenticated( courseKey, instructorID ) ) {
		System.err.println( "Instructor " + instructorID + " is not authenticated to add grade for this course" );
		resp.sendRedirect("/welcome.jsp");
		return;
	}
*/
	Query q = new Query("Grade").setAncestor(courseKey);
	
	if(studentID != "" && name == "") {
		Filter studentIDFilter =
  			new FilterPredicate("studentID",
                      	FilterOperator.EQUAL, studentID);
		q = q.setFilter(studentIDFilter);
	} else if(studentID == "" && name != "") {
		Filter nameFilter =
  			new FilterPredicate("name",
                      	FilterOperator.EQUAL, name);
		q = q.setFilter(nameFilter);
	} else if(studentID != "" && name != "") {
		Filter studentIDFilter =
  			new FilterPredicate("studentID",
                      	FilterOperator.EQUAL, studentID);
		Filter nameFilter =
  			new FilterPredicate("name",
                      	FilterOperator.EQUAL, name);
		Filter StudentIDNameFilter =
			CompositeFilterOperator.and(studentIDFilter, nameFilter);
		q = q.setFilter(StudentIDNameFilter);
	}

	  PreparedQuery pq = datastore.prepare(q);

	List<Grade> gradeList = new ArrayList<>();
	for (Entity ent : pq.asIterable()) {
		String gradeKeyStr = KeyFactory.keyToString( ent.getKey() );
		studentID = (String) ent.getProperty("studentID");
		name = (String) ent.getProperty("name"); 
		String grader = (String) ent.getProperty("grader");
		int score = ( (Long) ent.getProperty("score") ).intValue(); 
		Date date= (Date) ent.getProperty("date"); 
		String attribute = (String) ent.getProperty("attribute");
		Grade grade = new Grade (gradeKeyStr, studentID, name, score, grader, date, attribute);
		grade.setHasNewComment( ((Boolean) ent.getProperty("hasNewComment")).booleanValue() );
		gradeList.add(grade);
	}

	courseID = (String) datastore.get(courseKey).getProperty("courseID");	
	forwardGradeList(req, resp, courseID, gradeList);
      } catch (Exception e) {
		System.out.println( "Error when looking for the course." );
		forwardGradeListWithWarning(req, resp, courseID, "Got exception when looking for the grades.");		
      }
    }

    // return the instructorID of the userID
    // if not an instructor, return null
    protected String getInstructorID ( String userId ) {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Filter filter = new FilterPredicate("userId",
                      	FilterOperator.EQUAL, userId);
	Query q = new Query("Instructor").setFilter(filter);
	PreparedQuery pq = datastore.prepare(q);

	try {
		Entity instructor = pq.asSingleEntity();
		if (instructor == null) {
			System.out.println( "No instructor is found for userId: "  + userId);
			return null;
		} else {
			return (String) instructor.getProperty("instructorID");
		}
	} catch (Exception e) {
		System.out.println( "Too many instructors are found for userId: "  + userId);
		return null;
	}
    }

    private void forwardGradeListWithWarning (HttpServletRequest req, HttpServletResponse resp, 
		String courseID, String warningMessage)
            throws ServletException, IOException {
        String nextJSP = "/grade/list_grade.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
	req.setAttribute("courseID", courseID);
        req.setAttribute("warningMessage", warningMessage);
        dispatcher.forward(req, resp);
    } 

    private void forwardGradeList (HttpServletRequest req, HttpServletResponse resp, 
		String courseID, List<Grade> gradeList)
           	throws ServletException, IOException {
        String nextJSP = "/grade/list_grade.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
	req.setAttribute("courseID", courseID);
        req.setAttribute("gradeList", gradeList);
	req.setAttribute("searchResult", "no grade");
        dispatcher.forward(req, resp);
    } 

    protected boolean isUserAuthenticated(Key courseKey, String instructorID) {
  	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      try{
	Entity course = datastore.get(courseKey);
	ArrayList<String> instructors = (ArrayList<String>) course.getProperty("instructorID");
	return ( instructors.contains( instructorID ));
      } catch (Exception e) {
		System.out.println( "The course is not found." );
		return false;
      }
    }

    protected Key getCourseKey( String propertyName, String propertyValue, String instructorID) {
  	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Filter courseFilter =
  			new FilterPredicate(propertyName,
                      	FilterOperator.EQUAL, propertyValue);	
	Filter instructorFilter =
  			new FilterPredicate("instructorID",
                      	FilterOperator.EQUAL, instructorID);
	courseFilter =
			CompositeFilterOperator.and(courseFilter, instructorFilter);
	Query cq = new Query("Course").setFilter(courseFilter);
	PreparedQuery cpq = datastore.prepare(cq);

	try {
		Entity courseEnt = cpq.asSingleEntity();
		if (courseEnt == null) {
			System.out.println( "No course is found for " + propertyName + ": " + propertyValue);
			return null;
		} else {
			return courseEnt.getKey();
		}
	} catch (Exception e) {
		System.out.println( "Too many courses are found for " + propertyName + ": " + propertyValue);
		return null;
	}
	
    }
}
