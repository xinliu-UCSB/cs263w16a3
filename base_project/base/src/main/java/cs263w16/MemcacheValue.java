package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

public class MemcacheValue implements Serializable{
  private String value;
  private Date date;
  public MemcacheValue(String value, Date date){
    this.value = value;
    this.date = date;
  }
  public String getValue(){
    return value;
  }
  public Date getDate(){
    return date;
  }
}
