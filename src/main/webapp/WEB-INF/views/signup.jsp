<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sign Up</title>
</head>
<body>
    <h2>Sign Up</h2>
    <form:form modelAttribute="user" action="signup/submit" method="post">
        <form:label path="username">Username:</form:label>
        <form:input path="username" /><br/>

        <form:label path="password">Password:</form:label>
        <form:password path="password" /><br/>
        
        <% if (request.getAttribute("passwordLengthError") != null) { %>
            <p style="color: red;">Password must be at least 4 characters long.</p>
        <% } %>

        <form:label path="email">Email:</form:label>
        <form:input path="email" /><br/>

        <input type="submit" value="Sign Up"/>
    </form:form>
    <button><a href="${pageContext.request.contextPath}/login">Login</a></button>
</body>
</html>