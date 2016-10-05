<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="relayDetail">
        
    <input id="device-id" type="hidden" value="${deviceId}">
    
    <dt:pickerIncludes/>
    <cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css"/>
    
    <%-- Page Actions Button --%>
    <div id="page-actions" class="dn">
        
        <!-- Actions: Map Network -->
        <cti:url var="mapNetworkUrl" value="/stars/mapNetwork/home?deviceId=${deviceId}"/>
        <cm:dropdownOption key=".mapNetwork" href="${mapNetworkUrl}"/>
        
        <li class="divider"/>
        
        <!-- Other Collection Actions -->
        <cti:url var="url" value="/bulk/collectionActions">
            <cti:param name="collectionType" value="idList"/>
            <cti:param name="idList.ids" value="${deviceId}"/>
        </cti:url>
        <cm:dropdownOption key=".otherActions.label" href="${url}"/>
    </div>
    
    <tags:widgetContainer deviceId="${deviceId}" identify="false">
        <div class="column-12-12 clear">
            <div class="one column">
                <tags:widget bean="relayInformationWidget"/>
                
                <!-- Including deviceGroupWidget's resources here since this particular
                     widget is being added to the page via ajax  -->
                <cti:includeScript link="JQUERY_TREE"/>
                <cti:includeScript link="JQUERY_TREE_HELPERS"/>
                <cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>
                <tags:widget bean="deviceGroupWidget"/>
            </div>
        </div>
    </tags:widgetContainer>
    
</cti:standardPage>