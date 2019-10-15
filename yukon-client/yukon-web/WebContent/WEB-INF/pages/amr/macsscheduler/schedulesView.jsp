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
            <th class="action-column">
                <cti:checkRolesAndProperties value="MACS_SCRIPTS" level="INTERACT">
                    <cti:icon icon="icon-cog" classes="M0"/>
                </cti:checkRolesAndProperties></th>
        </thead>
        <tfoot>
        </tfoot>
        <tbody>
            <c:forEach var="schedule" items="${list.resultList}">
                <c:set var="id" value="${schedule.id}"/>
                <c:choose>
                    <c:when test="${schedule.showControllable}">
                        <tr>
                    </c:when>
                    <c:otherwise>
                        <tr class="disabled">
                    </c:otherwise>
                </c:choose>
                    <td>
                        <cti:checkRolesAndProperties value="MACS_SCRIPTS" level="UPDATE">
                            <c:set var="isLinkVisible" value="true" />
                        </cti:checkRolesAndProperties>
                        <c:choose>
                            <c:when test="${isLinkVisible == 'true'}">
                                <cti:url var="viewUrl" value="/macsscheduler/schedules/${id}/view" />
                                <a href="${viewUrl}"> ${fn:escapeXml(schedule.scheduleName)} </a>
                            </c:when>
                            <c:otherwise>
                                ${fn:escapeXml(schedule.scheduleName)}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        ${fn:escapeXml(schedule.categoryName)}
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${schedule.isUpdating()}">
                                <c:set var="color" value="yellow" />
                            </c:when>
                            <c:when test="${schedule.isRunning()}">
                                <c:set var="color" value="green" />
                            </c:when>
                            <c:when test="${schedule.isWaiting()}">
                                <c:set var="color" value="white" />
                            </c:when>
                            <c:when test="${schedule.isDisabled()}">
                                <c:set var="color" value="red" />
                            </c:when>
                            <c:when test="${schedule.isPending()}">
                                <c:set var="color" value="blue" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="color" value="white" />
                            </c:otherwise>
                        </c:choose>
                        <span class="box state-box ${color}">
                        </span>
                        <c:choose>
                            <c:when test="${schedule.isUpdating()}">
                                <cti:msg2 key=".state.updating"/>
                            </c:when>
                            <c:otherwise>
                            <!-- Change to use enum -->
                                <i:inline key=".state.${schedule.getState().getStateString()}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <cti:formatDate value="${schedule.nextRunTime}" type="BOTH" nullText="----"/>
                    </td>
                    <td>
                        <cti:formatDate value="${schedule.nextStopTime}" type="BOTH" nullText="----"/>
                    </td>
                    <cti:checkRolesAndProperties value="MACS_SCRIPTS" level="INTERACT">
                        <td>
                            <cm:dropdown icon="icon-cog">
                                <cti:url var="startStopUrl" value="/macsscheduler/schedules/${id}/startStop" />
                                <c:choose>
                                    <c:when test="${schedule.isUpdating() || schedule.isDisabled()}">
                                        <cm:dropdownOption key="yukon.common.start" icon="icon-bullet-go" disabled="true"/>
                                        <cm:dropdownOption key="yukon.common.cancel" icon="icon-cross" disabled="true"/>
                                    </c:when>
                                    <c:when test="${schedule.isRunning() || schedule.isPending()}">
                                        <cm:dropdownOption key="yukon.common.start" icon="icon-bullet-go" disabled="true"/>
                                        <cm:dropdownOption key="yukon.common.cancel" icon="icon-cross" data-popup="#stopDialog-${id}"/>
                                        <div id="stopDialog-${id}" class="dn" data-dialog data-event="yukon:schedule:cancel"
                                            data-title="<cti:msg2 key=".cancel.dialogTitle"/>" data-width="500" data-ok-text="<cti:msg2 key="yukon.common.stop"/>"
                                            data-url="${startStopUrl}"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <cm:dropdownOption key="yukon.common.start" icon="icon-bullet-go" data-popup="#startDialog-${id}"/>
                                        <div id="startDialog-${id}" class="dn" data-dialog
                                            data-title="<cti:msg2 key=".start.dialogTitle"/>" data-width="500" data-ok-text="<cti:msg2 key="yukon.common.start"/>"
                                            data-url="${startStopUrl}" data-event="yukon:schedule:start"></div>            
                                        <cm:dropdownOption key="yukon.common.cancel" icon="icon-cross" disabled="true"/>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${schedule.isUpdating()}">
                                        <cm:dropdownOption key="yukon.common.enable" icon="icon-accept" disabled="true"/>
                                        <cm:dropdownOption key="yukon.common.disable" icon="icon-delete" disabled="true"/>
                                    </c:when>
                                    <c:when test="${schedule.isDisabled()}">
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
    <tags:pagingResultsControls result="${list}" adjustPageCount="true" hundreds="true"/>
</cti:msgScope>