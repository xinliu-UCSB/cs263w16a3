/*
 * Xin Liu
 * Last modified on Feb 18, 2016
 * The Grades servlet should be mapped to the "student/grade" URL.
 * forward to /list_student_grade.jsp with the list of search result grades
 * check the authentication of the current user
 * whether the current user is the student of studentID
 * accept courseID, and (grade name) as parameters
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
public class StudentGradeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
  	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	UserService userService = UserServiceFactory.getUserService();

	User user = userService.getCurrentUser();
        String courseID = req.getParameter("courseID");
        String name = req.getParameter("name");

	if (user == null) {
		System.err.println( "User has not logged in, but try to add grade" );
		resp.sendRedirect("/welcome.jsp");
	}

	// find the corresponding "Student" Entity of current user
	// if the student has the courseID in its property "courseID"
	// the courseID will be used for searching the grade
	Entity student =  getStudent ( user.getEmail(), courseID );
	if (student == null) {
		System.err.println( "Error in StudentGradeServlet: got student == null for current user with courseID: " + courseID);
		resp.sendRedirect("/welcome.jsp");
		return;
	}
	String perm = (String) student.getProperty("perm");
		
	Key courseKey;

     try {

		courseKey = getCourseKey( "courseID", courseID, perm );

	if (courseKey == null) {
		forwardGradeListWithWarning(req, resp, courseID, "Error when looking for the course, please try agian!");
		return;
	}

	String studentID = perm;

	Query q = new Query("Grade").setAncestor(courseKey);
	
	if( name.equals("") ) {
		Filter studentIDFilter =
  			new FilterPredicate("studentID",
                      	FilterOperator.EQUAL, studentID);
		q = q.setFilter(studentIDFilter);
	} else {
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
		gradeList.add(grade);
	}

	forwardGradeList(req, resp, courseID, gradeList);
      } catch (Exception e) {
		System.out.println( "Error when looking for the course." );
		forwardGradeListWithWarning(req, resp, courseID, "Got exception when looking for the grades.");		
      }
    }

	// return the perm of the current user 
	//if it has a "Student" Entity with courseID in the "courseID" property
    protected Entity getStudent ( String email, String courseID) {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Filter filter = new FilterPredicate("email",
                      	FilterOperator.EQUAL, email);
	Filter courseFilter = new FilterPredicate("courseID",
                      	FilterOperator.EQUAL, courseID);
	filter =
			CompositeFilterOperator.and(filter, courseFilter);
	Query q = new Query("Student").setFilter(filter);
	PreparedQuery pq = datastore.prepare(q);

	try {
		Entity student = pq.asSingleEntity();
		return student;
	} catch (Exception e) {
		System.out.println( "Too many students are found for user with email: "  + email);
		return null;
	}
    }

    private void forwardGradeListWithWarning (HttpServletRequest req, HttpServletResponse resp, String courseID, String warningMessage)
            throws ServletException, IOException {
        String nextJSP = "/grade/list_student_grade.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
	req.setAttribute("courseID", courseID);
        req.setAttribute("warningMessage", warningMessage);
        dispatcher.forward(req, resp);
    } 

    private void forwardGradeList (HttpServletRequest req, HttpServletResponse resp, 
		String courseID, List<Grade> gradeList)
           	throws ServletException, IOException {
        String nextJSP = "/grade/list_student_grade.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
	req.setAttribute("courseID", courseID);
        req.setAttribute("gradeList", gradeList);
	req.setAttribute("searchResult", "no grade");
        dispatcher.forward(req, resp);
    } 

	// this method require that for a courseID, there is only a single course entity
    protected Key getCourseKey( String propertyName, String propertyValue, String perm) {
  	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Filter courseFilter =
  			new FilterPredicate(propertyName,
                      	FilterOperator.EQUAL, propertyValue);	
	
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
