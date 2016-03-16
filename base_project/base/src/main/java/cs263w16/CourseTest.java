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
public class CourseTest implements Serializable {
  private String courseID;
  private String courseName;
  private String instructorID;
  public CourseTest(){

  }
  public CourseTest(String courseID, String courseName){
    this.courseID = courseID;
    this.courseName = courseName;
  }
  public String getCourseID(){
    return courseID;
  }
  public void setCourseID(String courseID){
    this.courseID = courseID;
  }
  public String getCourseName(){
    return courseName;
  }
  public void setCourseName(String courseName){
    this.courseName = courseName;
  }
  public String getIntructorID(){
    return instructorID;
  }
  public void setInstructorID(String instructorID){
    this.instructorID = instructorID;
  }

}