<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="operator" page="workOrder.${mode}">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
    <tags:setFormEditMode mode="${mode}"/>

    <script type="text/javascript">
        $(function() {
            var curStateSel = $('select[name="workOrderBase.currentStateId"]'),
                initialState;
            if ( curStateSel.length > 0 ) {
                initialState = curStateSel.val();
                $(curStateSel).on('change', function (ev) {
                    var newState = curStateSel.val(),
                        enabled = initialState !== newState;
                    // check if the element even exists
                    if (0 !== $('#eventDate').length) {
                        $('#eventDate').prop('disabled', !enabled);
                    }
                });
            }
        });

        $('#workOrderConfirmCancel', function() {
            $('#confirmDeleteWorkOrderDialog').dialog('close');
        });

        var assignedServiceCompanyChanged = function() {
            var curStateSel = $('select[name="workOrderBase.currentStateId"]'),
                curStateSelVal = curStateSel.val();
            if (curStateSelVal != "${assignedEntryId}") {
                curStateSel.val("${assignedEntryId}");
                $('#currentStateChangedDialog').dialog('open');
            }
            $('#eventDate').prop('disabled', false);
        }

        var submitWorkOrder = function () {
            return true;
        }

    </script>

    <i:simplePopup titleKey=".deleteWorkOrderConfirmation.title" id="confirmDeleteWorkOrderDialog" on="#confirmDelete">
        <cti:msg2 key=".deleteWorkOrderConfirmation.message" arguments="${workOrderDto.workOrderBase.orderNumber}"/>
         <form id="deleteForm" action="/stars/operator/workOrder/deleteWorkOrder" method="post">
            <cti:csrfToken/>  
          <input type="hidden" name="accountId" value="${accountId}">
          <input type="hidden" name="deleteWorkOrderId" value="${workOrderDto.workOrderBase.orderId}">
          <div class="action-area">
            <cti:button id="workOrderConfirmDelete" type="submit" nameKey="delete" classes="primary action"/>
            <cti:button id="workOrderConfirmCancel" nameKey="cancel"/>  
          </div>
        </form>
    </i:simplePopup>

    <cti:url var="submitUrl" value="/stars/operator/workOrder/updateWorkOrder"/>
    <form:form modelAttribute="workOrderDto" action="${submitUrl}" onsubmit="submitWorkOrder()">
        <cti:csrfToken/>
        <input type="hidden" name="accountId" value="${accountId}">
        <form:hidden path="workOrderBase.accountId"/>
        <form:hidden path="workOrderBase.orderId"/>
        <form:hidden path="workOrderBase.energyCompanyId"/>

        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="workOrderContainer">
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
                            <tags:nameValue2 nameKey=".workOrderNumber">${fn:escapeXml(workOrderDto.workOrderBase.orderNumber)}</tags:nameValue2>
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
                            <div class="action-area">
                                <cti:button nameKey="ok" onclick="$('#currentStateChangedDialog').dialog('close');" classes="primary action"/>
                            </div>
                        </i:simplePopup>
                        
                        <tags:nameValue2 nameKey=".assignTo">
                            <tags:selectWithItems path="workOrderBase.serviceCompanyId" items="${allServiceCompanies}" 
                                                  itemValue="companyId" itemLabel="companyName"
                                                  onchange="assignedServiceCompanyChanged()"/>
                        </tags:nameValue2>
        
                        <tags:textareaNameValue nameKey=".description" path="workOrderBase.description" rows="4" cols="20"/>
        
                    </tags:nameValueContainer2>
                
                </tags:sectionContainer2>
            </div>
            <div class="column two nogutter">
                <cti:displayForPageEditModes modes="VIEW,EDIT">
                    <tags:sectionContainer2 nameKey="statusContainer">
                        <tags:nameValueContainer2>
                        
                            <tags:yukonListEntrySelectNameValue nameKey=".currentState" path="workOrderBase.currentStateId" 
                                                                energyCompanyId="${energyCompanyId}" listName="SERVICE_STATUS"/>
                            
                            <tags:nameValue2 nameKey=".eventDate">
                                <dt:dateTime id="eventDate" path="eventDate" value="${workOrderDto.eventDate}" disabled="true"/>
                            </tags:nameValue2>
            
                            <tags:textareaNameValue nameKey=".actionTaken" path="workOrderBase.actionTaken" rows="4" cols="23"/>
                        </tags:nameValueContainer2>
                        
                        <div class="action-area stacked">
                            <%-- GENERATE REPORTS --%>
                            <cti:url var="pdfExportUrl" value="/stars/operator/workOrder/generateWorkOrderReport">
                                <cti:param name="export" value="PDF"/>
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="workOrderId" value="${workOrderDto.workOrderBase.orderId}"/>
                            </cti:url>
                            <cti:button nameKey="pdfExport" href="${pdfExportUrl}" icon="icon-pdf" busy="true"/>
                        </div>

                    </tags:sectionContainer2>
                            
                    <tags:sectionContainer2 nameKey="eventHistory">
                        <table class="compact-results-table dashed">
                            <thead>
                                <tr>
                                    <th><i:inline key=".eventHistory.date"/></th>
                                    <th><i:inline key=".eventHistory.state"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="eventBase" items="${eventHistory}">
                                    <tr>
                                        <td ><cti:formatDate value="${eventBase.eventTimestamp}" type="BOTH"/></td>
                                        <td><tags:yukonListEntry value="${eventBase.actionId}" energyCompanyId="${energyCompanyId}" listName="SERVICE_STATUS" /></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </tags:sectionContainer2>
                    
                </cti:displayForPageEditModes>
            </div>
        </div>

        <%-- buttons --%>
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="CREATE,EDIT">
                <cti:button nameKey="save" type="submit" classes="js-blocker primary action"/>
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
                    <cti:button nameKey="delete" id="confirmDelete" classes="delete"/>
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
        </div>
    </form:form>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>