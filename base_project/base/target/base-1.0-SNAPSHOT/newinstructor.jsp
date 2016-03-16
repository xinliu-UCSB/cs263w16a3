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
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
    <title>Welcome Instuctor</title>
    <h1>Welcome Instructor</h1>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
  </head>

  <body>
    <%
    BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if(user!=null){
      pageContext.setAttribute("user", user);
			List<Entity> instructors;
			String userId = user.getUserId();
			Filter propertyFilter = new FilterPredicate("userId", FilterOperator.EQUAL, userId);
			try{
				Query q = new Query("Instructor").setFilter(propertyFilter);
				instructors = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
			}finally{}
			if(instructors.size()!=0){
				String instructorID = "";
				for(Entity instructor : instructors)
				  instructorID = (String) instructor.getProperty("instructorID");
					pageContext.setAttribute("instructorID", instructorID);
				if(instructorID==null){
					out.println("instructorID is null");
				}
    			response.sendRedirect("/instructorpersonal.jsp");
		}else{
		%>
		<form action="/instructorenqueue" method="post">
      Enter Instructor ID:<br>
			<input type="text" name="instructorID"><br>
      Enter LastName:<br>
			<input type="text" name="iln"><br>
			Enter FirstName:<br>
			<input type="text" name="ifn"><br>
			Enter Email:<br>
	    <input type="text" name="ie"><br>
      <input type="submit" value="Submit">
    </form>
		<%
		}
	}else{
		out.println("you are not logged in");
	}
	%>
  </body>
</html>
