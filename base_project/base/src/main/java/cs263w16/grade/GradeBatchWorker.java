/*
 * Xin Liu
 * Last modified on Feb 18, 2016
 * The Worker servlet should be mapped to the "/grade/batch_worker" URL.
 * create a list of "Grade" Entities and put them into datastore
 * use a "Course" Entity as the parent
 * may need to check for this course, studentID and name, whether the "Grade" Entity already exists 
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


public class GradeBatchWorker extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        String courseKeyStr = req.getParameter("courseKeyStr");
        String name = req.getParameter("name");
        String instructorID = req.getParameter("instructorID");
        String content = req.getParameter("content");
        String studentID;
        String score;
	String attribute;
	Date date = new Date();

     try {
	Key courseKey = KeyFactory.stringToKey(courseKeyStr);

	if (courseKey == null) {
		System.out.println( "Error in GradeBatchWorker: conrseKey not found" );	
		return;
	}

	String[] lines = content.split(";");
	int i = 0;

	List<Entity> gradeList = new ArrayList<>();

	for(String line : lines) {
		i++;
		String[] tokens = line.split(",");
		if(tokens.length < 2) {
			String errorStr = "ERROR in add_batch_grade: line " + i + " " 
				+ line + " doesn't contain studentID and score.";
			System.out.println(errorStr);
			continue;
		} else if(tokens.length == 2) {
			attribute = "";
		} else {
			attribute = tokens[2].trim();
		}
		studentID = tokens[0].trim();
		score = tokens[1].trim();
		int scoreInt = Integer.parseInt( score );

		//create an Entity of kind TaskData and put it into Datastore.
		// the corresponding course is set as the parent
		Entity tne = new Entity("Grade", courseKey);
		tne.setProperty("studentID", studentID);
		tne.setProperty("name", name);
		tne.setProperty("score", scoreInt);
		tne.setProperty("grader", instructorID);
		tne.setProperty("date", date);
		tne.setProperty("attribute", attribute);
		tne.setProperty("hasNewComment", new Boolean(false) );
		gradeList.add(tne);
	}

	datastore.put(gradeList);
/*	forwardWithWarning(req, resp, "Adding batch Grade has finished for course: " + courseID 
			+ ", grade " + name );
*/        	
	} catch (Exception e) {
		System.out.println( "Exception in running GradeBatchWorker." );		
	}

    }

}
