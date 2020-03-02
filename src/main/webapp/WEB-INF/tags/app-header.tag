<%@tag description="IDEAS app header" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="appHeader">
        <security:authorize access="hasAuthority('ADMIN') || hasAuthority('RESEARCHER')">
            <div id="menuToggler"></div>
        </security:authorize>
        
	<div id="appLogo" style="background-image: url('./img/${studioConfiguration.images['logo']}')">            
        </div>
        <security:authorize access="isAuthenticated()">
            <script>
                    jQuery(function() {
                            AppPresenter.loadUserData(function() {

                            });
                    });
            </script>
	<div id="userTab" class="dropdown-toggle" data-toggle="dropdown">
		<div id="principalUserInfo">
			<span></span> </br>
			
			<security:authentication property="principal.authorities" var="auths"/>
			<span id="principalUserAuths"></span>
			             
            <c:choose>
            <c:when test="${!isgdriveconnected}">
            <img src="../../img/logo-gdrive.png" width="30" style="float:left; bottom:5px; left:15px;  position:relative;">
            
            </a>
           
            </c:when>
            <c:otherwise>
			<img src="../../img/Google_Drive_logo_color.png" width="30" style="float:left; bottom:5px; left:15px; position:relative;">

            </a>
            </c:otherwise>
            </c:choose>
		</div>
		
		<div id="principalUserAvatar"></div>
		<span id="userCornerMenu" class="glyphicon glyphicon-chevron-down"></span>
		<span id="userTabHandler"></span>
	</div>
	<ul class="dropdown-menu" role="menu">
		<li><a href="settings/user#profile" target="_self" title=""><spring:message code="master.page.account" /></a></li>
		 <c:choose>
            <c:when test="${!isgdriveconnected}">
            <li> <a id="gdrive-ws" href="gdrive/googlesignin" data-placement="bottom" title="">
      		<spring:message code="master.page.account.googledrive.connect"/>
            </a>
           </li>
            </c:when>
            <c:otherwise>
           <li> <a id="gdrive-ws" href="gdrive/logout" data-placement="bottom" title="">
           <spring:message code="master.page.account.googledrive.disconnect"/>
            </a>
            </li>
            </c:otherwise>
            </c:choose>
		
<!--		<li><a href="settings/user" target="_self"><spring:message code="master.page.settings" /></a></li>-->		<security:authorize access="hasAnyRole('ADMIN')">
		
			<li><a href="settings/admin" target="_self"><spring:message code="master.page.admin" /></a></li>
		</security:authorize>
		<li class="divider"></li>
		<li><a href="/logout" target="_self"><spring:message code="master.page.logout" /></a></li>
	</ul>
        </security:authorize>
	<!-- 	<div id="sectionText"> -->
	<%-- 		<spring:message code="app.header.sectiontext.editor" /> --%>
	<!-- 	</div> -->
	<div class="shadowCurvedBottom1"></div>
</div>
