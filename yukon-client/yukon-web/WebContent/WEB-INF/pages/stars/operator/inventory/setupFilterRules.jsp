<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="operator" page="filterSelection">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/simpleDialog.js"/>
    <cti:includeScript link="/JavaScript/tableCreation.js" />
    <cti:includeScript link="/JavaScript/picker.js" />
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

    <cti:msg key="yukon.common.calendarcontrol.months" var="months" />
    <cti:msg key="yukon.common.calendarcontrol.days" var="days" />
    <cti:msg key="yukon.common.calendarcontrol.clear" var="clear" />
    <cti:msg key="yukon.common.calendarcontrol.close" var="close" />

    <script type="text/javascript">
        function removeRule(row) {
            jQuery('#removeRule').val(row);
            jQuery('#selectionForm').submit();
        }
        jQuery(document).on('click', 'a.f_show-calendar', function(event) {
            var inputId = jQuery(event.target).prev('input').attr('id');
            showCalendarControl(inputId, '${months}', '${days}', '${clear}', '${close}');
        });
    </script>
    
    <tags:simpleDialog id="addRuleDialog"/>
    
    <form:form id="selectionForm" cssClass="f_preventSubmitViaEnterKey" commandName="filterModel" action="/stars/operator/inventory/applyFilter" method="post">
        <input type="hidden" name="removeRule" id="removeRule">
        <div class="stacked">
            <i:inline key=".filterModePrefix"/>
            <tags:selectWithItems items="${filterModes}" itemLabel="displayName" path="filterMode" itemValue="value"/>
            <i:inline key=".filterModeSuffix"/>
        </div>
        <tags:boxContainer2 nameKey="filterRules">
            <c:choose>
                <c:when test="${empty filterModel.filterRules}">
                    <span class="empty-list"><i:inline key=".noRules"/></span>
                </c:when>
                <c:otherwise>
                    <table class="compactResultsTable">
                        <thead>
                            <tr>
                                <th colspan="2"><i:inline key=".tableHeader.rules"/></th>
                                <th><i:inline key=".tableHeader.remove"/></th>
                            </tr>
                        </thead>
                        
                        <tbody>
                            <c:forEach items="${filterModel.filterRules}" var="rule" varStatus="row">
                                <tr>
                                    <td class="selectionTypeCol">
                                        <form:hidden path="filterRules[${row.index}].ruleType"/>
                                        <i:inline key="${rule.ruleType.formatKey}"/>
                                    </td>
                                    <td>
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
                                                <cti:msg2 var="none" key="defaults.none" />
                                                <tags:yukonListEntrySelect energyCompanyId="${energyCompanyId}" listName="DEVICE_STATUS" 
                                                                           path="filterRules[${row.index}].deviceStatusId" 
                                                                           defaultItemValue="0" defaultItemLabel="${none}" />
                                            </c:when>
    
                                            <c:when test="${rule.ruleType eq 'DEVICE_STATUS_DATE_RANGE'}">
                                                <form:input path="filterRules[${row.index}].deviceStateDateFrom" id="deviceStateDateFrom_${row.index}" size="10" maxlength="10" cssClass="fl"/>
                                                <a href="javascript:void(0);" class="icon icon-calendar-view-day f_show-calendar"></a>
                                                                                            
                                                <div class="fl"><i:inline key=".deviceStateDateRangeSeperator"/>&nbsp;</div>
                                                                                            
                                                <form:input path="filterRules[${row.index}].deviceStateDateTo" id="deviceStateDateTo_${row.index}" size="10" maxlength="10" cssClass="fl"/>
                                                <a href="javascript:void(0);" class="icon icon-calendar-view-day f_show-calendar"></a>
                                                                                            
                                                <div><form:errors path="filterRules[${row.index}].deviceStateDateFrom" cssClass="errorMessage"/></div>
                                                <div><form:errors path="filterRules[${row.index}].deviceStateDateTo" cssClass="errorMessage"/></div>
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
                                                <div><form:errors path="filterRules[${row.index}].postalCode" cssClass="errorMessage"/></div>
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
                                    </td>
                                    <td class="removeColumn"><cti:icon nameKey="remove" href="javascript:removeRule(${row.index})" icon="icon-cross"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
            <div class="actionArea">
                <cti:button nameKey="add" type="submit" name="addButton" classes="f_allowSubmitViaEnterKey" icon="icon-add"/>
                <select name="ruleType" class="f_allowSubmitViaEnterKey fr">
                    <c:forEach items="${ruleTypes}" var="ruleType">
                        <option value="${ruleType}"><cti:formatObject value="${ruleType}"/></option>
                    </c:forEach>
                </select>
            </div>
        </tags:boxContainer2>
        
        <div class="pageActionArea">
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