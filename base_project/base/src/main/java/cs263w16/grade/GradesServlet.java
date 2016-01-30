package cs263w16.grade;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class GradesServlet extends HttpServlet {
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();


  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");

/*        String studentID = req.getParameter("studentID");
        String name = req.getParameter("studentID");
        int score = Integer.parseInt( req.getParameter("score") );
        String grader = req.getParameter("grader");
*/
//        String keyname = studentID + name;
      String keyname = req.getParameter("keyname");
      PrintWriter out = resp.getWriter();

      if(keyname == null) {
	//Display every element of kind Grade in the datastore
	  resp.getWriter().println("All elements of kind Grade in Datastore:");
	  Query q = new Query("Grade");
	  PreparedQuery pq = datastore.prepare(q);
	  List<String> keys = new LinkedList<String>();
	  for (Entity ent : pq.asIterable()) {
	    out.println( 
		"<br />studnetID:\t" + ent.getProperty("studentID") 
		+ "<br />name:\t" + ent.getProperty("name") 
		+ "<br />grader:\t" + ent.getProperty("grader") 
		+ "<br />date:\t" + ent.getProperty("date") 
		+ "<br />attribute:\t" + ent.getProperty("attribute") );
		keys.add( ent.getKey().getName() ); 
	  }
	  Map<String, Object> map = syncCache.getAll(keys);
	  Set<String> memKeys = map.keySet();
	  resp.getWriter().println("<br />All elements of kind Grade in Memcache:");
	  for(String key : memKeys) {
		Grade mv = (Grade) map.get(key);
		resp.getWriter().println( "<br />" + key + ": " + mv.toString() );
	  }
	
      } else {
	//check memcache for the keyname
	Grade mv = (Grade) syncCache.get(keyname);
	Key entKey = KeyFactory.createKey("Grade", keyname);
	try{	  
	  if(mv != null) {
		out.println(keyname + ": " + mv.toString() +" (Both)");
	  } else {
		Entity ent = datastore.get(entKey);
		out.println(
		"<br />studnetID:\t" + ent.getProperty("studentID") 
		+ "<br />name:\t" + ent.getProperty("name") 
		+ "<br />score:\t" + ent.getProperty("score") 
		+ "<br />grader:\t" + ent.getProperty("grader") 
		+ "<br />date:\t" + ent.getProperty("date") 
		+ "<br />attribute:\t" + ent.getProperty("attribute")  + " (DataStore)");

		syncCache.put(keyname, new Grade( (String) ent.getProperty("studentID"), (String) ent.getProperty("name"), 
			(int) ent.getProperty("score") , (String) ent.getProperty("grader"), 
			(Date)ent.getProperty("date"), (String) ent.getProperty("attribute") ) );
	  }
	} catch(EntityNotFoundException e) {
	  resp.getWriter().println( "Neither" );
	}
      } 

      resp.getWriter().println("</body></html>");
  }

}
