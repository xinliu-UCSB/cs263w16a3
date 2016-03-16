package cs263w16;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import javax.xml.bind.JAXBElement;

import course.Course;

//Map this class to /courses route
@Path("/courses")
public class CoursesResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;


  // Return the list of entities to the user in the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public List<Course> getCoursesBrowser() {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	//MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

	List<Course> courseList = new ArrayList<Course>();
	Query q = new Query("Course");
	PreparedQuery pq = datastore.prepare(q);
	for (Entity courseEnt : pq.asIterable()) {
		Course c = new Course();
		c.setCourseID( (String) courseEnt.getProperty("courseID") );
		c.setCourseName((String) courseEnt.getProperty("courseName") );
		c.setInstructors ( (ArrayList<String>)  courseEnt.getProperty("instructorID") );
		courseList.add(c);
	}
	return courseList;
  }

  // Return the list of entities to applications
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public List<Course> getCourses() {
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	//MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

	List<Course> courseList = new ArrayList<Course>();
	Query q = new Query("Course");
	PreparedQuery pq = datastore.prepare(q);
	for (Entity courseEnt : pq.asIterable()) {
		Course c = new Course();
		c.setCourseID( (String) courseEnt.getProperty("courseID") );
		c.setCourseName((String) courseEnt.getProperty("courseName") );
		c.setInstructors ( (ArrayList<String>)  courseEnt.getProperty("instructorID") );
		courseList.add(c);
	}
	return courseList;
  }

  //Add a new entity to the datastore and memcache
  @POST
  @Produces(MediaType.TEXT_HTML)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public void newTaskData(
      @FormParam("courseID") String courseID,
      @FormParam("courseName") String courseName,
      @FormParam("instructors") String instructors,
      @Context HttpServletResponse servletResponse) throws IOException {
    System.out.println("Posting new Course: " + courseID +" courseName: "+ courseName);

    //add your code here
    CourseResource cr = new CourseResource(uriInfo, request, courseID);

    cr.putCourse(courseName, instructors);

    servletResponse.sendRedirect("../done.html");
  }

  //The @PathParam annotation says that courseID can be inserted as parameter after this class's route /courses
  @Path("{courseID}")
  public CourseResource getCourse(@PathParam("courseID") String courseID) {
    System.out.println("GETting Course for courseID: " +courseID);
    return new CourseResource(uriInfo, request, courseID);
  }
} 