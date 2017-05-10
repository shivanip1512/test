<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:uniqueIdentifier var="id" />

<cti:msgScope paths="modules.dashboard,widgets.dataCollectionWidget">

<div class="js-data-collection-widget">

     <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".widgetParameter.deviceGroup">
            <cti:list var="group"><cti:item value="${deviceGroup}"/></cti:list>
            <tags:deviceGroupPicker inputName="groupName" inputValue="${group}" classes="js-group-name"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <div style="max-height: 220px;" class="js-pie-chart js-initialize"></div>
    
    <input type="checkbox" id="includeDisabled" <c:if test="${includeDisabled}">checked="checked"</c:if>/><cti:msg2 key=".widgetParameter.includeDisabled"/>
    <span class="fr"><cti:button nameKey="forceUpdate" classes="js-force-update"/><cti:icon icon="icon-clock" classes="cp" data-popup=".js-last-updated-popup${id}"/></span>
    
        <%-- LAST UPDATED POPUP --%>
    <div class="dn js-last-updated-popup${id}" data-dialog data-title="<cti:msg2 key=".lastUpdated"/>">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".lastUpdated">
                <span class="js-last-updated"></span>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </div>

</div>

</cti:msgScope>

<cti:includeScript link="HIGH_STOCK"/>
<cti:includeScript link="/resources/js/widgets/yukon.widget.dataCollection.js"/>

