<%@page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<%@taglib prefix="ideas" tagdir="/WEB-INF/tags/" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
<c:when test="${isConnected}">
<p> You are connected to Google Drive </p>
<a href="/app/editor">Return to editor</a>
</c:when>
<c:otherwise>
<a href="https://accounts.google.com/o/oauth2/auth?access_type=online&client_id=528647562600-5j5s8fboaocg34068paoladbd3c3n69o.apps.googleusercontent.com&redirect_uri=http://localhost:8888/Callback&response_type=code&scope=https://www.googleapis.com/auth/drive.file">Gdrive</a>

 <p>You are disconnected </p>
</c:otherwise>
</c:choose>
