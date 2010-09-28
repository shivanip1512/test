<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>


<cti:standardPage module="operator" page="workOrder.${mode}">
<tags:setFormEditMode mode="${mode}"/>

    <script type="text/javascript">

        Event.observe(window, "load", function() {
            var initialState = $F('workOrderBase.currentStateId');
            Event.observe('workOrderBase.currentStateId', "change", function() {
                var newState = $F('workOrderBase.currentStateId');
                var enabled = initialState != newState;
                $("eventDateDatePart").disabled = !enabled;
                $("eventDateTimePart").disabled = !enabled;
            });
        });
    
        var assignedServiceCompanyChanged = function() {
            $('workOrderBase.currentStateId').value = ${assignedEntryId};
            $("eventDateDatePart").disabled = false;
            $("eventDateTimePart").disabled = false;
        
        }
    
        var combineDateAndTimeFieldsAndSubmit = function() {
        	var dateReported = $("eventDateDatePart");
            if (dateReported != null) {
                combineDateAndTimeFields('eventDate');
            }

            $("workOrderUpdateForm").submit();
        }

    </script>
    
    <form id="deleteForm" action="/spring/stars/operator/workOrder/deleteWorkOrder" method="post">
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="deleteWorkOrderId" value="${workOrderDto.workOrderBase.orderId}">
    </form>
    
    <form id="viewAllForm" action="/spring/stars/operator/workOrder/workOrderList">
        <input type="hidden" name="accountId" value="${accountId}">
    </form>
    
    <form:form id="workOrderUpdateForm" commandName="workOrderDto" action="/spring/stars/operator/workOrder/updateWorkOrder">
        <input type="hidden" name="accountId" value="${accountId}">
        <form:hidden path="workOrderBase.accountId"/>
        <form:hidden path="workOrderBase.orderId"/>

        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;">
            <cti:dataGridCell>

                <tags:formElementContainer nameKey="workOrderContainer">
                    <tags:nameValueContainer2>
                        
                        <cti:displayForPageEditModes modes="CREATE">
                            <c:if test="${showWorkOrderNumberField}">
                                <tags:nameValue2 nameKey=".workOrderNumber">
                                    <tags:input path="workOrderBase.orderNumber"/>
                                </tags:nameValue2>
                            </c:if>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="EDIT">
                            <form:hidden path="workOrderBase.orderNumber"/>
                            <tags:nameValue2 nameKey=".workOrderNumber">
                                <spring:escapeBody htmlEscape="true">${workOrderDto.workOrderBase.orderNumber}</spring:escapeBody>
                            </tags:nameValue2>
                        </cti:displayForPageEditModes>
                        
                        
                        <tags:yukonListEntrySelectNameValue nameKey=".serviceType" path="workOrderBase.workTypeId" energyCompanyId="${energyCompanyId}" listName="SERVICE_TYPE"/>
                        
                        <tags:nameValue2 nameKey=".orderedBy">
                            <tags:input path="workOrderBase.orderedBy"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".additionalOrderNumber">
                            <tags:input path="workOrderBase.additionalOrderNumber"/>
                        </tags:nameValue2>
                        
                        <cti:displayForPageEditModes modes="CREATE">
                            <tags:yukonListEntrySelectNameValue nameKey=".currentState" path="workOrderBase.currentStateId" energyCompanyId="${energyCompanyId}" listName="SERVICE_STATUS" />
                        
                            <tags:nameValue2 nameKey=".eventDate">
                                <tags:dateTimeInput path="eventDate" inline="true" fieldValue="${workOrderDto.eventDate}"/>
                            </tags:nameValue2>
                        </cti:displayForPageEditModes>
                        
                        <tags:nameValue2 nameKey=".assignTo">
                            <tags:selectWithItems path="workOrderBase.serviceCompanyId" items="${allServiceCompanies}" 
                                                  itemValue="companyId" itemLabel="companyName"
                                                  defaultItemValue="${0}" defaultItemLabel="NONE" 
                                                  onchange="assignedServiceCompanyChanged()"/>
                        </tags:nameValue2>
        
                        <tags:textareaNameValue nameKey=".description" path="workOrderBase.description" rows="4" cols="20"/>
        
                    </tags:nameValueContainer2>
                
                </tags:formElementContainer>

            </cti:dataGridCell>
            <cti:displayForPageEditModes modes="VIEW,EDIT">
                <cti:dataGridCell>
                    <tags:formElementContainer nameKey="statusContainer">
                        <tags:nameValueContainer2>
                        
                            <tags:yukonListEntrySelectNameValue nameKey=".currentState" path="workOrderBase.currentStateId" 
                                                                energyCompanyId="${energyCompanyId}" listName="SERVICE_STATUS"/>
                            
                            <tags:nameValue2 nameKey=".eventDate">
                                <tags:dateTimeInput path="eventDate" inline="true" fieldValue="${workOrderDto.eventDate}" disabled="true"/>
                            </tags:nameValue2>
            
                            <tags:textareaNameValue nameKey=".actionTaken" path="workOrderBase.actionTaken" rows="4" cols="23"/>
                        </tags:nameValueContainer2>
                        <br>
                        <div style="text-align: right;">
                            <%-- GENERATE REPORTS --%>
                            <cti:url var="pdfExportUrl" value="/spring/stars/operator/workOrder/generateWorkOrderReport">
                                <cti:param name="export" value="PDF"/>
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="workOrderId" value="${workOrderDto.workOrderBase.orderId}"/>
                            </cti:url>
                            <cti:labeledImg key="pdfExport" href="${pdfExportUrl}"/>
                    
                        </div>

                        <tags:boxContainer2 nameKey="eventHistory">
                            <table class="compactResultsTable">
                                <tr>
                                    <th><i:inline key=".eventHistory.date"/></th>
                                    <th><i:inline key=".eventHistory.state"/></th>
                                </tr>
                                
                                <c:forEach var="eventBase" items="${eventHistory}">
                                    <tr>
                                        <td class="nonwrapping" ><cti:formatDate value="${eventBase.eventTimestamp}" type="BOTH"/></td>
                                        <td><tags:showYukonListEntry value="${eventBase.actionId}" energyCompanyId="${energyCompanyId}" listName="SERVICE_STATUS" /></td>
                                    </tr>
                                </c:forEach>
                                
                            </table>
                        </tags:boxContainer2>
                            
                    </tags:formElementContainer>
                </cti:dataGridCell>
            </cti:displayForPageEditModes>
        </cti:dataGrid>
        <br>
        
        <%-- buttons --%>
        <tags:slowInput2 formId="workOrderUpdateForm" key="save" onsubmit="combineDateAndTimeFieldsAndSubmit"/>
        <tags:slowInput2 formId="viewAllForm" key="cancel"/>
        
        <cti:displayForPageEditModes modes="EDIT">
            <tags:slowInput2 formId="deleteForm" key="delete"/>
        </cti:displayForPageEditModes>
        
    </form:form>
    
</cti:standardPage>