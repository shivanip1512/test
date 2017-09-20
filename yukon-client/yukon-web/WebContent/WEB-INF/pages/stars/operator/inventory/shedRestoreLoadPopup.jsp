<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:msgScope paths=",modules.operator.hardware">
    <cti:msg2 var="popupTitle" key=".shedLoadPopup.title"/>
    <c:if test="${isAllowDRControl}">
        <cti:msg2 var="popupTitle" key=".shedRestoreLoadPopup.title"/>
    </c:if>
    <div id="device-shed-restore-load-popup" class="dn"
        data-dialog
        data-title="${popupTitle}" 
        data-url="<cti:url value="/stars/operator/inventory/shedRestoreLoadPopup/${hardware.inventoryId}"/>" 
        data-width="300" 
        data-min-width="300" 
        data-event="yukon:assets:shed:restore:load:send" 
        data-ok-text="<cti:msg2 key="components.button.send.label"/>" 
        ></div>
<script>
/** 'Send' button clicked on the shed/restore load configuration popup. */
$(document).on('yukon:assets:shed:restore:load:send', function (ev) {
    var data = $('#shed-restore-load-form').serialize();
    $('#device-shed-restore-load-popup').dialog('close');
    yukon.ui.busy('#sendShedRestore');
            $.ajax({
                 url: yukon.url('/stars/operator/inventory/shedRestoreLoad'),
                 data: data,
                 type: 'post',
                 success: function (result, status, xhr, $form) {
                    window.location.href = window.location.href;
                    yukon.ui.unbusy('#sendShedRestore');
                 },
                 error: function (xhr, status, error, $form) {
                    yukon.ui.unbusy('#sendShedRestore');
                     }
                 });
}); 
</script>
</cti:msgScope>
