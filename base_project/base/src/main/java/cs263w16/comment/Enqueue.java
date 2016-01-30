// The Enqueue servlet should be mapped to the "/enqueue" URL.
package cs263w16.comment;

import java.io.IOException;
import javax.servlet.ServletException;

import javax.servlet.http.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class Enqueue extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

        String gradeKeyname = req.getParameter("gradeKeyname");
        String studentID = req.getParameter("studentID");
        String grader = req.getParameter("grader");
        String name = req.getParameter("name");
        String content = req.getParameter("content");

        // Add the task to the default queue.
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/comment/worker")
		.param("gradeKeyname", gradeKeyname).param("studentID", studentID)
		.param("grader", grader).param("name", name)
		.param("content", content) ) ;

        response.sendRedirect("/done.html");
    }
}
