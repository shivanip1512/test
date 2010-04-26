<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:url var="controlHistoryView" value="/spring/stars/operator/program/controlHistory"/>
<cti:url var="innerViewUrl" value="${controlHistoryView}/innerCompleteHistoryView"/>

<cti:standardPage module="operator" page="completeControlHistory">
    
    <table width="100%">
        <tr>
            <td style="text-align: right">
                <i:inline key=".viewTitle"/>
                <select onchange="javascript:updateControlEvents(this.options[this.options.selectedIndex].value)">
                    <c:forEach var="controlPeriod" items="${controlPeriods}" >
                        <option value="${controlPeriod}">
                            <i:inline key="${controlPeriod.formatKey}"/>
                        </option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <cti:msg key="${program.displayName}" var="controlEventsTitle" />
                <tags:boxContainer2 nameKey="controlEventsTitle" hideEnabled="false">
                    <div id="controlEventsDiv"><i:inline key=".loading"/></div>
                </tags:boxContainer2>
            </td>
        </tr>
    </table>

<script type="text/javascript">
Event.observe(window, 'load', function() {
    updateControlEvents('PAST_DAY');
});

function updateControlEvents(controlPeriod) {
    new Ajax.Updater('controlEventsDiv', '${innerViewUrl}', {
        'method': 'POST', //IE caches GET requests
        'parameters': { 
            'programId': '${program.programId}',
            'accountId': '${accountId}',
            'controlPeriod': controlPeriod
        }
    });    
}
</script>  
        
</cti:standardPage>    