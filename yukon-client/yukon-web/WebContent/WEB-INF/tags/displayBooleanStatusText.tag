<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="value" type="java.lang.Boolean" description="Boolean value for the text to be displayed." %>
<%@ attribute name="yesKey" description="i18n key for the text to be displayed if the boolean value is true. Defaults to 'yukon.common.yes'" %>
<%@ attribute name="noKey" description="i18n key for the text to be displayed if the boolean value is false. Defaults to 'yukon.common.no'." %>

<cti:default var="yesKey" value="yukon.common.yes"/>
<cti:default var="noKey" value="yukon.common.no"/>

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