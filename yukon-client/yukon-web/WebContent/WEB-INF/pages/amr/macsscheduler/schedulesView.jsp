<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:url var="controlUrl" value="/spring/macsscheduler/schedules/controlView"/>
<c:url var="toggleUrl" value="/spring/macsscheduler/schedules/toggleUrl"/>
<c:url var="viewUrl" value="/spring/macsscheduler/schedules/view" />
<c:url var="toggleUrl" value="/spring/macsscheduler/schedules/toggleState" />

    <h2>Scheduled Scripts</h2><BR>
    <div id="schedules">
        <table class="resultsTable">
        <tr>
            <th id="Schedule Name">
                <form id="form_scheduleName" action="${viewUrl}" method="POST">
                    <a
                        href="javascript:document.getElementById('form_scheduleName').submit();">Schedule
                        Name</a>
                    <c:choose>
                        <c:when test="${sortBy == 'Schedule Name'}">
                            <c:choose>
                                <c:when test="${descending}">
                                    <span title="Sorted descending">&#9660;</span>
                                    <input type="hidden"
                                        name="descending" value="false" />
                                </c:when>
                                <c:otherwise>
                                    <span title="Sorted ascending">&#9650;</span>
                                    <input type="hidden"
                                        name="descending" value="true" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="descending"
                                value="false" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="sortBy"
                        value="Schedule Name" />
                </form>
            </th>
            <th id="Category Name">
                <form id="form_categoryName" action="${viewUrl}" method="POST">
                    <a
                        href="javascript:document.getElementById('form_categoryName').submit();">Category
                        Name</a>
                    <c:choose>
                        <c:when test="${sortBy == 'Category Name'}">
                            <c:choose>
                                <c:when test="${descending}">
                                    <span title="Sorted descending">&#9660;</span>
                                    <input type="hidden"
                                        name="descending" value="false" />
                                </c:when>
                                <c:otherwise>
                                    <span title="Sorted ascending">&#9650;</span>
                                    <input type="hidden"
                                        name="descending" value="true" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="descending"
                                value="false" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="sortBy"
                        value="Category Name" />
                </form>
            </th>
            <th id="Current State">
                <form id="form_currentState" action="${viewUrl}" method="POST">
                    <a
                        href="javascript:document.getElementById('form_currentState').submit();">Current
                        State</a>
                    <c:choose>
                        <c:when test="${sortBy == 'Current State'}">
                            <c:choose>
                                <c:when test="${descending}">
                                    <span title="Sorted descending">&#9660;</span>
                                    <input type="hidden"
                                        name="descending" value="false" />
                                </c:when>
                                <c:otherwise>
                                    <span title="Sorted ascending">&#9650;</span>
                                    <input type="hidden"
                                        name="descending" value="true" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="descending"
                                value="false" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="sortBy"
                        value="Current State" />
                </form>
            </th>
            <th id="Start Date/Time">
                <form id="form_startDate" action="${viewUrl}" method="POST">
                    <a
                        href="javascript:document.getElementById('form_startDate').submit();">Start
                        Date/Time</a>
                    <c:choose>
                        <c:when test="${sortBy == 'Start Date/Time'}">
                            <c:choose>
                                <c:when test="${descending}">
                                    <span title="Sorted descending">&#9660;</span>
                                    <input type="hidden"
                                        name="descending" value="false" />
                                </c:when>
                                <c:otherwise>
                                    <span title="Sorted ascending">&#9650;</span>
                                    <input type="hidden"
                                        name="descending" value="true" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="descending"
                                value="false" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="sortBy"
                        value="Start Date/Time" />
                </form>
            </th>
            <th id="Stop Date/Time">
                <form id="form_stopDate" action="${viewUrl}" method="POST">
                    <a
                        href="javascript:document.getElementById('form_stopDate').submit();">Stop
                        Date/Time</a>
                    <c:choose>
                        <c:when test="${sortBy == 'Stop Date/Time'}">
                            <c:choose>
                                <c:when test="${descending}">
                                    <span title="Sorted descending">&#9660;</span>
                                    <input type="hidden"
                                        name="descending" value="false" />
                                </c:when>
                                <c:otherwise>
                                    <span title="Sorted ascending">&#9650;</span>
                                    <input type="hidden"
                                        name="descending" value="true" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="descending"
                                value="false" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="sortBy"
                        value="Stop Date/Time" />
                </form>
            </th>
            <th id="Disable"></th>
        </tr>
        <c:forEach var="scheduleInfo" items="${list}">
            <c:choose>
                <c:when test="${scheduleInfo.updatingState}">
                    <c:set var="color" scope="page">#FFFF99</c:set>
                </c:when>
                <c:when test="${scheduleInfo.runningState}">
                    <c:set var="color" scope="page">#99FF99</c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="color" scope="page">white</c:set>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${scheduleInfo.showControllable}">
                    <tr
                        style="background-color: ${color}">
                </c:when>
                <c:otherwise>
                    <tr
                        style="background-color: ${color}; color: #999999;">
                </c:otherwise>
            </c:choose>

            <td>
                <c:choose>
                    <c:when
                        test="${scheduleInfo.showControllable}">
                        <form id="controlform_${scheduleInfo.schedule.id}"
                            action="${controlUrl}" method="POST">
                            <a
                                href="javascript:document.getElementById('controlform_${scheduleInfo.schedule.id}').submit();">${scheduleInfo.schedule.scheduleName}</a>
                            <input type="hidden" name="id"
                                value="${scheduleInfo.schedule.id}" />
                            <input type="hidden" name="sortBy"
                                value="${sortBy}" />
                            <input type="hidden" name="descending"
                                value="${descending}" />
                        </form>
                    </c:when>
                    <c:otherwise>
                        ${scheduleInfo.schedule.scheduleName}    
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                ${scheduleInfo.schedule.categoryName}
            </td>
            <td>
                <c:choose>
                    <c:when test="${scheduleInfo.updatingState}">
                        Updating...
                    </c:when>
                    <c:otherwise>
                        ${scheduleInfo.schedule.currentState}                        
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
            <td style="background-color: white">
                <c:if test="${scheduleInfo.showToggleButton}">
                    <form id="toggleform_${scheduleInfo.schedule.id }"
                        action="${toggleUrl}" method="POST">
                        <button
                        	onclick="javascript:document.getElementById('toggleform_${scheduleInfo.schedule.id}').submit();">
                        	<c:choose>
                        		<c:when
                                    test="${scheduleInfo.disabledState}">
                                	Enable
                            	</c:when>
                            	<c:otherwise>
                            		Disable
                            	</c:otherwise>
                            </c:choose>
                        </button>
                        
                        <input type="hidden" name="id"
                            value="${scheduleInfo.schedule.id}" />
                        <input type="hidden" name="sortBy"
                            value="${sortBy}" />
                        <input type="hidden" name="descending"
                            value="${descending}" />
                    </form>
                </c:if>
            </td>
            </tr>
        </c:forEach>
    </table>
    </div>


