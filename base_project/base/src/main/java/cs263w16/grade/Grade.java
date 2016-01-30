package cs263w16.grade;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.io.*;

@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class Grade implements Serializable {
  private String studentID;
  private String name;
  private int score;
  private String grader;
  private Date date;
  private String attribute;

  public Grade() { }

  public Grade(String studentID, String name, int score, String grader, Date date, String attribute) {
	this.studentID = studentID;
	this.name = name;
	this.score = score;
	this.grader = grader;
	this.date = date;
	this.attribute = attribute;
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

  public int getScore() {
	return score;
  }

  public void setScore(int attribute) {
	this.score = score;
  }

  public String getGrader() {
	return grader;
  }

  public void setGrader(String grader) {
	this.grader = grader;
  }

  public Date getDate() {
	return date;
  }

  public void setDate(Date date) {
	this.date = date;
  }

  public String getAttribute() {
	return attribute;
  }

  public void setAttribute(String attribute) {
	this.attribute = attribute;
  }

  public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("\tstudentID:\t").append(studentID);
	sb.append("\n\tname:\t").append(name);
	sb.append("\n\tscore:\t").append(score);
	sb.append("\n\tgrader:\t").append(grader);
	sb.append("\n\tdate:\t").append(grader);
	sb.append("\n\tattribute:\t").append(attribute);

	return sb.toString();
  }
} 
