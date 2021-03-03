<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css" force="true"/>
<cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css" force="true"/>

<cti:includeScript link="JQUERY_TREE" force="true"/>
<cti:includeScript link="JQUERY_TREE_HELPERS" force="true"/>

<script type="text/javascript">
function setSelectedDeviceGroup(uniqueId) {
    var changedGroupName = $('#changedGroupName_' + uniqueId).val();
    $('#changeDeviceGroupLink_' + uniqueId).html(changedGroupName);
    $('#groupName_' + uniqueId).val(changedGroupName);
}
</script>

<cti:uniqueIdentifier var="uniqueId"/>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey="${param.parameterKey}">
        <c:set var="group" value="${fn:escapeXml(param.parameterValue)}"/>

        <cti:msg2 var="okButton" key="yukon.common.okButton"/>
        <cti:msg2 var="cancelButton" key="yukon.common.cancel"/>
        <cti:msg2 var="selectGroupText" key="yukon.common.selectGroup.title"/>

        <c:if test="${param.parameterValue == ''}">
           <c:set var="linkText" value="${selectGroupText}"/>
        </c:if> 
        <c:if test="${param.parameterValue != ''}">
           <c:set var="linkText" value="${group}"/>
        </c:if>
        <a href="javascript:void(0);" id="changeDeviceGroupLink_${uniqueId}">${linkText}</a>
        <cti:url var="groupDataUrl" value="/group/editor/allGroupsJson?groupName=${group}"/>
    </tags:nameValue2>

    <input type="hidden" id="groupName_${uniqueId}" name="${param.path}.parameters['${param.parameterName}']" value="${group}">
    <jsTree:nodeValueSelectingPopupTree 
        fieldId="changedGroupName_${uniqueId}"
        fieldName="changedGroupName_${uniqueId}"
        fieldValue="${group}"
        nodeValueName="groupName"
        submitButtonText="${okButton}"
        cancelButtonText="${cancelButton}"
        id="changeDeviceGroupTree_${uniqueId}"
        triggerElement="changeDeviceGroupLink_${uniqueId}"
        loadDataOnTrigger="true"
        dataUrl="${groupDataUrl}"
        title="${selectGroupText}"
        submitCallback="setSelectedDeviceGroup(${uniqueId});"
        includeControlBar="true" />
</tags:nameValueContainer2>