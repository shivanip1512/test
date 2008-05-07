<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" title="Consumer Energy Services">
<cti:standardMenu/>

<table class="contentTable">
    <tr>
        <td class="leftColumn">
            <h3>
                <cti:msg key="yukon.dr.consumer.scheduleHints.header" /><br>
            </h3>
            
            <div class="message">
                <div style="text-align: left;">
                    <cti:msg key="yukon.dr.consumer.scheduleHints.hint" />
                </div>
                
                <a href="/spring/stars/consumer/thermostat/schedule/view?thermostatIds=${thermostatIds}"><cti:msg key="yukon.dr.consumer.scheduleHints.back" /></a>
            </div>

        </td>
        <td class="rightColumn">
            <div id="rightDiv">
                <cti:customerAccountInfoTag accountNumber="${customerAccount.accountNumber}"/>
            </div>
        </td>
    </tr>
</table>

</cti:standardPage>