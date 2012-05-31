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

<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<c:choose>
	<c:when test="${not empty nameKey}">
        <c:set var="the_paths" value="${nameKey},.${nameKey},device.bulk.selectDevicesTabbed.${nameKey},device.bulk.selectDevicesTabbed"/>
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
									submitCallback="WaterLeakReport.group_selected_callback();" />
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
							    <input type="hidden" id="picker_meters" name="idList.ids" value="${deviceCollection.collectionParameters['idList.ids']}"/>
								<tags:pickerDialog id="selectDevicesPicker" 
	                                type="meterPicker"
	                                destinationFieldId="picker_meters"
									destinationFieldName="idList.ids"
									multiSelectMode="true"
									linkType="selection"
									selectionProperty="paoName"
									endAction="WaterLeakReport.individual_selected_callback"
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
			<div class="errorRed">deviceCollection must be initialized</div>
		</c:otherwise>
	</c:choose>
</cti:msgScope>