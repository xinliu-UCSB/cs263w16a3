<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.*"%>
<%@ page import="com.google.appengine.api.datastore.Query.*"%>
<%@ page import="java.util.*"%>
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

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
    <title>New Student</title>
    <h1>New Student</h1>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
  </head>

  <body>
		<%
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if(user!=null){
      pageContext.setAttribute("user", user);
			List<Entity> students;
			String email = user.getEmail();
			Filter propertyFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
			try{
				Query q = new Query("Student").setFilter(propertyFilter);
				students = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
			}finally{}
			if(students.size()!=0){
				String perm = "";
				for(Entity student : students)
				  perm = (String) student.getProperty("perm");
					pageContext.setAttribute("perm", perm);
				if(perm==null){
					out.println("perm is null");
				}
    			response.sendRedirect("/personal.jsp");
		}else{
		%>
		<p>Sorry! You are not enrolled right now!</p>
		<%
		}
	}else{
		out.println("you are not logged in");
	}
	%>
  </body>

</html>
