<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:url var="controlHistoryView" value="/spring/stars/consumer/controlhistory"/>
<cti:url var="innerViewUrl" value="${controlHistoryView}/innerCompleteHistoryView"/>

<cti:standardPage module="consumer" page="completecontrolhistory">
    <cti:standardMenu />
    <cti:includeCss link="/WebConfig/yukon/styles/controlHistory.css"/>
    
    <h3><i:inline key="yukon.dr.consumer.completecontrolhistory.header" /></h3>
    <table width="100%">
        <tr>
            <td align="right">
                <br>
                <i:inline key="yukon.dr.consumer.completecontrolhistory.viewTitle"/>
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
                <cti:msg key="yukon.dr.consumer.completecontrolhistory.controlEventsTitle" var="controlEventsTitle" />
                <cti:msg key="${program.displayName}" var="controlEventsTitle" />
                <tags:boxContainer title="${controlEventsTitle}" escapeTitle="true" hideEnabled="false">
                    <div id="controlEventsDiv"><cti:msg key="yukon.dr.consumer.completecontrolhistory.loading"/></div>
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
jQuery(function() {
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