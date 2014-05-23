<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:url value="/stars/operator/inventory/deviceReconfig/save" var="deviceReconfigOptionsURl"/>
<cti:standardPage module="operator" page="deviceReconfig">

    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="setupContainer">
                <form:form id="saveForm" commandName="deviceReconfigOptions" action="${deviceReconfigOptionsURl}" method="post">
                    <cti:csrfToken/>
                    <div class="stacked">
                        <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                        <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
                    </div>
                    
                    <tags:sectionContainer2 nameKey="configurationOptions">
                        <tags:nameValueContainer2>
                            <tags:inputNameValue nameKey=".name" path="name" size="35" maxlength="250"/>
                            <tags:checkboxNameValue nameKey=".sendInServiceLabel" checkBoxDescriptionNameKey=".sendInService" path="sendInService"/>
                        </tags:nameValueContainer2>
                    </tags:sectionContainer2>
                    
                    <div class="page-action-area">
                        <cti:button nameKey="save" type="submit" classes="primary action"/>
                        <cti:button nameKey="cancel" name="cancelButton" type="submit"/>
                    </div>
                </form:form>
                
            </tags:sectionContainer2>
        </div>
                
        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="pagingSettings">
                <c:choose>
                    <c:when test="${empty schedules}">
                        <div>
                            <i:inline key="yukon.web.widgets.commandScheduleWidget.noSchedules"/>
                        </div>
                    </c:when>
                
                    <c:otherwise>
                    
                        <table class="compact-results-table">
                            <thead>
                                <tr>
                                    <th><i:inline key="yukon.web.widgets.commandScheduleWidget.start"/></th>
                                    <th><i:inline key="yukon.web.widgets.commandScheduleWidget.runPeriod"/></th>
                                    <th><i:inline key="yukon.web.widgets.commandScheduleWidget.delayPeriod"/></th>
                                    <th><i:inline key="yukon.web.widgets.commandScheduleWidget.enabled"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="schedule" items="${schedules}">
                                    <tr>
                                        <td><span><cti:formatCron value="${schedule.startTimeCronString}"/></span></td>
                                        <td><span><cti:formatPeriod type="HM_SHORT" value="${schedule.runPeriod}"/></span></td>
                                        <td><span><cti:formatPeriod type="S" value="${schedule.delayPeriod}"/></span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${schedule.enabled}">
                                                    <span class="success"><i:inline key=".scheduleEnabled"/></span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="error"><i:inline key=".scheduleDisabled"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </tags:sectionContainer2>
        </div>
        
    </div>
</cti:standardPage>