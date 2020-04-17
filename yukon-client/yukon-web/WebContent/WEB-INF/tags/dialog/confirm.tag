<%@ tag body-content="empty"
    description="Create an AJAX confirmation dialog. 
                 The dialog will trigger 'yukon.dialog.confirm.ok' or 'yukon.dialog.confirm.cancel' 
                 if the dialog is confirmed or cancelled (repsectively) either via a button click 
                 or by calling yukon.dialogConfirm methods directly."%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="on" required="true" description="registers click event on the element with this CSS selector"%>
<%@ attribute name="nameKey" required="true" description="The base i18n key.  This tag requires title and message keys."%>
<%@ attribute name="argument" type="java.lang.Object"%>
<%@ attribute name="disableGroup" description="Creates a 'data-disable-group' attribute on buttons at time of show to allow disabling all dialog buttons on click."%>
<%@ attribute name="userMessage" required="false" description="User message to be displayed on the confirmation dialog."%>
<%@ attribute name="userMessageClass" required="false" description="Class of the user message viz. error, success, info, warning. Default: info"%>
<%@ attribute name="okClasses" %>

<cti:msgScope paths=".${nameKey},components.ajaxConfirm.${nameKey}">
    <cti:msg2 var="dialogTitleMsg" key=".title" argument="${pageScope.argument}" javaScriptEscape="true"/>
    <cti:msg2 var="confirmationMsg" key=".message" argument="${pageScope.argument}" javaScriptEscape="true"/>
    <cti:msgScope paths=",components.dialog">
        <cti:msg2 var="okBtnMsg" key=".ok"/>
        <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
    </cti:msgScope>
</cti:msgScope>

<c:if test="${not empty pageScope.disableGroup}">
    <c:set var="disable_group">'disable_group': '${disableGroup}',</c:set>
</c:if>
<script type="text/javascript">
$(function() {
    yukon.dialogConfirm.add({
        'on': '${on}',
        ${disable_group}
        'strings':{
            'title': '${dialogTitleMsg}',
            'message': '${confirmationMsg}',
            'ok': '${okBtnMsg}',
            'cancel': '${cancelBtnMsg}',
            'okClasses': '${okClasses}',
            'userMessage' : '${userMessage}',
            'userMessageClass' : '${userMessageClass}'
        }
    });
});
</script>