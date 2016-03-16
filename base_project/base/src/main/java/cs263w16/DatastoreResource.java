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
  public List<TaskData> getEntitiesBrowser() {
    //datastore dump -- only do this if there are a small # of entities
    List<TaskData> list = new ArrayList<TaskData>();
    //add your code here
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    Query q = new Query("TaskData");
    PreparedQuery pq = datastore.prepare(q);
    for(Entity result : pq.asIterable()){
      String value1 = (String) result.getProperty("value");
      Date date1 = (Date) result.getProperty("date");
      TaskData ts = new TaskData(result.getKey().getName(),value1, date1);
      list.add(ts);
    }
    return list;
  }

  // Return the list of entities to applications
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public List<TaskData> getEntities() {
    //datastore dump -- only do this if there are a small # of entities
    //same code as above method
    List<TaskData> list = new ArrayList<TaskData>();
    //add your code here
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query q = new Query("TaskData");
    PreparedQuery pq = datastore.prepare(q);
    for(Entity result : pq.asIterable()){
      String value1 = (String) result.getProperty("value");
      Date date1 = (Date) result.getProperty("date");
      TaskData ts = new TaskData(result.getKey().getName(),value1, date1);
      list.add(ts);
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
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    Entity td = new Entity("TaskData", keyname);
    td.setProperty("value", value);
    td.setProperty("date", date);
    datastore.put(td);
    TaskData ts = new TaskData(keyname, value, date);
    syncCache.put(keyname, ts);
    servletResponse.sendRedirect("../done.html");
  }

  //The @PathParam annotation says that keyname can be inserted as parameter after this class's route /ds
  @Path("{keyname}")
  public TaskDataResource getEntity(@PathParam("keyname") String keyname) {
    System.out.println("GETting TaskData for " +keyname);
    return new TaskDataResource(uriInfo, request, keyname);
  }
}
