<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.util.List" %>
<%@ page import="cs263w16.grade.Grade" %>

<html>
    <head>
      <link rel="stylesheet" href="../css/bootstrap.min.css"/>         
       <script src="../js/bootstrap.min.js"></script> 
	<title>List Grade</title>      
    </head>


  <body>
        <div class="container">
	<jsp:include page="/navbar.jsp" />	
	<%
		  String courseID = request.getParameter("courseID");
		  pageContext.setAttribute("courseID", courseID);
	%>
            <h2>${fn:escapeXml(courseID)} Grade List</h2>

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
	

    <p>Enter the information for the grade you are looking for:</p>
    <!--Search Form -->
    <form action="/grade" method="get"  id="seachGradeForm" role="form">
	<div class="form-group col-xs-5">
	<input type="hidden" name="courseID" value=${fn:escapeXml(courseID)}><br>
	studentID:
	<input type="text" name="studentID"><br>
	grade name:
	<input type="text" name="name"><br>
	</div>
      <button type="submit" class="btn btn-info">
               <span class="glyphicon glyphicon-search"></span> Search
       </button>
       <br></br>
    </form>

        <!--grades List-->
	<%
		//whether user has logged in
		UserService userService = UserServiceFactory.getUserService();
		User userName = userService.getCurrentUser();
		if (userName != null) {
	    		//check whether userName is an instructor of courseID
	    		// if yes, search according to studentID and grade name in the grade in that course 		%>

            <id="GradeForm" role="form" > 
               
		<%
                List<Grade> gradeList = (List<Grade>) request.getAttribute("gradeList");
		String searchResult = (String) request.getAttribute("searchResult");
		if(gradeList != null) {
		%>
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                    <td>studentID</td>
                                    <td>grade name</td>
                                    <td>score</td>
                                    <td>grader</td>
                                    <td>date</td>
                                    <td>attribute</td>                                 
                                </tr>
                            </thead>
			    <% 
			    
			    for(Grade grade : gradeList) {
				pageContext.setAttribute( "gradeKeyname", grade.getGradeKeyStr() );
				pageContext.setAttribute("studentID", grade.getStudentID() );
				pageContext.setAttribute("name", grade.getName() );
				pageContext.setAttribute("score", grade.getScore() );
				pageContext.setAttribute("grader", grade.getGrader() );
				pageContext.setAttribute("date", grade.getDate() );
				pageContext.setAttribute("attribute", grade.getAttribute() );

				%>
                                <tr>
                                    <td>${fn:escapeXml(studentID)}</td>
                                    <td>${fn:escapeXml(name)}</td>
                                    <td>${fn:escapeXml(score)}</td>
                                    <td>${fn:escapeXml(grader)}</td>
                                    <td>${fn:escapeXml(date)}</td>
                                    <td>${fn:escapeXml(attribute)}</td>    

    <!-- GetComment Form -->
    <% if(grade.getHasNewComment()) { %>
    <td><form action="/grade_comment" method="get">
	<input type="hidden" name="gradeKeyname" value=${fn:escapeXml(gradeKeyname)}>
	<input type="hidden" name="courseID" value=${fn:escapeXml(courseID)}>
      <button type="submit" class="btn btn-info">
               <span class="glyphicon" ></span>Comment
       </button></form></td>
    <% } else { %>    
    <td><form action="/grade_comment" method="get">
	<input type="hidden" name="gradeKeyname" value=${fn:escapeXml(gradeKeyname)}>
      <button type="submit" class="btn btn-default">
               <span class="glyphicon" ></span>Comment
       </button></form></td>
    <% } %> 
                          
                                </tr>
			    <%
			    }
			    %>             
                        </table>  
		<%
		} else if ( searchResult != null && searchResult.equals("no grade") ) {
		%>                    
                        <br>           
                        <div class="alert alert-info">
                            No grade found matching your search criteria.
                        </div>
 		<%
		} 

	} else {
		//user has not logged in, redirect to log in
		%>
      		<a href="<%=userService.createLoginURL("/newstudent.jsp")%>">Sign In</a><br>
      		<%	
	}
		%>                      
            </form>
    </div>
  </body>
</html>
