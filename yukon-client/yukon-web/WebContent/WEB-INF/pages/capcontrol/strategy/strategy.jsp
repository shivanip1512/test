<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="strategy.${mode}">

<%@ include file="/capcontrol/capcontrolHeader.jspf" %>

    <tags:setFormEditMode mode="${mode}" />
    <cti:displayForPageEditModes modes="VIEW">
        <div class="js-page-additional-actions dn">
            <cti:checkRolesAndProperties value="SYSTEM_WIDE_CONTROLS">
                <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                    <li class="divider" />
                </cti:checkRolesAndProperties>
            </cti:checkRolesAndProperties>

            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <cti:url var="editUrl"
                    value="/capcontrol/strategies/${strategy.id}/edit" />
                <cm:dropdownOption key="components.button.edit.label"
                    icon="icon-pencil" href="${editUrl}" />
            </cti:checkRolesAndProperties>
        </div>
    </cti:displayForPageEditModes>

    <cti:url var="action" value="/capcontrol/strategies" />
    <form:form id="strategy-from" modelAttribute="strategy" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="id" />
        
        <c:set var="viewMode" value="${false}" />
        <cti:displayForPageEditModes modes="VIEW">
            <c:set var="viewMode" value="${true}" />
        </cti:displayForPageEditModes>

        <c:set var="tableClass" value="${viewMode ? '' : 'with-form-controls'}" />
        
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="info" styleClass="stacked-lg">
                    <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">
                    
                        <tags:nameValue2 nameKey="yukon.common.name">
                            <tags:input path="name" maxlength="32"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".controlMethod">
                            <tags:selectWithItems id="control-method" path="controlMethod" items="${ControlMethods}" 
                                inputClass="with-option-hiding"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".controlAlgorithm">
                            <tags:selectWithItems id="control-algorithm" path="algorithm" items="${ControlAlgorithms}" 
                                inputClass="with-option-hiding" />
                        </tags:nameValue2>
                        
                        <cti:default var="format" value="DHMS_REDUCED"/>
                        
                        
                        <tags:nameValue2 nameKey=".controlInterval" rowClass="js-not-time-of-day">
                            <tags:intervalDropdown path="controlInterval" intervals="${analysisIntervals}" noneKey=".newDataOnly"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".minResponseTime">
                            <tags:intervalDropdown path="minResponseTime" intervals="${analysisIntervals}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".minConfirmPercent">
                            <tags:input path="minConfirmPercent" size="3"/>
                            <i:inline key="yukon.common.units.PERCENT"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".failurePercent">
                            <tags:input path="failurePercent" size="3"/>
                            <i:inline key="yukon.common.units.PERCENT"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".controlSendRetries" >
                            <tags:input path="controlSendRetries" size="3"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".controlDelayTime" rowClass="js-not-time-of-day">
                            <tags:intervalDropdown path="controlDelayTime" intervals="${analysisIntervals}" />
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".integrateControl" rowClass="js-not-time-of-day">
                            <spring:bind path="integrateFlag">
                                <c:if test="${not viewMode or not status.value}">
                                    <tags:switchButton path="integrateFlag" offClasses="M0"
                                        toggleGroup="integrateControl" toggleAction="hide" color="${not viewMode}" />
                                </c:if>
                                <c:if test="${not viewMode or status.value}">
                                    <tags:intervalDropdown path="integratePeriod"  classes="vat"
                                        intervals="${integrateIntervals}" toggleGroup="integrateControl" />
                                </c:if>
                            </spring:bind>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".likeDay" rowClass="js-not-time-of-day">
                            <%-- Fall back to like-day history control --%>
                            <tags:switchButton path="likeDayFallBack" offClasses="M0" color="${not viewMode}"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".maxDailyOperations">
                            <tags:input path="maxDailyOperation" size="3" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".maxDailyOperationsHalt"  rowClass="js-not-time-of-day">
                            <spring:bind path="maxOperationEnabled">
                                <tags:switchButton path="maxOperationEnabled" offClasses="M0"
                                    color="${not viewMode}" />
                            </spring:bind>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".endDaySetting" rowClass="js-not-time-of-day">
                            <div class="button-group">
                                <c:forEach var="endDaySetting" items="${EndDaySettings}">
                                    <tags:radio path="endDaySettings" value="${endDaySetting}" key="${endDaySetting}" 
                                        classes="yes M0" />
                                </c:forEach>
                            </div>
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="peakSettings" styleClass="js-not-time-of-day">
                    <tags:nameValueContainer2 tableClass="name-collapse natural-width ${tableClass}">
                        <tags:nameValue2 nameKey=".peakDays">
                            <div class="button-group stacked">
                                <c:set var="noDaysSelected" value="${true}"/>
                                <c:set var="daysString" value=""/>
                                <c:forEach var="dayOfWeek" items="${DaysOfWeek}">
                                    <cti:displayForPageEditModes modes="VIEW">
                                        <spring:bind path="peakDays[${dayOfWeek}]">
                                            <c:if test="${status.value && dayOfWeek.display}">
                                                <c:if test="${not empty daysString}">
                                                    <c:set var="daysString">${daysString}<i:inline key="yukon.common.comma"/>&nbsp;<i:inline key="${dayOfWeek}"/></c:set>
                                                </c:if>
                                                <c:if test="${empty daysString }">
                                                    <c:set var="daysString"><i:inline key="${dayOfWeek}"/></c:set>
                                                </c:if>
                                            </c:if>
                                        </spring:bind>
                                    </cti:displayForPageEditModes>
                                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                                        <c:set var="classes" value="${dayOfWeek.display ? '' : 'dn'}"/>
                                        <tags:check id="${dayOfWeek}_chk" path="peakDays[${dayOfWeek}]" key="${dayOfWeek}" classes="${classes}"/>
                                    </cti:displayForPageEditModes>
                                </c:forEach>
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${not empty daysString}">${daysString}</c:if>
                                    <c:if test="${empty daysString}"><span class="empty-list"><i:inline key="yukon.common.none"/></span></c:if>
                                </cti:displayForPageEditModes>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".peakTimes">
                            <dt:time id="startPeak" path="peakStartTime" wrapClass="fn"/>
                            <span class="${viewMode ? '' : 'vatb'}">&nbsp;<i:inline key="yukon.common.to"/>&nbsp;</span>
                            <dt:time id="stopPeak" path="peakStopTime" wrapClass="fn"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
            </div>

            <div class="column two nogutter">
            
                <table class="stacked-lg full-width section-table striped ${tableClass}">
                    <thead>
                        <tr class="js-not-time-of-day">
                            <th><i:inline key=".targetSetting"/></th>
                            <th class="tar"><i:inline key=".peak"/></th>
                            <th></th>
                            <th class="tar"><i:inline key=".offPeak"/></th>
                            <th></th>
                        </tr>
                        <tr class="js-time-of-day-only">
                            <th>Time</th>
                            <th class="tar"><i:inline key="yukon.common.weekday"/></th>
                            <th></th>
                            <th class="tar"><i:inline key="yukon.common.weekend"/></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="targetSettingType" items="${TargetSettingTypes}">
                            <tr data-mapping="${targetSettingType}">
                                
                                <td><i:inline key="${targetSettingType}"/></td>
                                
                                <td class="tar">
                                    <tags:input path="targetSettings[${targetSettingType}].peakValue" size="5" />
                                </td>
                                <td class="tal">${targetSettingType.units}<span class="js-time-of-day-only">&nbsp;<i:inline key="yukon.common.close"/></span></td>
                                
                                <td class="tar">
                                    <tags:input path="targetSettings[${targetSettingType}].offPeakValue" size="5" />
                                </td>
                                <td class="tal">${targetSettingType.units}<span class="js-time-of-day-only">&nbsp;<i:inline key="yukon.common.close"/></span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <tags:sectionContainer2 nameKey="maxDeltaVoltage" styleClass="stacked-lg js-max-delta">
                    <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">
                        <tags:nameValue2 nameKey=".targetSetting.MAX_DELTA">
                            <tags:input path="maxDeltaVoltage" size="5" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                 <div id="kvarMessage" class="empty-list dn"><i:inline key="yukon.web.modules.capcontrol.strategy.message.KVAR"/></div>
                </cti:displayForPageEditModes>
            
                <tags:sectionContainer2 nameKey="powerFactorSettings" styleClass="stacked-lg js-bus-ivvc-only">
                    <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">
                        
                        <tags:nameValue2 nameKey=".bandwidth" valueClass="tar">
                            <tags:input path="powerFactorCorrectionSetting.bandwidth" size="5"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".cost" valueClass="tar">
                            <tags:input path="powerFactorCorrectionSetting.cost" size="5" />
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".maxCost" valueClass="tar">
                            <tags:input path="powerFactorCorrectionSetting.maxCost" size="5" />
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="commPercentage" styleClass="stacked-lg js-ivvc-only">
                    <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">
                        
                         <tags:nameValue2 nameKey=".banks" valueClass="tar">
                            <tags:input path="minCommunicationPercentageSetting.banksReportingRatio" size="5"/>
                            <i:inline key="yukon.common.units.PERCENT"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".regulators" valueClass="tar">
                            <tags:input path="minCommunicationPercentageSetting.regulatorReportingRatio" size="5"/>
                            <i:inline key="yukon.common.units.PERCENT"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".voltageMonitors" valueClass="tar">
                            <tags:input path="minCommunicationPercentageSetting.voltageMonitorReportingRatio" size="5"/>
                            <i:inline key="yukon.common.units.PERCENT"/>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".considerPhase">
                            <tags:switchButton path="minCommunicationPercentageSetting.considerPhase" offClasses="M0"
                                    color="${not viewMode}"/>
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                    
                <tags:sectionContainer2 nameKey="voltViolations" styleClass="js-ivvc-only">
                
                    <table>
                        <thead>
                            <th></th>
                            <th class="tar"><i:inline key="yukon.common.low"/></th>
                            <th></th>
                            <th class="tar"><i:inline key="yukon.common.high"/></th>
                            <th></th>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <tr>
                                <td><i:inline key=".bandwidth"/></td>
                                
                                <td class="tar">
                                    <tags:input path="voltageViolationSettings[LOW_VOLTAGE_VIOLATION].bandwidth" size="5" />
                                </td>                
                                <td class="tal"><i:inline key="yukon.common.units.VOLTS"/></td>
                                
                                <td class="tar">
                                    <tags:input path="voltageViolationSettings[HIGH_VOLTAGE_VIOLATION].bandwidth" size="5" />
                                </td>
                                <td class="tal"><i:inline key="yukon.common.units.VOLTS"/></td>
                            </tr>                
                            <tr>
                                <td><i:inline key=".cost"/></td>
                                
                                <td class="tar">
                                    <tags:input path="voltageViolationSettings[LOW_VOLTAGE_VIOLATION].cost" size="5" />
                                </td>
                                <td></td>                
                                
                                <td class="tar">
                                    <tags:input path="voltageViolationSettings[HIGH_VOLTAGE_VIOLATION].cost" size="5" />
                                </td>
                                <td></td>
                            </tr>                
                            <tr>
                                <td><i:inline key=".emergencyCost"/></td>
                                
                                <td class="tar">
                                    <tags:input path="voltageViolationSettings[LOW_VOLTAGE_VIOLATION].emergencyCost" size="5" />
                                </td>
                                <td></td>                
                                
                                <td class="tar">
                                    <tags:input path="voltageViolationSettings[HIGH_VOLTAGE_VIOLATION].emergencyCost" size="5" />
                                </td>
                                <td></td>
                            </tr>                
                        </tbody>
                    </table>
                </tags:sectionContainer2>
            </div>
        </div>
        
        <div class="page-action-area">
            
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:msg2 var="deleteTitle" key=".strategies.delete"/>
                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:strategy:delete"
                    title="${deleteTitle}"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${strategy.name}"/>
                
                <cti:url var="viewUrl" value="/capcontrol/strategies/${strategy.id}" />
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    
    <cti:url var="url" value="/capcontrol/strategies/${strategy.id}"/>
    <form:form id="delete-strategy" method="DELETE" action="${url}">
        <cti:csrfToken/>
    </form:form>
    
    <cti:toJson id="method-to-algorithms" object="${methodToAlgorithms}"/>
    <cti:toJson id="algorithm-to-settings" object="${algorithmToSettings}"/>
    
    <cti:includeScript link="/resources/js/pages/yukon.da.strategy.js"/>
    
</cti:standardPage>