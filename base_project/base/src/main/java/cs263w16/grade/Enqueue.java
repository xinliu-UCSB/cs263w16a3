/*
 * Xin Liu
 * Last modified on Feb 12, 2016
 * The Enqueue servlet should be mapped to the "/enqueue" URL.
 */
package cs263w16.grade;

import java.io.IOException;
import javax.servlet.ServletException;

import javax.servlet.http.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class Enqueue extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

        String courseKeyStr = req.getParameter("courseKeyStr");

        String studentID = req.getParameter("studentID");
        String name = req.getParameter("name");
        String score = req.getParameter("score");
        String grader = req.getParameter("grader");
	String attribute = req.getParameter("attribute");

        // Add the task to the default queue.
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/grade/worker").param("courseKeyStr", courseKeyStr)
		.param("studentID", studentID).param("name", name).param("score", score)
		.param("grader", grader).param("attribute", attribute) ) ;

        response.sendRedirect("/done.html");
    }
}
