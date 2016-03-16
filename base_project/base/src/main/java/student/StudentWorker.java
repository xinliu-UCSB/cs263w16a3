// The Worker servlet should be mapped to the "/worker" URL.
package student;

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

public class StudentWorker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService sync = MemcacheServiceFactory.getMemcacheService();
        /*String perm = request.getParameter("perm");
        String lastName = request.getParameter("lastName");
        String firstName = request.getParameter("firstName");
        String email = request.getParameter("email");
        String courseID = request.getParameter("courseID");*/

        String courseID = request.getParameter("courseID");
        String wholeInfor = request.getParameter("wholeInfor");
        String[] stuInfor = wholeInfor.split(";");
        for(int i=0;i<stuInfor.length;i++){
            String[] singleInfor = stuInfor[i].split(",");
            String perm = singleInfor[0].trim();
            String lastName = singleInfor[1].trim();
            String firstName = singleInfor[2].trim();
            String email = singleInfor[3].trim();
            //String courseID = singleInfor[4].trim();

            //add roster in course entity
            Filter propertyFilter1 = new FilterPredicate("courseID", FilterOperator.EQUAL, courseID);
            Query q1 = new Query("Course").setFilter(propertyFilter1);
            List<Entity> courses = datastore.prepare(q1).asList(FetchOptions.Builder.withDefaults());
            for(Entity course : courses){
                ArrayList<String> roster = (ArrayList<String>) course.getProperty("roster");
                List<String> newRoster = new ArrayList<String>();
            
            
                if(roster==null){
                    roster = new ArrayList<String>();
                    roster.add(perm);
                    course.setProperty("roster", roster);
                    datastore.put(course);
                }
                else{
                    roster.add(perm);
                    course.setProperty("roster", roster);
                    datastore.put(course);
                }
            }

            //create new student entity
            Filter propertyFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
            Query q = new Query("Student").setFilter(propertyFilter);
            List<Entity> students = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
            if(students.size()!=0){
                for(Entity student : students){
                    ArrayList<String> courseIDs = (ArrayList<String>) student.getProperty("courseID");
                    ArrayList<String> courseids = new ArrayList<String>();
                    if(courseIDs.size()!=0){
                        for(String courseid : courseIDs){
                            if(!courseid.equals(courseID))
                                courseids.add(courseid);
                        }
                    }
                    courseids.add(courseID);
                    student.setProperty("courseID", courseids);
                    datastore.put(student);
                }
            }
            else{
    //        String userId = request.getParameter("userId");
            // Do something with key.
                Entity student = new Entity("Student", perm);
                student.setProperty("perm", perm);
                student.setProperty("lastName", lastName);
                student.setProperty("firstName", firstName);
                student.setProperty("email", email);
                ArrayList<String> courseIDs = new ArrayList<String>();
                courseIDs.add(courseID);
                student.setProperty("courseID", courseIDs);
        //        student.setProperty("userId", userId);
                datastore.put(student);
            }
        }
        
        /*Filter propertyFilter1 = new FilterPredicate("courseID", FilterOperator.EQUAL, courseID);
        Query q1 = new Query("Course").setFilter(propertyFilter1);
        List<Entity> courses = datastore.prepare(q1).asList(FetchOptions.Builder.withDefaults());
        for(Entity course : courses){
            ArrayList<String> roster = (ArrayList<String>) course.getProperty("roster");
            List<String> newRoster = new ArrayList<String>();
            
            
            if(roster==null){
                roster = new ArrayList<String>();
                roster.add(perm);
                course.setProperty("roster", roster);
                datastore.put(course);
            }
            else{
                roster.add(perm);
                course.setProperty("roster", roster);
                datastore.put(course);
            }
        }*/

        /*Filter propertyFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
        Query q = new Query("Student").setFilter(propertyFilter);
        List<Entity> students = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        if(students.size()!=0){
            for(Entity student : students){
                ArrayList<String> courseIDs = (ArrayList<String>) student.getProperty("courseID");
                ArrayList<String> courseids = new ArrayList<String>();
                if(courseIDs.size()!=0){
                    for(String courseid : courseIDs){
                        if(!courseid.equals(courseID))
                            courseids.add(courseid);
                    }
                }
                courseids.add(courseID);
                student.setProperty("courseID", courseids);
                datastore.put(student);
            }
        }
        else{
    //        String userId = request.getParameter("userId");
            // Do something with key.
            Entity student = new Entity("Student", perm);
            student.setProperty("perm", perm);
            student.setProperty("lastName", lastName);
            student.setProperty("firstName", firstName);
            student.setProperty("email", email);
            ArrayList<String> courseIDs = new ArrayList<String>();
            courseIDs.add(courseID);
            student.setProperty("courseID", courseIDs);
    //        student.setProperty("userId", userId);
            datastore.put(student);
        }*/
    }
}
