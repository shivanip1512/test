<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.operator.optOut.main">

<h1 class="dialogQuestion">
    <cti:msg2 key=".resetToLimit.confirm" arguments="${hardware.displayName}"/>
</h1>

<cti:url var="resetToLimitUrl" value="/spring/stars/operator/program/optOut/resetToLimit"/>
<form id="confirmForm" action="${resetToLimitUrl}">
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
