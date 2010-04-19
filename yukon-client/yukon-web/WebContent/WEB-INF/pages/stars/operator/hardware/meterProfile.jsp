<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="meterProfile.${mode}">
<tags:setFormEditMode mode="${mode}"/>
    <cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>
    
    <script type="text/javascript">
    
    function showDeletePopup() {
        $('deleteHardwarePopup').show();
    }
    
    function hideDeletePopup() {
        $('deleteHardwarePopup').hide();
    }
    
    </script>
    
    <!-- Delete Hardware Popup -->
    <i:simplePopup titleKey=".delete" id="deleteHardwarePopup" arguments="${hardwareDto.displayName}" styleClass="smallSimplePopup">
        <form id="deleteForm" action="/spring/stars/operator/hardware/deleteHardware" method="post">
            <input type="hidden" name="inventoryId" value="${hardwareDto.inventoryId}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
            <input type="hidden" name="deleteOption" value="delete">
            
            <i:inline key=".deleteQuestion"/>
            <br><br>
            
            <table width="100%">
                <tr>
                    <td>
                        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                            <input type="submit" class="createAddButton formSubmit" value="<cti:msg2 key="yukon.web.components.slowInput.delete.label"/>"/>
                        </cti:checkRolesAndProperties>
                        <input type="button" class="createAddButton formSubmit" onclick="hideDeletePopup()" value="<cti:msg2 key="yukon.web.components.slowInput.cancel.label"/>"/>
                    </td>
                </tr>
            </table>
        </form>
    </i:simplePopup>
    
    <cti:displayForPageEditModes modes="EDIT">
        <c:set var="action" value="/spring/stars/operator/hardware/updateHardware"/>
    </cti:displayForPageEditModes>
    
    <cti:displayForPageEditModes modes="CREATE">
        <c:set var="action" value="/spring/stars/operator/hardware/createHardware"/>
    </cti:displayForPageEditModes>
    
    <form:form id="updateForm" commandName="hardwareDto" action="${action}">
    
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="inventoryId" value="${hardwareDto.inventoryId}">
        <form:hidden path="hardwareType"/>
        <form:hidden path="hardwareTypeEntryId"/>
        <form:hidden path="energyCompanyId"/>
        
        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:50%;" tableClasses="widgetColumns">
        
            <%-- METER INFO --%>
            <cti:dataGridCell>
            
                <tags:sectionContainer2 key="meterInfoSection">
                
                    <tags:nameValueContainer2>
                    
                        <tags:inputNameValue nameKey="yukon.web.modules.operator.hardware.meters.meterNumber" path="meterNumber"/>
                        
                        <tags:inputNameValue nameKey="yukon.web.modules.operator.hardware.label" path="displayLabel"/>
                            
                        <tags:inputNameValue nameKey="yukon.web.modules.operator.hardware.altTrackingNumber" path="altTrackingNumber"/>
                            
                        <tags:yukonListEntrySelectNameValue nameKey="yukon.web.modules.operator.hardware.voltage" path="voltageEntryId" accountId="${accountId}" listName="DEVICE_VOLTAGE"/>
                        
                        <tags:textareaNameValue nameKey="yukon.web.modules.operator.hardware.deviceNotes" path="deviceNotes" rows="4" cols="30" />
                    
                    </tags:nameValueContainer2>
                
                </tags:sectionContainer2>
                
            </cti:dataGridCell>
            
            <%-- GENERAL INVENTORY INFO --%>
            <cti:dataGridCell>
            
                <tags:sectionContainer2 key="availableSwitchesSection">
                    <c:choose>
                        <c:when test="${not empty hardwareDto.switchAssignments}">
                            
                            <table class="resultsTable">
                                <tr>
                                    <th><i:inline key="yukon.web.modules.operator.hardware.serialNumber"/></th>
                                    <th><i:inline key="yukon.web.modules.operator.hardware.label"/></th>
                                </tr>
                                <c:forEach var="switch" items="${hardwareDto.switchAssignments}" varStatus="switchRow">
                                    <tr>
                                        <td>
                                            <tags:checkbox path="switchAssignments[${switchRow.index}].assigned" /><span class="checkBoxLabel"><spring:escapeBody htmlEscape="true">${switch.serialNumber}</spring:escapeBody></span>
                                            <form:hidden path="switchAssignments[${switchRow.index}].inventoryId"/>
                                            <form:hidden path="switchAssignments[${switchRow.index}].serialNumber"/>
                                        </td>
                                        <td><spring:escapeBody htmlEscape="true">${switch.label}</spring:escapeBody></td>
                                    </tr>
                                    
                                </c:forEach>
                            </table>
                            
                        </c:when>
                        <c:otherwise>
                        
                            <i:inline key=".noAvailableSwitches"/>
                        
                        </c:otherwise>
                    </c:choose>
                
                </tags:sectionContainer2>
                
            </cti:dataGridCell>
            
        </cti:dataGrid>
        
        <%-- BUTTONS --%>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <br>
            <tags:slowInput2 myFormId="updateForm" key="save" width="80px"/>
            <tags:reset/>
            
            <cti:displayForPageEditModes modes="CREATE">
                <input type="submit" class="formSubmit" id="cancelButton" name="cancel" value="<cti:msg2 key="yukon.web.components.slowInput.cancel.label"/>">
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <input type="button" class="formSubmit" onclick="showDeletePopup()" value="<cti:msg2 key="yukon.web.components.slowInput.delete.label"/>"/>
            </cti:displayForPageEditModes>
            
        </cti:displayForPageEditModes>
        
    </form:form>
</cti:standardPage>