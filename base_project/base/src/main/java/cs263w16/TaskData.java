package cs263w16;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class TaskData implements Serializable{
  private String keyname;
  private String value;
  private Date date;

  //add constructors (default () and (String,String,Date))
  //add getters and setters for all fields
  public TaskData(){
    this.date = new Date();
  }
  public TaskData(String keyname, String value, Date date){
    this.keyname = keyname;
    this.value = value;
    this.date = date;
  }
  public String getKeyname(){
    return keyname;
  }
  public void setKeyname(String keyname){
    this.keyname = keyname;
  }
  public String getValue(){
    return value;
  }
  public void setValue(String value){
    this.value = value;
  }
  public Date getDate(){
    return date;
  }
  public void setDate(Date date){
    this.date = date;
  }
}
