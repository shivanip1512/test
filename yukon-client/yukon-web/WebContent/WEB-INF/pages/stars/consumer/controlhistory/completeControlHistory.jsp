<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:url var="controlHistoryView" value="/spring/stars/consumer/controlhistory"/>
<cti:url var="innerViewUrl" value="${controlHistoryView}/innerCompleteHistoryView"/>

<cti:standardPage module="consumer" page="completeControlHistory">
    <cti:standardMenu />
    
    <h3><i18n:inline key="yukon.dr.consumer.completecontrolhistory.header" /></h3>
    <table width="100%">
        <tr>
            <td align="right">
                <br>
                <i18n:inline key="yukon.dr.consumer.completecontrolhistory.viewTitle"/>
                <select onchange="javascript:updateControlEvents(this.options[this.options.selectedIndex].value)">
                    <option value="PAST_DAY"><cti:msg key="yukon.dr.consumer.completecontrolhistory.view.pastDay"/></option>
                    <option value="PAST_WEEK"><cti:msg key="yukon.dr.consumer.completecontrolhistory.view.pastWeek"/></option>
                    <option value="PAST_YEAR"><cti:msg key="yukon.dr.consumer.completecontrolhistory.view.pastYear"/></option>
                    <option value="ALL"><cti:msg key="yukon.dr.consumer.completecontrolhistory.view.all"/></option>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <cti:msg key="yukon.dr.consumer.completecontrolhistory.controlEventsTitle" var="controlEventsTitle" />
                <cti:msg key="${program.displayName}" var="controlEventsTitle" />
                <tags:boxContainer title="${controlEventsTitle}" hideEnabled="false">
                    <div id="controlEventsDiv"></div>
                </tags:boxContainer>
            </td>
        </tr>
        <tr>
            <td align="right">
                <br>
                <input type="button" value="<cti:msg key="yukon.dr.consumer.completecontrolhistory.back"/>" onclick="javascript:location.href='${controlHistoryView}';"/>
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
            'controlPeriod': controlPeriod
        }
    });    
}
</script>  
        
</cti:standardPage>    