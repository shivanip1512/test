<%@ tag body-content="empty" %>
<%@ attribute name="dialogId" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="actionUrl" required="true" %>
<%@ attribute name="styleClass" description="link class, defaults to 'simpleLink'" %>
<%@ attribute name="skipLabel" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/simpleDialog.js"/>

<cti:msgScope paths=".${pageScope.nameKey}">
    <cti:msg2 var="dialogTitleText" key=".title" javaScriptEscape="true"/>
</cti:msgScope>

<cti:uniqueIdentifier var="dialogTitle" prefix="simpleDialogLink2_"/>
<script type="text/javascript">
    var ${pageScope.dialogTitle} = '${pageScope.dialogTitleText}';
</script>

<c:set var="simpleDialogUrl"
    value="javascript:openSimpleDialog('${pageScope.dialogId}', '${pageScope.actionUrl}', ${pageScope.dialogTitle})"/>

<c:if test="${!pageScope.skipLabel}">
    <cti:labeledImg nameKey="${pageScope.nameKey}" href="${pageScope.simpleDialogUrl}"
        styleClass="${pageScope.styleClass}"/>
</c:if>
<c:if test="${pageScope.skipLabel}">
    <cti:img nameKey="${pageScope.nameKey}" href="${simpleDialogUrl}"
        styleClass="${pageScope.styleClass}"/>
</c:if>
