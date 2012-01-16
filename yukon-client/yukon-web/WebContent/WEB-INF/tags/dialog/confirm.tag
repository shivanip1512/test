<%@ tag body-content="empty" description="Create an AJAX confirmation dialog."%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="on" required="true" description="registers click event on the element with this CSS selector"%>
<%@ attribute name="nameKey" required="true" description="The base i18n key.  This tag requires dialogTitle and confirmationMsg keys."%>
<%@ attribute name="argument" type="java.lang.Object"%>

<cti:includeScript link="/JavaScript/yukon/ui/confirm_dialog_manager.js"/>

<cti:msgScope paths=".${nameKey},components.ajaxConfirm.${nameKey}">
    <cti:msg2 var="dialogTitleMsg" key=".title" argument="${pageScope.argument}" javaScriptEscape="true" htmlEscape="false" />
    <cti:msg2 var="confirmationMsg" key=".message" argument="${pageScope.argument}" javaScriptEscape="true" htmlEscape="false" />
    <cti:msgScope paths=",components.dialog">
        <cti:msg2 var="okBtnMsg" key=".ok"/>
        <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
    </cti:msgScope>
</cti:msgScope>

<script type="text/javascript">
jQuery(function(){
    debug(Yukon.Dialog.ConfirmationManager.add({
        on: '${on}',
        strings:{
            title: "${dialogTitleMsg}",
            message: "${confirmationMsg}",
            ok: "${okBtnMsg}",
            cancel: "${cancelBtnMsg}"
        }
    }));
});
</script>