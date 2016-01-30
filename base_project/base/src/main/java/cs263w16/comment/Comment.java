package cs263w16.comment;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.io.*;

@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class Comment implements Serializable {
  private String gradeKeyname;
  private String studentID;
  private String grader;
  private String name;
  private String content;
  private Date date;


  public Comment() { }

  public Comment(String gradeKeyname, String studentID, String grader, 
		String name, String content, Date date) {
	this.gradeKeyname = gradeKeyname;
	this.studentID = studentID;
	this.grader = grader;
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

  public String getStudentID() {
	return studentID;
  }

  public void setStudentID(String studentID) {
	this.studentID = studentID;
  }

  public String getName() {
	return name;
  }

  public void setName(String name) {
	this.name = name;
  }

  public String getGrader() {
	return grader;
  }

  public void setGrader(String grader) {
	this.grader = grader;
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
	sb.append("\tstudentID:\t").append(studentID);
	sb.append("\n\tgrader:\t").append(grader);
	sb.append("\n\tname:\t").append(name);
	sb.append("\n\tcontent:\t").append(content);
	sb.append("\n\tdate:\t").append(grader);

	return sb.toString();
  }
} 
