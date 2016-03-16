// The Worker servlet should be mapped to the "/worker" URL.
package cs263w16;

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

public class Worker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService sync = MemcacheServiceFactory.getMemcacheService();
        String key = request.getParameter("keyname");
        String value = request.getParameter("value");
        // Do something with key.
        Entity td = new Entity("TaskData", key);
        td.setProperty("value", value);
        Date date = new Date();
        td.setProperty("date", date);
        datastore.put(td);
        TaskData m1 = new TaskData(key, value, date);
        sync.put(key, m1);
    }
}
