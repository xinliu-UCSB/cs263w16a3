<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.*"%>
<%@ page import="com.google.appengine.api.datastore.Query.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="java.io.*"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Query.Filter"%>
<%@ page import="com.google.appengine.api.datastore.Query.FilterPredicate"%>
<%@ page import="com.google.appengine.api.datastore.Query.FilterOperator"%>
<%@ page import="com.google.appengine.api.datastore.Query.CompositeFilter"%>
<%@ page import="com.google.appengine.api.datastore.Query.CompositeFilterOperator"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="java.util.logging.*"%>

<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
    <title>Course Details</title>
    <h1>Course Details</h1>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
  </head>

  <body>
    <%
      String warningMessage = (String) request.getAttribute("warningMessage");
      if(warningMessage != null) {
      pageContext.setAttribute("warningMessage", warningMessage );
    %>
      <p><div class="alert alert-info">
      ${fn:escapeXml(warningMessage)}
      </div></p>
    <%
      }
    %>
  	<%
  	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  	String courseID = request.getParameter("courseID");
  	pageContext.setAttribute("courseID", courseID);
  	Filter propertyFilter = new FilterPredicate("courseID", FilterOperator.EQUAL, courseID);
	try{
		Query q = new Query("Course").setFilter(propertyFilter);
		List<Entity> courses = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		ArrayList<String> instructorID = null;
		for(Entity course : courses){
			String courseName = (String) course.getProperty("courseName");
			pageContext.setAttribute("courseName", courseName);
			instructorID = (ArrayList<String>) course.getProperty("instructorID");

////
      
////
			String courseKeyStr = KeyFactory.keyToString( course.getKey() );
			pageContext.setAttribute("courseKeyStr", courseKeyStr);
		}

		%>
		<form>
      	CourseID:<br> 
      	${fn:escapeXml(courseID)}<br>
      	CourseName:<br> 
      	${fn:escapeXml(courseName)}<br>
    	</form>
    	Instructors:<a href="/addinstructor.jsp?courseID=${fn:escapeXml(courseID)}">Add</a><br>
    	<%
    	if(instructorID.size()!=0){
    	int i = 0;
    	for(String instructor : instructorID){
    		String instructorid = instructor + i;
    		pageContext.setAttribute("instructorid", instructor);
    	%>
    	${fn:escapeXml(instructorid)} <a href="/deleteinstructor?courseID=${fn:escapeXml(courseID)}&instructorID=${fn:escapeXml(instructorid)}">Delete</a><br>
    	<%
    	i++;
    	}
    	}
    	%>
	
	<a href="/grade/list_grade.jsp?courseID=${fn:escapeXml(courseID)}">grades</a><br>
	<a href="/grade/add_batch_grade.jsp?courseID=${fn:escapeXml(courseID)}">add batch grades</a><br>

	<%
	}finally{
	}
  	%>
  	<form action="/studentenqueue" method="post">
  		Add Student Roster<br>
  		<input type="textarea" rows="50" cols="10" name="roster"><br>
  		<input type="submit" value="Submit">
  	</form>
  	<p>Student List</p>
  	<%
  	Filter propertyFilter1 = new FilterPredicate("courseID", FilterOperator.EQUAL, courseID);
	try{
		Query q = new Query("Student").setFilter(propertyFilter1);
		List<Entity> students = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		for(Entity student : students){
			String perm = (String) student.getProperty("perm");
			pageContext.setAttribute("perm", perm);
		%>
		${fn:escapeXml(perm)} <a href="/deletestudent?perm=${fn:escapeXml(perm)}&courseID=${fn:escapeXml(courseID)}">Delete</a><br>
		<%	
		}
	}finally{}
  		%>

  </body>
 </html>