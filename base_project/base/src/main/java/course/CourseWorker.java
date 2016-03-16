// The Worker servlet should be mapped to the "/worker" URL.
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

public class CourseWorker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService sync = MemcacheServiceFactory.getMemcacheService();
        String courseID = request.getParameter("courseID");
        Filter propertyFilter = new FilterPredicate("courseID", FilterOperator.EQUAL, courseID);
        Query q = new Query("Course").setFilter(propertyFilter);
        List<Entity> courses = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
//        if(courses.size()==0){
            String courseName = request.getParameter("courseName");
            String instructors = request.getParameter("instructorID");
            // Do something with key.
            String[] instructorCollection = instructors.split(",");
            List<String> instructorID = new ArrayList<String>();
            for(int i=0;i<instructorCollection.length;i++){
                instructorID.add(instructorCollection[i]);
            }
            Entity course = new Entity("Course", courseID);
            course.setProperty("courseID", courseID);
            course.setProperty("courseName", courseName);
            course.setProperty("instructorID", instructorID);
            ArrayList<String> roster = new ArrayList<String>();
            course.setProperty("roster", roster);
            datastore.put(course);
//            response.sendRedirect("/instructorpersonal.jsp");
/*        }else{
            String warningMessage = "This course already exists!";
            forwardGradeListWithWarning(request, response, warningMessage);
        }*/
    }

/*    private void forwardGradeListWithWarning (HttpServletRequest req, HttpServletResponse resp, String warningMessage)
            throws ServletException, IOException {
        String nextJSP = "/createcourse.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("warningMessage", warningMessage);
        dispatcher.forward(req, resp);
    } */
}
