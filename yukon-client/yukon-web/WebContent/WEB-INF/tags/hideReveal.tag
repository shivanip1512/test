<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>
<%@ attribute name="slide" required="false" type="java.lang.Boolean" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:includeScript link="/JavaScript/simpleCookies.js"/>
<cti:includeScript link="/JavaScript/hideReveal.js"/>

<cti:uniqueIdentifier prefix="hideReveal_" var="thisId"/>

<div class="titledContainer triangleContainer ${pageScope.styleClass}" <c:if test="${!empty pageScope.id}" >id="${pageScope.id}"</c:if>>
<div class="titleBar triangleContainer_titleBar">
<span id="${thisId}_control" class="controls">
<img id="${thisId}_plusImg" src="/WebConfig/yukon/Icons/triangle-right.gif">
<img id="${thisId}_minusImg" src="/WebConfig/yukon/Icons/triangle-down.gif">

<span class="title triangleContainer_title">
<c:choose>
    <c:when test="${pageScope.escapeTitle}">
        <spring:escapeBody htmlEscape="true">${pageScope.title}</spring:escapeBody>
    </c:when>
    <c:otherwise>
        ${pageScope.title} 
    </c:otherwise>
</c:choose>
</span>
</span>
</div>
<div id="${thisId}_content" class="content triangleContainer_content">
<div>
<jsp:doBody/>
</div>
</div>

</div>

<script type="text/javascript">
hideRevealSectionSetup('${thisId}_plusImg', '${thisId}_minusImg', '${thisId}_control', '${thisId}_content', ${pageScope.showInitially ? true : false}, '${cti:jsSafe(title)}', ${pageScope.slide ? true : false});
</script>
	