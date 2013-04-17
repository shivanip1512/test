<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="thermostatHistory.${pageNameSuffix}">

<cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
    
<script type="text/javascript">
jQuery(function(){
    Yukon.ThermostatManualEditor.init({
        thermostat: {
            HEAT: {
                upper: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.upperLimitHeat.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.lowerLimitHeat.value}), unit:'F'}),
                temperature: new Temperature({degrees: parseFloat(${event.previousHeatTemperature.value}), unit: 'F'})
            },
            COOL: {
                upper: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.upperLimitCool.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.lowerLimitCool.value}), unit:'F'}),
                temperature: new Temperature({degrees: parseFloat(${event.previousCoolTemperature.value}), unit: 'F'})
            },
            mode: '${event.mode}',
            fan: '${event.fanState}'
        },
        unit: '${temperatureUnit}'
    });
});
</script>

    <div id="ajaxDialog"></div>
    
    <c:set var="multipleThermostatsSelected" value="${fn:length(thermostatNames) > 1}"></c:set>
    <table class="thermostatPageContent">
        <tr>
            <td>
                <%-- THERMOSTAT NAMES --%>
                <jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
            </td>
        </tr>
        <tr>
            <td>
                <cti:msg2 var="historyTableTitle" key="yukon.web.modules.operator.thermostatHistory.historyTableTitle"/>
                <tags:pagedBox title="${historyTableTitle}" searchResult="${searchResult}"
                    filterDialog="" baseUrl="/stars/operator/thermostat/history/view"
                    isFiltered="false" showAllUrl="/stars/operator/thermostat/history/view">
                    
                    <jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/history/commandHistory.jsp" />
                    
                </tags:pagedBox>
            </td>
        </tr>
    </table>

</cti:standardPage>
