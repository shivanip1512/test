<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext"%>

<%@ attribute name="groupDataJson" required="true" type="java.lang.String"%>

<cti:uniqueIdentifier var="uniqueId" prefix="composedGroupItem_"/>

<c:set var="selectDeviceGroupText" value="Select Device Group"/>
<c:set var="selectDeviceGroupChooseText" value="Select Group"/>
<c:set var="selectDeviceGroupCancelText" value="Cancel"/>

<td style="text-align:center;">
	<input type="checkbox">
</td>
<td>
	<input type="text" id="deviceGroupNameField_${uniqueId}" value="${uniqueId}" readonly style="width:400px;">
	<input type="button" id="chooseGroupButton_${uniqueId}" value="Choose Group">
</td>

<ext:nodeValueSelectingPopupTree fieldId="deviceGroupNameField_${uniqueId}"
                                 fieldName="deviceGroupNameField_${uniqueId}"
                                 nodeValueName="groupName"
                                 submitButtonText="${selectDeviceGroupChooseText}"
                                 cancelButtonText="${selectDeviceGroupCancelText}"
                                 
                                 id="selectGroupTree_${uniqueId}"
                                 treeAttributes="{}"
                                 triggerElement="chooseGroupButton_${uniqueId}"
                                 dataJson="${groupDataJson}"
                                 title="${selectDeviceGroupText}"
                                 width="432"
                                 height="600" />