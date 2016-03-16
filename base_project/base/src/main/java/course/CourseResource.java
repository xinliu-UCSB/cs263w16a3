package cs263w16;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import javax.xml.bind.JAXBElement;

import course.Course;

public class CourseResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String courseID;

  public CourseResource(UriInfo uriInfo, Request request, String courseID) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.courseID = courseID;
  }

  // for the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public Course getCourseHTML() {
    //get course Entity from memcache, if not found, get it from datastore using this.courseID
    // throw new RuntimeException("Get: course " + courseID +  " not found");
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

	//try to get course from memcache
	Course course = (Course)memcache.get("courseID:" + courseID);
	if(course != null) return course;

	//try to get course from datastore
	try{
		Key entKey = KeyFactory.createKey("Course", courseID);
		Entity courseEnt = datastore.get(entKey);
		course = new Course();
		course.setCourseID( (String) courseEnt.getProperty("courseID") );
		course.setCourseName((String) courseEnt.getProperty("courseName") );
		course.setInstructors ( (ArrayList<String>)  courseEnt.getProperty("instructorID") );
		//put the course into memcache
		memcache.put("courseID:" + courseID, course);
		return course;
	} catch (Exception e) {
		throw new RuntimeException("Get: course with " + this.courseID +  " not found");
	}
  }

  // for the application
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Course getCourse() {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    //get course Entity from memcache, if not found, get it from datastore using this.courseID
    // throw new RuntimeException("Get: course " + courseID +  " not found");
    //if not found

	//try to get course from memcache
	Course course = (Course)memcache.get("courseID:" + courseID);
	if(course != null) return course;

	//try to get course from datastore
	try{
		Key entKey = KeyFactory.createKey("Course", courseID);
		Entity courseEnt = datastore.get(entKey);
		course = new Course();
		course.setCourseID( (String) courseEnt.getProperty("courseID") );
		course.setCourseName((String) courseEnt.getProperty("courseName") );
		course.setInstructors ( (ArrayList<String>)  courseEnt.getProperty("instructorID") );
		//put the course into memcache
		memcache.put("courseID:" + courseID, course);
		return course;
	} catch (Exception e) {
		throw new RuntimeException("Get: course with " + this.courseID +  " not found");
	}	
  }

  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  public Response putCourse(
	@FormParam("courseName") String courseName, 
	@FormParam("instructors") String instructors) {

	Response res = null;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

	Course course = new Course(courseID, courseName);
	ArrayList<String> instructorList = new ArrayList<String>();
	String[] ins = instructors.split(",");
	for(String i: ins) {
		i = i.trim();
		if(i.length() > 0)
			instructorList.add(i);
	}
	course.setInstructors(instructorList);
	memcache.put("courseID:" + courseID, course);

	Key entKey = KeyFactory.createKey("Course", courseID);
	try {
		//if it is, signal that we updated the entity
		Entity courseEnt = datastore.get(entKey);
        	courseEnt.setProperty("courseID", courseID);
        	courseEnt.setProperty("courseName", courseName);
        	courseEnt.setProperty("instructorID", instructorList);
		datastore.put(courseEnt);
		res = Response.noContent().build();
	}  catch(EntityNotFoundException e) {
		//if it is not, create it and 
    		//signal that we created the entity in the datastore 
		Entity courseEnt = new Entity("Course", courseID);
        	courseEnt.setProperty("courseID", courseID);
        	courseEnt.setProperty("courseName", courseName);
        	courseEnt.setProperty("instructorID", instructorList);
		datastore.put(courseEnt);
		res = Response.created(uriInfo.getAbsolutePath()).build();
	} 
	return res;
  }



} 