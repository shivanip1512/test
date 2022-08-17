<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="tools" page="schedule">

<c:set var="clazz" value="${schedule.isRunning() ? '' : 'dn'}"/>
<div class="${clazz} js-running-warning user-message warning">
    <i:inline key=".runningEditWarning"/>
</div>

    <tags:setFormEditMode mode="${mode}" />

    <cti:url var="action" value="/macsscheduler/schedules/save" />
    <form:form id="macs-schedule" modelAttribute="schedule" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="id" />
        <form:hidden path="generateScript"/>

        <cti:tabs>
            <cti:msg2 var="generalTab" key=".generalTab" />
            <cti:tab title="${generalTab}">

                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".name">
                        <tags:input path="scheduleName" maxlength="60" size="30" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".categoryName">
                        <tags:selectWithItems path="categoryName" items="${categories}" inputClass="fl js-category-select"/>
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:button renderMode="button" nameKey="add" icon="icon-add" data-popup="#category-popup"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <cti:displayForPageEditModes modes="EDIT">
                        <c:set var="disableType" value="true"/>
                        <tags:hidden path="type"/>
                        <tags:hidden path="template"/>
                    </cti:displayForPageEditModes>
                    <tags:nameValue2 nameKey=".type">
                        <tags:selectWithItems path="type" items="${types}" inputClass="js-type" disabled="${disableType}"/>
                    </tags:nameValue2>
                    <c:set var="clazz" value="${schedule.isSimple() ? 'dn' : ''}"/>
                    <tags:nameValue2 nameKey=".template" rowClass="js-template ${clazz}">
                        <tags:selectWithItems path="template" items="${templates}" inputClass="js-template-value" disabled="${disableType}" itemLabel="description"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <tags:sectionContainer2 nameKey="startPolicySection">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".type">
                            <tags:selectWithItems path="startPolicy.policy"
                                items="${startPolicyOptions}" inputClass="js-start-policy"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".startPolicy.startTime" rowClass="js-start-time">
                            <div class="stacked">
                                <select name="startPolicy.time.hours">
                                    <c:forEach var="hour" begin="1" end="12" step="1">
                                        <option value="${hour}" <c:if test="${schedule.startPolicy.time.hours == hour}">selected</c:if>>${hour}</option>
                                    </c:forEach>
                                </select> :
                                
                                <select name="startPolicy.time.minutes">
                                    <c:forEach var="minute" begin="0" end="59" step="5">
                                        <option value="${minute}" <c:if test="${schedule.startPolicy.time.minutes == minute}">selected</c:if>>
                                            <c:if test="${minute < 10}">
                                                <c:set var="minute" value="0${minute}"/>
                                            </c:if>
                                            ${minute}
                                        </option>
                                    </c:forEach>
                                </select>
                                
                                <select name="startPolicy.time.amPm">
                                    <option value="AM" <c:if test="${schedule.startPolicy.time.amPm == 'AM'}">selected</c:if>>
                                        <i:inline key="yukon.web.components.cronPicker.am"/>
                                    </option>
                                    <option value="PM" <c:if test="${schedule.startPolicy.time.amPm == 'PM'}">selected</c:if>>
                                        <i:inline key="yukon.web.components.cronPicker.pm"/>
                                    </option>
                                </select>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".startPolicy.startDateTime" rowClass="js-start-dateTime">
                            <dt:dateTime path="startPolicy.startDateTime" value="${schedule.startPolicy.startDateTime}"/>
                            <tags:checkbox path="startPolicy.everyYear" styleClass="js-start-every-year"/>
                            <i:inline key=".startPolicy.everyYear" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".startPolicy.daysOfWeek" rowClass="js-start-weekDay" valueClass="button-group M0">
                            <c:forEach var="dayOfWeek" items="${daysOfWeek}">
                                <tags:check path="startPolicy.days[${dayOfWeek}]" key="yukon.common.day.${dayOfWeek}.short" classes="M0"/>
                            </c:forEach>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".startPolicy.dayOfMonth" rowClass="js-start-dayOfMonth">
                            <tags:input path="startPolicy.dayOfMonth" size="5"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".holiday">
                            <tags:selectWithItems path="startPolicy.holidayScheduleId"
                                items="${holidaySchedules}" itemValue="holidayScheduleId"
                                itemLabel="holidayScheduleName" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>

                <tags:sectionContainer2 nameKey="stopPolicySection">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".type">
                            <tags:selectWithItems path="stopPolicy.policy" inputClass="js-stop-policy"
                                items="${stopPolicyOptions}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".stopPolicy.stopTime" rowClass="js-stop-time">
                            <div class="stacked">
                                <select name="stopPolicy.time.hours">
                                    <c:forEach var="hour" begin="1" end="12" step="1">
                                        <option value="${hour}" <c:if test="${schedule.stopPolicy.time.hours == hour}">selected</c:if>>${hour}</option>
                                    </c:forEach>
                                </select> :
                                
                                <select name="stopPolicy.time.minutes">
                                    <c:forEach var="minute" begin="0" end="59" step="5">
                                        <option value="${minute}" <c:if test="${schedule.stopPolicy.time.minutes == minute}">selected</c:if>>
                                            <c:if test="${minute < 10}">
                                                <c:set var="minute" value="0${minute}"/>
                                            </c:if>
                                            ${minute}
                                        </option>
                                    </c:forEach>
                                </select>
                                
                                <select name="stopPolicy.time.amPm">
                                    <option value="AM" <c:if test="${schedule.stopPolicy.time.amPm == 'AM'}">selected</c:if>>
                                        <i:inline key="yukon.web.components.cronPicker.am"/>
                                    </option>
                                    <option value="PM" <c:if test="${schedule.stopPolicy.time.amPm == 'PM'}">selected</c:if>>
                                        <i:inline key="yukon.web.components.cronPicker.pm"/>
                                    </option>
                                </select>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".stopPolicy.duration" rowClass="js-stop-duration">
                            <tags:input path="stopPolicy.duration" size="5"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>

            </cti:tab>
            
            <c:set var="getTemplateClazz" value="${getTemplate ? 'js-get-template' : ''}"/>
            <c:set var="clazz" value="${schedule.isScript() ? 'dn' : ''}"/>
            <cti:msg2 var="commandsTab" key=".commandsTab" />
            <cti:tab title="${commandsTab}" headerClasses="js-commands-tab ${clazz} ${getTemplateClazz}">       
                <div id="command-content">     
                    <%@ include file="commandsTab.jsp" %>
                </div>
            </cti:tab>

            <c:set var="clazz" value="${schedule.isSimple() ? 'dn' : ''}"/>
            <cti:msg2 var="scriptTab" key=".scriptTab" />
            <cti:tab title="${scriptTab}" headerClasses="js-script-tab ${clazz} ${getTemplateClazz}">
                <div id="script-content">
                    <%@ include file="scriptsTab.jsp" %>
                </div>
            </cti:tab>
        </cti:tabs>

        <div class="page-action-area">
            <cti:displayForPageEditModes modes="VIEW">
                <cti:checkRolesAndProperties value="MACS_SCRIPTS" level="UPDATE">
                    <cti:url var="editUrl" value="/macsscheduler/schedules/${id}/edit" />
                    <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}" busy="true" disabled="${schedule.isRunning()}"/>
                </cti:checkRolesAndProperties>
                <c:if test="${schedule.isScript() && !schedule.template.isNoTemplateSelected()}">
                    <cti:button nameKey="viewScript" classes="js-view-script" busy="true"/>
                </c:if>
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <c:set var="clazz" value="${schedule.isScript() && !schedule.template.isNoTemplateSelected() ? '' : 'dn'}"/>
                <cti:button nameKey="save" classes="primary action js-script-save-generate ${clazz}" busy="true" disabled="${getTemplate}" />
                <cti:button nameKey="generateScript" classes="js-script-generate ${clazz}" busy="true" disabled="${getTemplate}" />
                <c:set var="clazz" value="${schedule.isScript() && !schedule.template.isNoTemplateSelected() ? 'dn' : ''}"/>
                <cti:button nameKey="save" type="submit" classes="primary action js-save-button ${clazz}" busy="true" disabled="${getTemplate}" />

                <cti:displayForPageEditModes modes="EDIT">
                    <cti:checkRolesAndProperties value="MACS_SCRIPTS" level="OWNER">
                        <cti:url var="deleteUrl" value="/macsscheduler/schedules/${id}/delete" />
                        <cti:button id="deleteSchedule" classes="delete" nameKey="delete" href="${deleteUrl}"/>
                        <d:confirm on="#deleteSchedule" nameKey="confirmDelete" argument="${schedule.scheduleName}"/>
                    </cti:checkRolesAndProperties>
                </cti:displayForPageEditModes>
                
                <cti:url var="viewUrl" value="/macsscheduler/schedules/${id}/view" />
                <cti:button nameKey="cancel" href="${viewUrl}" />
            </cti:displayForPageEditModes>
        </div>

    </form:form>
    <cti:toJson id="retry-types" object="${retryTypes}"/>
    <cti:toJson id="ied-300-types" object="${ied300Types}"/>
    <cti:toJson id="ied-400-types" object="${ied400Types}"/>
    
    <cti:includeScript link="/resources/js/pages/yukon.ami.macs.js" />
    
    <div class="dn" data-dialog id="category-popup"
        data-title="<cti:msg2 key=".category.title"/>"
        data-event="yukon:ami:macs:category:add">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".categoryName">
                <input id="categoryNameText" size="30" maxlength="50"/><br/>
                <div class="error dn js-category-blank"><i:inline key=".categoryName.error.blank"/></div>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </div>
</cti:standardPage>
