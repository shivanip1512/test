<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="rfnTest.viewDataSimulator">

<script type="text/javascript">
jQuery(function() {
    jQuery('#start').click(function(event) {
        var lcr6200serialFrom = jQuery("input[name='Lcr6200serialFrom']").val();
        var lcr6200serialTo = jQuery("input[name='Lcr6200serialTo']").val();
        var lcr6600serialFrom = jQuery("input[name='Lcr6600serialFrom']").val();
        var lcr6600serialTo = jQuery("input[name='Lcr6600serialTo']").val();
        var messageId = jQuery("input[name='messageId']").val();
        var messageIdTimestamp = jQuery("input[name='messageIdTimestamp']").val();
    
        var data = { 'lcr6200serialFrom': lcr6200serialFrom, 'lcr6200serialTo': lcr6200serialTo,
                'lcr6600serialFrom': lcr6600serialFrom, 'lcr6600serialTo': lcr6600serialTo,
                'messageId': messageId, 'messageIdTimestamp': messageIdTimestamp};
        
        jQuery.ajax({
            url: '/support/development/rfn/startDataSimulator',
            type: 'GET',
            data: data 
        });
    });
    
    jQuery('#stop').click(function(event) {
        jQuery.ajax({
            url: '/support/development/rfn/stopDataSimulator',
            type: 'GET'
        });
    });
});
</script>

<tags:sectionContainer2 nameKey="lcrDataSimulator">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.serialNumberRangeLcr6200">
            <input name="Lcr6200serialFrom" type="text" value="100000"> <i:inline key="yukon.common.to"/> <input name="Lcr6200serialTo" type="text" value="200000">
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.serialNumberRangeLcr6600">
            <input name="Lcr6600serialFrom" type="text" value="300000"> <i:inline key="yukon.common.to"/> <input name="Lcr6600serialTo" type="text" value="320000">
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.messageId">
            <input name="messageId" type="text" value="123456789">
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.messageIdTimestamp">
            <input name="messageIdTimestamp" type="text" value="1390000000">
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div>
        <cti:button nameKey="start" id="start"/>
        <cti:button nameKey="stop" id="stop"/>
    </div>
</tags:sectionContainer2>
</cti:standardPage>