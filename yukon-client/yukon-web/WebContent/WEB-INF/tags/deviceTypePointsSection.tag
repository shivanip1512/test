<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="columnCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="deviceType" type="com.cannontech.common.pao.PaoType"%>
<%@ attribute name="deviceTypeDeviceCollection" type="com.cannontech.common.bulk.collection.device.model.DeviceCollection"%>
<%@ attribute name="paoTypeMasks" required="true" type="com.cannontech.web.bulk.model.PaoTypeMasks"%>

<cti:uniqueIdentifier prefix="section-container-" var="thisId"/>

<div class="titled-container section-container" id="${thisId}">

    <div class="title-bar clearfix">
            
        <c:choose>
            <c:when test="${not empty deviceType}">
                <h3 class="title">${deviceType}</h3>
            </c:when>
            <c:otherwise>
                <h3 class="title"><cti:msg key="yukon.common.device.bulk.addPointsHome.sharedPointsOptionLabel"/></h3>
            </c:otherwise>
        </c:choose>

        <c:if test="${not empty deviceType}">
            <cti:msg var="viewDefinitionDetailPopupTitle" key="yukon.common.device.bulk.addRemovePointsHome.viewDefinitionDetailPopupTitle"/>
            <cti:url var="definitionUrl" value="/common/deviceDefinition.xml">    
                <cti:param name="deviceType" value="${pageScope.deviceType}"/>
            </cti:url>
            <cti:icon icon="icon-help" href="${definitionUrl}" title="${viewDefinitionDetailPopupTitle}"/>
        </c:if>
        
        <c:if test="${not empty pageScope.deviceTypeDeviceCollection}">
            <tags:selectedDevicesPopup  deviceCollection="${pageScope.deviceTypeDeviceCollection}" />
        </c:if>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content">
    
        <c:forEach var="pointTypesMapEntry" items="${paoTypeMasks.pointTypeToPointTemplateMap}">
            
            <c:set var="pointType" value="${pointTypesMapEntry.key}"/>
        
            <div class="stacked"><strong><cti:msg key="${pointType}"/></strong></div>
            
            <c:choose>
                <c:when test="${not empty deviceType}">
                    <tags:pointsCheckboxTable deviceType="${deviceType.deviceTypeId}" pointTemplates="${pointTypesMapEntry.value}" columnCount="${columnCount}"></tags:pointsCheckboxTable>
                </c:when>
                <c:otherwise>
                    <tags:pointsCheckboxTable deviceType="0" pointTemplates="${pointTypesMapEntry.value}" columnCount="${columnCount}"></tags:pointsCheckboxTable>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    
    </div>    
</div>