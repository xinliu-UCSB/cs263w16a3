package student;

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
public class Student implements Serializable {
  private String perm;
  private String lastName;
  private String firstName;
  private String email;
  private String courseID;
//  private String userId;

  public Student() { }

  public Student(String perm, String lastName, String firstName, String email, String courseID){
    this.perm = perm;
    this.lastName = lastName;
    this.firstName = firstName;
    this.email = email;
    this.courseID = courseID;
//    this.userId = userId;
  }
  public String getPerm(){
    return perm;
  }
  public void setPerm(String perm){
    this.perm = perm;
  }
  public String getLastName(){
    return lastName;
  }
  public void setLastName(String lastName){
    this.lastName = lastName;
  }
  public String getFirstName(){
    return firstName;
  }
  public void setFirstName(String firstName){
    this.firstName = firstName;
  }
  public String getEmail(){
    return email;
  }
  public void setEmail(String email){
    this.email = email;
  }
  public String getCourseID(){
    return courseID;
  }
  public void setCourseID(String courseID){
    this.courseID = courseID;
  }
/*  public String getUserId(){
    return userId;
  }
  public void setUserId(String userId){
    this.userId = userId;
  }*/
}
