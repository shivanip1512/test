<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.operator.optOut.main">

<h1 class="dialogQuestion">
    <cti:msg2 key=".resendOptOut.confirm" arguments="${hardware.displayName}"/>
</h1>

<cti:url var="resendUrl" value="/spring/stars/operator/program/optOut/resend"/>
<form id="confirmForm" action="${resendUrl}">
    <input type="hidden" name="accountId" value="${param.accountId}"/>
    <input type="hidden" name="inventoryId" value="${param.inventoryId}"/>

    <div class="actionArea">
        <input type="button" value="<cti:msg2 key=".confirmDialogOk"/>"
            onclick="submitFormViaAjax('confirmDialog', 'confirmForm')"/>
        <input type="button" value="<cti:msg2 key=".confirmDialogCancel"/>"
            onclick="$('confirmDialog').hide()"/>
    </div>
</form>

</cti:msgScope>
