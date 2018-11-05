<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:uniqueIdentifier var="id" />

<cti:msgScope paths="modules.dashboard,widgets.dataCollectionWidget,modules.dr">
    <div class="js-asset-availability-widget">
        <c:forEach var="status" items="${statuses}">
            <input type="hidden" class="js-asset-${status}" value="<cti:msg2 key=".assetDetails.status.${status}"/>"/>
        </c:forEach>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".widgetParameter.controlAreaOrLMProgramOrScenarioId">
                <tags:pickerDialog id="controlAreaOrLMProgramOrScenarioPicker_${id}"
                                   type="controlAreaOrLMProgramOrScenarioPicker"
                                   linkType="selection"
                                   multiSelectMode="false"
                                   selectionProperty="paoName"
                                   allowEmptySelection="false"
                                   initialId="${controlAreaOrLMProgramOrScenarioId}"
                                   destinationFieldName="areaOrLMProgramOrScenarioId"
                                   endEvent="yukon:asset:availability:selection"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div style="max-height: 200px;" class="js-pie-chart js-initialize"></div>
        
        <span class="fr">
            <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
            <span class="js-last-updated fl" style="font-size:11px" title="${lastUpdatedMsg}"></span>
            <cti:button renderMode="image" icon="icon-arrow-refresh" classes="js-update-asset-availability"></cti:button>
        </span>
    </div>
</cti:msgScope>