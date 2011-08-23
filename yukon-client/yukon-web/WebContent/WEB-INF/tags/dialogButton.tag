<%@ tag body-content="empty" %>
<%@ attribute name="dialogId" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="actionUrl" required="true" rtexprvalue="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/simpleDialog.js"/>

<cti:msgScope paths=".${nameKey}">
    <cti:msg2 var="dialogTitleText" key=".title" javaScriptEscape="true"/>
</cti:msgScope>

<cti:uniqueIdentifier var="dialogTitle" prefix="dialogButton_"/>
<script type="text/javascript">
    var ${dialogTitle} = '${dialogTitleText}';
</script>

<cti:button nameKey="${nameKey}" onclick="openSimpleDialog('${dialogId}', '${actionUrl}', ${dialogTitle})"/>
