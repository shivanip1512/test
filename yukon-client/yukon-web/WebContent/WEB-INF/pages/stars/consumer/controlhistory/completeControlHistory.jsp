<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:url var="controlHistoryView" value="/spring/stars/consumer/controlhistory"/>
<cti:url var="innerViewUrl" value="${controlHistoryView}/innerCompleteHistoryView"/>

<cti:standardPage module="consumer" page="completecontrolhistory">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.completecontrolhistory.header" /></h3>
    <table width="100%">
        <tr>
            <td>
                <table class="programTitle">
                    <tr>
                        <td rowspan="2">
                            <img src="../../../../WebConfig/${program.applianceCategoryLogo}"></img>
                        </td>
                        <td>
                            <b><cti:msg key="${program.displayName}"></cti:msg></b>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${totalDuration > 0}">
                                    <cti:formatDuration var="formattedDuration" type="HM" value="${totalDuration}"/>
                                    <cti:msg key="yukon.dr.consumer.completecontrolhistory.todaysProgramControl" argument="${formattedDuration}"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:msg key="yukon.dr.consumer.completecontrolhistory.noProgramControl"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td align="right">
                <br>
                <cti:msg key="yukon.dr.consumer.completecontrolhistory.viewTitle"/>
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
                <ct:boxContainer title="${controlEventsTitle}" hideEnabled="false">
                    <div id="controlEventsDiv"></div>
                </ct:boxContainer>
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