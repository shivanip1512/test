<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="org.apache.commons.lang3.ObjectUtils" %>
<%@ page import="com.cannontech.common.exception.NotLoggedInException" %>
<%@ page import="com.cannontech.util.ServletUtil" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%
Object status_code = request.getAttribute("javax.servlet.error.status_code");
status_code = ObjectUtils.defaultIfNull(status_code, "no status code");
pageContext.setAttribute("code", status_code.toString());
try {
    ServletUtil.getYukonUser(request);
    pageContext.setAttribute("loggedIn", true);
} catch (NotLoggedInException ignore) {
    pageContext.setAttribute("loggedIn", false);
}    
%>

<html>
<head>
<title>Yukon Error Page</title>
<cti:includeCss link="NORMALIZE" force="true"/>
<cti:includeCss link="YUKON" force="true"/>
<c:if test="${loggedIn}">
    <cti:css key="yukon.web.error.errorStyles"/>
</c:if>
<c:if test="${not loggedIn}">
    <link rel="stylesheet" href="<cti:url value="/WebConfig/yukon/errorStyles.css"/>" type="text/css">
</c:if>
<link rel="shortcut icon" href="<cti:url value="/resources/favicon.ico"/>" type="image/x-icon">
<c:if test="${loggedIn}">
    <cti:msg var="genericMainMessage" key="yukon.web.error.genericMainMessage"/>
    <c:set var="logoHtml"><cti:logo key="yukon.web.error.logo"/></c:set>
    <c:choose>
        <c:when test="${code == '404'}">
            <cti:msg var="errorMessage" key="yukon.web.error.404Message"/>
        </c:when>
        <c:when test="${code == '403'}">
            <cti:msg var="errorMessage" key="yukon.web.error.403Message"/>
        </c:when>
        <c:when test="${code == '401'}">
            <cti:msg var="errorMessage" key="yukon.web.error.401Message"/>
        </c:when>
        <c:when test="${code == '400'}">
            <cti:msg var="errorMessage" key="yukon.web.error.400Message"/>
        </c:when>
        <c:otherwise>
            ${code}
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test="${not loggedIn}">
    <c:set var="genericMainMessage" value="An error occurred while processing your request."/>
    <c:set var="logoHtml">
        <img class="logoImage" src="<cti:url value="/WebConfig/yukon/YukonBW.gif"/>">
    </c:set>
    <c:choose>
        <c:when test="${code == '404'}">
            <c:set var="errorMessage" value="404 - page not found"/>
        </c:when>
        <c:when test="${code == '403'}">
            <c:set var="errorMessage" value="403 - forbidden"/>
        </c:when>
        <c:when test="${code == '401'}">
            <c:set var="errorMessage" value="401 - unauthorized"/>
        </c:when>
        <c:when test="${code == '400'}">
            <c:set var="errorMessage" value="400 - bad request"/>
        </c:when>
        <c:otherwise>
            ${code}
        </c:otherwise>
    </c:choose>
</c:if>

</head>

<body>
<div id="error">
    <div id="errorImg">
        <a href="<cti:url value="/"/>">${logoHtml}</a>
    </div>
    <div id="errorMain">
        ${genericMainMessage}
    </div>
    <div id="errorMain">
        ${errorMessage}
    </div>
</div>
</body>

</html>
