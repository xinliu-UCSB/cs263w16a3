package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class DatastoreServlet extends HttpServlet {
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
  public DatastoreServlet(){
    String keyname = "aaa";
    Entity td1 = new Entity("TaskData", keyname);
    td1.setProperty("value", "bbb");
    Date date = new Date();
    td1.setProperty("date", date);
    datastore.put(td1);
    TaskData ts = new TaskData(keyname,"bbb", date);
    syncCache.put(keyname, ts);
  }
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");
//      resp.getWriter().println("<h2>Hello World</h2>"); //remove this line
      syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
      Enumeration<String> en = req.getParameterNames();
      boolean flag = false;
      while(en.hasMoreElements()){
        String test = en.nextElement();
        if(!test.equals("keyname")&&!test.equals("value")){
          resp.getWriter().println("IllegalArgument passed in! " + ".<br>");
          flag = true;
        }
      }
      if(!flag){
        String keyname = req.getParameter("keyname");
        String value = req.getParameter("value");
        if(keyname==null&&value==null){

  //        List<String> keynames = (List<String>) syncCache.get(Constants.MESSAGE_CACHE_KEY);
          Query q = new Query("TaskData");
          PreparedQuery pq = datastore.prepare(q);
          for(Entity result : pq.asIterable()){
            String value1 = (String) result.getProperty("value");
            Date date1 = (Date) result.getProperty("date");
            resp.getWriter().println("Datastore: " + result.getKey() + ", " + value1 + ", " + date1 + ".<br>");
            if(syncCache.get(result.getKey().getName())!=null){
              TaskData ts1 = (TaskData) syncCache.get(result.getKey().getName());
              resp.getWriter().println("Memcache: " + result.getKey() + ts1.getValue() + ", " + ts1.getDate() + ".<br>");
            }
          }
          // if(keynames!=null){
          //   resp.getWriter().println("Memcache:");
          //   for(String str : keynames){
          //     resp.getWriter().println(str);
          //   }
          // }

        }

        if(keyname!=null&&value==null){
          Key entKey = KeyFactory.createKey("TaskData", keyname);
          try{
              Entity taskdata = datastore.get(entKey);
              resp.getWriter().println(taskdata.getKey() + ", " + taskdata.getProperty("value")+".<br>");
          }catch(EntityNotFoundException e){
            resp.getWriter().println("entity not found <br>");
          }
          try{
              if(syncCache.get(keyname)!=null&&datastore.get(entKey)!=null){
                resp.getWriter().println("Both <br>");
              }
              else if(syncCache.get(keyname)==null&&datastore.get(entKey)!=null){
                resp.getWriter().println("Datastore <br>");
                TaskData mv1 = new TaskData(keyname, (String)datastore.get(entKey).getProperty("value"), (Date)datastore.get(entKey).getProperty("date"));
                syncCache.put(keyname, mv1);
              }
              else{
                resp.getWriter().println("Neither <br>");
              }
            }catch(EntityNotFoundException e){
                resp.getWriter().println("entity not found <br>");
            }
        }

        if(keyname!=null&&value!=null){
          Entity taskdata = new Entity("TaskData", keyname);
          taskdata.setProperty("value", value);
          Date date = new Date();
          taskdata.setProperty("date", date);
          datastore.put(taskdata);
          resp.getWriter().println("Stored " + taskdata.getKey() + "and " + taskdata.getProperty("value") + " in Datastore <br>");
          TaskData mv1 = new TaskData(keyname, value, date);
          syncCache.put(keyname, mv1);
          resp.getWriter().println("Stored " + taskdata.getKey() + "and " + taskdata.getProperty("value") + " in Memcache <br>");
        }

        //Add your code here

        resp.getWriter().println("</body></html>");
    }
  }
}
