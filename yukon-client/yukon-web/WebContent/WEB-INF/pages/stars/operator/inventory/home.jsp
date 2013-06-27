<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<cti:standardPage module="operator" page="inventory.home">

<cti:checkEnergyCompanyOperator showError="true" >
<cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
<cti:url var="action" value="/stars/operator/inventory/setupFilterRules"/>

<cti:msg2 key="yukon.web.defaults.upload" var="uploadText"/>
<cti:msg2 key="yukon.common.cancel" var="cancelText"/>

<script type="text/javascript">
function submitSelectionForm(items) {
    jQuery('#selectByInventoryPickerForm').submit();
    return true;
}
function addMeter() {
    jQuery('#addMeterForm').submit();
    return true;
}

function showFileUpload() {
	var buttons = [{text: '${cancelText}', click: function() {jQuery('#fileUploadPopup input[type=file]').val('');jQuery(this).dialog("close");}},
	               {text: '${uploadText}', click: function() {jQuery('#fileUploadPopup form').submit();}, class: 'primary action'}];
    jQuery('#fileUploadPopup').dialog({width:500, 'buttons': buttons});
}
</script>
        
        <tags:widgetContainer identify="false">
        
        <div class="column_12_12">
            <div class="one column">
            
                <tags:boxContainer2 nameKey="deviceSelection" hideEnabled="false">
                    <table class="inventoryActionsTable">
                        <%-- INVENTORY PICKER--%>
                        <tr>
                            <td>
                                <form id="selectByInventoryPickerForm" action="/stars/operator/inventory/inventoryActions" method="post">
                                    <input type="hidden" name="collectionType" value="idList"/>
                                    <input type="hidden" name="idList.ids" id="inventoryIds"/>
                                    <tags:pickerDialog type="lmHardwareBasePicker" 
                                                        extraArgs="${energyCompanyId}" 
                                                        id="inventoryPicker" 
                                                        multiSelectMode="true" 
                                                        destinationFieldId="inventoryIds" 
                                                        endAction="submitSelectionForm" 
                                                        nameKey="selectInventoryButton"><i:inline key=".selectInventoryButton.label"/></tags:pickerDialog>
                                </form>
                            </td>
                            <td><i:inline key=".selectInventoryDescription"/></td>
                        </tr>
                        <%-- INVENTORY FILTER --%>
                        <tr>
                            <td><a href="setupFilterRules"><i:inline key=".selectFilterButton.label"/></a></td>
                            <td><i:inline key=".selectFilterDescription"/></td>
                        </tr>
                        <%-- FILE UPLOAD --%>
                        <tr>
                            <td>
                                <a href="javascript:showFileUpload();"><i:inline key=".selectFileButton.label"/></a>
                                <div id="fileUploadPopup" title="${fileUploadTitle}" class="dn">
                                    <cti:url var="submitUrl" value="/stars/operator/inventory/uploadFile"/>
                                    <form method="post" action="${submitUrl}" enctype="multipart/form-data">
                                        <tags:nameValueContainer2>
                                            <tags:nameValue2 nameKey=".fileLabel">
                                                <input type="file" id="fileUpload.dataFile" name="fileUpload.dataFile" size="40">
                                                <input type="hidden" name="collectionType" value="fileUpload">
                                                <input type="hidden" name="fileUpload.energyCompanyId" value="${energyCompanyId}">
                                            </tags:nameValue2>
                                        </tags:nameValueContainer2>
                                    </form>
                                </div>
                            </td>
                            <td><i:inline key=".selectFileDescription"/></td>
                        </tr>
                    </table>
                </tags:boxContainer2>
                
                <%-- DEVICE RECONFIG MONITORS WIDGET --%>
                <tags:widget bean="deviceReconfigMonitorsWidget"/>
                
                <%-- COMMAND SCHEDULE WIDGET --%>
                <tags:widget bean="commandScheduleWidget" />
            
            </div>
            
            <div class="two column nogutter">
                    <%--SEARCH --%>
                    <c:if test="${showSearch}">
                        <%@ include file="search.jsp" %>
                    </c:if>
                    
                    <c:if test="${showActions}">
                    
                        <tags:boxContainer2 nameKey="actions">
                            <ul class="buttonStack">
                                <li>
                                    <form action="creationPage" method="post">
                                        <cti:button nameKey="addHardware" id="addHardwareBtn" type="submit"/>
                                        <select name="hardwareTypeId">
                                            <c:forEach items="${addHardwareTypes}" var="deviceType">
                                                <option value="${deviceType.entryID}"><spring:escapeBody htmlEscape="true">${deviceType.entryText}</spring:escapeBody></option>
                                            </c:forEach>
                                        </select>
                                    </form>
                                </li>
                                <li>
                                    <form action="abr/view" method="post">
                                        <cti:button nameKey="addHardwareByRange" id="addHardwareByRangeBtn" type="submit"/>
                                        <select name="hardwareTypeId">
                                            <c:forEach items="${addHardwareByRangeTypes}" var="deviceType">
                                                <option value="${deviceType.entryID}"><spring:escapeBody htmlEscape="true">${deviceType.entryText}</spring:escapeBody></option>
                                            </c:forEach>
                                        </select>
                                    </form>
                                </li>
                                
                                <c:if test="${showAddMeter}">
                                    <li>
                                        <form action="addMeter/view" id="addMeterForm">
                                            <tags:pickerDialog id="addMeterPicker"
                                                                type="drUntrackedMctPicker"
                                                                nameKey="addMeter"
                                                                linkType="button"
                                                                destinationFieldName="mctId"
                                                                allowEmptySelection="false"
                                                                multiSelectMode="false"
                                                                immediateSelectMode="true"
                                                                endAction="addMeter"/>
                                        </form>
                                    </li>
                                </c:if>
                            </ul>
                        </tags:boxContainer2>
                    
                    </c:if>
                        
                    <c:if test="${showLinks}">
                        <tags:boxContainer2 nameKey="links">
                            <a href="/stars/operator/inventory/zbProblemDevices/view"><i:inline key=".zbProblemDevices" /></a>
                        </tags:boxContainer2>
                    </c:if>
                </div>
            </div>
        </tags:widgetContainer>
    </cti:checkEnergyCompanyOperator>
</cti:standardPage>