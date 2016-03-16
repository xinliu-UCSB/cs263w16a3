<!-- A add_batch_grade.jsp file served from the "/grade/" URL. -->
<!-- This is used to add a batch of grades for courseID -->

<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="java.io.*"%>

<%@ page import="java.util.logging.*"%>

<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>


<html>
    <head>
      <link rel="stylesheet" href="../css/bootstrap.min.css"/>         
       <script src="../js/bootstrap.min.js"></script>
	<title>Add Batch Grade</title>       
    </head>

  <body>
        <div class="container">
	<jsp:include page="/navbar.jsp" />
	<%
		  String courseID = request.getParameter("courseID");
		  pageContext.setAttribute("courseID", courseID);
	%>
            <h2>${fn:escapeXml(courseID)} Add Batch Grades</h2>

    <p>Enter the information for a batch of grades:</p>
    <!--Add Batch grade Form -->
    <form action="/grade/enqueue_batch" method="post"  id="addBatchGrade" role="form">
	<input type="hidden" name="courseID" value=${fn:escapeXml(courseID)}>
      Grade name:
      <input type="text" name="name"><br>
      studentID, score, attribute; (use ';' to seperate grades, attribute may be ignored):<br>
      <input type="text" name="content" style="width: 400px; height: 500px"><br>
      <button type="submit" class="btn btn-info">
               <span class="glyphicon glyphicon-search"></span> Submit
    </form>

	</div>
  </body>
</html>
