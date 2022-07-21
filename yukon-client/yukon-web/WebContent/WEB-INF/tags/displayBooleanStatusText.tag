<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="value" type="java.lang.Boolean" description="Boolean value for the text to be displayed." %>
<%@ attribute name="yesKey" description="Positive boolean value text" %>
<%@ attribute name="noKey" description="Negative Boolean value text" %>

<c:choose>
    <c:when test="${value}">
        <c:set value="green" var="cssClass"/>
        <cti:msg key="${yesKey}" var="valueTxt"/>
    </c:when>
    <c:otherwise>
        <c:set value="red" var="cssClass"/>
        <cti:msg key="${noKey}" var="valueTxt"/>
    </c:otherwise>
</c:choose>

<span class="${cssClass}">${valueTxt}</span>