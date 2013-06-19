<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="dialogId" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="actionUrl" required="true" %>
<%@ attribute name="styleClass" description="link class, defaults to 'simpleLink'" %>
<%@ attribute name="skipLabel" type="java.lang.Boolean" %>
<%@ attribute name="icon" description="the icon classname" %>

<cti:includeScript link="/JavaScript/simpleDialog.js"/>
<c:if test="${skipLabel}">
    <c:set var="renderMode" value="image"/>
</c:if>
<c:if test="${not skipLabel}">
    <c:set var="renderMode" value="labeledImage"/>
</c:if>
<cti:msgScope paths=".${pageScope.nameKey}">
    <cti:msg2 var="dialogTitleText" key=".title" javaScriptEscape="true"/>
</cti:msgScope>
<cti:uniqueIdentifier var="dialogTitle" prefix="simpleDialogLink2_"/>
<script type="text/javascript">
    var ${pageScope.dialogTitle} = '${pageScope.dialogTitleText}';
</script>
<c:set var="simpleDialogUrl" value="javascript:openSimpleDialog('${pageScope.dialogId}', '${pageScope.actionUrl}', ${pageScope.dialogTitle}, null, 'post', {width:600})"/>
<cti:button renderMode="${renderMode}" icon="${pageScope.icon}" nameKey="${pageScope.nameKey}" classes="${pageScope.styleClass}" href="${pageScope.simpleDialogUrl}"/>