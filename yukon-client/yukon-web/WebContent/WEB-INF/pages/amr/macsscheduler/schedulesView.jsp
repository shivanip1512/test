<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:url var="controlUrl" value="/macsscheduler/schedules/controlView"/>
<cti:url var="toggleUrl" value="/macsscheduler/schedules/toggleUrl"/>
<cti:url var="viewUrl" value="/macsscheduler/schedules/view" />
<cti:url var="toggleUrl" value="/macsscheduler/schedules/toggleState" />

<cti:msgScope paths="yukon.web.modules.tools.scripts.innerView">

<div id="schedules">
    <table class="compact-results-table">
        <thead>
            <tr>
                <th>
                    <form id="form_scheduleName" action="${viewUrl}" method="POST">
                        <cti:csrfToken/>
                        <a href="javascript:document.getElementById('form_scheduleName').submit();">
                            <cti:msg2 key=".scheduleName"/>
                        </a>
                        <c:choose>
                            <c:when test="${sortBy == 'Schedule Name'}">
                                <c:choose>
                                    <c:when test="${descending}">
                                    <span title="<cti:msg2 key=".sortedDescending"/>"><cti:icon icon="icon-bullet-arrow-up" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="false" />
                                    </c:when>
                                    <c:otherwise>
                                        <span title="<cti:msg2 key=".sortedAscending"/>"><cti:icon icon="icon-bullet-arrow-down" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="true" />
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="descending" value="false" />
                            </c:otherwise>
                        </c:choose>
                        <input type="hidden" name="sortBy" value="Schedule Name" />
                    </form>
                </th>
                <th>
                    <form id="form_categoryName" action="${viewUrl}" method="POST">
                        <cti:csrfToken/>
                        <a href="javascript:document.getElementById('form_categoryName').submit();">
                            <cti:msg2 key=".categoryName"/>
                        </a>
                        <c:choose>
                            <c:when test="${sortBy == 'Category Name'}">
                                <c:choose>
                                    <c:when test="${descending}">
                                        <span title="<cti:msg2 key=".sortedDescending"/>"><cti:icon icon="icon-bullet-arrow-up" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="false" />
                                    </c:when>
                                    <c:otherwise>
                                        <span title="<cti:msg2 key=".sortedAscending"/>"><cti:icon icon="icon-bullet-arrow-down" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="true" />
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="descending" value="false" />
                            </c:otherwise>
                        </c:choose>
                        <input type="hidden" name="sortBy" value="Category Name" />
                    </form>
                </th>
                <th id="Current State">
                    <form id="form_currentState" action="${viewUrl}" method="POST">
                        <cti:csrfToken/>
                        <a href="javascript:document.getElementById('form_currentState').submit();">
                            <cti:msg2 key=".currentState"/>
                        </a>
                        <c:choose>
                            <c:when test="${sortBy == 'Current State'}">
                                <c:choose>
                                    <c:when test="${descending}">
                                        <span title="<cti:msg2 key=".sortedDescending"/>"><cti:icon icon="icon-bullet-arrow-up" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="false" />
                                    </c:when>
                                    <c:otherwise>
                                        <span title="<cti:msg2 key=".sortedAscending"/>"><cti:icon icon="icon-bullet-arrow-down" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="true" />
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="descending" value="false" />
                            </c:otherwise>
                        </c:choose>
                        <input type="hidden" name="sortBy" value="Current State" />
                    </form>
                </th>
                <th id="Start Date/Time">
                    <form id="form_startDate" action="${viewUrl}" method="POST">
                        <cti:csrfToken/>
                        <a href="javascript:document.getElementById('form_startDate').submit();">
                            <cti:msg2 key=".startDateTime"/>
                        </a>
                        <c:choose>
                            <c:when test="${sortBy == 'Start Date/Time'}">
                                <c:choose>
                                    <c:when test="${descending}">
                                        <span title="<cti:msg2 key=".sortedDescending"/>"><cti:icon icon="icon-bullet-arrow-up" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="false" />
                                    </c:when>
                                    <c:otherwise>
                                        <span title="<cti:msg2 key=".sortedAscending"/>"><cti:icon icon="icon-bullet-arrow-down" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="true" />
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="descending" value="false" />
                            </c:otherwise>
                        </c:choose>
                        <input type="hidden" name="sortBy" value="Start Date/Time" />
                    </form>
                </th>
                <th id="Stop Date/Time">
                    <form id="form_stopDate" action="${viewUrl}" method="POST">
                        <cti:csrfToken/>
                        <a href="javascript:document.getElementById('form_stopDate').submit();">
                            <cti:msg2 key=".stopDateTime"/>
                        </a>
                        <c:choose>
                            <c:when test="${sortBy == 'Stop Date/Time'}">
                                <c:choose>
                                    <c:when test="${descending}">
                                        <span title="<cti:msg2 key=".sortedDescending"/>"><cti:icon icon="icon-bullet-arrow-up" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="false" />
                                    </c:when>
                                    <c:otherwise>
                                        <span title="<cti:msg2 key=".sortedAscending"/>"><cti:icon icon="icon-bullet-arrow-down" classes="fn"/></span>
                                        <input type="hidden" name="descending" value="true" />
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="descending" value="false" />
                            </c:otherwise>
                        </c:choose>
                        <input type="hidden" name="sortBy" value="Stop Date/Time" />
                    </form>
                </th>
                <th id="Disable"></th>
            </tr>
        </thead>
        <tfoot>
        </tfoot>
        <tbody>
            <c:forEach var="scheduleInfo" items="${list}">
            <c:choose>
                <c:when test="${scheduleInfo.showControllable}">
                    <tr>
                </c:when>
                <c:otherwise>
                    <tr class="disabled">
                </c:otherwise>
            </c:choose>
                    <td>
                        <c:choose>
                            <c:when test="${scheduleInfo.showControllable}">
                                <form id="controlform_${scheduleInfo.schedule.id}" action="${controlUrl}" method="POST">
                                    <cti:csrfToken/>
                                    <a href="javascript:document.getElementById('controlform_${scheduleInfo.schedule.id}').submit();">
                                        ${fn:escapeXml(scheduleInfo.schedule.scheduleName)}
                                    </a>
                                    <input type="hidden" name="id" value="${scheduleInfo.schedule.id}" />
                                    <input type="hidden" name="sortBy" value="${sortBy}" />
                                    <input type="hidden" name="descending" value="${descending}" />
                                </form>
                            </c:when>
                            <c:otherwise>
                                ${fn:escapeXml(scheduleInfo.schedule.scheduleName)}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        ${fn:escapeXml(scheduleInfo.schedule.categoryName)}
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
                                <cti:formatDate value="${scheduleInfo.schedule.nextRunTime}"
                                    type="BOTH" var="formattedStartTime" />
                                ${formattedStartTime}
                            </c:when>
                            <c:otherwise>----</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${scheduleInfo.schedule.nextStopTime.time > cti:constantValue('com.cannontech.message.macs.message.Schedule.INVALID_DATE')}">
                                <cti:formatDate value="${scheduleInfo.schedule.nextStopTime}"
                                    type="BOTH" var="formattedStopTime" />
                                ${formattedStopTime}
                            </c:when>
                            <c:otherwise>----</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${scheduleInfo.showToggleButton}">
                            <form action="${toggleUrl}" method="POST">
                                <cti:csrfToken/>
   
                                <tags:switch checked="${!scheduleInfo.disabledState}" name="toggleState" data-script-id="${scheduleInfo.schedule.id}" 
                                 classes="js-scripts-toggle toggle-sm"/>
                             
                                <input type="hidden" name="id" value="${scheduleInfo.schedule.id}" />
                                <input type="hidden" name="sortBy" value="${sortBy}" />
                                <input type="hidden" name="descending" value="${descending}" />
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</cti:msgScope>
<cti:includeScript link="/resources/js/widgets/yukon.widget.scripts.js"/>