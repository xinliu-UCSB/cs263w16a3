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

public class InstructorAdd extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//       Transaction t = datastore.beginTransaction();
//        try{
            String userId = request.getParameter("userId");
            String blobKey = request.getParameter("blobKey");
            Filter propertyFilter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
            Query q = new Query("Instructor").setFilter(propertyFilter);
            List<Entity> instructors = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
            if(instructors!=null){
                System.out.println("instructors is not null");
                response.sendRedirect("/welcome.jsp");
            }
            
            for(Entity instructor : instructors){
                instructor.setProperty("blobKey", blobKey);
                datastore.put(instructor);
//                t.commit();
            }
 //       }finally{
 //           if(t.isActive()){
 //               System.out.println("blobKey is not saved");
 //               t.rollback();
 //           }
 //       }
    }
}
