// The Worker servlet should be mapped to the "couseTest/worker" URL.

/**
 * Course Test Worker for curl test
 * 
 * @author Xin Liu
 *
 */
package course;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
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
import cs263w16.CourseTest;

public class CourseTestWorker extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

        String courseID = request.getParameter("courseID");
        String courseName = request.getParameter("courseName");
        String instructorID = request.getParameter("instructorID");

    if(courseID != null) {
	Key entKey = KeyFactory.createKey("CourseTest", courseID);
	try {
		//In order not to affect the running app, cannot update an existing course
		Entity courseEnt = datastore.get(entKey);
	}  catch(EntityNotFoundException e) {
		//if it is not exist, create it and in the datastore
		Entity courseEnt = new Entity("CourseTest", courseID);
        	courseEnt.setProperty("courseID", courseID);
        	 if(courseName != null) courseEnt.setProperty("courseName", courseName);
        	 if(instructorID != null) courseEnt.setProperty("instructorID", instructorID);
		datastore.put(courseEnt);
	} 
	CourseTest ct = new CourseTest();
	ct.setCourseID(courseID);
	ct.setCourseName(courseName);    
	ct.setInstructorID(instructorID);
	memcache.put(courseID, ct);
    }
  }

}
