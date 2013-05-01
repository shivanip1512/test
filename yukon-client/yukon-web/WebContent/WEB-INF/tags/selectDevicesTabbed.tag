<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="deviceCollection" required="true" type="java.lang.Object"%>
<%@ attribute name="nameKey"%>
<%@ attribute name="tabClass"%>
<%@ attribute name="hideGroup" type="java.lang.Boolean"%>
<%@ attribute name="hideIndividual" type="java.lang.Boolean"%>
<%@ attribute name="showCount" type="java.lang.Boolean"%>
<%@ attribute name="individualPickerType" type="java.lang.String" description="The type of the Individual picker. This defaults to meterPicker."%>
<%@ attribute name="groupSelectedCallback" description="The callback called when a group is selected." %>
<%@ attribute name="individualSelectedCallback" description="The callback called when an individual device is selected." %>
<%@ attribute name="uniqueId" %>

<c:set var="pickerType" value="${individualPickerType}"/>
<c:if test="${empty pickerType}">
	<c:set var="pickerType" value="meterPicker"/>
</c:if>

<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<c:choose>
	<c:when test="${not empty nameKey}">
        <c:set var="the_paths" value=".${nameKey},device.bulk.selectDevicesTabbed.${nameKey},device.bulk.selectDevicesTabbed"/>
	</c:when>
	<c:otherwise>
        <c:set var="the_paths" value="device.bulk.selectDevicesTabbed"/>
	</c:otherwise>
</c:choose>

<cti:msgScope paths="${the_paths}">
    <cti:msg2 var="groupTabTitle" key=".group"/>
    <cti:msg2 var="individualTabTitle" key=".individual"/>
	<c:choose>
		<c:when test="${deviceCollection != null}">
			<c:set var="isDeviceGroup" value="${deviceCollection.collectionParameters['collectionType'] == 'group'}" />
			<c:set var="isIdList" value="${deviceCollection.collectionParameters['collectionType'] == 'idList'}" />
			<cti:tabbedContentSelector>
				<c:if test="${hideGroup == null || !hideGroup}">
					<!-- GROUP -->
					<cti:tabbedContentSelectorContent selectorName="${groupTabTitle}" cssClass="${tabClass}"
						initiallySelected="${isDeviceGroup}">
						<tags:nameValueContainer2>
							<tags:nameValue2 nameKey=".deviceGroup">
								<cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
								<tags:deviceGroupNameSelector fieldName="group.name"
									fieldValue="${deviceCollection.collectionParameters['group.name']}"
									dataJson="${groupDataJson}" linkGroupName="false"
									submitCallback="${groupSelectedCallback}" />
							</tags:nameValue2>
							<c:if test="${showCount}">
							    <tags:nameValue2 nameKey=".devicesCount">
							        ${isDeviceGroup ? deviceCollection.deviceCount : 0}
							    </tags:nameValue2>
						    </c:if>
						</tags:nameValueContainer2>
					</cti:tabbedContentSelectorContent>
				</c:if>
				<c:if test="${hideIndividual == null || !hideIndividual}">
					<!-- INDIVIDUAL -->
					<cti:tabbedContentSelectorContent selectorName="${individualTabTitle}" cssClass="${tabClass}"
					    initiallySelected="${isIdList}">
						<tags:nameValueContainer2>
							<tags:nameValue2 nameKey=".devices">
							    <input type="hidden" id="picker_meters_${uniqueId}" name="idList.ids" value="${deviceCollection.collectionParameters['idList.ids']}"/>
								<tags:pickerDialog id="selectDevicesPicker_${uniqueId}" 
	                                type="${pickerType}"
	                                destinationFieldId="picker_meters_${uniqueId}"
									destinationFieldName="idList.ids"
									multiSelectMode="true"
									linkType="selection"
									selectionProperty="paoName"
									endAction="${individualSelectedCallback}"
									styleClass="selectDevicesPicker" />
							</tags:nameValue2>
	                        <c:if test="${showCount}">
							    <tags:nameValue2 nameKey=".devicesCount">
	                                ${isIdList ? deviceCollection.deviceCount : 0}
							    </tags:nameValue2>
						    </c:if>
						</tags:nameValueContainer2>
					</cti:tabbedContentSelectorContent>
				</c:if>
			</cti:tabbedContentSelector>
		</c:when>
		<c:otherwise>
			<div class="errorMessage">deviceCollection must be initialized</div>
		</c:otherwise>
	</c:choose>
</cti:msgScope>