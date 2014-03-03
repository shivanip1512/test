<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:url var="controlHistoryView" value="/stars/consumer/controlhistory"/>
<cti:url var="innerViewUrl" value="${controlHistoryView}/innerCompleteHistoryView"/>

<cti:standardPage module="consumer" page="completecontrolhistory">
    <cti:standardMenu />
    <c:set var="controls">    
        <i:inline key="yukon.dr.consumer.completecontrolhistory.viewTitle"/>
        <select onchange="updateControlEvents(this.options[this.options.selectedIndex].value)">
            <c:forEach var="controlPeriod" items="${controlPeriods}" >
                <option value="${controlPeriod}">
                    <i:inline key="${controlPeriod.formatKey}"/>
                </option>
            </c:forEach>
        </select>
    </c:set>

    <cti:msg key="${program.displayName}" htmlEscape="true" var="controlEventsTitle"/>
    <tags:sectionContainer title="${controlEventsTitle}" escapeTitle="true" controls="${controls}" styleClass="form-controls">
        <div id="controlEventsDiv" class="f-block-this"><cti:msg key="yukon.dr.consumer.completecontrolhistory.loading"/></div>
    </tags:sectionContainer>

    <div class="page-action-area">
        <cti:msg key="yukon.dr.consumer.completecontrolhistory.back" var="backText"/>
        <cti:button label="${backText}" onclick="location.href='${controlHistoryView}';"/>
    </div>
    
<script type="text/javascript">
jQuery(function() {
    updateControlEvents('PAST_DAY');
});

function updateControlEvents(controlPeriod) {
    yukon.ui.elementGlass.show(jQuery('#controlEventsDiv'));
    jQuery.ajax({
        url: '${innerViewUrl}',
        method: 'POST',
        data: { 
            'programId': '${program.programId}',
            'controlPeriod': controlPeriod
        }
    }).done(function(data){
        jQuery('#controlEventsDiv').html(data);
    }).always(function() {
        yukon.ui.elementGlass.hide(jQuery('#controlEventsDiv'));
    });
}
</script>  
        
</cti:standardPage>    