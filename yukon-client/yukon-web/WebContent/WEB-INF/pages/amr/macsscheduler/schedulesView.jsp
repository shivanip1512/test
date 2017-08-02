<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:msgScope paths="yukon.web.modules.tools.scripts.innerView">

    <table class="compact-results-table has-actions">
        <thead>
            <tags:sort column="${scheduleName}"/>
            <tags:sort column="${categoryName}"/>
            <tags:sort column="${currentState}"/>
            <tags:sort column="${startDateTime}"/>
            <tags:sort column="${stopDateTime}"/>
            <th class="action-column"><cti:checkRolesAndProperties value="ENABLE_DISABLE_SCRIPTS"><cti:icon icon="icon-cog" classes="M0"/></cti:checkRolesAndProperties></th>
        </thead>
        <tfoot>
        </tfoot>
        <tbody>
            <c:forEach var="scheduleInfo" items="${list.resultList}">
                <c:set var="id" value="${scheduleInfo.schedule.id}"/>
                <c:choose>
                    <c:when test="${scheduleInfo.showControllable}">
                        <tr>
                    </c:when>
                    <c:otherwise>
                        <tr class="disabled">
                    </c:otherwise>
                </c:choose>
                    <td>
                        <!-- TODO: This will link to view/edit the schedule -->
                        ${fn:escapeXml(scheduleInfo.schedule.scheduleName)}
                    </td>
                    <td>
                        <cti:url var="scriptUrl" value="/macsscheduler/schedules/${id}/view" />
                        <a href="${scriptUrl}">
                        ${fn:escapeXml(scheduleInfo.schedule.categoryName)}
                        </a>
                    </td>
                    <td>
                        <c:set var="state" value="${scheduleInfo.schedule.currentState}" />
                        <c:choose>
                            <c:when test="${scheduleInfo.updatingState}">
                                <c:set var="color" value="yellow" />
                            </c:when>
                            <c:when test="${state eq 'Running'}">
                                <c:set var="color" value="green" />
                            </c:when>
                            <c:when test="${state eq 'Waiting'}">
                                <c:set var="color" value="white" />
                            </c:when>
                            <c:when test="${state eq 'Disabled'}">
                                <c:set var="color" value="red" />
                            </c:when>
                            <c:when test="${state eq 'Pending'}">
                                <c:set var="color" value="blue" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="color" value="white" />
                            </c:otherwise>
                        </c:choose>
                        <span class="box state-box ${color}">
                        </span>
                        <c:choose>
                            <c:when test="${scheduleInfo.updatingState}">
                                <cti:msg2 key=".state.updating"/>
                            </c:when>
                            <c:otherwise>
                                <i:inline key=".state.${state}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${scheduleInfo.schedule.nextRunTime.time > cti:constantValue('com.cannontech.message.macs.message.Schedule.INVALID_DATE')}">
                                <cti:formatDate value="${scheduleInfo.schedule.nextRunTime}" type="BOTH"/>
                            </c:when>
                            <c:otherwise>----</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${scheduleInfo.schedule.nextStopTime.time > cti:constantValue('com.cannontech.message.macs.message.Schedule.INVALID_DATE')}">
                                <cti:formatDate value="${scheduleInfo.schedule.nextStopTime}" type="BOTH"/>
                            </c:when>
                            <c:otherwise>----</c:otherwise>
                        </c:choose>
                    </td>
                    <cti:checkRolesAndProperties value="ENABLE_DISABLE_SCRIPTS">
                        <td>
                            <cm:dropdown icon="icon-cog">
                                <cti:url var="startStopUrl" value="/macsscheduler/schedules/${id}/startStop" />
                                <c:choose>
                                    <c:when test="${scheduleInfo.updatingState || scheduleInfo.disabledState}">
                                        <cm:dropdownOption key="yukon.common.start" icon="icon-bullet-go" disabled="true"/>
                                        <cm:dropdownOption key="yukon.common.cancel" icon="icon-cross" disabled="true"/>
                                    </c:when>
                                    <c:when test="${scheduleInfo.runningState || scheduleInfo.pendingState}">
                                        <cm:dropdownOption key="yukon.common.start" icon="icon-bullet-go" disabled="true"/>
                                        <cm:dropdownOption key="yukon.common.cancel" icon="icon-cross" data-popup=".js-stop-dialog-${id}"/>
                                        <div class="dn js-stop-dialog-${id}" data-dialog data-event="yukon:schedule:cancel"
                                            data-title="<cti:msg2 key=".cancel.dialogTitle"/>" data-width="500" data-ok-text="<cti:msg2 key="yukon.common.stop"/>"
                                            data-url="${startStopUrl}"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <cm:dropdownOption key="yukon.common.start" icon="icon-bullet-go" data-popup=".js-start-dialog-${id}"/>
                                        <div class="dn js-start-dialog-${id}" data-dialog
                                            data-title="<cti:msg2 key=".start.dialogTitle"/>" data-width="500" data-ok-text="<cti:msg2 key="yukon.common.start"/>"
                                            data-url="${startStopUrl}" data-event="yukon:schedule:start"></div>            
                                        <cm:dropdownOption key="yukon.common.cancel" icon="icon-cross" disabled="true"/>
                                    </c:otherwise>
                                </c:choose>
                                <cti:url var="toggleUrl" value="/macsscheduler/schedules/toggleState?id=${id}" />
                                <c:choose>
                                    <c:when test="${scheduleInfo.updatingState}">
                                        <cm:dropdownOption key="yukon.common.enable" icon="icon-accept" disabled="true"/>
                                        <cm:dropdownOption key="yukon.common.disable" icon="icon-delete" disabled="true"/>
                                    </c:when>
                                    <c:when test="${scheduleInfo.disabledState}">
                                        <cm:dropdownOption key="yukon.common.enable" icon="icon-accept" classes="js-script-toggle" data-schedule-id="${id}"/>
                                        <cm:dropdownOption key="yukon.common.disable" icon="icon-delete" disabled="true"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cm:dropdownOption key="yukon.common.enable" icon="icon-accept" disabled="true"/>
                                        <cm:dropdownOption key="yukon.common.disable" icon="icon-delete" classes="js-script-toggle" data-schedule-id="${id}"/>
                                    </c:otherwise>
                                </c:choose>
                            </cm:dropdown>
                        </td>
                    </cti:checkRolesAndProperties>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${list}" adjustPageCount="true"/>
</cti:msgScope>