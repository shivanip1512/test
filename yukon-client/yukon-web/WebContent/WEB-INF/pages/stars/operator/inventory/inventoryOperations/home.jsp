<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:url var="action" value="/spring/stars/operator/inventory/inventoryOperations/setupFilterRules"/>

<cti:standardPage module="operator" page="inventoryOperations">

    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    
    <script type="text/javascript">
    function submitSelectionForm(items) {
        $('selectByInventoryPickerForm').submit();
        return true;
    }

    function showFileUpload() {
        $('fileUploadPopup').show();
    }

    function closeFileUpload() {
        $('fileUpload.dataFile').value = '';
        $('fileUploadPopup').hide();
    }
    </script>
    
    <tags:widgetContainer identify="false">
    
        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-bottom:10px;" tableClasses="inventoryOperationsLayout">
        
            <%-- LEFT SIDE COLUMN --%>
            <cti:dataGridCell>
            
                <tags:boxContainer2 nameKey="deviceSelection" hideEnabled="false">
                    <table class="inventoryActionsTable">
                    
                        <%-- INVENTORY PICKER--%>
                        <tr>
                            <td class="button top">
                                <form id="selectByInventoryPickerForm" action="/spring/stars/operator/inventory/inventoryOperations/inventoryActions" method="get">
                                    <input type="hidden" name="collectionType" value="idList"/>
                                    <input type="hidden" name="idList.ids" id="inventoryIds"/>
                                    <tags:pickerDialog type="lmHardwareBasePicker" extraArgs="${energyCompanyId}" id="inventoryPicker" multiSelectMode="true" 
                                                            destinationFieldId="inventoryIds" endAction="submitSelectionForm" linkType="button" buttonStyleClass="buttonGroup" nameKey="selectInventoryButton"/>
                                </form>
                            </td>
                
                            <td class="description top"><i:inline key=".selectInventoryDescription"/></td>
                        </tr>
                        
                        <%-- INVENTORY FILTER --%>
                        <tr>
                            <td class="button middle">
                                <form id="selectByFilterForm" action="/spring/stars/operator/inventory/inventoryOperations/setupFilterRules" method="get">
                                    <cti:button key="selectFilterButton" type="submit" styleClass="buttonGroup" name="filterButton"/>
                                </form>
                            </td>
                
                            <td class="description middle"><i:inline key=".selectFilterDescription"/></td>
                        </tr>
                
                        <%-- FILE UPLOAD --%>
                        <tr>
                            <td class="button">
                                <cti:button key="selectFileButton" type="button" styleClass="buttonGroup" name="fileButton" onclick="showFileUpload()"/>
                                <tags:simplePopup id="fileUploadPopup" title="${fileUploadTitle}" styleClass="mediumSimplePopup">
                                    <form id="selectByFileForm" method="post" action="/spring/stars/operator/inventory/inventoryOperations/uploadFile" enctype="multipart/form-data">
                                        <tags:nameValueContainer2>
                                            <tags:nameValue2 nameKey=".fileLabel">
                                                <input type="file" id="fileUpload.dataFile" name="fileUpload.dataFile" size="40">
                                                <input type="hidden" name="collectionType" value="fileUpload">
                                                <input type="hidden" name="fileUpload.energyCompanyId" value="${energyCompanyId}">
                                            </tags:nameValue2>
                                        </tags:nameValueContainer2>
                                        <div class="actionArea">
                                            <tags:slowInput2 key="ok" formId="selectByFileForm"/>
                                            <cti:button key="cancel" onclick="closeFileUpload();"/>            
                                        </div>
                                    </form>
                                </tags:simplePopup>
                            </td>
                
                            <td class="description"><i:inline key=".selectFileDescription"/></td>
                        </tr>
                    </table>
                             
                </tags:boxContainer2>
                
                <br>
                    
                <%-- DEVICE RECONFIG MONITORS WIDGET --%>
                <tags:widget bean="deviceReconfigMonitorsWidget"/>
            
            </cti:dataGridCell>
            
            <%-- RIGHT SIDE COLUMN --%>
            <cti:dataGridCell>
            
                <%-- COMMAND SCHEDULE WIDGET --%>
                <tags:widget bean="commandScheduleWidget" />
                
                <c:if test="${showLinks}">
                    <br>
                    
                    <tags:boxContainer2 nameKey="links">
                        <cti:link href="/spring/stars/operator/inventory/zbProblemDevices/view" key="yukon.web.modules.operator.inventoryOperations.zbProblemDevices"/>
                    </tags:boxContainer2>
                </c:if>
                
            </cti:dataGridCell>
            
        </cti:dataGrid>

    </tags:widgetContainer>
    
</cti:standardPage>