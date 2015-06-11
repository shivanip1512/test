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
                        
                        <tags:nameValue name="Analysis Interval">
                            <%-- Update Only (0) , 1 Sec, 1 Day --%>
                            <tags:input path="controlInterval" size="5"/>
                            sec
                        </tags:nameValue>
                        
                        <tags:nameValue name="Max Confirm Time">
                            <%-- None (0) , 1 Sec, 1 Day --%>
                            <tags:input path="minResponseTime" size="5"/>
                            sec
                        </tags:nameValue>
                        
                        <tags:nameValue name="Failure Percent">
                            <tags:input path="failurePercent" size="3"/>
                            %
                        </tags:nameValue>
                        
                        <tags:nameValue name="Pass Percent">
                            <tags:input path="minConfirmPercent" size="3"/>
                            %
                        </tags:nameValue>
                        
                        <tags:nameValue name="Send Retries" size="3">
                            <tags:input path="controlSendRetries" size="3"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Delay Time">
                            <%-- None (0), 1 sec, 1 day--%>
                            <tags:input path="controlDelayTime" size="5"/>
                            sec
                        </tags:nameValue>
                        
                        <tags:nameValue name="Integrate Control?">
                            <spring:bind path="integrateFlag">
                                <c:if test="${not viewMode or not status.value}">
                                    <tags:switchButton path="integrateFlag" toggleGroup="integrateControl"
                                        color="${not viewMode}" />
                                </c:if>
                                <c:if test="${not viewMode or status.value}">
                                    <%--1 min - 15 min--%>
                                    <tags:input path="integratePeriod" size="4" toggleGroup="integrateControl"/>
                                    sec
                                </c:if>
                            </spring:bind>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Like-day Control Fallback">
                            <tags:switchButton path="likeDayFallBack" color="${not viewMode}"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Max Daily Operation">
                            <spring:bind path="maxOperationDisabled">
                                <c:if test="${not viewMode or not status.value}">
                                    <tags:switchButton path="maxOperationDisabled" toggleGroup="maxOperation"
                                        color="${not viewMode}" />
                                </c:if>
                                <c:if test="${not viewMode or status.value}">
                                    <tags:input path="maxDailyOperation" size="3" toggleGroup="maxOperation"/>
                                </c:if>
                            </spring:bind>
                        </tags:nameValue>
                        
                        <tags:nameValue name="End Day Settings">
                            <div class="button-group">
                                <tags:selectWithItems items="" path=""/>
                                <tags:radio path="endDaySettings" value="(none)" label="(none)" classes="yes"/>
                                <tags:radio path="endDaySettings" value="Trip" label="Trip" classes="yes"/>
                                <tags:radio path="endDaySettings" value="Close" label="Close" classes="yes"/>
                            </div>
                                <%-- '(none)', 'Trip', 'Close'  --%>
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                </tags:sectionContainer>
                
                <tags:sectionContainer title="Peak Settings">
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
                        <tr>
                            <th>Target Setting</th>
                            <th class="tar">Peak</th>
                            <th></th>
                            <th class="tar">Off Peak</th>
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
                                <td class="tal">${targetSettingType.units}</td>
                                
                                <td class="tar">
                                    <tags:input path="targetSettings[${targetSettingType}].offPeakValue" size="5" />
                                </td>
                                <td class="tal">${targetSettingType.units}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            
                <tags:sectionContainer title="Power Factor Correction Settings" styleClass="stacked-lg js-bus-ivvc-only">
                    <tags:nameValueContainer tableClass="natural-width ${viewMode ? 'joe' : 'with-form-controls'}">
                        
                        <tags:nameValue name="Bandwidth">
                            <tags:input path="powerFactorCorrectionSetting.bandwidth" size="5"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Cost">
                            <tags:input path="powerFactorCorrectionSetting.cost" size="5" />
                        </tags:nameValue>
                        
                        <tags:nameValue name="Max Cost">
                            <tags:input path="powerFactorCorrectionSetting.maxCost" size="5" />
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                </tags:sectionContainer>
                
                <tags:sectionContainer title="Comm Percentage Setting" styleClass="stacked-lg js-ivvc-only">
                    <tags:nameValueContainer tableClass="natural-width ${tableClass}">
                        
                        <tags:nameValue name="Banks">
                            <tags:input path="minCommunicationPercentageSetting.banksReportingRatio" size="5"/>
                            %
                        </tags:nameValue>
                        
                        <tags:nameValue name="Regulator">
                            <tags:input path="minCommunicationPercentageSetting.regulatorReportingRatio" size="5"/>
                            %
                        </tags:nameValue>
                        
                        <tags:nameValue name="Voltage Monitors">
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
    
    <cti:includeScript link="/JavaScript/yukon.da.strategy.js"/>
    
</cti:standardPage>