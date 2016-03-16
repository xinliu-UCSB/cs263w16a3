//The servlet should be mapped to the "/grade_comment" URL.

package cs263w16.grade;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


//import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class CommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");

	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

/*	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	if (user == null) {
		System.err.println( "Error in CommentsServlet: User has not logged in, but try to view comment" );
		resp.sendRedirect("/welcome.jsp");
	}
*/
        String gradeKeyname = req.getParameter("gradeKeyname");
	String courseID = req.getParameter("courseID");
	Key gradeKey = null;

    try{
	try{
		gradeKey = KeyFactory.stringToKey(gradeKeyname);
	} catch(Exception e) {
		System.out.println( "Error in CommentsServlet: gradeKeyname to key exception - " + e.getMessage() );
		forwardTo( "/grade/grade_comment.jsp", req, resp, gradeKeyname, "warningMessage", "Grade not found!");

	}

	if (gradeKey == null) {
		forwardTo("/grade/grade_comment.jsp", req, resp, gradeKeyname, "warningMessage", "Grade not found!");
		return;
	}

	//check whether current user is the student for this grade or one of the instructors of the course
	// will be added

	Query q = new Query("Comment").setAncestor(gradeKey)
				.addSort("date", SortDirection.ASCENDING);
		
	PreparedQuery pq = datastore.prepare(q);

	List<Comment> commentList = new ArrayList<>();

	String userID; // the user who write this comment
	String userType; //student or instructor
	String name;
	String content;
	Date date;

	for (Entity ent : pq.asIterable()) {
		userID = (String) ent.getProperty("userID"); 
		userType = (String) ent.getProperty("userType"); 		
		name = (String) ent.getProperty("name"); 
		content = (String) ent.getProperty("content");
		date= (Date) ent.getProperty("date"); 

		Comment c = new Comment (gradeKeyname, userID, userType, name, content, date);
		commentList.add(c);
	}

	Entity grade = datastore.get(gradeKey);
	Grade g = new Grade (gradeKeyname, (String) grade.getProperty("studentID"), (String) grade.getProperty("name"),
		 ( (Long) grade.getProperty("score") ).intValue(),  (String) grade.getProperty("grader"),  
		 (Date) grade.getProperty("date"),  (String) grade.getProperty("attribute") );


//calculte the percentile
	int thisScore = ( (Long) grade.getProperty("score") ).intValue();
	String gradeName = (String) grade.getProperty("name");
	Filter gradeNameFilter = new FilterPredicate("name", FilterOperator.EQUAL, gradeName);
	Key parentCourseKey = grade.getParent();
	Query gradeQuery = new Query("Grade").setAncestor(parentCourseKey)
				.setFilter(gradeNameFilter);
	PreparedQuery gradePQ = datastore.prepare(gradeQuery);

	ArrayList<Integer> scoreList = new ArrayList<>();
	for (Entity ent : gradePQ.asIterable()) {
		scoreList.add( ( (Long) ent.getProperty("score") ).intValue() );
	}
	int scoreSize = scoreList.size();
	Integer[] scoreArray = new Integer[scoreSize];
	scoreArray = scoreList.toArray(scoreArray);
	Arrays.sort(scoreArray);
	int median = (int) ( ( scoreArray[ (int) ((scoreSize-1) / 2) ] + scoreArray[ (int) (scoreSize / 2) ] ) / 2 );

	int index = scoreSize - 1;
	while ( thisScore < scoreArray[index] ) {index--;}
	index++;
	int percentile = (int) ( (index * 100) / scoreSize );
	System.out.println("CommentsServlet forward with grade and commentList.");
	 forwardTo( "/grade/grade_comment.jsp", req, resp, gradeKeyname, courseID, g, 
			commentList, Integer.toString(median), Integer.toString(percentile) );

	forwardTo( "/grade/grade_comment.jsp", req, resp, gradeKeyname, courseID, g, 
			commentList);	
      } catch(Exception exp) {
		System.out.println( "Error in CommentsServlet: gradeKeyname to key exception - " + exp.getMessage() );
      }

    }

    private void forwardTo (String nextJSP, HttpServletRequest req, HttpServletResponse resp, 
			String gradeKeyname, String attributeName, String attributeVal)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("gradeKeyname", gradeKeyname);
        req.setAttribute(attributeName, attributeVal);
        dispatcher.forward(req, resp);
    } 


    private void forwardTo (String nextJSP, HttpServletRequest req, HttpServletResponse resp, 
			String gradeKeyname, String courseID, Grade grade, List<Comment> commentList)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("gradeKeyname", gradeKeyname);
        req.setAttribute("courseID", courseID);
        req.setAttribute("grade", grade);
        req.setAttribute("commentList", commentList);
        dispatcher.forward(req, resp);
    } 

    private void forwardTo (String nextJSP, HttpServletRequest req, HttpServletResponse resp, 
			String gradeKeyname, String courseID, Grade grade, List<Comment> commentList,
			String median, String percentile)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("gradeKeyname", gradeKeyname);
        req.setAttribute("courseID", courseID);
        req.setAttribute("grade", grade);
        req.setAttribute("commentList", commentList);
        req.setAttribute("median", median);
        req.setAttribute("percentile", percentile);
        dispatcher.forward(req, resp);
    } 


/*      if(keyname == null) {
	//Display every element of kind Grade in the datastore
	  resp.getWriter().println("All elements of kind Comment in Datastore:");
	  Query q = new Query("Comment");
	  PreparedQuery pq = datastore.prepare(q);
	  List<String> keys = new LinkedList<String>();
	  for (Entity ent : pq.asIterable()) {
	    resp.getWriter().println( 
		"<br />gradeKeyname:\t" + ent.getProperty("gradeKeyname") 
		+ "<br />userID:\t" + ent.getProperty("userID") 
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
		+ "<br />userID:\t" + ent.getProperty("userID") 
		+ "<br />name:\t" + ent.getProperty("name") 
		+ "<br />content:\t" + ent.getProperty("content") 
		+ "<br />date:\t" + ent.getProperty("date") );

		syncCache.put( keyname, new Comment ( (String) ent.getProperty("gradeKeyname"), 
			(String) ent.getProperty("userID"),
			(String) ent.getProperty("name") , (String) ent.getProperty("content"), 
			(Date) ent.getProperty("date") ) );
	  }
	} catch(EntityNotFoundException e) {
	  resp.getWriter().println( "Neither" );
	}
      } 

      resp.getWriter().println("</body></html>");
  }
*/


}
