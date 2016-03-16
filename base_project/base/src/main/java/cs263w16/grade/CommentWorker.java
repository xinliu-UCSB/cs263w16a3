// The Worker servlet should be mapped to the "/comment/worker" URL.
package cs263w16.grade;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;



public class CommentWorker extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

        String gradeKeyname = req.getParameter("gradeKeyname");
        String userID = req.getParameter("userID");
        String userType = req.getParameter("userType");
        String name = req.getParameter("name");
        String content = req.getParameter("content");
	Date date = new Date();
	
        String keyname = gradeKeyname + name;

	try{
		Key gradeKey = KeyFactory.stringToKey(gradeKeyname);
		Comment gd = new Comment(gradeKeyname, userID, userType, name, content, date);


		//create an Entity of kind TaskData and put it into Datastore.
		Entity tne = new Entity("Comment", gradeKey);
		tne.setProperty("userID", userID);
		tne.setProperty("userType", userType);
		tne.setProperty("name", name);
		tne.setProperty("content", content);
		tne.setProperty("date", date);


		datastore.put(tne);
		syncCache.put(keyname, gd );
		System.out.println( "Stored " + gradeKeyname + " / " + name + " in Datastore and Memcache" );

		//change the grade property hasNewComment
		Entity grade = datastore.get(gradeKey);
		if( userType.equals( "student" ) ) {
			grade.setProperty("hasNewComment", new Boolean(true) );
		} else {
			grade.setProperty("hasNewComment", new Boolean(false));
		}
		datastore.put(grade);

	} catch(Exception e) {
		System.out.println( "Error in CommentWorker: The grade is not found." );
		return;
	 }
    }
}
