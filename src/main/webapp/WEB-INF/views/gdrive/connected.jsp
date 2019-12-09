<%@page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<%@taglib prefix="ideas" tagdir="/WEB-INF/tags/" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<c:if test="${disconnected}">
<p> You are disconnected from Google Drive <p>
<a href="/app/editor">Return to editor</a>
</c:if>

${message_type} 
