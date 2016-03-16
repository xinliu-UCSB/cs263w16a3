/*
 * Xin Liu
 * Last modified on Feb 12, 2016
 * 
 */

package cs263w16.grade;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.io.*;

// @XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class Grade implements Serializable {

  //the key of the corresponding Grade Entity in datastore to string
  private String gradeKeyStr;

  private String studentID;
  private String name;
  private int score;
  private String grader;
  private Date date;
  private String attribute;

  // whether the last comment is from the student, and not read by the instructor
  private boolean hasNewComment = false;

  // the comment for this grade:
  //private List<Comment> commentList;

  public Grade() { }

  public Grade(String gradeKeyStr, String studentID, String name, int score, String grader, Date date, String attribute) {

	this.gradeKeyStr = gradeKeyStr;

	this.studentID = studentID;
	this.name = name;
	this.score = score;
	this.grader = grader;
	this.date = date;
	this.attribute = attribute;
  }

  public String getGradeKeyStr() {
	return gradeKeyStr;
  }

  public void setGradeKeyStr(String gradeKeyStr) {
	this.gradeKeyStr = gradeKeyStr;
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

  public boolean getHasNewComment() {
	return hasNewComment;
  }

  public void setHasNewComment(boolean hasNewComment) {
	this.hasNewComment = hasNewComment;
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
