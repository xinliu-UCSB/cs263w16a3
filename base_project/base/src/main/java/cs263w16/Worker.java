// The Worker servlet should be mapped to the "/worker" URL.
package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;


public class Worker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyname = request.getParameter("keyname");
	String value = request.getParameter("value");

	//create an Entity of kind TaskData and put it into Datastore.
	Entity tne = new Entity("TaskData", keyname);
	tne.setProperty("value", value);
	Date date = new Date();
	tne.setProperty( "date", date );
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	datastore.put(tne);
	syncCache.put(keyname, value);
	System.err.println( "Stored " + keyname + " and " + value + " in Datastore and Memcache" );
    }
}
