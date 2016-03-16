<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <link rel="stylesheet" href="./css/bootstrap.min.css"/>         
    <script src="./js/bootstrap.min.js"></script> 
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
    <title>GradeSpace</title>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
<style>
h1 {
    font-size: 250%;
}

p {
    font-size: 150%;
}
</style>
  </head>

  <body>
    <div class="container">
    <%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if(user!=null){
      pageContext.setAttribute("user", user);
    }
    %>
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1" align="right">
    <ul class="nav navbar-nav">
    <li><a href="/">Home</a></li>
    <li><a href="/help.jsp">Help</a></li>
   <li> 
    <%
    if(user!=null){
    %>
    <a href="<%=userService.createLogoutURL("/welcome.jsp")%>">Sign Out</a>
    <%
    }else{
      %>
      <a href="<%=userService.createLoginURL("/choose.jsp")%>">Sign In</a><br>
      <%
    }
    %>
    </li>
    </ul>
    </div>

    <h1>Welcome to GradeSpace</h1>
    <br>
    <p>GradeSpace is a platform for organizing the grade for courses in a simple way. It is especially useful for an instructor and a student to communicate with each other about a grade, and then change the grade if it is needed.</p>

    <p>An instructor can create a course and then upload the grades of this course. A student, verified by account (email), can log in to see his own grades for a course. If he wants to contest this grade, he can add comment to this grade and then wait for the instructor's reply. Since all the comments are kept with the grade being contested, both the grade and the comments have the property of date, and the update history of the grade is kept, it will be very clear to both the student and the instructor what has happened and what is the final result.</p>
    </div>
  </body>
</html>
