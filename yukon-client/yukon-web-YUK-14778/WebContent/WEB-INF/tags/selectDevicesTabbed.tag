<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="deviceCollection" required="true" type="java.lang.Object" %>
<%@ attribute name="nameKey" %>
<%@ attribute name="pickerType" description="The type of the device picker." %>
<%@ attribute name="groupCallback" description="The callback fired when a group is selected." %>
<%@ attribute name="deviceCallback" description="The callback fired when an individual device is selected." %>
<%@ attribute name="uniqueId" description="Used as part of an html id attribute and part of a javascript function name. 
                                           Should not contain invalid characters like '.' or '-'." %>

<c:set var="type" value="${deviceCollection.collectionParameters['collectionType']}"/>
<c:set var="isGroup" value="${type == 'group'}"/>
    
<c:choose>
    <c:when test="${not empty nameKey}">
        <c:set var="paths" value=".${nameKey},device.bulk.selectDevicesTabbed.${nameKey},device.bulk.selectDevicesTabbed"/>
    </c:when>
    <c:otherwise>
        <c:set var="paths" value="device.bulk.selectDevicesTabbed"/>
    </c:otherwise>
</c:choose>

<cti:msgScope paths="${paths}">
    <cti:msg2 var="group" key=".group"/>
    <cti:msg2 var="selected" key=".selected"/>
    <input id="collection-type-${uniqueId}" type="hidden" name="collectionType" value="${isGroup ? 'group' : 'idList'}">
    <div class="button-group button-group-toggle fl" style="margin-right:10px;">
        <cti:button label="${group}" icon="icon-folder" classes="${isGroup ? 'on M0' : 'M0'}" data-show="#group-selector-${uniqueId}"/>
        <cti:button label="${selected}" icon="icon-table" classes="${!isGroup ? 'on' : ''}" data-show="#device-selector-${uniqueId}"/>
    </div>
    <%-- GROUP --%>
    <div id="group-selector-${uniqueId}" class="dib fl form-control ${!isGroup ? 'dn' : ''}">
        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson"/>
        <tags:deviceGroupNameSelector
            fieldName="group.name" 
            fieldId="groupName_${uniqueId}"
            fieldValue="${deviceCollection.collectionParameters['group.name']}"
            dataJson="${groupDataJson}" 
            linkGroupName="false"
            submitCallback="${groupCallback}"/>
    </div>
    <%-- SELECTED --%>
    <div id="device-selector-${uniqueId}" class="dib fl form-control ${isGroup ? 'dn' : ''}">
        <input type="hidden" id="selectDevicesInput_${uniqueId}" name="idList.ids" 
            value="${deviceCollection.collectionParameters['idList.ids']}">
        <tags:pickerDialog id="selectDevicesPicker_${uniqueId}"
            type="${pickerType}"
            destinationFieldId="selectDevicesInput_${uniqueId}"
            destinationFieldName="idList.ids"
            multiSelectMode="true"
            linkType="selection"
            selectionProperty="paoName"
            endAction="${deviceCallback}"/>
    </div>
</cti:msgScope>