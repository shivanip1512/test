<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="inventory.config.schedule">
    
    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="setupContainer">
                <cti:url var="url" value="/stars/operator/inventory/deviceReconfig/save"/>
                <form:form id="saveForm" modelAttribute="deviceReconfigOptions" action="${url}" method="post">
                    <cti:csrfToken/>
                    <div class="stacked">
                        <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                        <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
                    </div>
                    
                    <tags:nameValueContainer2 tableClass="with-form-controls">
                        <tags:inputNameValue nameKey=".taskName" path="name" size="35" maxlength="250"/>
                        <tags:nameValue2 excludeColon="true">
                            <tags:checkbox path="sendInService" descriptionNameKey=".sendInService"/>
                        </tags:nameValue2>
                        <tags:nameValue2 excludeColon="true">
                            <tags:checkbox path="sendOutOfService" descriptionNameKey=".sendOutOfService"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    
                    <div class="page-action-area">
                        <cti:button nameKey="save" type="submit" classes="primary action"/>
                        <cti:url var="url" value="/stars/operator/inventory/inventoryConfiguration">
                            <c:forEach items="${inventoryCollection.collectionParameters}" var="entry">
                                <cti:param name="${entry.key}" value="${entry.value}"/>
                            </c:forEach>
                        </cti:url>
                        <cti:button nameKey="cancel" href="${url}"/>
                    </div>
                </form:form>
                
            </tags:sectionContainer2>
        </div>
                
        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="pagingSettings">
                <cti:msgScope paths=",widgets.commandScheduleWidget">
                    <c:choose>
                        <c:when test="${empty schedules}">
                            <div class="empty-list">
                                <i:inline key=".noSchedules"/>
                            </div>
                        </c:when>
                        
                        <c:otherwise>
                        
                            <table class="compact-results-table">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".start"/></th>
                                        <th class="tar"><i:inline key=".runPeriod"/></th>
                                        <th class="tar"><i:inline key=".delayPeriod"/></th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:forEach var="schedule" items="${schedules}">
                                        <tr>
                                            <td><span><cti:formatCron value="${schedule.startTimeCronString}"/></span></td>
                                            <td class="tar"><span><cti:formatPeriod type="HM_SHORT" value="${schedule.runPeriod}"/></span></td>
                                            <td class="tar"><span><cti:formatPeriod type="S_SHORT" value="${schedule.delayPeriod}"/></span></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${schedule.enabled}">
                                                        <span class="success"><i:inline key="yukon.common.enabled"/></span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="error"><i:inline key="yukon.common.disabled"/></span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </cti:msgScope>
            </tags:sectionContainer2>
        </div>
        
    </div>
</cti:standardPage>