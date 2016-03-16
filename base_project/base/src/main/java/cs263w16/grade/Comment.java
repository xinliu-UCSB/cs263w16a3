package cs263w16.grade;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.io.*;

@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class Comment implements Serializable {
  private String gradeKeyname; //keyToString(key of the grade)
  private String userID; // the user who write this comment
  private String userType; //student or instructor
  private String name;
  private String content;
  private Date date;


  public Comment() { }

  public Comment(String gradeKeyname, String userID, String userType,
		String name, String content, Date date) {
	this.gradeKeyname = gradeKeyname;
	this.userID = userID;
	this.userType = userType;	
	this.name = name;
	this.content = content;
	this.date = date;
  }

  public String getGradeKeyname() {
	return gradeKeyname;
  }

  public void setGradeKeyname(String gradeKeyname) {
	this.gradeKeyname = gradeKeyname;
  }

  public String getUserID() {
	return userID;
  }

  public void setUserID(String userID) {
	this.userID = userID;
  }

  public String getUserType() {
	return userType;
  }

  public void setUserType(String userType) {
	this.userType = userType;
  }
  public String getName() {
	return name;
  }

  public void setName(String name) {
	this.name = name;
  }

  public String getContent() {
	return content;
  }

  public void setContent(String content) {
	this.content = content;
  }

  public Date getDate() {
	return date;
  }

  public void setDate(Date date) {
	this.date = date;
  }

  public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("\tgradeKeyname:\t").append(gradeKeyname);
	sb.append("\tuserID:\t").append(userID);
	sb.append("\n\tname:\t").append(name);
	sb.append("\n\tcontent:\t").append(content);
	sb.append("\n\tdate:\t").append(date);

	return sb.toString();
  }
} 
