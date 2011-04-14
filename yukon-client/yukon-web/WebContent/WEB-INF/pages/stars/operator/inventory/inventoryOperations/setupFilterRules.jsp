<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="filterSelection">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/simpleDialog.js"/>
    <cti:includeScript link="/JavaScript/tableCreation.js" />
    <cti:includeScript link="/JavaScript/picker.js" />
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    
    <script>
        function removeRule(row) {
            $('removeRule').value = row;
            $('selectionForm').submit();
        }
    </script>
    
    <tags:simpleDialog id="addRuleDialog" styleClass="smallSimplePopup"/>
    
    <form:form id="selectionForm" commandName="filterModel" action="/spring/stars/operator/inventory/inventoryOperations/applyFilter" method="post">
        <input type="hidden" name="removeRule" id="removeRule">
        
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".filterMode">
                <i:inline key=".filterModePrefix"/>
                <tags:selectWithItems items="${filterModes}" itemLabel="displayName" path="filterMode" itemValue="value"/>
                <i:inline key=".filterModeSuffix"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <table class="resultsTable selectionTable">
            
            <thead>
                <tr>
                    <th colspan="2"><i:inline key=".tableHeader.rules"/></th>
                    <th class="removeColumn"><i:inline key=".tableHeader.remove"/></th>
                </tr>
            </thead>
            
            <tbody>
                
                <c:choose>
                    <c:when test="${empty filterModel.filterRules}">
                        <tr>
                            <td colspan="3">
                                <i:inline key=".noRules"/>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                    
                        <c:forEach items="${filterModel.filterRules}" var="rule" varStatus="row">
                            <form:hidden path="filterRules[${row.index}].ruleType"/>
                            <tr>
                                <td class="selectionTypeCol">
                                    <i:inline key="${rule.ruleType.formatKey}"/>
                                </td>
                                <td>
                                    <c:choose>
                                        
                                        <c:when test="${rule.ruleType eq 'APPLIANCE_TYPE'}">
                                            <form:select path="filterRules[${row.index}].applianceType">
                                                <form:options items="${applianceTypes}" itemLabel="name" itemValue="applianceCategoryId"/>
                                            </form:select>
                                        </c:when>

                                        <c:when test="${rule.ruleType eq 'CUSTOMER_TYPE'}">
                                            <form:select path="filterRules[${row.index}].modelCustomerType">
                                                <cti:msg2 var="residentialLabel" key=".filterEntry.residential"/>
                                                <form:option label="${residentialLabel}" value="${residentialModelEntryId}"/>
                                                <form:options items="${customerTypes}" itemLabel="entryText" itemValue="entryID"/>
                                            </form:select>
                                        </c:when>
                                        
                                        <c:when test="${rule.ruleType eq 'DEVICE_STATUS'}">
                                            <cti:msg2 var="none" key="defaults.none" />
                                            <tags:yukonListEntrySelect energyCompanyId="${energyCompanyId}" listName="DEVICE_STATUS" 
                                                                       path="filterRules[${row.index}].deviceStatusId" 
                                                                       defaultItemValue="0" defaultItemLabel="${none}" />
                                        </c:when>

                                        <c:when test="${rule.ruleType eq 'DEVICE_STATUS_DATE_RANGE'}">
                                            <tags:dateInputCalendar fieldName="filterRules[${row.index}].deviceStateDateFrom" springInput="true" showErrorOnNextLine="false"/>
                                            <i:inline key=".deviceStateDateRangeSeperator"/>
                                            <tags:dateInputCalendar fieldName="filterRules[${row.index}].deviceStateDateTo" springInput="true"/>
                                        </c:when>

                                                                                
                                        <c:when test="${rule.ruleType eq 'DEVICE_TYPE'}">
                                            <tags:yukonListEntrySelect energyCompanyId="${energyCompanyId}" listName="DEVICE_TYPE" 
                                                                       path="filterRules[${row.index}].deviceType" />
                                        </c:when>

                                        <c:when test="${rule.ruleType eq 'FIELD_INSTALL_DATE'}">
                                            <tags:dateInputCalendar fieldName="filterRules[${row.index}].fieldInstallDate" springInput="true"/>
                                        </c:when>
                                        
                                        <c:when test="${rule.ruleType eq 'LOAD_GROUP'}">
                                            <form:hidden path="filterRules[${row.index}].groupIds" id="groupIds_${row.index}"/>
                                            <tags:pickerDialog  type="lmGroupPaoPermissionCheckingPicker"
                                                    id="loadGroupPicker_${row.index}" selectionProperty="paoName"
                                                    multiSelectMode="true"
                                                    destinationFieldId="groupIds_${row.index}" linkType="selection"/>
                                        </c:when>
                                        
                                        <c:when test="${rule.ruleType eq 'MEMBER'}">
                                            <form:select path="filterRules[${row.index}].memberOfEnergyCompanyId">
                                                <form:options items="${energyCompanies}" itemLabel="name" itemValue="energyCompanyId"/>
                                            </form:select>
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
                                            <div><form:errors path="filterRules[${row.index}].postalCode" cssClass="errorMessage"/></div>
                                        </c:when>
                                        
                                        <c:when test="${rule.ruleType eq 'PROGRAM'}">
                                            <form:hidden path="filterRules[${row.index}].programIds" id="programIds_${row.index}"/>
                                            <tags:pickerDialog  type="lmDirectProgramPaoPermissionCheckingByEnergyCompanyIdPicker"
                                                    id="loadProgramPicker_${row.index}" selectionProperty="paoName"
                                                    multiSelectMode="true"
                                                    extraArgs="${energyCompanyId}"
                                                    destinationFieldId="programIds_${row.index}" linkType="selection"/>
                                        </c:when>
                                        
                                        <c:when test="${rule.ruleType eq 'PROGRAM_SIGNUP_DATE'}">
                                            <tags:dateInputCalendar fieldName="filterRules[${row.index}].programSignupDate" springInput="true"/>
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
                                            <div><form:errors path="filterRules[${row.index}].serialNumberFrom" cssClass="errorMessage"/></div>
                                            <div><form:errors path="filterRules[${row.index}].serialNumberTo" cssClass="errorMessage"/></div>
                                        </c:when>
                                        
                                        <c:when test="${rule.ruleType eq 'SERVICE_COMPANY'}">
                                            <c:choose>
                                                <c:when test="${empty serviceCompanies}">
                                                    <tags:hidden path="filterRules[${row.index}].serviceCompanyId" />
                                                    <i:inline key=".noServiceCompanies" />
                                                </c:when>
                                                <c:otherwise>
                                                    <form:select path="filterRules[${row.index}].serviceCompanyId">
                                                        <form:options items="${serviceCompanies}" itemLabel="companyName" itemValue="companyID"/>
                                                    </form:select>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>

                                        <c:when test="${rule.ruleType eq 'UNENROLLED'}">
                                            <i:inline key=".unenrolled"/>
                                        </c:when>

                                        <c:when test="${rule.ruleType eq 'WAREHOUSE'}">
                                            <c:choose>
                                                <c:when test="${empty warehouses}">
                                                    <tags:hidden path="filterRules[${row.index}].warehouseId" />
                                                    <i:inline key=".anyDeviceInInventory" />
                                                </c:when>
                                                <c:otherwise>
                                                    <form:select path="filterRules[${row.index}].warehouseId">
                                                        <form:options items="${warehouses}" itemLabel="warehouseName" itemValue="warehouseID"/>
                                                    </form:select>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        
                                    </c:choose>
                                </td>
                                <td class="removeColumn"><cti:img key="remove" href="javascript:removeRule(${row.index})"/></td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                
            </tbody>
            
            <%-- ADD RULE --%>
            <tfoot>
                <tr>
                    <td colspan="3">
                        <select name="ruleType">
                            <c:forEach items="${ruleTypes}" var="ruleType">
                                <option value="${ruleType}"><cti:formatObject value="${ruleType}"/></option>
                            </c:forEach>
                        </select>
                        <cti:button key="add" type="submit" name="addButton"/>
                     </td>
                </tr>
            </tfoot>
        </table>
            
        <br>
        
        <div>
            <c:choose>
                <c:when test="${empty filterModel.filterRules}">
                    <button disabled="disabled" class="formSubmit"><i:inline key=".select.label"/></button>
                </c:when>
                <c:otherwise>
                    <cti:button key="select" type="submit" name="apply"/>
                </c:otherwise>
            </c:choose>
            <cti:button key="cancel" type="submit" name="cancelButton"/>
        </div>
    
    </form:form>
    
</cti:standardPage>