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
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
    <title>Welcome Page</title>
    <h1>Welcome Page</h1>
  <!--  <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/> !-->
  </head>

  <body>
    <%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if(user!=null){
      pageContext.setAttribute("user", user);
    %>
    <p> Welcome! ${fn:escapeXml(user)}</p>
    <a href="<%=userService.createLogoutURL("/welcome.jsp")%>">Sign Out</a>
    <%
    }else{
      %>
      <a href="<%=userService.createLoginURL("/choose.jsp")%>">Sign In</a><br>
      <%
    }
    %>

  </body>
</html>
