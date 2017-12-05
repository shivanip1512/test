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
    var data = $('#shed-restore-load-form').serialize(),
        sendShedOption = $('#sendShedRestore'),
        sendShedIcon = sendShedOption.find('i'),
        sendShedIconClass = sendShedIcon.attr('class');
    $('#device-shed-restore-load-popup').dialog('close');
    sendShedOption.addClass('disabled-look');
    sendShedIcon.attr('class', 'icon icon-spinner');
        $.ajax({
             url: yukon.url('/stars/operator/inventory/shedRestoreLoad'),
             data: data,
             type: 'post',
             success: function (result, status, xhr, $form) {
                window.location.href = window.location.href;
             },
             error: function (xhr, status, error, $form) {
                 sendShedOption.removeClass('disabled-look');
                 sendShedIcon.attr('class', sendShedIconClass);
             }
         });
}); 
</script>
</cti:msgScope>
