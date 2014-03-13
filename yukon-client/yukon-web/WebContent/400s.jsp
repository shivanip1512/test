<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="org.apache.commons.lang.ObjectUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%
Object status_code = request.getAttribute("javax.servlet.error.status_code");
status_code = ObjectUtils.defaultIfNull(status_code, "no status code");
pageContext.setAttribute("code", status_code.toString());
%>

<html>
<head>
<title>Yukon Error Page</title>
<link rel="stylesheet" href="<cti:url value="/WebConfig/yukon/styles/normalize.css"/>" type="text/css">
<link rel="stylesheet" href="<cti:url value="/WebConfig/yukon/styles/yukon.css"/>" type="text/css">
<cti:css key="yukon.web.error.errorStyles"/>

</head>

<body>
<div id="error">
<div id="errorImg">
    <cti:link key="yukon.web.error.logoLink" href="/" escapeBody="false">
        <cti:logo key="yukon.web.error.logo" />
    </cti:link>
</div>
<div id="errorMain">
    <cti:msg key="yukon.web.error.genericMainMessage" />
</div>
<div id="errorMain">

<c:choose>
    <c:when test="${code == '404'}">
        <cti:msg key="yukon.web.error.404Message" />
    </c:when>
    <c:when test="${code == '403'}">
        <cti:msg key="yukon.web.error.403Message" />
    </c:when>
    <c:when test="${code == '401'}">
        <cti:msg key="yukon.web.error.401Message" />
    </c:when>
    <c:when test="${code == '400'}">
        <cti:msg key="yukon.web.error.400Message" />
    </c:when>
    <c:otherwise>
        ${code}
    </c:otherwise>
</c:choose>
</div>

</body>

</html>
