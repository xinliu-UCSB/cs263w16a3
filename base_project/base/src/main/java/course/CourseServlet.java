package course;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class CourseServlet extends HttpServlet {
  public CourseServlet(){

  }
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.getWriter().println("<html><body>");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    Entity course1 = new Entity("Course", "1");
    course1.setProperty("courseID", "1");
    course1.setProperty("courseName", "cs263");
    datastore.put(course1);
    Course course2 = new Course("1","cs263");
    syncCache.put("1",course2);
    String courseID = req.getParameter("courseID");
//    String courseName = req.getParameter("courseName");
    Key entKey = KeyFactory.createKey("Course", courseID);
    Course course3 = (Course)syncCache.get(courseID);
    if(course3!=null){
      resp.getWriter().println("Memcache: " + "courseID: " + course3.getCourseID() + " courseName: " + course3.getCourseName()+ "<br>");
    }
    try{
      Entity course1_1 = datastore.get(entKey);
      resp.getWriter().println("Datastore: " + "courseID: " + course1_1.getProperty("courseID") + "  name: " + course1_1.getProperty("name") + "<br>");
    }catch(EntityNotFoundException e){
      resp.getWriter().println("entity not found!");
    }
//    resp.getWriter().println("courseID: " + course1_1.getProperty("courseID") + "  name: " + course1_1.getProperty("name") + "<br>");
    resp.getWriter().println("</body></html>");
  }
}
