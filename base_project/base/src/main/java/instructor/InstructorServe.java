package instructor;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.*;
import com.google.appengine.api.datastore.Query.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class InstructorServe extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
       String instructorID = request.getParameter("instructorID");
       Key imageKey = KeyFactory.createKey("Instructor", instructorID);
       DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
       Entity instructor = null;
       try{
       		instructor = datastore.get(imageKey);
       }catch(EntityNotFoundException e){
       		e.printStackTrace();
       }
       if(instructor!=null){
       		String blobKey = (String)instructor.getProperty("blobKey");
       		if(blobKey!=null){
       			BlobKey key = new BlobKey(blobKey);
       			blobstore.serve(key, response);
       		}
       }
       else{
       		response.sendRedirect("/instructorpersonal.jsp");
       }
    }
}
