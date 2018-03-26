<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:uniqueIdentifier var="id" />

<cti:msgScope paths="modules.dashboard,widgets.dataCollectionWidget">

<div class="js-data-collection-widget">

    <c:forEach var="range" items="${rangeTypes}">
        <input type="hidden" class="js-${range}" value="<cti:msg2 key="yukon.web.modules.amr.dataCollection.detail.rangeType.${range}"/>"></input>
    </c:forEach>

     <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".widgetParameter.deviceGroup">
            <cti:list var="group"><cti:item value="${deviceGroup}"/></cti:list>
            <tags:deviceGroupPicker inputName="groupName" inputValue="${group}" classes="js-group-name"/>
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

</div>

</cti:msgScope>

