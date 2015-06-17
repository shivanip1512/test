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
        
    <tags:setFormEditMode mode="${mode}" />

    <cti:url var="action" value="/capcontrol/strategies" />
    <form:form id="strategy-from" commandName="strategy" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="id" />
        
        <c:set var="viewMode" value="${false}" />
        <cti:displayForPageEditModes modes="VIEW">
            <c:set var="viewMode" value="${true}" />
        </cti:displayForPageEditModes>
        <c:set var="tableClass" value="${viewMode ? '' : 'with-form-controls'}" />
        
        <div class="column-12-12 clearfix">
            <div class="column one">
            
                <tags:sectionContainer title="Strategy Info" styleClass="stacked-lg">
                    <tags:nameValueContainer tableClass="natural-width ${tableClass}">
                    
                        <tags:nameValue name="Name">
                            <tags:input path="name" />
                        </tags:nameValue>
                        
                        <tags:nameValue name="Control Method">
                            <tags:selectWithItems id="control-method" path="controlMethod" items="${ControlMethods}" 
                                inputClass="with-option-hiding"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Control Algorithm">
                            <tags:selectWithItems id="control-algorithm" path="algorithm" items="${ControlAlgorithms}" 
                                inputClass="with-option-hiding" />
                        </tags:nameValue>
                        
                        <tags:nameValue name="Analysis Interval" class="js-not-time-of-day">
                            <%-- How often the system should check to determine the need for control --%>
                            <tags:intervalStepper path="controlInterval" intervals="${analysisIntervals}" noneKey=".newDataOnly"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Max Confirm Time">
                            <%-- How much time the system waits until the control is considered successful --%>
                            <tags:intervalStepper path="minResponseTime" intervals="${analysisIntervals}" />
                        </tags:nameValue>
                        <tags:nameValue name="Pass Percent" >
                            <%--This amount of change or higher is considered to be a successful control --%>
                            <tags:input path="minConfirmPercent" size="3"/>
                            %
                        </tags:nameValue>
                        
                        <tags:nameValue name="Failure Percent">
                            <%-- This amount of change or lower is considered to be a failed control --%>
                            <tags:input path="failurePercent" size="3"/>
                            %
                        </tags:nameValue>
                        
                        <tags:nameValue name="Send Retries" size="3">
                            <%-- How many times the control should be repeatedly sent out to the field --%>
                            <tags:input path="controlSendRetries" size="3"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Delay Time" class="js-not-time-of-day">
                            <%-- How much time we should wait before sending the control command into the field --%>
                            <tags:intervalStepper path="controlDelayTime" intervals="${analysisIntervals}" />
                        </tags:nameValue>
                        
                        <tags:nameValue name="Integrate Control" class="js-not-time-of-day">
                            <spring:bind path="integrateFlag">
                                <c:if test="${not viewMode or not status.value}">
                                    <tags:switchButton path="integrateFlag" toggleGroup="integrateControl"
                                        color="${not viewMode}" />
                                </c:if>
                                <c:if test="${not viewMode or status.value}">
                                    <tags:intervalStepper path="integratePeriod" intervals="${integrateIntervals}" toggleGroup="integrateControl" />
                                </c:if>
                            </spring:bind>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Like-day Control Fallback">
                            <%-- Fall back to like-day history control --%>
                            <tags:switchButton path="likeDayFallBack" color="${not viewMode}"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Max Daily Operation" class="js-not-time-of-day">
                            <spring:bind path="maxOperationEnabled">
                                <c:if test="${not viewMode or not status.value}">
                                    <%-- Disable automatic control on this device upon reaching the max number of operations --%>
                                    <tags:switchButton path="maxOperationEnabled" toggleGroup="maxOperation"
                                        color="${not viewMode}" />
                                </c:if>
                                <c:if test="${not viewMode or status.value}">
                                    <%-- The total number of controls allowed per day. 0=Unlimited --%>
                                    <tags:input path="maxDailyOperation" size="3" toggleGroup="maxOperation"/>
                                </c:if>
                            </spring:bind>
                        </tags:nameValue>
                        
                        <tags:nameValue name="End Day Settings" class="js-not-time-of-day">
                            <div class="button-group">
                                <c:forEach var="endDaySetting" items="${EndDaySettings}">
                                    <tags:radio path="endDaySettings" value="${endDaySetting}" key="${endDaySetting}" 
                                        classes="yes"/>
                                </c:forEach>
                            </div>
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                </tags:sectionContainer>
                
                <tags:sectionContainer title="Peak Settings" styleClass="js-not-time-of-day">
                    <tags:nameValueContainer tableClass="name-collapse natural-width ${tableClass}">
                        <tags:nameValue name="Peak Days">
                            <div class="button-group stacked">
                                <c:set var="noDaysSelected" value="${true}"/>
                                <c:forEach var="dayOfWeek" items="${DaysOfWeek}">
                                    <cti:displayForPageEditModes modes="VIEW">
                                        <spring:bind path="peakDays[${dayOfWeek}]">
                                            <c:if test="${status.value}">
                                                <c:if test="${not noDaysSelected}">, </c:if>
                                                <c:set var="noDaysSelected" value="${false}"/>
                                            </c:if>
                                        </spring:bind>
                                    </cti:displayForPageEditModes>
                                    <tags:check path="peakDays[${dayOfWeek}]" key="${dayOfWeek}"/>
                                </c:forEach>
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${noDaysSelected}">
                                        <span class="empty-list">None</span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </div>
                        </tags:nameValue>
                        <tags:nameValue name="Peak Times">
                            <dt:time path="peakStartTime" wrapClass="fn"/>
                            <span class="${viewMode ? '' : 'vatb'}">&nbsp;to&nbsp;</span>
                            <dt:time path="peakStopTime" wrapClass="fn"/>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    <div class="clear">
                    </div>
                </tags:sectionContainer>
                
            </div>

            <div class="column two nogutter">
            
                <table class="stacked-lg full-width section-table striped ${tableClass}">
                    <thead>
                        <tr class="js-not-time-of-day">
                            <th>Target Setting</th>
                            <th class="tar">Peak</th>
                            <th></th>
                            <th class="tar">Off Peak</th>
                            <th></th>
                        </tr>
                        <tr class="js-time-of-day-only">
                            <th>Time</th>
                            <th class="tar">Weekday</th>
                            <th></th>
                            <th class="tar">Weekend</th>
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
                                <td class="tal"><span class="js-time-of-day-only">Close&nbsp;</span>${targetSettingType.units}</td>
                                
                                <td class="tar">
                                    <tags:input path="targetSettings[${targetSettingType}].offPeakValue" size="5" />
                                </td>
                                <td class="tal"><span class="js-time-of-day-only">Close&nbsp;</span>${targetSettingType.units}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            
                <tags:sectionContainer title="Power Factor Correction Settings" styleClass="stacked-lg js-bus-ivvc-only">
                    <tags:nameValueContainer tableClass="natural-width ${viewMode ? 'joe' : 'with-form-controls'}">
                        
                        <tags:nameValue name="Bandwidth" valueClass="tar">
                            <tags:input path="powerFactorCorrectionSetting.bandwidth" size="5"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Cost" valueClass="tar">
                            <tags:input path="powerFactorCorrectionSetting.cost" size="5" />
                        </tags:nameValue>
                        
                        <tags:nameValue name="Max Cost" valueClass="tar">
                            <tags:input path="powerFactorCorrectionSetting.maxCost" size="5" />
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                </tags:sectionContainer>
                
                <tags:sectionContainer title="Comm Percentage Setting" styleClass="stacked-lg js-ivvc-only">
                    <tags:nameValueContainer tableClass="natural-width ${tableClass}">
                        
                        <tags:nameValue name="Banks" valueClass="tar">
                            <tags:input path="minCommunicationPercentageSetting.banksReportingRatio" size="5"/>
                            %
                        </tags:nameValue>
                        
                        <tags:nameValue name="Regulator" valueClass="tar">
                            <tags:input path="minCommunicationPercentageSetting.regulatorReportingRatio" size="5"/>
                            %
                        </tags:nameValue>
                        
                        <tags:nameValue name="Voltage Monitors" valueClass="tar">
                            <tags:input path="minCommunicationPercentageSetting.voltageMonitorReportingRatio" size="5"/>
                            %
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                </tags:sectionContainer>
                    
                <tags:sectionContainer title="Voltage Violations" styleClass="js-ivvc-only">
                
                    <table>
                        <thead>
                            <td></td>
                            <td class="tar">Low</td>
                            <td></td>
                            <td class="tar">High</td>
                            <td></td>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <tr>
                                <td>Bandwidth</td>
                                
                                <td class="tar">
                                    <tags:input path="voltageViolationSettings[LOW_VOLTAGE_VIOLATION].bandwidth" size="5" />
                                </td>                
                                <td class="tal">Volts</td>
                                
                                <td class="tar">
                                    <tags:input path="voltageViolationSettings[HIGH_VOLTAGE_VIOLATION].bandwidth" size="5" />
                                </td>
                                <td class="tal">Volts</td>
                            </tr>                
                            <tr>
                                <td>Cost</td>
                                
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
                                <td>Emergency Cost</td>
                                
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
                </tags:sectionContainer>
            </div>
        </div>
        
        <div class="page-action-area">
            
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="editUrl" value="/capcontrol/strategies/${strategy.id}/edit" />
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                
                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:regulator:delete"
                    disabled="${deleteDisabled}" title="${deleteTitle}"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${regulator.name}"/>
                
                <cti:url var="viewUrl" value="/capcontrol/strategies/${strategy.id}" />
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    
    <cti:url var="url" value="/capcontrol/strategy/${strategy.id}"/>
    <form:form id="delete-strategy" method="DELETE" action="${url}"></form:form>
    
    <cti:toJson id="method-to-algorithms" object="${methodToAlgorithms}"/>
    <cti:toJson id="algorithm-to-settings" object="${algorithmToSettings}"/>
    
    <cti:includeScript link="/resources/js/pages/yukon.da.strategy.js"/>
    
</cti:standardPage>