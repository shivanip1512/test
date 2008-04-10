<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" title="Consumer Energy Services">
<cti:standardMenu/>

<cti:includeScript link="/JavaScript/temp_conversion.js" />

<table class="contentTable">
    <tr>
        <td class="leftColumn">
            <h3>
                <cti:msg key="yukon.dr.consumer.manualComplete.header" /><br>
            </h3>
            
            <div class="message">
                <cti:msg key="yukon.dr.consumer.manualComplete.${message}" arguments="${thermostat.label}" />
                <c:url var="viewUrl" value="/spring/stars/consumer/thermostat/view">
                    <c:param name="thermostatId" value="${thermostat.id}" />
                </c:url>
                <br><br>
                <a href="${viewUrl}"><cti:msg key="yukon.dr.consumer.manualComplete.ok" /></a>
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