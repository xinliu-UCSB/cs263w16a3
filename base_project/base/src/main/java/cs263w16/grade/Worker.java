// The Worker servlet should be mapped to the "/grade/worker" URL.
package cs263w16.grade;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;


public class Worker extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

        String studentID = req.getParameter("studentID");
        String name = req.getParameter("name");
        int score = Integer.parseInt( req.getParameter("score") );
        String grader = req.getParameter("grader");
	String attribute = req.getParameter("attribute");
	Date date = new Date();
	
        String keyname = studentID + name;

	Grade gd = new Grade(studentID, name, score, grader, date, attribute);


	//create an Entity of kind TaskData and put it into Datastore.
	Entity tne = new Entity("Grade", keyname);
	tne.setProperty("studentID", studentID);
	tne.setProperty("name", name);
	tne.setProperty("score", score);
	tne.setProperty("grader", grader);
	tne.setProperty("date", date);
	tne.setProperty("attribute", attribute);

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	datastore.put(tne);
	syncCache.put(keyname, gd );
	System.err.println( "Stored " + studentID + " / " + name + " in Datastore and Memcache" );
    }
}
