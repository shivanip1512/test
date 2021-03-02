<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:uniqueIdentifier var="id" />

<cti:msgScope paths="modules.dashboard,widgets.dataCollectionWidget">

<div class="js-data-collection-widget">

    <c:forEach var="range" items="${rangeTypes}">
        <input type="hidden" class="js-${range}" value="<cti:msg2 key="yukon.web.modules.amr.dataCollection.detail.rangeType.${range}"/>"></input>
    </c:forEach>

     <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".widgetParameter.deviceGroup">
             <a href="javascript:void(0);" id="changeDeviceGroupLink">${deviceGroup}</a>
             <input class="js-group-name" type="hidden" id="groupName" name="groupName" value="${deviceGroup}">
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <div id="error-msg" class="user-message error dn"></div>
    
    <div style="max-height: 200px;" class="js-pie-chart js-initialize"></div>
    
    <input type="checkbox" id="includeDisabled" class="js-include-disabled" <c:if test="${includeDisabled}">checked="checked"</c:if>/><cti:msg2 key=".widgetParameter.includeDisabled"/>
    <span class="fr">
        <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
        <span class="js-last-updated fl" style="font-size:11px" title="${lastUpdatedMsg}"></span>
        <cti:button renderMode="image" icon="icon-arrow-refresh" classes="js-update-data-collection"></cti:button>
    </span>

	<cti:url var="groupDataUrl" value="/group/editor/allGroupsJson"/>
    <cti:msg2 var="okButton" key="yukon.common.okButton"/>
    <cti:msg2 var="cancelButton" key="yukon.common.cancel"/>
    <cti:msg2 var="popupTitle" key="yukon.common.selectGroup.title"/>
    
    
    <jsTree:nodeValueSelectingPopupTree fieldId="changedGroupName"
        fieldName="changedGroupName"
        fieldValue="${deviceGroup}"
        nodeValueName="groupName"
        highlightNodePath = "${deviceGroup}"
        submitButtonText="${okButton}"
        cancelButtonText="${cancelButton}"
        id="changeDeviceGroupTree"
        triggerElement="changeDeviceGroupLink"
        dataUrl="${groupDataUrl}"
        title="${popupTitle}"
        submitCallback="yukon.widget.dataCollection.setSelectedDeviceGroup();"
        includeControlBar="true" />
</div>

</cti:msgScope>

