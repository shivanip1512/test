<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operator.enrollmentEdit, modules.operator.enrollmentList">

<c:if test="${isNest}">
    <tags:alertBox key="yukon.web.modules.dr.nest.changeGroupMessage" type="info"/>
</c:if>

<div><i:inline key=".headerMessage" arguments="${assignedProgram.displayName}"/></div>

<cti:url var="submitUrl" value="/stars/operator/enrollment/confirmSave">
    <cti:param name="isAdd" value="${isAdd}"/>
</cti:url>
<form:form id="edit-enroll-form" modelAttribute="programEnrollment" action="${submitUrl}">
    <cti:csrfToken/>
    <input type="hidden" name="accountId" value="${accountId}"/>
    <input type="hidden" name="assignedProgramId" value="${param.assignedProgramId}"/>


    <tags:sectionContainer2 nameKey="hardwareAssigned">
        <div class="scroll-md">
        <c:choose>
            <c:when test="${!isDisable}"> 
            <table id="hardwareAssignedTable" class="compact-results-table with-form-controls clearfix">
                <thead>
                    <tr>
                        <th></th>
                        <th class="deviceLabel"><i:inline key=".deviceLabel"/></th>
                        <th class="relay"><i:inline key=".relay"/></th>
                        <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                        	<th class="group"><i:inline key=".group"/></th>
                        </cti:checkEnergyCompanySetting>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="item" varStatus="status" items="${programEnrollment.inventoryEnrollments}">
                    
                        <c:set var="inventoryId" value="${item.inventoryId}"/>
                        <c:set var="inventory" value="${inventoryById[inventoryId]}"/>
                        <c:set var="enrolled" value="${item.enrolled}"/>
                        <c:set var="loadGroupId" value="${item.loadGroupId}"/>
                        <tr data-inventory-id="${inventoryId}">
                            <td>
                                <form:hidden path="inventoryEnrollments[${status.index}].inventoryId"/>
                                <c:set var="enrollClass" value="${isNest ? 'disabled-look' : 'js-enroll-cb'}"/>
                                <c:if test="${isNest}">
                                    <cti:msg2 var="titleMessage" key="yukon.web.modules.dr.nest.enrollmentDisabled"/>
                                </c:if>
                                <form:checkbox id="enroll-cb-${inventoryId}" cssClass="${enrollClass}"
                                    path="inventoryEnrollments[${status.index}].enrolled" title="${titleMessage}"/>
                            </td>
                            <td class="deviceLabel">
                                <label for="enroll-cb-${inventoryId}">${fn:escapeXml(inventory.displayName)}</label>
                            </td>
                            <td>
                                <form:select class="js-enroll-relay" path="inventoryEnrollments[${status.index}].relay" 
                                        disabled="${!enrolled}">
                                    <form:option value="0"><cti:msg2 key=".noRelay"/></form:option>
                                    <c:if test="${!isMeter}">
                                        <c:forEach var="relayNumber" begin="1" end="${inventory.numRelays}">
                                            <form:option value="${relayNumber}">${relayNumber}</form:option>
                                        </c:forEach>
                                    </c:if>
                                </form:select>
                            </td>
                            
                                <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                                <td>
                                    <c:set var="nestClass" value="${isNest ? 'js-nest-group' : ''}"/>
        							<tags:nameValueContainer2 tableClass="stacked">
                							<c:if test="${empty loadGroups}">
                    							<i:inline key=".groupNotApplicable"/>
                							</c:if>
                							<c:if test="${!empty loadGroups}">
                    							<c:set var="selectedLoadGroupId" value="${programEnrollment.loadGroupId}"/>
                    							<form:select path="inventoryEnrollments[${status.index}].loadGroupId" items="${loadGroups}"
                        						itemLabel="name" itemValue="paoIdentifier.paoId" cssClass="js-enroll-relay ${nestClass}" disabled="${!enrolled}"/>
                							</c:if>
       								 </tags:nameValueContainer2>
       								 </td>
   							 	</cti:checkEnergyCompanySetting>
                            
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            </c:when>
            <c:otherwise>
                <span class="error"><cti:msg2 key=".noHardware"/></span>
            </c:otherwise>
        </c:choose>
        <input type="hidden" name="isDisable" id="isDisable" value="${isDisable}" /> 
        </div>
    </tags:sectionContainer2>

</form:form>

</cti:msgScope>
