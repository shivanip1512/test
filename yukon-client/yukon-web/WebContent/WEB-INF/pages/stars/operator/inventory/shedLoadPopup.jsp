<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:msgScope paths=",modules.operator.hardware">
    <div id="device-shedload-popup" class="dn"
        data-dialog
        data-title="<cti:msg2 key=".shedLoadPopup.title"/>" 
        data-url="<cti:url value="/stars/operator/inventory/shedLoadPopup/${hardware.inventoryId}"/>" 
        data-width="300" 
        data-min-width="300" 
        data-event="yukon:assets:shedload:save" 
        data-ok-text="<cti:msg2 key="components.button.send.label"/>" 
        ></div>
        
<script>
/** 'Save' button clicked on the shed load configuration popup. */
$(document).on('yukon:assets:shedload:save', function (ev) {
    var data = $('#shed-load-form').serialize();
    $('#device-shedload-popup').dialog('close');
    yukon.ui.busy('#testShed');
            $.ajax({
                 url: yukon.url('/stars/operator/inventory/shedLoad'),
                 data: data,
                 type: 'post',
                 success: function (result, status, xhr, $form) {
                    window.location.href = window.location.href;
                    yukon.ui.unbusy('#testShed');
                 },
                 error: function (xhr, status, error, $form) {
                    yukon.ui.unbusy('#testShed');
                     }
                 });
}); 
</script>
</cti:msgScope>
