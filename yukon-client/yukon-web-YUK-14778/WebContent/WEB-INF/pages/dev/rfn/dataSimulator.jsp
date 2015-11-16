<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.viewDataSimulator">

<script type="text/javascript">
$(function() {
    $('#start').click(function(event) {
        var data = { 'lcr6200serialFrom': $("input[name='Lcr6200serialFrom']").val(),
                'lcr6200serialTo': $("input[name='Lcr6200serialTo']").val(),
                'lcr6600serialFrom': $("input[name='Lcr6600serialFrom']").val(),
                'lcr6600serialTo': $("input[name='Lcr6600serialTo']").val(),
                'messageId': $("input[name='messageId']").val(),
                'messageIdTimestamp': $("input[name='messageIdTimestamp']").val(),
                'daysBehind': $("input[name='daysBehind']").val()};
        $.ajax({
            url: yukon.url('/dev/rfn/startDataSimulator'),
            type: 'GET',
            data: data 
        });
    });
    
    $('#stop').click(function(event) {
        $.ajax({
            url: yukon.url('/dev/rfn/stopDataSimulator'),
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
        <tags:nameValue2 nameKey=".lcrDataSimulator.daysBehind">
            <input name="daysBehind" type="text" value="0">
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div>
        <cti:button nameKey="start" id="start"/>
        <cti:button nameKey="stop" id="stop"/>
    </div>
</tags:sectionContainer2>
</cti:standardPage>