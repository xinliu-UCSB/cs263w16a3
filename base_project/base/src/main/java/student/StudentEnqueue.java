// The Enqueue servlet should be mapped to the "/enqueue" URL.
package student;

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

public class StudentEnqueue extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String wholeInfor = request.getParameter("roster");
        String courseID = request.getParameter("courseID");
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/studentworker")
		.param("wholeInfor", wholeInfor).param("courseID", courseID));
        /*String[] stuInfor = wholeInfor.split(";");
        Queue queue = QueueFactory.getDefaultQueue();
        for(int i=0;i<stuInfor.length;i++){
            String[] singleInfor = stuInfor[i].split(",");
            String perm = singleInfor[0];
            String lastName = singleInfor[1];
            String firstName = singleInfor[2];
            String email = singleInfor[3];
            //String courseID = singleInfor[4];
            queue.add(TaskOptions.Builder.withUrl("/studentworker").param("perm", perm).param("lastName", lastName).param("firstName", firstName).param("email", email).param("courseID", courseID));
        }*/
        /*String perm = request.getParameter("sperm");
        String lastName = request.getParameter("sln");
        String firstName = request.getParameter("sfn");
        String email = request.getParameter("se");
        String courseID = request.getParameter("sc");
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        String userId = user.getUserId();*/

        // Add the task to the default queue.
        /*Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/studentworker").param("perm", perm).param("lastName", lastName).param("firstName", firstName).param("email", email).param("courseID", courseID).param("userId", userId));
        */response.sendRedirect("/instructorpersonal.jsp");
    }
}
