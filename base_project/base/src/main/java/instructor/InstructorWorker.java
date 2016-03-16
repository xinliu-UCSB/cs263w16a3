// The Worker servlet should be mapped to the "/worker" URL.
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

public class InstructorWorker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService sync = MemcacheServiceFactory.getMemcacheService();
        String instructorID = request.getParameter("instructorID");
        String lastName = request.getParameter("lastName");
        String firstName = request.getParameter("firstName");
        String email = request.getParameter("email");
        String userId = request.getParameter("userId");
        // Do something with key.
        Entity instructor = new Entity("Instructor", instructorID);
        instructor.setProperty("instructorID", instructorID);
        instructor.setProperty("lastName", lastName);
        instructor.setProperty("firstName", firstName);
        instructor.setProperty("email", email);
        instructor.setProperty("userId", userId);
        datastore.put(instructor);
    }
}
