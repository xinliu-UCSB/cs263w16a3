package cs263w16;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import com.google.appengine.api.taskqueue.*;
import javax.xml.bind.JAXBElement;


//Map this class to /courses route
@Path("/test")
public class CourseTestResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;

  // Return the list of entities to applications
  // get all the courses from Datastore
  //entity type is "CourseTest"
  @Path("/ds")
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public List<CourseTest> getCoursesFromDS() {
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	List<CourseTest> courseList = new ArrayList<>();
	Query q = new Query("CourseTest");
	PreparedQuery pq = datastore.prepare(q);
	for (Entity courseEnt : pq.asIterable()) {
		CourseTest c = new CourseTest();
		c.setCourseID( (String) courseEnt.getProperty("courseID") );
		c.setCourseName((String) courseEnt.getProperty("courseName") );
		c.setInstructorID ( (String)  courseEnt.getProperty("instructorID") );
		courseList.add(c);
	}
	return courseList;
  }


  //Add a new entity to Datastore
  @Path("/ds")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response postCourseToDS(CourseTest course) {
    String courseID = course.getCourseID();
    String courseName = course.getCourseName();
    String instructorID = course.getIntructorID();
//    System.out.println("courseID: " + courseID + ", courseName: " + courseName);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    if(courseID != null) {
	Key entKey = KeyFactory.createKey("CourseTest", courseID);
	try {
		//In order not to affect the running app, cannot update an existing course
		Entity courseEnt = datastore.get(entKey);
	}  catch(EntityNotFoundException e) {
		//if it is not exist, create it and in the datastore
		Entity courseEnt = new Entity("CourseTest", courseID);
        	courseEnt.setProperty("courseID", courseID);
        	 if(courseName != null) courseEnt.setProperty("courseName", courseName);
        	 if(instructorID != null) courseEnt.setProperty("instructorID", instructorID);
		datastore.put(courseEnt);
	} 
    }
    return Response.ok().build();
  }


  // get the course with courseID from Memcache
  @Path("/mc/{courseID}")
  @GET
  @Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public CourseTest getCourseFromMC(@PathParam("courseID") String courseID) {
	
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

	CourseTest course = (CourseTest)memcache.get(courseID);
	return course;
  }

  @Path("/mc")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response postCourseToMC(CourseTest course) {
	String courseID = course.getCourseID();
//	String courseName = course.getCourseName();	
//	String instructorID = course.getIntructorID();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();	
	memcache.put(courseID, course);
	return Response.ok().build();
  }

  @Path("/tq")
  @GET
  @Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML,
			MediaType.APPLICATION_JSON })
  public Response getQueue() {
	Queue queue = QueueFactory.getDefaultQueue();
	return Response.ok("{\"queuename\":\""
				+ queue.fetchStatistics().getQueueName()
				+ "\",\"numoftasks\":\"" + queue.fetchStatistics().getNumTasks() + "\"}").build();
  }


  @Path("/tq")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response postQueue(CourseTest course) {
	String courseID = course.getCourseID();
	String courseName = course.getCourseName();	
	String instructorID = course.getIntructorID();
	if ( courseID == null || courseName == null || instructorID == null ) {
			return Response.ok().build();
	}
	Queue queue = QueueFactory.getDefaultQueue();
	queue.add(TaskOptions.Builder.withUrl("/couseTest/worker")
		.param("courseID", courseID).param("courseName", courseName)
		.param("instructorID", instructorID));	
	return Response.ok().build();
  }

} 