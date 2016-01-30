// The Worker servlet should be mapped to the "/grade/worker" URL.
package cs263w16.comment;

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

        String gradeKeyname = req.getParameter("gradeKeyname");
        String studentID = req.getParameter("studentID");
        String grader = req.getParameter("grader");
        String name = req.getParameter("name");
        String content = req.getParameter("content");
	Date date = new Date();
	
        String keyname = gradeKeyname + name;

	Comment gd = new Comment(gradeKeyname, studentID, grader, name, content, date);


	//create an Entity of kind TaskData and put it into Datastore.
	Entity tne = new Entity("Comment", keyname);
	tne.setProperty("gradeKeyname", gradeKeyname);
	tne.setProperty("studentID", studentID);
	tne.setProperty("grader", grader);
	tne.setProperty("name", name);
	tne.setProperty("content", content);
	tne.setProperty("date", date);

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	datastore.put(tne);
	syncCache.put(keyname, gd );
	System.err.println( "Stored " + gradeKeyname + " / " + name + " in Datastore and Memcache" );
    }
}
