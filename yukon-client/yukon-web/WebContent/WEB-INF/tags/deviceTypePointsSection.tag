<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="pointTypesMap" required="true" type="java.util.Map"%>
<%@ attribute name="deviceType" required="true" type="java.lang.Integer"%>
<%@ attribute name="deviceTypeEnum" required="false" type="com.cannontech.common.pao.PaoType"%>
<%@ attribute name="deviceTypeDeviceCollection" required="false" type="com.cannontech.common.bulk.collection.DeviceCollection"%>
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
            ${title} 
            <c:if test="${deviceType > 0}">
            	<cti:msg var="viewDefinitionDetailPopupTitle" key="yukon.common.device.bulk.addRemovePointsHome.viewDefinitionDetailPopupTitle"/>
            	<cti:url var="definitionUrl" value="/spring/common/deviceDefinition.xml">	
            		<cti:param name="deviceType" value="${pageScope.deviceTypeEnum}"/>
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
        
		<c:forEach var="pointTypesMapEntry" items="${pointTypesMap}">
			
			<c:set var="pointType" value="${pointTypesMapEntry.key}"/>
		
			<span class="smallBoldLabel"><cti:msg key="${pointType}"/></span>
			<br>
			<tags:pointsCheckboxTable deviceType="${deviceType}" pointTemplates="${pointTypesMap[pointType]}" columnCount="${columnCount}"></tags:pointsCheckboxTable>
		
		
		</c:forEach>
	
    </div>    

</div>


<br>