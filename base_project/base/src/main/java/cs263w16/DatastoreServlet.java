package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class DatastoreServlet extends HttpServlet {
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();


  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");

      String keyname = req.getParameter("keyname");
      String value = req.getParameter("value");

      if(keyname == null && value == null) {
	//Display every element of kind TaskData in the datastore
	  resp.getWriter().println("All elements of kind TaskData in Datastore:");
	  Query q = new Query("TaskData");
	  PreparedQuery pq = datastore.prepare(q);
	  List<String> keys = new LinkedList<String>();
	  for (Entity ent : pq.asIterable()) {
	    resp.getWriter().println( "<br />" + ent.getKey().getName() + ": " + ent.getProperty("value") + ": " + ent.getProperty("date") );
		keys.add( ent.getKey().getName() ); 
	  }
	  Map<String, Object> map = syncCache.getAll(keys);
	  Set<String> memKeys = map.keySet();
	  resp.getWriter().println("<br />All elements of kind TaskData in Memcache:");
	  for(String key : memKeys) {
		TaskData mv =(TaskData) map.get(key);
		resp.getWriter().println( "<br />" + key + ": " + mv.getValue() + ": " + mv.getDate() );
	  }
	
      } else if(keyname != null && value == null) {
	//check memcache for the key
	TaskData mv = (TaskData) syncCache.get(keyname);
	Key entKey = KeyFactory.createKey("TaskData", keyname);
	try{	  
	  if(mv != null) {
		resp.getWriter().println(keyname + ": " + mv.getValue()  + ": " + mv.getDate() +" (Both)");
	  } else {
		Entity ent = datastore.get(entKey);
		resp.getWriter().println(keyname + ": " + (String) ent.getProperty("value") + ": " +  (Date) ent.getProperty("Date") + " (DataStore)");
		syncCache.put(keyname, new TaskData( keyname, (String) ent.getProperty("value"), (Date) ent.getProperty("date")) );
	  }
	  //resp.getWriter().println(  "key: " + keyname + " value: " + ent.getProperty("value")  );
	} catch(EntityNotFoundException e) {
	  resp.getWriter().println( "Neither" );
	}
      } else if(keyname != null && value != null) {
	Entity tne = new Entity("TaskData", keyname);
	tne.setProperty("value", value);
	Date date = new Date();
	tne.setProperty( "date", date );
	datastore.put(tne);
	TaskData mv = new TaskData(keyname, value, date);
	syncCache.put(keyname, mv);
	resp.getWriter().println( "Stored " + keyname + " and " + value + " in Datastore and Memcache" );
      } else {
        resp.getWriter().println("ERROR: incorrect parameters have been passed in.");
      }

      resp.getWriter().println("</body></html>");
  }

}
