<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="requestTypeLabel" key="yukon.common.device.schedules.home.requestType"/>
<cti:msg var="attibuteRequestTypeLabel" key="yukon.common.device.schedules.home.requestType.attribute"/>
<cti:msg var="commandRequestTypeLabel" key="yukon.common.device.schedules.home.requestType.command"/>
<cti:msg var="groupLabel" key="yukon.common.device.schedules.home.group"/>
<cti:msg var="chooseGroupText" key="yukon.common.device.schedules.home.group.chooseGroup"/>
<cti:msg var="changeGroupText" key="yukon.common.device.schedules.home.group.changeGroup"/>
<cti:msg var="selectDeviceGroupText" key="yukon.common.device.schedules.home.group.selectDeviceGroup"/>
<cti:msg var="selectDeviceGroupChooseText" key="yukon.common.device.schedules.home.group.selectDeviceGroupChoose"/>
<cti:msg var="selectDeviceGroupCancelText" key="yukon.common.device.schedules.home.group.selectDeviceGroupCancel"/>
<cti:msg var="scheduleNameLabel" key="yukon.common.device.schedules.home.scheduleName"/>
<cti:msg var="timeFrequencyLabel" key="yukon.common.device.schedules.home.timeFrequency"/>
<cti:msg var="retryLabel" key="yukon.common.device.schedules.home.retry"/>
<cti:msg var="scheduleStateLabel" key="yukon.common.device.schedules.home.scheduleState"/>
<cti:msg var="scheduleButtonText" key="yukon.common.device.schedules.home.scheduleButton"/>
<cti:msg var="updateButtonText" key="yukon.common.device.schedules.home.updateButton"/>
<cti:msg var="enableJobButtonText" key="yukon.common.device.schedules.home.enableJobButton"/>
<cti:msg var="disableJobButtonText" key="yukon.common.device.schedules.home.disableJobButton"/>
<cti:msg var="disableAndDeleteJobButtonText" key="yukon.common.device.schedules.home.disableAndDeleteJobButton"/>

