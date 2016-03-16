<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.util.List" %>
<%@ page import="cs263w16.grade.Grade" %>
<%@ page import="cs263w16.grade.Comment" %>
<%@ page import="cs263w16.grade.CommentEnqueue" %>

<html>
    <head>
      <link rel="stylesheet" href="../css/bootstrap.min.css"/>         
       <script src="../js/bootstrap.min.js"></script>       
    </head>


  <body>
        <div class="container">
	<jsp:include page="/navbar.jsp" />
        <!--grades List-->
	<%
		//whether user has logged in
		String courseID = request.getParameter("courseID");
		pageContext.setAttribute("courseID", courseID);

		UserService userService = UserServiceFactory.getUserService();
		User userName = userService.getCurrentUser();
		
		if (userName != null) {
			String email = userName.getEmail();
			pageContext.setAttribute( "email", email );
			String userID = userName.getUserId();
			pageContext.setAttribute( "userID", userID );
	%>
        <h2>${fn:escapeXml(courseID)}</h2>
               <form id="GradeForm" role="form" >

		<%
                Grade grade = (Grade) request.getAttribute("grade");
		if(grade != null) {
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
                                </tr>			             
                        </table>  
		<%
		} else {
		%>                    
                        <br>           
                        <div class="alert alert-info">
                            Error: No grade.
                        </div>
 		<%
		} 
		%>

                     
            </form>

	<!--Update Grade Form -->
	<!-- if current user is an instructor of the corresponding course -->
    <% CommentEnqueue ce = new CommentEnqueue();
	  String[] userIsInstructor = ce.getUserInfo( email, userID, grade.getGradeKeyStr() );
	  if( userIsInstructor != null && userIsInstructor[1].equals("instructor") ) { 
    %>
    <form action="/grade/update_enqueue" method="post"  id="updateGradeForm" role="form">
	<div class="form-group col-xs-5">
	<input type="hidden" name="gradeKeyname" value=${fn:escapeXml(gradeKeyname)}>
	New Score:
	<input type="text" name="score"><br>
	Update reason (can be blank):
	<input type="text" name="reason"><br>
	</div>
      <button type="submit" class="btn btn-info">
               <span class="glyphicon"></span> Update
       </button>
       <br></br>
    </form>
    <%
    }
    %>

	<!-- Comment Form -->
	<form id="CommentForm" role="form" >

		<%
                List<Comment> commentList = (List<Comment>) request.getAttribute("commentList");
		if(commentList != null) {
		%>
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                    <td>User</td>
                                    <td>Subject</td>
                                    <td>date</td>    
                                    <td>content</td>                            
                                </tr>
                            </thead>
			    <% 
			    
			    for(Comment c : commentList) {
				pageContext.setAttribute( "sender", c.getUserID() );
				pageContext.setAttribute( "userType", c.getUserType() );
				pageContext.setAttribute("subject", c.getName() );
				pageContext.setAttribute("content", c.getContent() );
				pageContext.setAttribute("date", c.getDate() );
				%>
                                <tr>
                                    <td>${fn:escapeXml(sender)}</td>
                                    <td>${fn:escapeXml(subject)}</td>
                                    <td>${fn:escapeXml(date)}</td>
                                    <td>${fn:escapeXml(content)}</td>                                     
                                </tr>
			    <%
			    }
			    %>             
                        </table>  
		<%
		} else {
		%>                    
                        <br>           
                        <div class="alert alert-info">
                            Currently, no comment for this grade.
                        </div>
 		<%
		} 
		%>                      
            </form>

	<!--AddComment Form -->
    <form action="/comment/enqueue" method="post"  id="addComment" role="form">
	<input type="hidden" name="gradeKeyname" value=${fn:escapeXml(gradeKeyname)}>
	<input type="hidden" name="email" value=${fn:escapeXml(email)}>
	<input type="hidden" name="userID" value=${fn:escapeXml(userID)}>
	Subject:
	<input type="text" name="name"><br>
	Content:
	<input type="text" name="content"><br>
	<button type="submit" class="btn btn-info">
               <span class="glyphicon glyphicon-search"></span> Submit
    </form>

	<%
	} else {
		//user has not logged in, redirect to log in
		%>
      		<a href="<%=userService.createLoginURL("/newstudent.jsp")%>">Sign In</a><br>
      		<%	
	}
	%> 

    </div>
  </body>
</html>
