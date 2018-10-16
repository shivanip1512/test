<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<script>
    // Reinit the datepickers.
    $(function () {
        if (typeof yukon.ui.initDateTimePickers !== 'undefined') {
            yukon.ui.initDateTimePickers();
        }
    });
</script>

<cti:msgScope paths="modules.capcontrol.schedules.edit, modules.capcontrol.schedules, modules.capcontrol">
    <tags:setFormEditMode mode="${mode}"/>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:msg2 var="createTitle"  key=".create"/>
        <input type="hidden" class="js-popup-title" value="${createTitle}">
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="EDIT,VIEW">
        <input type="hidden" class="js-popup-title" value="${schedule.name}">
    </cti:displayForPageEditModes>
    <cti:msg2 var="confirmDeleteMsg" key=".confirmDelete" argument="${schedule.name}"/>
    <input type="hidden" class="js-confirm-delete" value="${confirmDeleteMsg}"/>
    <form:form modelAttribute="schedule" action="" method="post" data-mode="${mode}">
        <cti:csrfToken/>
        <form:hidden path="id"/>
        <form:hidden path="lastRunTime"/>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey="yukon.common.name">
                <tags:input path="name"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".interval">
                <cti:displayForPageEditModes modes="VIEW">
                    <c:if test="${schedule.repeatSeconds == 0}">
                        <cti:msg2 key="yukon.common.none.choice"/>
                    </c:if>
                    <c:if test="${schedule.repeatSeconds != 0}">
                        <cti:formatDuration type="DHMS_REDUCED" value="${schedule.repeatDuration}"/>
                    </c:if>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <select name="repeatSeconds">
                        <c:forEach var="interval" items="${intervals}">
                            <c:set var="selected" value="" />
                            <c:if test="${schedule.repeatSeconds == interval.seconds}">
                                <c:set var="selected" value="selected" />
                            </c:if>
                            <option value="${interval.seconds}" ${selected}>
                                <c:if test="${interval.seconds == 0}">
                                    <cti:msg2 key="yukon.common.none.choice"/>
                                </c:if>
                                <c:if test="${interval.seconds != 0}">
                                    <cti:formatDuration type="DHMS_REDUCED" value="${interval.duration}"/>
                                </c:if>
                            </option>
                        </c:forEach>
                    </select>
                </cti:displayForPageEditModes>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".lastRunTime">
                <c:choose>
                    <c:when test="${empty schedule.lastRunTime or schedule.lastRunTime.millis < epoch1990.millis}">
                        <i:inline key="yukon.common.dashes"/>
                    </c:when>
                    <c:otherwise>
                        <cti:formatDate value="${schedule.lastRunTime}" type="DATEHM"/>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".nextRunTime">
       
                    <tags:switchButton path="later" onNameKey="yukon.common.later" 
                        offNameKey="yukon.common.now" offClasses="M0" color="false" toggleGroup="next-run"/>
                
                <spring:bind path="nextRunTime">
                    <dt:dateTime path="nextRunTime" minDate="${nowTime}" cssClass="${status.error ? 'error' : ''}" 
                            toggleGroup="next-run" disabled="${!schedule.later}"/>
                    <c:if test="${status.error}"><br><form:errors path="${path}" cssClass="clear db error"/></c:if>
                </spring:bind>
            </tags:nameValue2>
            <tags:nameValue2 excludeColon="true">
                <label>
                    <tags:switchButton path="disabled" inverse="true" onNameKey=".enabled" offNameKey=".disabled"
                            offClasses="M0"/>
                </label>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <%-- Coming soon in a manageable way --%>
        <cti:displayForPageEditModes modes="VIEW,EDIT">
            <c:if test="${empty assignments}">
                <span class="empty-list"><i:inline key=".assignments.none"/></span>
            </c:if>
            <c:if test="${not empty assignments}">
                <tags:hideReveal2 titleKey=".assignments" showInitially="false">
                <ul class="simple-list">
                <c:forEach var="device" items="${assignments}">
                    <li>
                        ${fn:escapeXml(device.key)}
                        <ul class="no-icon-list">
                            <c:forEach var="command" items="${device.value}">
                                <li>
                                    ${command}
                                </li>
                            </c:forEach>
                        </ul>
                    </li>
                </c:forEach>
                </ul>
                </tags:hideReveal2>
            </c:if>
        </cti:displayForPageEditModes>
    </form:form>
    <cti:msg2 var="deleteMessage"  key=".deleteSuccess" />
    <form:form method="DELETE" data-delete-message="${deleteMessage}">
        <cti:csrfToken/>
    </form:form>
</cti:msgScope>