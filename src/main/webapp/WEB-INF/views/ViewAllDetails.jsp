<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="com.div.util.ImageUtil"%>
<%@page isELIgnored="false"%>

<!DOCTYPE html>
<html>
<head>
    <title>View All Details</title>
</head>
<body>
    <h2>View All Details</h2>

    <table border="1">
        <thead>
            <tr>
                <th>Title</th>
                <th>Public URL</th>
                <th>Published Date</th>
                <th>Banner</th>
                <th>Access Category</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="detail" items="${details}">
                <tr>
                    <td>${detail.title}</td>
                    <td>${detail.publicURL}</td>
                    <td><fmt:formatDate value="${detail.publishedDate}" pattern="yyyy-MM-dd" /></td>
                    <td>
                        <c:if test="${not empty detail.banner}">
                            <img src="${ImageUtil.getBase64EncodedImage(detail.banner, detail.contentType)}" width="120" height="100" />
                        </c:if>
                    </td>
                    <td>${detail.accessCategory}</td>
                    <td><a href="deleteDetail?id=${detail.id}"
						class="btn-delete">Delete</a> <a
						href="editDetail?id=${detail.id}" class="btn-edit">Edit</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <form action="signup" method="get" style="text-align: center; margin-top: 20px;">
        <input type="submit" value="Home"/>
    </form>
    <form action="addDetails" method="get" style="text-align: center; margin-top: 20px;">
        <input type="submit" value="Go To Form">
    </form>
</body>
</html>