<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:url var="controlHistoryView" value="/spring/stars/operator/program/controlHistory"/>
<cti:url var="innerViewUrl" value="${controlHistoryView}/innerCompleteHistoryView"/>

<cti:standardPage module="operator" page="completeControlHistory">
    <cti:standardMenu />
    
    <tags:sectionContainer2 key="header">
    <table width="100%">
        <tr>
            <td align="right">
                <i18n:inline key=".viewTitle"/>
                <select onchange="javascript:updateControlEvents(this.options[this.options.selectedIndex].value)">
                    <option value="PAST_DAY"><i18n:inline key=".pastDay"/></option>
                    <option value="PAST_WEEK"><i18n:inline key=".pastWeek"/></option>
                    <option value="PAST_YEAR"><i18n:inline key=".pastYear"/></option>
                    <option value="ALL"><i18n:inline key=".all"/></option>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <cti:msg key="${program.displayName}" var="controlEventsTitle" />
                <tags:boxContainer2 keyBase="controlEventsTitle" hideEnabled="false">
                    <div id="controlEventsDiv"></div>
                </tags:boxContainer2>
            </td>
        </tr>
    </table>
    </tags:sectionContainer2>
    
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