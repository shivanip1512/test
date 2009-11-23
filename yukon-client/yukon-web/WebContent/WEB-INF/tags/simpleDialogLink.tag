<%@ tag body-content="empty" %>
<%@ attribute name="dialogId" required="true"%>
<%@ attribute name="titleKey" required="true"%>
<%@ attribute name="actionUrl" required="true"%>
<%@ attribute name="logoKey" required="false"%>
<%@ attribute name="labelKey" required="false" description="i18n key of label text"%>
<%@ attribute name="labelText" required="false" description="plain text of label text"%>
<%@ attribute name="styleClass" required="false" description="link class, defaults to 'simpleLink'"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:includeScript link="/JavaScript/simpleDialog.js"/>

<cti:uniqueIdentifier var="dialogTitle" prefix="simpleDialogTitle"/>

<script type="text/javascript">
    var ${dialogTitle} = '<cti:msg key="${pageScope.titleKey}" javaScriptEscape="true" />';
</script>            

<c:if test="${empty styleClass}">
	<c:set var="styleClass" value="simpleLink"/>
</c:if>

<a href="javascript:void(0)" class="${styleClass}" onclick="openSimpleDialog('${pageScope.dialogId}', '${pageScope.actionUrl}', ${dialogTitle})">
	<c:if test="${not empty pageScope.logoKey}">
    	<cti:logo key="${pageScope.logoKey}"/>
    </c:if>
    <c:choose>
	    <c:when test="${not empty pageScope.labelKey}">
	        <cti:msg key="${pageScope.labelKey}" />
	    </c:when>
	    <c:when test="${not empty pageScope.labelText}">
	    	${fn:escapeXml(pageScope.labelText)}
	    </c:when>
	    <c:otherwise>
	    	<%-- no label --%>
	    </c:otherwise>
    </c:choose>
</a>