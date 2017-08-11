<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="tools" page="schedule">

    <tags:setFormEditMode mode="${mode}" />

    <cti:url var="action" value="/macsscheduler/schedules/save" />
    <form:form id="macs-schedule" commandName="schedule" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="id" />

        <cti:tabs>
            <cti:msg2 var="generalTab" key=".generalTab" />
            <cti:tab title="${generalTab}">

                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".name">
                        <tags:input path="scheduleName" maxlength="60" size="30" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".categoryName">
                        <tags:selectWithItems path="categoryName" items="${categories}" />
                    </tags:nameValue2>
                    <cti:displayForPageEditModes modes="EDIT">
                        <c:set var="disableType" value="true"/>
                    </cti:displayForPageEditModes>
                    <tags:nameValue2 nameKey=".type">
                        <tags:selectWithItems path="type" items="${types}" inputClass="js-type" disabled="${disableType}"/>
                    </tags:nameValue2>
                    <c:set var="clazz" value="${schedule.isSimple() ? 'dn' : ''}"/>
                    <tags:nameValue2 nameKey=".template" rowClass="js-template ${clazz}">
                        <tags:selectWithItems path="template" items="${templates}" inputClass="js-template" disabled="${disableType}"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <tags:sectionContainer2 nameKey="startPolicySection">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".type">
                            <tags:selectWithItems path="startPolicy.policy"
                                items="${startPolicyOptions}" />
                        </tags:nameValue2>
<%--                         <tags:nameValue2 nameKey=".startPolicy.startTime">
                            <tags:input path="startPolicy.manualStartTime" />
                        </tags:nameValue2> --%>
                        <tags:nameValue2 nameKey=".startPolicy.startDateTime">
                            <dt:dateTime path="startPolicy.startDateTime" />
                            <tags:checkbox path="startPolicy.everyYear" />
                            <i:inline key=".startPolicy.everyYear" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".startPolicy.daysOfWeek">
                            <div class="button-group stacked">
                                <c:forEach var="dayOfWeek" items="${daysOfWeek}">
                                    <tags:check id="${dayOfWeek}_chk"
                                        path="startPolicy.days[${dayOfWeek}]" key="${dayOfWeek}"
                                        classes="M0" />
                                </c:forEach>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".startPolicy.dayOfMonth">
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
                            <tags:selectWithItems path="stopPolicy.policy"
                                items="${stopPolicyOptions}" />
                        </tags:nameValue2>
<%--                         <tags:nameValue2 nameKey=".stopPolicy.stopTime">
                            <tags:input path="stopPolicy.manualStopTime" />
                        </tags:nameValue2> --%>
                        <tags:nameValue2 nameKey=".stopPolicy.duration">
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
                <cti:url var="editUrl" value="/macsscheduler/schedules/${id}/edit" />
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}" />
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action" busy="true" />
                
                <cti:displayForPageEditModes modes="EDIT">
                    <cti:url var="deleteUrl" value="/macsscheduler/schedules/${id}/delete" />
                    <cti:button id="deleteSchedule" classes="delete" nameKey="delete" href="${deleteUrl}"/>
                    <d:confirm on="#deleteSchedule" nameKey="confirmDelete" argument="${schedule.scheduleName}"/>
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
</cti:standardPage>




