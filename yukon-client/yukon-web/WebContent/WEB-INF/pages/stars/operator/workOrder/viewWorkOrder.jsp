<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="operator" page="workOrder.${mode}">
<tags:setFormEditMode mode="${mode}"/>

    <script type="text/javascript">
        jQuery(function () {
            var curStateSel = jQuery('select[name="workOrderBase.currentStateId"]'),
                initialState;
            if ( curStateSel.length > 0 ) {
                initialState = curStateSel.val();
                jQuery(curStateSel).on('change', function (ev) {
                    var newState = curStateSel.val(),
                        enabled = initialState !== newState;
                    // check if the element even exists
                    if (0 !== jQuery('#eventDate').length) {
                        jQuery('#eventDate').prop('disabled', !enabled);
                    }
                });
            }
        });

        jQuery('#workOrderConfirmCancel', function () {
            jQuery('#confirmDeleteWorkOrderDialog').dialog('close');
        });

        var assignedServiceCompanyChanged = function () {
            var curStateSel = jQuery('select[name="workOrderBase.currentStateId"]'),
                curStateSelVal = curStateSel.val();
            if (curStateSelVal != "${assignedEntryId}") {
                curStateSel.val("${assignedEntryId}");
                jQuery('#currentStateChangedDialog').dialog('open');
            }
            jQuery('#eventDate').prop('disabled', false);
        }

        var submitWorkOrder = function () {
            return true;
        }

    </script>

    <i:simplePopup titleKey=".deleteWorkOrderConfirmation.title" id="confirmDeleteWorkOrderDialog" on="#confirmDelete">
        <cti:msg2 key=".deleteWorkOrderConfirmation.message" arguments="${workOrderDto.workOrderBase.orderNumber}"/>
         <form id="deleteForm" action="/stars/operator/workOrder/deleteWorkOrder" method="post">
          <input type="hidden" name="accountId" value="${accountId}">
          <input type="hidden" name="deleteWorkOrderId" value="${workOrderDto.workOrderBase.orderId}">
          <div class="actionArea">
            <cti:button id="workOrderConfirmDelete" type="submit" nameKey="delete" classes="primary action"/>
            <cti:button id="workOrderConfirmCancel" type="button" nameKey="cancel"/>  
          </div>
        </form>
    </i:simplePopup>

    <cti:url var="submitUrl" value="/stars/operator/workOrder/updateWorkOrder"/>
    <form:form commandName="workOrderDto" action="${submitUrl}" onsubmit="submitWorkOrder()">
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
                        <cti:displayForPageEditModes modes="EDIT,VIEW">
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
                                <dt:dateTime id="eventDate" path="eventDate" value="${workOrderDto.eventDate}"/>
                            </tags:nameValue2>
                        </cti:displayForPageEditModes>
                        
                        <i:simplePopup titleKey=".currentStateChangedTitle" id="currentStateChangedDialog">
                        	<cti:msg2 key=".currentStateChanged"/>
                        	<div class="actionArea">
                        		<cti:button nameKey="ok" onclick="jQuery('#currentStateChangedDialog').hide()"/>
                       		</div>
                        </i:simplePopup>
                        
                        <tags:nameValue2 nameKey=".assignTo">
                            <tags:selectWithItems path="workOrderBase.serviceCompanyId" items="${allServiceCompanies}" 
                                                  itemValue="companyId" itemLabel="companyName"
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
                                <dt:dateTime id="eventDate" path="eventDate" value="${workOrderDto.eventDate}" disabled="true"/>
                            </tags:nameValue2>
                            <tags:textareaNameValue nameKey=".actionTaken" path="workOrderBase.actionTaken" rows="4" cols="23"/>
                        </tags:nameValueContainer2>
                        <div class="actionArea stacked">
                            <%-- GENERATE REPORTS --%>
                            <cti:url var="pdfExportUrl" value="/stars/operator/workOrder/generateWorkOrderReport">
                                <cti:param name="export" value="PDF"/>
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="workOrderId" value="${workOrderDto.workOrderBase.orderId}"/>
                            </cti:url>
                            <cti:button nameKey="pdfExport" href="${pdfExportUrl}" icon="icon-pdf"/>
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
                                        <td><tags:yukonListEntry value="${eventBase.actionId}" energyCompanyId="${energyCompanyId}" listName="SERVICE_STATUS" /></td>
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
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <cti:button nameKey="save" type="submit" classes="f-blocker"/>
            <cti:displayForPageEditModes modes="CREATE">
                <cti:url value="/stars/operator/workOrder/workOrderList" var="cancelUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                </cti:url>
            </cti:displayForPageEditModes>
           
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url value="/stars/operator/workOrder/view" var="cancelUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                    <cti:param name="workOrderId" value="${workOrderDto.workOrderBase.orderId}"/>
                </cti:url>
                <cti:button nameKey="delete" id="confirmDelete"/>
            </cti:displayForPageEditModes>
           
           <cti:button nameKey="cancel" href="${cancelUrl}"/>
        </cti:displayForPageEditModes>
        
       
        <cti:displayForPageEditModes modes="VIEW">
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <cti:url value="/stars/operator/workOrder/edit" var="editUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                    <cti:param name="workOrderId" value="${workOrderDto.workOrderBase.orderId}"/>
                </cti:url>
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
    </form:form>
    
</cti:standardPage>