package instructor;

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

public class DeleteInstructor extends HttpServlet {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String courseID = request.getParameter("courseID");
        String instructorID = request.getParameter("instructorID");
        ArrayList<String> newInstructorID = new ArrayList<String>();
        Filter propertyFilter = new FilterPredicate("courseID", FilterOperator.EQUAL, courseID);
        try{
            Query q = new Query("Course").setFilter(propertyFilter);
            List<Entity> courses = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
            ArrayList<String> instructors = null;
            for(Entity course : courses){
                instructors = (ArrayList<String>) course.getProperty("instructorID");
            }
            if(instructors.size()!=0){
                for(String instructor : instructors){
                    if(!instructor.equals(instructorID))
                        newInstructorID.add(instructor);
                }
            }
            if(newInstructorID.size()!=0){
                for(Entity course : courses){
                    course.setProperty("instructorID", newInstructorID);
                    datastore.put(course);
                }
            }
            else{
                response.sendRedirect("/coursedetail.jsp?courseID="+courseID);
            }
        }finally{}
        response.sendRedirect("/coursedetail.jsp?courseID="+courseID);
    }
}