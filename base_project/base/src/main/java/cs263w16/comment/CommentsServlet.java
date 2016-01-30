package cs263w16.comment;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class CommentsServlet extends HttpServlet {
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();


  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");

      String keyname = req.getParameter("keyname");

      if(keyname == null) {
	//Display every element of kind Grade in the datastore
	  resp.getWriter().println("All elements of kind Comment in Datastore:");
	  Query q = new Query("Comment");
	  PreparedQuery pq = datastore.prepare(q);
	  List<String> keys = new LinkedList<String>();
	  for (Entity ent : pq.asIterable()) {
	    resp.getWriter().println( 
		"<br />gradeKeyname:\t" + ent.getProperty("gradeKeyname") 
		+ "<br />studnetID:\t" + ent.getProperty("studentID") 
		+ "<br />grader:\t" + ent.getProperty("grader") 
		+ "<br />name:\t" + ent.getProperty("name") 
		+ "<br />content:\t" + ent.getProperty("content") ); 
		keys.add( ent.getKey().getName() ); 
	  }
	  Map<String, Object> map = syncCache.getAll(keys);
	  Set<String> memKeys = map.keySet();
	  resp.getWriter().println("<br />All elements of kind Comment in Memcache:");
	  for(String key : memKeys) {
		Comment mv = (Comment) map.get(key);
		resp.getWriter().println( "<br />" + key + ": " + mv.toString() );
	  }
	
      } else {
	//check memcache for the keyname
	Comment mv = (Comment) syncCache.get(keyname);
	try{	  
	  if(mv != null) {
		resp.getWriter().println(keyname + ": " + mv.toString() +" (Both)");
	  } else {
		Key entKey = KeyFactory.createKey("Comment", keyname);
		Entity ent = datastore.get(entKey);
		resp.getWriter().println(
		"<br />gradeKeyname:\t" + ent.getProperty("gradeKeyname") 
		+ "<br />studnetID:\t" + ent.getProperty("studentID") 
		+ "<br />grader:\t" + ent.getProperty("grader") 
		+ "<br />name:\t" + ent.getProperty("name") 
		+ "<br />content:\t" + ent.getProperty("content") 
		+ "<br />date:\t" + ent.getProperty("date") );

		syncCache.put( keyname, new Comment ( (String) ent.getProperty("gradeKeyname"), 
			(String) ent.getProperty("studentID"), (String) ent.getProperty("grader"), 
			(String) ent.getProperty("name") , (String) ent.getProperty("content"), 
			(Date) ent.getProperty("date") ) );
	  }
	} catch(EntityNotFoundException e) {
	  resp.getWriter().println( "Neither" );
	}
      } 

      resp.getWriter().println("</body></html>");
  }

}
