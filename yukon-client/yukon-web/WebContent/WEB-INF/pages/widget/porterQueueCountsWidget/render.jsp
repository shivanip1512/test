<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:uniqueIdentifier var="id" />

<cti:msgScope paths="modules.dashboard,widgets.porterQueueCountsWidget">

<div id="${id}" class="js-porter-queue-counts-widget">

     <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".widgetParameter.selectPorts">
            <cti:globalSetting var="maxNumPorts" globalSettingType="PORTER_QUEUE_COUNTS_TREND_MAX_NUM_PORTS"/>
            <tags:pickerDialog id="portId${id}" type="portPicker" destinationFieldName="portIds"
                linkType="selection" selectionProperty="paoName" multiSelectMode="true"
                maxNumSelections="${maxNumPorts}" allowEmptySelection="false" initialIds="${portIds}" endEvent='okClicked'/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <div style="max-height: 500px;" class="js-chart js-initialize"></div>
    
    <span class="fr">
        <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
        <span class="js-last-updated fl" style="font-size:11px" title="${lastUpdatedMsg}"></span>
        <cti:msg2 key="yukon.web.widgets.forceUpdate" var="forceUpdateMsg"/>
        <cti:icon icon="icon-arrow-refresh" title="${forceUpdateMsg}" classes=" js-force-update cp"/>
    </span>

</div>

</cti:msgScope>