<style type="text/css">
    .scheduleButtonDiv {border-top:solid 1px #cccccc;padding-top:6px;}
</style>
<c:set var="take10" value="10px"/>
<c:set var="take12" value="12px"/>

<cti:standardPage module="tools" page="schedules.${mode}">
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
            <div class="error stacked">${fn:escapeXml(errorMsg)}</div>
        </c:if>
        
        <%-- DEVICE GROUP JSON DATA --%>
        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" selectGroupName="${deviceGroupName}" selectedNodePathVar="selectedNodePath"/>
        
        <%-- TOGGLE/DELETE FORMS --%>
        <form id="toggleJobEnabledForm" action="<cti:url value="/group/scheduledGroupRequestExecution/toggleJobEnabled"/>" method="post">
            <cti:csrfToken/>
            <input type="hidden" name="toggleJobId" value="${editJobId}">
        </form>
        
        <form id="disabledAndDeleteJobForm" action="<cti:url value="/group/scheduledGroupRequestExecution/deleteJob"/>" method="post">
            <cti:csrfToken/>
            <input type="hidden" name="deleteJobId" value="${editJobId}">
        </form>
        
        <%-- TABS --%>
        <cti:tabs>
                
            <%-- ATTRIBUTE TAB --%>
            <cti:tab title="${attibuteRequestTypeLabel}" selected="${empty requestType || requestType == 'SCHEDULED_GROUP_ATTRIBUTE_READ'}">
                
                <form id="scheduledGroupRequestExecutionForm_attr" action="<cti:url value="/group/scheduledGroupRequestExecution/schedule"/>" method="post" >
                  <cti:csrfToken/>
                     <input type="hidden" name="editJobId" value="${editJobId}">
                     <input type="hidden" name="requestType" value="SCHEDULED_GROUP_ATTRIBUTE_READ">
                     <cti:uniqueIdentifier var="formUniqueId" prefix="attrFormUniqueId_"/>
                     <input type="hidden" name="formUniqueId" value="${formUniqueId}">
                     
                     <cti:uniqueIdentifier var="cronTagId_attr" prefix="cronTagIdAttr_"/>
                     <input type="hidden" name="cronTagId" value="${cronTagId_attr}">
                    <tags:setFormEditMode mode="${mode}"/>
                 
                     <tags:nameValueContainer>
                     
                         <tags:nameValue name="${scheduleNameLabel}">
                             <input type="text" name="scheduleName" value="${fn:escapeXml(scheduleName)}">
                         </tags:nameValue>
                         
                         <tags:nameValueGap gapHeight="${take12}"/>
                         
                         <cti:msg var="selectAttributeLabel" key="yukon.common.device.commander.attributeSelector.selectAttribute"/>
                         <tags:nameValue name="${selectAttributeLabel}" nameColumnWidth="160px">
                             <tags:attributeSelector name="attribute" attributes="${allGroupedReadableAttributes}" 
                                selectedAttributes="${selectedAttributes}" multipleSize="8" groupItems="true"/>
                         </tags:nameValue>
                     
                         <tags:nameValueGap gapHeight="${take10}"/>
                         
                         <tags:nameValue id="attributeDeviceGroups" name="${groupLabel}">
                             <tags:deviceGroupNameSelector fieldName="deviceGroupName_${formUniqueId}" 
                                                           fieldValue="${deviceGroupName}" 
                                                           dataJson="${groupDataJson}" 
                                                           linkGroupName="true" 
                                                           showSelectedDevicesIcon="false"/>
                         </tags:nameValue>
                         
                         <tags:nameValueGap gapHeight="${take12}"/>
                         
                         <tags:nameValue name="${timeFrequencyLabel}">
                             <tags:cronExpressionData id="${formUniqueId}" state="${cronExpressionTagState}" allowManual="${true}"/>
                         </tags:nameValue>
                         
                         <tags:nameValue name="${retryLabel}">
                             <tags:requestRetryOptions retryCheckbox="${retryCheckbox}" queuedRetryCount="${fn:escapeXml(queuedRetryCount)}" nonQueuedRetryCount="${fn:escapeXml(nonQueuedRetryCount)}" maxTotalRunTimeHours="${fn:escapeXml(maxTotalRunTimeHours)}"/>
                         </tags:nameValue>
                         
                         <cti:displayForPageEditModes modes="EDIT">
                             <tags:nameValue name="${scheduleStateLabel}">
                                 <cti:msg key="yukon.common.device.schedules.state.${status}"/>
                             </tags:nameValue>
                         </cti:displayForPageEditModes>
                         
                    </tags:nameValueContainer>
                    
                </form>
                    
                <div class="page-action-area">    
                    <cti:displayForPageEditModes modes="CREATE">
                        <cti:button onclick="$('#scheduledGroupRequestExecutionForm_attr').submit();" 
                            label="${scheduleButtonText}" 
                            busy="true"
                            classes="primary action"/>
                    </cti:displayForPageEditModes>
                    
                    <cti:displayForPageEditModes modes="EDIT">
                        
                        <cti:button onclick="$('#scheduledGroupRequestExecutionForm_attr').submit();" 
                            label="${updateButtonText}" 
                            busy="true"
                            classes="primary action"/>
                        <c:if test="${status ne 'RUNNING'}">
                            <c:choose>
                                <c:when test="${disabled}">
                                    <cti:button onclick="$('#toggleJobEnabledForm').submit();" label="${enableJobButtonText}" busy="true"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:button onclick="$('#toggleJobEnabledForm').submit();" label="${disableJobButtonText}" busy="true"/>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        
                        <cti:button onclick="$('#disabledAndDeleteJobForm').submit();" 
                            label="${disableAndDeleteJobButtonText}" 
                            busy="true" 
                            classes="delete"/>
                    </cti:displayForPageEditModes>
                </div>
            </cti:tab>
            
            <%-- COMMAND TAB --%>
            <cti:tab title="${commandRequestTypeLabel}" selected="${requestType == 'SCHEDULED_GROUP_COMMAND'}">
            
                <form id="scheduledGroupRequestExecutionForm_cmd" action="<cti:url value="/group/scheduledGroupRequestExecution/schedule"/>" method="post" >
                    <cti:csrfToken/>
                    <input type="hidden" name="editJobId" value="${editJobId}">
                    <input type="hidden" name="requestType" value="SCHEDULED_GROUP_COMMAND">
                    <cti:uniqueIdentifier var="formUniqueId" prefix="cmdFormUniqueId_"/>
                    <input type="hidden" name="formUniqueId" value="${formUniqueId}">
                    
                    <tags:nameValueContainer>
                    
                        <tags:nameValue name="${scheduleNameLabel}">
                            <input type="text" name="scheduleName" value="${fn:escapeXml(scheduleName)}">
                        </tags:nameValue>
                        
                        <tags:nameValueGap gapHeight="${take12}"/>
                        
                        <cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
                        <tags:nameValue name="${selectCommandLabel}" nameColumnWidth="160px">
                            <amr:commandSelector selectName="commandSelectValue" fieldName="commandString" 
                                commands="${commands}" selectedSelectValue="${commandSelectValue}" 
                                selectedCommandString="${fn:escapeXml(commandString)}"/>  
                        </tags:nameValue>
                        
                        <tags:nameValueGap gapHeight="${take10}"/>
                        
                        <tags:nameValue id="commandDeviceGroups" name="${groupLabel}">
                            <tags:deviceGroupNameSelector fieldName="deviceGroupName_${formUniqueId}" 
                                                          fieldValue="${deviceGroupName}" 
                                                          dataJson="${groupDataJson}" 
                                                          linkGroupName="true" 
                                                          showSelectedDevicesIcon="false"/>
                        </tags:nameValue>
                        
                        <tags:nameValueGap gapHeight="${take12}"/>
                        
                        <tags:nameValue name="${timeFrequencyLabel}">
                            <tags:cronExpressionData id="${formUniqueId}" state="${cronExpressionTagState}" allowManual='true'/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="${retryLabel}">
                            <tags:requestRetryOptions retryCheckbox="${retryCheckbox}" queuedRetryCount="${fn:escapeXml(queuedRetryCount)}" nonQueuedRetryCount="${fn:escapeXml(nonQueuedRetryCount)}" maxTotalRunTimeHours="${fn:escapeXml(maxTotalRunTimeHours)}"/>
                        </tags:nameValue>
                        
                        <cti:displayForPageEditModes modes="EDIT">
                            <tags:nameValue name="${scheduleStateLabel}">
                                <cti:msg key="yukon.common.device.schedules.state.${status}"/>
                            </tags:nameValue>
                        </cti:displayForPageEditModes>
                        
                    </tags:nameValueContainer>

                    <div class="page-action-area">
                        <cti:displayForPageEditModes modes="CREATE">
                            <cti:button type="submit" label="${scheduleButtonText}" busy="true" classes="primary action"/>
                        </cti:displayForPageEditModes>
                            
                        <cti:displayForPageEditModes modes="EDIT">
        
                            <cti:button type="submit" label="${updateButtonText}" busy="true" classes="primary action"/>
                            <c:if test="${status ne 'RUNNING'}">
                            <c:choose>
                                <c:when test="${disabled}">
                                    <cti:button onclick="$('#toggleJobEnabledForm').submit();" label="${enableJobButtonText}" busy="true"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:button onclick="$('#toggleJobEnabledForm').submit();" label="${disableJobButtonText}" busy="true"/>
                                </c:otherwise>
                            </c:choose>
                            </c:if>
                            
                            <cti:button onclick="$('#disabledAndDeleteJobForm').submit();" 
                                label="${disableAndDeleteJobButtonText}" 
                                busy="true"
                                classes="delete"/>
        
                         </cti:displayForPageEditModes>
                   </div>
                </form>
            </cti:tab>
        
        </cti:tabs>
            
</cti:standardPage>