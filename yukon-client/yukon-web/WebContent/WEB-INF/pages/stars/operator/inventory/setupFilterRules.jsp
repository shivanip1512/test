<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.filter">

    <script type="text/javascript">
        function removeRule(row) {
            $('#removeRule').val(row);
            $('#selectionForm').submit();
        }
    </script>
    
    <cti:url var="url" value="/stars/operator/inventory/applyFilter"/>
    <form:form id="selectionForm" cssClass="js-no-submit-on-enter" modelAttribute="filterModel" action="${url}" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="removeRule" id="removeRule">
        <div class="stacked">
            <i:inline key=".filterModePrefix"/>
            <tags:selectWithItems items="${filterModes}" itemLabel="displayName" path="filterMode" itemValue="value"/>
            <i:inline key=".filterModeSuffix"/>
        </div>
        <tags:sectionContainer2 nameKey="filterRules">
            <c:choose>
                <c:when test="${empty filterModel.filterRules}">
                    <span class="empty-list"><i:inline key=".noRules"/></span>
                </c:when>
                <c:otherwise>
                    <table class="compact-results-table with-form-controls">
                        <tbody>
                            <c:forEach items="${filterModel.filterRules}" var="rule" varStatus="row">
                                <tr>
                                    <td class="selectionTypeCol">
                                        <form:hidden path="filterRules[${row.index}].ruleType"/>
                                        <i:inline key="${rule.ruleType.formatKey}"/>
                                    </td>
                                    <td>
                                        <div class="clearfix column-18-6">
                                            <div class="column one">
                                            <c:choose>
                                                
                                                <c:when test="${rule.ruleType eq 'APPLIANCE_TYPE'}">
                                                    <form:select path="filterRules[${row.index}].applianceType">
                                                        <form:options items="${applianceTypes}" itemLabel="name" itemValue="applianceCategoryId"/>
                                                    </form:select>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'ASSIGNED'}">
                                                    <i:inline key=".assigned"/>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'CUSTOMER_TYPE'}">
                                                    <form:select path="filterRules[${row.index}].modelCustomerType">
                                                        <cti:msg2 var="residentialLabel" key=".filterEntry.residential"/>
                                                        <form:option label="${residentialLabel}" value="${residentialModelEntryId}"/>
                                                        <form:options items="${ciCustomerTypes}" itemLabel="entryText" itemValue="entryID"/>
                                                    </form:select>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'DEVICE_STATUS'}">
                                                    <cti:msg2 var="none" key="yukon.common.none.choice" />
                                                    <tags:yukonListEntrySelect energyCompanyId="${energyCompanyId}" listName="DEVICE_STATUS" 
                                                                               path="filterRules[${row.index}].deviceStatusId" 
                                                                               defaultItemValue="0" defaultItemLabel="${none}" />
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'DEVICE_STATUS_DATE_RANGE'}">
                                                    <div class="clearfix">
                                                        <dt:date path="filterRules[${row.index}].deviceStateDateFrom" id="deviceStateDateFrom_${row.index}" hideErrors="true"/>
                                                        <div class="fl" style="margin-right: 5px;line-height: 26px;"><i:inline key=".deviceStateDateRangeSeperator"/></div>
                                                        <dt:date path="filterRules[${row.index}].deviceStateDateTo" id="deviceStateDateTo_${row.index}" hideErrors="true"/>
                                                    </div>
                                                    <div><form:errors path="filterRules[${row.index}].deviceStateDateFrom" cssClass="error"/></div>
                                                    <div><form:errors path="filterRules[${row.index}].deviceStateDateTo" cssClass="error"/></div>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'DEVICE_TYPE'}">
                                                    <tags:yukonListEntrySelect energyCompanyId="${energyCompanyId}" listName="DEVICE_TYPE" 
                                                                               path="filterRules[${row.index}].deviceType" />
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'FIELD_INSTALL_DATE'}">
                                                    <dt:date path="filterRules[${row.index}].fieldInstallDate" />
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'LOAD_GROUP'}">
                                                    <form:hidden path="filterRules[${row.index}].groupIds" id="groupIds_${row.index}"/>
                                                    <tags:pickerDialog  type="lmGroupPaoPermissionCheckingPicker"
                                                            id="loadGroupPicker_${row.index}" selectionProperty="paoName"
                                                            multiSelectMode="true"
                                                            destinationFieldId="groupIds_${row.index}" linkType="selection"/>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'POSTAL_CODE'}">
                                                    <spring:bind path="filterRules[${row.index}].postalCode" >
                                                        <c:choose>
                                                            <c:when test="${status.error}">
                                                                <form:input path="filterRules[${row.index}].postalCode" cssClass="error"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form:input path="filterRules[${row.index}].postalCode"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </spring:bind>
                                                    <div><form:errors path="filterRules[${row.index}].postalCode" cssClass="error"/></div>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'PROGRAM'}">
                                                    <form:hidden path="filterRules[${row.index}].programIds" id="programIds_${row.index}"/>
                                                    <tags:pickerDialog type="assignedProgramPicker"
                                                            id="loadProgramPicker_${row.index}" selectionProperty="displayName"
                                                            multiSelectMode="true"
                                                            extraArgs="${energyCompanyId}"
                                                            destinationFieldId="programIds_${row.index}" linkType="selection"/>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'PROGRAM_SIGNUP_DATE'}">
                                                    <dt:date path="filterRules[${row.index}].programSignupDate" />
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'SERIAL_NUMBER_RANGE'}">
                                                    <spring:bind path="filterRules[${row.index}].serialNumberFrom">
                                                        <c:choose>
                                                            <c:when test="${status.error}">
                                                                <form:input path="filterRules[${row.index}].serialNumberFrom" cssClass="error"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form:input path="filterRules[${row.index}].serialNumberFrom"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </spring:bind>
                                                    <i:inline key=".serialNumberRangeSeperator"/>
                                                    <spring:bind path="filterRules[${row.index}].serialNumberTo">
                                                        <c:choose>
                                                            <c:when test="${status.error}">
                                                                <form:input path="filterRules[${row.index}].serialNumberTo" cssClass="error"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form:input path="filterRules[${row.index}].serialNumberTo"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </spring:bind>
                                                    <div><form:errors path="filterRules[${row.index}].serialNumberFrom" cssClass="error"/></div>
                                                    <div><form:errors path="filterRules[${row.index}].serialNumberTo" cssClass="error"/></div>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'SERVICE_COMPANY'}">
                                                    <c:choose>
                                                        <c:when test="${empty serviceCompanies}">
                                                            <tags:hidden path="filterRules[${row.index}].serviceCompanyId" />
                                                            <i:inline key=".noServiceCompanies" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form:select path="filterRules[${row.index}].serviceCompanyId">
                                                                <form:options items="${serviceCompanies}" itemLabel="companyName" itemValue="companyId"/>
                                                            </form:select>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'UNENROLLED'}">
                                                    <i:inline key=".unenrolled"/>
                                                </c:when>
                                                
                                                <c:when test="${rule.ruleType eq 'WAREHOUSE'}">
                                                    <cti:checkRolesAndProperties value="ADMIN_MULTI_WAREHOUSE">
                                                            <form:select path="filterRules[${row.index}].warehouseId">
                                                                <form:options items="${warehouses}" itemLabel="warehouseName" itemValue="warehouseID"/>
                                                            </form:select>
                                                    </cti:checkRolesAndProperties>
                                                    <cti:checkRolesAndProperties value="!ADMIN_MULTI_WAREHOUSE">
                                                            <i:inline key=".inWarehouse"/>
                                                    </cti:checkRolesAndProperties>
                                                </c:when>
                                            </c:choose>
                                            </div>
                                            <div class="column two nogutter">
                                                <cti:button renderMode="buttonImage" href="javascript:removeRule(${row.index})" icon="icon-cross" classes="fr"/>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
            <div class="action-area">
                <cti:button nameKey="add" type="submit" name="addButton" classes="js-submit-on-enter right" icon="icon-add"/>
                <select name="ruleType" class="js-submit-on-enter fr left">
                    <c:forEach var="type" items="${ruleTypes}">
                        <option value="${type}"><cti:msg2 key="${type}"/></option>
                    </c:forEach>
                </select>
            </div>
        </tags:sectionContainer2>
        
        <div class="page-action-area">
            <c:choose>
                <c:when test="${empty filterModel.filterRules}">
                    <cti:button nameKey="select" type="submit" name="apply" classes="primary action" disabled="true"/>
                </c:when>
                <c:otherwise>
                    <cti:button nameKey="select" type="submit" name="apply" classes="primary action"/>
                </c:otherwise>
            </c:choose>
            <cti:button nameKey="cancel" type="submit" name="cancelButton"/>
        </div>
    </form:form>
    
</cti:standardPage>