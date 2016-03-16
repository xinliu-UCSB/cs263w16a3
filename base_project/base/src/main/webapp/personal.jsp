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

<%@ page import="com.google.appengine.api.blobstore.BlobKey"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>

<%@ page import="com.google.appengine.api.images.ServingUrlOptions"%>
<%@ page import="com.google.appengine.api.images.Image"%>
<%@ page import="com.google.appengine.api.images.ImagesService"%>
<%@ page import="com.google.appengine.api.images.ImagesServiceFactory"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
    <title>Personal Details</title>
    <h1>Personal Details</h1>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
  </head>

  <body>
    <%
    BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ImagesService imageService = ImagesServiceFactory.getImagesService();
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if(user!=null){
      pageContext.setAttribute("user", user);
      String email = user.getEmail();
			Filter propertyFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
			try{
				Query q = new Query("Student").setFilter(propertyFilter);
				List<Entity> students = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
				String perm, lastName, firstName;
        ArrayList<String> courseID = null;
				for(Entity student : students){
				  perm = (String) student.getProperty("perm");
          pageContext.setAttribute("perm", perm);
          lastName = (String) student.getProperty("lastName");
          pageContext.setAttribute("lastName", lastName);
          firstName = (String) student.getProperty("firstName");
          pageContext.setAttribute("firstName", firstName);
          email = (String) student.getProperty("email");
          pageContext.setAttribute("email", email);
          courseID = (ArrayList<String>) student.getProperty("courseID");
          String blobKey = (String)student.getProperty("blobKey");
          BlobKey key = null;
          if(blobKey!=null){
            key = new BlobKey(blobKey);
          }
          else{
            System.out.println("blobkey is null");
          }
          if(key!=null){
            ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(key);
            String imageUrl = imageService.getServingUrl(options);
            pageContext.setAttribute("imageUrl", imageUrl);
            %>
            <img u="image" style="width=800px;height=400px" src="${fn:escapeXml(imageUrl)}"/>
            <%
          }
          else{
            System.out.println("imagekey is null");
          }
        }
    %>
    <p> Welcome! ${fn:escapeXml(user)}  <a href="<%=userService.createLogoutURL("/welcome.jsp")%>">Sign Out</a></p>
    <form action="<%=blobstore.createUploadUrl("/student/upload")%>" method="post" enctype="multipart/form-data">
        Please Choose An Image To Upload: <input type="file" name="myFile"> <input type="submit" value="upload">
    </form>
    <form>
      Perm:<br>
      ${fn:escapeXml(perm)}<br>
      LastName:<br>
      ${fn:escapeXml(lastName)}<br>
      FirstName:<br>
      ${fn:escapeXml(firstName)}<br>
      Email:<br>
      ${fn:escapeXml(email)}<br>
      CourseID:<br>
      <%
      for(String courseid : courseID){
        pageContext.setAttribute("courseid", courseid);
      %>
      ${fn:escapeXml(courseid)} 	<a href="/grade/list_student_grade.jsp?courseID=${fn:escapeXml(courseid)}&&studentID=${fn:escapeXml(perm)}">grades</a><br><br>
      <%
      }
      %>
    </form>
    <%
    }finally{}
    %>
    <%
    }else{
      out.println("You are not logged in!");
    }
    %>
  </body>
</html>
