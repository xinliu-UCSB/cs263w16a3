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

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");

      String keyname = req.getParameter("key");
      String value = req.getParameter("value");

      if(keyname == null && value == null) {
	//Display every element of kind TaskData in the datastore
	  resp.getWriter().println("All elements of kind TaskData:");
	  Query q = new Query("TaskData");
	  PreparedQuery pq = datastore.prepare(q);
	  for (Entity ent : pq.asIterable()) {
	    resp.getWriter().println( "<br />key: " + ent.getKey().getName() + " value: " + ent.getProperty("value") );
	  }
	
      } else if(keyname != null && value == null) {
	Key entKey = KeyFactory.createKey("TaskData", keyname);
	try{
	  Entity ent = datastore.get(entKey);
	  resp.getWriter().println(  "key: " + keyname + " value: " + ent.getProperty("value")  );
	} catch(EntityNotFoundException e) {
	  resp.getWriter().println( "cannot find " + keyname );
	}
      } else if(keyname != null && value != null) {
	Entity tne = new Entity("TaskData", keyname);
	tne.setProperty("value", value);
	Date date = new Date();
	tne.setProperty( "createDate", date );
	datastore.put(tne);
	resp.getWriter().println( "Stored " + keyname + " and " + value + " in Datastore" );
      } else {
        resp.getWriter().println("ERROR: incorrect parameters have been passed in.");
      }

      resp.getWriter().println("</body></html>");
  }


}
