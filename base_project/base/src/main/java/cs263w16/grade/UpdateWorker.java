/*
 * Xin Liu
 * created on Mar 1, 2016
 * The Worker servlet should be mapped to the "/grade/update_worker" URL.
 * update the score of an exist grade, record the old score and update reason in "attribute"
 */

package cs263w16.grade;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

import com.google.appengine.api.datastore.EntityNotFoundException;


public class UpdateWorker extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	String gradeKeyname = req.getParameter("gradeKeyname");
        String score = req.getParameter("score");
        String reason = req.getParameter("reason");
        String instructorID = req.getParameter("instructorID");
	Date date = new Date();

     try {
	Key gradeKey = KeyFactory.stringToKey(gradeKeyname);

	if (gradeKey == null) {
		System.out.println( "Error in GradeBatchWorker: gradeKey not found" );	
		return;
	}

	Entity grade = datastore.get(gradeKey);
	int previousScore = ( (Long) grade.getProperty("score") ).intValue(); 
	String attribute = (String) grade.getProperty("attribute") + "preScore: " + previousScore
				+ " preDate: " + (Date) grade.getProperty("date") 
				+ " by grader: " +  (String) grade.getProperty("grader");
	if(reason != null) {
		attribute = attribute + ", updated for reason: " + reason;
	}

	grade.setProperty( "score", Integer.parseInt(score) );
	grade.setProperty( "grader", instructorID);
	grade.setProperty( "date", date);
	grade.setProperty("attribute", attribute);

	datastore.put(grade);
      	
	} catch (Exception e) {
		System.out.println( "Exception in running GradeBatchWorker." );		
	}

    }

}
