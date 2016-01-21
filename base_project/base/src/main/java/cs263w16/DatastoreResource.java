package cs263w16;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

//Map this class to /ds route
@Path("/ds")
public class DatastoreResource {
  // Allows to insert contextual objects into the class,
  // e.g. ServletContext, Request, Response, UriInfo
  @Context
  UriInfo uriInfo;
  @Context
  Request request;

  // Return the list of entities to the user in the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public List<TaskData> getDatastoreBrowser() {
    //datastore dump -- only do this if there are a small # of entities
    List<TaskData> list = new ArrayList<TaskData>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query q = new Query("TaskData");
    PreparedQuery pq = datastore.prepare(q);
    for (Entity ent : pq.asIterable()) {
	list.add( new TaskData( ent.getKey().getName(), (String) ent.getProperty("value"), (Date) ent.getProperty("date") ) );
    }
    return list;
  }

  // Return the list of entities to applications
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public List<TaskData> getDatastore() {
    //datastore dump -- only do this if there are a small # of entities
    //same code as above method
    List<TaskData> list = new ArrayList<TaskData>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query q = new Query("TaskData");
    PreparedQuery pq = datastore.prepare(q);
    for (Entity ent : pq.asIterable()) {
	list.add( new TaskData( ent.getKey().getName(), (String) ent.getProperty("value"), (Date) ent.getProperty("date") ) );
    }
    return list;
  }

  //Add a new entity to the datastore
  @POST
  @Produces(MediaType.TEXT_HTML)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public void newTaskData(@FormParam("keyname") String keyname,
      @FormParam("value") String value,
      @Context HttpServletResponse servletResponse) throws IOException {
    Date date = new Date();
    System.out.println("Posting new TaskData: " +keyname+" val: "+value+" ts: "+date);

    //add your code here
    TaskDataResource tdr = new TaskDataResource(uriInfo, request, keyname);

    tdr.putTaskData(value);

    servletResponse.sendRedirect("../done.html");
  }

  //The @PathParam annotation says that keyname can be inserted as parameter after this class's route /ds
  @Path("{keyname}")
  public TaskDataResource getTaskData(@PathParam("keyname") String keyname) {
    System.out.println("GETting TaskData for " +keyname);
    return new TaskDataResource(uriInfo, request, keyname);
  }
}

