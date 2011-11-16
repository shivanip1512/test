<%@ tag body-content="empty" description="Create an AJAX confirmation dialog."%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="on" required="true" description="registers click event on the element with this CSS selector"%>
<%@ attribute name="nameKey" required="true" description="The base i18n key.  This tag requires dialogTitle and confirmationMsg keys."%>
<%@ attribute name="argument" type="java.lang.Object"%>
<%@ attribute name="id"%>
<%@ attribute name="trigger" description="The name of an event to fire if OK is pressed.  Defaults to 'yukonConfirmDialogOK' (if neither form nor href is specified).  Cannot be used with href or form."%>
<%@ attribute name="form" description="A selector for a form to submit if OK is pressed.  Use 'closest' to submit a form the dialog is inside.  Cannot be used with href or trigger."%>
<%@ attribute name="href" description="A URL to head over to if OK is pressed.  Cannot be used with form or trigger."%>

<c:if test="${empty form && empty href && empty trigger}">
    <c:set var="trigger" value="yukonConfirmDialogOK"/>
</c:if>

<c:if test="${empty pageScope.id}">
    <cti:uniqueIdentifier var="id" prefix="confirm"/>
</c:if>

<cti:msgScope paths=".${nameKey},components.ajaxConfirm.${nameKey}">
    <cti:msg2 var="dialogTitleMsg" key=".title" argument="${pageScope.argument}"/>
    <cti:msg2 var="confirmationMsg" key=".message" argument="${pageScope.argument}" />
    <cti:msgScope paths=",components.dialog">
        <cti:msg2 var="okBtnMsg" key=".ok"/>
        <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
    </cti:msgScope>
</cti:msgScope>

<script type="text/javascript">
jQuery(document).ready(function() {
    var on = '${on}';
    jQuery(on).live('click', function() {
        function confirmOk() {
            <c:if test="${!empty pageScope.trigger}">
                jQuery('#${id}').trigger('${trigger}');
            </c:if>
            <c:if test="${!empty pageScope.form}">
                <c:if test="${form == 'closest'}">
                    jQuery(on).closest('form').submit();
                </c:if>
                <c:if test="${form != 'closest'}">
                    jQuery('${form}').submit();
                </c:if>
            </c:if>
            <c:if test="${!empty pageScope.href}">
                window.location = '${href}';
            </c:if>
        }
        var buttons = [];
        buttons.push({'text' : '${okBtnMsg}', 'click' : confirmOk});
        buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
        var dialogOpts = {
                'position' : 'center',
                'width' : 'auto',
                'height' : 'auto',
                'buttons' : buttons };
        jQuery('#${id}').dialog(dialogOpts);
    });
});
</script>

<div id="${id}" title="${dialogTitleMsg}" style="display: none">
    <p>${confirmationMsg}<p>
</div>
