/*package student;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import javax.xml.bind.JAXBElement;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Path("/student")
public class StudentResource {
  /*@Context
  UriInfo uriInfo;
  @Context
  Request request;
  String perm;
  public StudentResource(UriInfo uriInfo, Request request, String perm) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.perm = perm;
  }*/

  // for the browser
  /*@POST
  @Path("/createnewstudent")
  @Consumes("application/x-www-form-urlencoded")
  public void createNewStudent(@("perm") String perm,
                      @FormParam("lastName") String lastName,
                      @FormParam("firstName") String firstName,
                      @FormParam("email") String email,
                      @FormParam("courseID") String courseID,
                      @Context HttpServletResponse response) throws Exception {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    String userId = user.getUserId();
    Queue queue = QueueFactory.getDefaultQueue();
    queue.add(TaskOptions.Builder.withUrl("/rest/student/createstudent").param("perm", perm).param("lastName", lastName).param("firstName", firstName).param("email", email).param("courseID", courseID).param("userId", userId));
    response.sendRedirect("/personal.jsp");
  }

  @POST
  @Path("/createstudent")
  public void createStudent(@Context HttpServletRequest request)
                      throws Exception {
    String perm = request.getParameter("perm");
    String lastName = request.getParameter("lastName");
    String firstName = request.getParameter("firstName");
    String email = request.getParameter("email");
    String courseID = request.getParameter("courseID");
    String userId = request.getParameter("userId");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity student = new Entity("Student", perm);
    student.setProperty("perm", perm);
    student.setProperty("lastName", lastName);
    student.setProperty("firstName", firstName);
    student.setProperty("email", email);
    student.setProperty("courseID", courseID);
    student.setProperty("userId", userId);
    datastore.put(student);
  }






}*/