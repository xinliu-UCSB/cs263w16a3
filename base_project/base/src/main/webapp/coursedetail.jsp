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
      <link rel="stylesheet" href="./css/bootstrap.min.css"/>         
       <script src="./js/bootstrap.min.js"></script>
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
    <title>Course Details</title>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
  </head>

  <body>
    <div class="container">

    <h1>Course Details</h1>
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
      	<h2>CourseID:  ${fn:escapeXml(courseID)}</h2>
      	CourseName:  ${fn:escapeXml(courseName)}<br>
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
		studentID, Last Name, First-Middle Nmae, email; (Please use ';' to seperate students, use ',' to seperate properties):<br>
  		<input type="hidden" name="courseID" value=${fn:escapeXml(courseID)}>
  		<input type="textarea" style="width: 400px; height: 50px" name="roster"><br>
  		<input type="submit" value="Submit">
  	</form>
  	<h2>Student List</h2>
        <id="StudentList" role="form" >
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                    <td>perm</td>
                                    <td>Last Name</td>
                                    <td>First-Middle Name</td>
                                    <td>Email</td>  
                                    <td></td>                               
                                </tr>
                            </thead>
  	<%
  	Filter propertyFilter1 = new FilterPredicate("courseID", FilterOperator.EQUAL, courseID);
	try{
		Query q = new Query("Student").setFilter(propertyFilter1).addSort("perm", SortDirection.ASCENDING);
		List<Entity> students = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		for(Entity student : students){
			String perm = (String) student.getProperty("perm");
			pageContext.setAttribute("perm", perm);
			String lastName = (String) student.getProperty("lastName");
			pageContext.setAttribute("lastName", lastName);
			String firstName = (String) student.getProperty("firstName");
			pageContext.setAttribute("firstName", firstName);
			String email = (String) student.getProperty("email");
			pageContext.setAttribute("email", email);
		%>
                 <tr>
		<td>${fn:escapeXml(perm)}</td>    
		<td>${fn:escapeXml(lastName)}</td>    
		<td>${fn:escapeXml(firstName)}</td> 
		<td>${fn:escapeXml(email)}</td>   
		<td><a href="/deletestudent?perm=${fn:escapeXml(perm)}&courseID=${fn:escapeXml(courseID)}">Delete</a></td>
		</tr>
		<%	
		}
	}finally{}
  		%>
    </div>
  </body>
 </html>