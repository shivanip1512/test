<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="components.cronPicker">

<script>

$(function () {
    if (typeof yukon.ui.initDateTimePickers !== 'undefined') {
        yukon.ui.initDateTimePickers();
    }
});
</script>

    <form id="startScheduleForm">
    
    <div id="errorMsg" class="error">${error}</div>

        <tags:nameValueContainer2>

            <tags:nameValue2 nameKey="yukon.web.widgets.schedules.runTime">
            
                <input type="hidden" name="${jobId}_CRONEXP_FREQ" value="ONETIME" />

                <tags:switchButton id="nowlater" name="nowlater" onNameKey="yukon.common.later"
                    offNameKey="yukon.common.now" offClasses="M0" color="false" toggleGroup="later"
                    toggleAction="hide" />

            </tags:nameValue2>

            <input type="hidden" id="${jobId}_future-start" name="${jobId}_future-start"/>

            <tags:nameValue2 nameKey="yukon.common.blank" excludeColon="true" data-toggle-group="later">

                <%-- TIME --%>
                <div id="${jobId}-cron-exp-time" class="stacked">
                    <select name="${jobId}_CRONEXP_HOUR">
                        <c:forEach var="hour" begin="1" end="12" step="1">
                            <option value="${hour}" <c:if test="${hours == hour}">selected</c:if>>${hour}</option>
                        </c:forEach>
                    </select> : <select name="${jobId}_CRONEXP_MINUTE">
                        <c:forEach var="minute" begin="0" end="59" step="5">
                            <option value="${minute}"
                                <c:if test="${minutes == minute}">selected</c:if>>
                                <c:if test="${minute < 10}">
                                    <c:set var="minute" value="0${minute}" />
                                </c:if> ${minute}
                            </option>
                        </c:forEach>
                    </select> <select name="${jobId}_CRONEXP_AMPM">
                        <option value="AM" <c:if test="${amPm == 'AM'}">selected</c:if>>
                            <i:inline key=".am" />
                        </option>
                        <option value="PM" <c:if test="${amPm == 'PM'}">selected</c:if>>
                            <i:inline key=".pm" />
                        </option>
                    </select>
                </div>

            </tags:nameValue2>

            <tags:nameValue2 nameKey="yukon.common.blank" excludeColon="true" data-toggle-group="later">

                <div id="${jobId}-cron-exp-one-time" class="stacked">
                    <dt:date name="${jobId}_CRONEXP_ONETIME_DATE" value="${now}" minDate="${now}" />
                </div>
            </tags:nameValue2>

        </tags:nameValueContainer2>

    </form>

</cti:msgScope>


