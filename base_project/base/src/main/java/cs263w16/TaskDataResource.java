package cs263w16;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import javax.xml.bind.JAXBElement;

public class TaskDataResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String keyname;

  public TaskDataResource(UriInfo uriInfo, Request request, String kname) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.keyname = kname;
  }

  // for the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public TaskData getTaskDataHTML() {
    //add your code here (get Entity from datastore using this.keyname)
    // throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
    //if not found
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Key entKey = KeyFactory.createKey("TaskData", this.keyname);
	try{
	  	Entity ent = datastore.get(entKey);
		TaskData td = new TaskData( this.keyname, (String) ent.getProperty("value"), (Date) ent.getProperty("date") );
		return td;
	} catch(EntityNotFoundException e) {
		throw new RuntimeException("Get: TaskData with " + this.keyname +  " not found");
	}
  }

  // for the application
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public TaskData getTaskData() {
    //same code as above method
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	TaskData td = (TaskData) syncCache.get(this.keyname);
	if(td != null) return td;

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Key entKey = KeyFactory.createKey("TaskData", this.keyname);
	try{
	  	Entity ent = datastore.get(entKey);
		td = new TaskData( this.keyname, (String) ent.getProperty("value"), (Date) ent.getProperty("date") );
		syncCache.put(keyname, td);
		return td;
	} catch(EntityNotFoundException e) {
		throw new RuntimeException("Get: TaskData with " + this.keyname +  " not found");
	}
  }

  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  public Response putTaskData(String value) {
    Response res = null;
    //add your code here
    //first check if the Entity exists in the datastore
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

	Key entKey = KeyFactory.createKey("TaskData", this.keyname);
	Date date = new Date();
	try{
		//if it is, signal that we updated the entity
	  	Entity ent = datastore.get(entKey);
		ent.setProperty("value", value);
		ent.setProperty("date", date );
		res = Response.noContent().build();
	} catch(EntityNotFoundException e) {
		//if it is not, create it and 
    		//signal that we created the entity in the datastore 
		Entity tne = new Entity("TaskData", this.keyname);
		tne.setProperty("value", value);
		tne.setProperty( "date", date );
		datastore.put(tne);
		res = Response.created(uriInfo.getAbsolutePath()).build();
	} 
	TaskData td = new TaskData( this.keyname, value, date);
	syncCache.put(keyname, td);
    return res;
  }

  @DELETE
  public void deleteIt() {

    //delete an entity from the datastore
    //just print a message upon exception (don't throw)
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	syncCache.delete(this.keyname);
	Key entKey = KeyFactory.createKey("TaskData", this.keyname);
	try{
	  	Entity ent = datastore.get(entKey);
		datastore.delete(entKey);
		System.err.println("Delete: TaskData with " + this.keyname +  " deleted in Datastore");
	} catch(EntityNotFoundException e) {
		System.err.println("Delete: TaskData with " + this.keyname +  " not found in Datastore");
    }
  }
} 
