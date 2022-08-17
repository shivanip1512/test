<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="type" description="Values are 'success', 'info', 'pending', 'warning', and 'error'. Default: 'error'" %>

<%@ attribute name="key" description="i18n key object to use for the alert message. Note: key can be an i18n key String, 
                                      MessageSourceResolvable, Displayable, DisplayableEnum, or ResolvableTemplate." %>
                                      
<%@ attribute name="arguments" type="java.lang.Object"
                         description="Arguments to use when resolving the i18n message. Only valid when 'key' is used." %>
<%@ attribute name="classes" %>
<%@ attribute name="includeCloseButton" type="java.lang.Boolean" %>
<%@ attribute name="imgUrl" type="java.lang.String" %>
<%@ attribute name="imgCss" type="java.lang.String" %>

                         
<cti:default var="type" value="error"/>

<div id="user-message" class="user-message ${type} ${classes}">
    <c:if test="${includeCloseButton}">
        <i class="cp fr icon icon-close-x" onclick="$(this).parent().addClass('dn');"></i>
    </c:if>
    <c:if test="${not empty imgUrl}">
        <img class="vam" src="${imgUrl}" style="${imgCss}">
    </c:if>
    <c:choose>
        <c:when test="${not empty pageScope.key}">
            <i:inline key="${key}" arguments="${arguments}"/>
        </c:when>
        <c:otherwise><jsp:doBody/></c:otherwise>
    </c:choose>
</div> 