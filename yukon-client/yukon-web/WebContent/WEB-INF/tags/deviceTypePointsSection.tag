<%@ attribute name="paoTypeMasks" required="true" type="com.cannontech.web.bulk.model.PaoTypeMasks"%>
<%@ attribute name="deviceType" required="false" type="com.cannontech.common.pao.PaoType"%>
<%@ attribute name="deviceTypeDeviceCollection" required="false" type="com.cannontech.common.bulk.collection.device.DeviceCollection"%>
<%@ attribute name="columnCount" required="true" type="java.lang.Integer"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:uniqueIdentifier prefix="sectionContainer_" var="thisId"/>
<div class="titledContainer sectionContainer id="${thisId}">

    <div class="titleBar sectionContainer_titleBar">
        <div class="titleBar sectionContainer_title">
        	
			<c:choose>
				<c:when test="${not empty deviceType}">
		            ${deviceType}
				</c:when>
				<c:otherwise>
			        <cti:msg key="yukon.common.device.bulk.addPointsHome.sharedPointsOptionLabel"/>
				</c:otherwise>
			</c:choose>

			<c:if test="${not empty deviceType}">
            	<cti:msg var="viewDefinitionDetailPopupTitle" key="yukon.common.device.bulk.addRemovePointsHome.viewDefinitionDetailPopupTitle"/>
            	<cti:url var="definitionUrl" value="/common/deviceDefinition.xml">	
            		<cti:param name="deviceType" value="${pageScope.deviceType}"/>
            	</cti:url>
            	
            	<a href="${definitionUrl}" title="${viewDefinitionDetailPopupTitle} style="text-decoration:none;">
					<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
				</a>
           	</c:if>
           	<c:if test="${not empty pageScope.deviceTypeDeviceCollection}">
            	<tags:selectedDevicesPopup  deviceCollection="${pageScope.deviceTypeDeviceCollection}" />
           	</c:if>
        </div>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content sectionContainer sectionContainer_content">
		<c:forEach var="pointTypesMapEntry" items="${paoTypeMasks.pointTypeToPointTemplateMap}">
			
			<c:set var="pointType" value="${pointTypesMapEntry.key}"/>
		
			<span class="smallBoldLabel"><cti:msg key="${pointType}"/></span>
			<br>
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

<br>
