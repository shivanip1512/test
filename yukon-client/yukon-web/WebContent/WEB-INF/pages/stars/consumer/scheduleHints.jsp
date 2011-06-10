<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="scheduleHints">
<cti:standardMenu/>


    <h3>
        <cti:msg key="yukon.dr.consumer.scheduleHints.header" /><br>
    </h3>
    
    <div class="message">
        <div style="text-align: left;">
            <cti:msg key="yukon.dr.consumer.scheduleHints.hint" />
        </div>
        
        <a href="/spring/stars/consumer/thermostat/schedule/view/saved?thermostatIds=${thermostatIds}"><cti:msg key="yukon.dr.consumer.scheduleHints.back" /></a>
    </div>

</cti:standardPage>