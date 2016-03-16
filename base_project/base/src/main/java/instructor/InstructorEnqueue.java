// The Enqueue servlet should be mapped to the "/enqueue" URL.
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

public class InstructorEnqueue extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String instructorID = request.getParameter("instructorID");
        String lastName = request.getParameter("iln");
        String firstName = request.getParameter("ifn");
        String email = request.getParameter("ie");
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        String userId = user.getUserId();
        String userEmail = user.getEmail();
        Filter propertyFilter = new FilterPredicate("email", FilterOperator.EQUAL, userEmail);
        Query q = new Query("Student").setFilter(propertyFilter);
        List<Entity> students = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        if(students.size()==0){
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withUrl("/instructorworker").param("instructorID", instructorID).param("lastName", lastName).param("firstName", firstName).param("email", email).param("userId", userId));
            response.sendRedirect("/instructorpersonal.jsp");    
        }else{
            String warningMessage = "You can not enroll as an instructor, because you are a student!";
            forwardGradeListWithWarning(request, response, warningMessage);
        }
        // Add the task to the default queue.
    }
    private void forwardGradeListWithWarning (HttpServletRequest req, HttpServletResponse resp, String warningMessage)
            throws ServletException, IOException {
        String nextJSP = "/choose.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("warningMessage", warningMessage);
        dispatcher.forward(req, resp);
    }
}
