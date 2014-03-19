<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:url var="controlHistoryView" value="/stars/operator/program/controlHistory"/>
<cti:url var="innerViewUrl" value="${controlHistoryView}/innerCompleteHistoryView"/>

<cti:standardPage module="operator" page="completeControlHistory">
    
    <c:set var="controls">
        <i:inline key=".viewTitle"/>
        <select onchange="updateControlEvents(this.options[this.options.selectedIndex].value)">
            <c:forEach var="controlPeriod" items="${controlPeriods}" >
                <option value="${controlPeriod}">
                    <i:inline key="${controlPeriod.formatKey}"/>
                </option>
            </c:forEach>
        </select>
    </c:set>
    
    <tags:sectionContainer2 nameKey="controlEventsTitle" controls="${controls}" styleClass="form-controls">
        <div id="controlEventsDiv" class="f-block-this"><i:inline key=".loading"/></div>
    </tags:sectionContainer2>

<script type="text/javascript">
$(function() {
    updateControlEvents('PAST_DAY');
});

function updateControlEvents(controlPeriod) {
    yukon.ui.elementGlass.show($('#controlEventsDiv'));
    $.ajax({
        url: '${innerViewUrl}',
        method: 'POST',
        data: { 
            'programId': '${program.programId}',
            'past': '${past}',
            'accountId': '${accountId}',
            'controlPeriod': controlPeriod
        }
    }).done(function(data){
        $('#controlEventsDiv').html(data);
    }).always(function() {
        yukon.ui.elementGlass.hide($('#controlEventsDiv'));
    });
}
</script>  
        
</cti:standardPage>    