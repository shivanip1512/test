<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css" force="true"/>
<cti:includeCss link="/resources/js/lib/dynatree/skin/device.group.css" force="true"/>

<cti:includeScript link="JQUERY_TREE" force="true"/>
<cti:includeScript link="JQUERY_TREE_HELPERS" force="true"/>
<%-- <cti:includeScript link="/resources/js/widgets/yukon.widget.dataCollection.js"/> --%>

<script type="text/javascript">
function setSelectedDeviceGroup() {
    var changedGroupName = $('#changedGroupName').val();
    $('#changeDeviceGroupLink').html(changedGroupName);
    $('#groupName').val(changedGroupName);
 //   _update(true);
}

</script>
<tags:nameValueContainer2>
    <tags:nameValue2 nameKey="${param.parameterKey}">
        <cti:list var="group"><cti:item value="${param.parameterValue}"/></cti:list>

        <cti:msg2 var="okButton" key="yukon.common.okButton"/>
        <cti:msg2 var="cancelButton" key="yukon.common.cancel"/>
        <cti:msg2 var="selectGroupText" key="yukon.common.selectGroup.title"/>

        <c:if test="${param.parameterValue == ''}">
           <c:set var="linkText" value="${selectGroupText}"/>
        </c:if> 
        <c:if test="${param.parameterValue != ''}">
           <c:set var="linkText" value="${param.parameterValue}"/>
        </c:if>
        <a href="javascript:void(0);" id="changeDeviceGroupLink">${linkText}</a>
        <cti:url var="groupDataUrl" value="/group/editor/allGroupsJson"/>
    </tags:nameValue2>

    <input type="hidden" id="groupName" name="groupName" value="${group}">
    <jsTree:nodeValueSelectingPopupTree fieldId="changedGroupName"
        fieldName="changedGroupName"
        fieldValue="${group}"
        nodeValueName="groupName"
        highlightNodePath = "${group}"
        submitButtonText="${okButton}"
        cancelButtonText="${cancelButton}"
        id="changeDeviceGroupTree"
        triggerElement="changeDeviceGroupLink"
        dataUrl="${groupDataUrl}"
        title="${selectGroupText}"
        submitCallback="setSelectedDeviceGroup();"
        includeControlBar="true" />
</tags:nameValueContainer2>