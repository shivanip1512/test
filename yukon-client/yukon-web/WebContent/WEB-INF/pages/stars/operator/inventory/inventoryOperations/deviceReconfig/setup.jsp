<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="deviceReconfig">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>

    
    <cti:dataGrid cols="2" tableClasses="deviceReconfigLayout">
        <cti:dataGridCell>
        <tags:boxContainer2 nameKey="setupContainer" hideEnabled="false">
            <form:form id="saveForm" commandName="deviceReconfigOptions" action="/spring/stars/operator/inventory/inventoryOperations/deviceReconfig/save" method="post">
                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
            
                <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
                
                <br><br>
                
                <tags:formElementContainer nameKey="configurationOptions">
                    <tags:nameValueContainer2>
                        <tags:inputNameValue nameKey=".name" path="name" size="45" maxlength="250"/>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
                
                
                <br>
                
                <div>
                    <cti:button key="save" type="submit"/>
                    <cti:button key="cancel" name="cancelButton" type="submit"/>
                </div>
            </form:form>
            
        </tags:boxContainer2>
        </cti:dataGridCell>
                
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="pagingSettings">
                <c:choose>
                    <c:when test="${empty schedules}">
                        <div>
                            <i:inline key="yukon.web.widgets.commandScheduleWidget.noSchedules"/>
                        </div>
                    </c:when>
                
                    <c:otherwise>
                    
                        <table class="compactResultsTable">
                    
                            <tr>
                                <th><i:inline key="yukon.web.widgets.commandScheduleWidget.start"/></th>
                                <th><i:inline key="yukon.web.widgets.commandScheduleWidget.runPeriod"/></th>
                                <th><i:inline key="yukon.web.widgets.commandScheduleWidget.delayPeriod"/></th>
                                <th style="text-align:right;"><i:inline key="yukon.web.widgets.commandScheduleWidget.enabled"/></th>
                            </tr>
                        
                            <c:forEach var="schedule" items="${schedules}">
                            
                                <tr>
                                    
                                    <td>
                                        <span><cti:formatCron value="${schedule.startTimeCronString}"/></span>
                                    </td>
                                    
                                    <td>
                                        <span><cti:formatPeriod type="HM_SHORT" value="${schedule.runPeriod}"/></span>
                                    </td>
                                    
                                    <td>
                                        <span><cti:formatPeriod type="S" value="${schedule.delayPeriod}"/></span>
                                    </td>
                                    
                                    <td style="text-align:right;">
                                        <c:choose>
                                            <c:when test="${schedule.enabled}">
                                                <span class="okGreen"><i:inline key=".scheduleEnabled"/></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="errorRed"><i:inline key=".scheduleDisabled"/></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                </tr>
                            
                            </c:forEach>
                        
                        </table>
                    </c:otherwise>
                </c:choose>
            </tags:boxContainer2>
        </cti:dataGridCell>
        
    </cti:dataGrid>

</cti:standardPage>