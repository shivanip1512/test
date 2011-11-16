<%@ tag language="java" pageEncoding="UTF-8" description="Use this tag to wrap a JSP which is to be an AJAX dialog."%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="nameKey" required="true"%>
<%@ attribute name="dialogId" description="The id of the div to put the dialog in.  This div is created in this tag.  This is also used to name the function to open the dialog."%>
<%@ attribute name="okAction" required="true" description="Use 'submit' to submit a form, 'none' to skip the ok button or an event name to trigger that event on the dialog."%>
<%@ attribute name="on" description="registers click event on the element with this CSS selector"%>

<cti:msgScope paths=".${nameKey},components.dialog.${nameKey},components.dialog">
    <cti:msg2 var="titleMsg" key=".title"/>
    <cti:msg2 var="okBtnMsg" key=".ok"/>
    <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
</cti:msgScope>

<c:if test="${empty pageScope.dialogId}">
    <cti:uniqueIdentifier var="dialogId" prefix="inlineDialog"/>
</c:if>

<script type="text/javascript">
function open_${dialogId}() {
    var buttons = [];
    <c:if test="${okAction != 'none'}">
        var okButton = {'text' : '${okBtnMsg}'};
        <c:if test="${okAction == 'submit'}">
            okButton.click = function() {
                var dialogDiv = jQuery('#${dialogId}');
                var form = dialogDiv.find('form');
                form.submit();
            }
        </c:if>
        <c:if test="${okAction != 'submit'}">
            okButton.click = function() { jQuery('#${dialogId}').trigger('${okAction}'); }
        </c:if>
        buttons.push(okButton);
    </c:if>
    buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
    var dialogOpts = {
            'title' : '${titleMsg}',
            'position' : 'center',
            'width' : 'auto',
            'height' : 'auto',
            'buttons' : buttons };
    jQuery('#${dialogId}').dialog(dialogOpts);
}
<c:if test="${!empty pageScope.on}">
jQuery(document).ready(function() {
    jQuery('${on}').live('click', function() {
        open_${dialogId}();
    });
});
</c:if>
</script>

<div id="${dialogId}" style="display: none">
    <cti:flashScopeMessages/>
    <jsp:doBody/>
</div>
