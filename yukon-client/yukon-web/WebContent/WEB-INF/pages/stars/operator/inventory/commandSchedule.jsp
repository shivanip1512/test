<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="commandSchedule.${mode}">
    <tags:setFormEditMode mode="${mode}"/>
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    
    <form:form id="updateForm" commandName="schedule" action="/stars/operator/inventory/updateSchedule">
        <form:hidden path="commandSchedule.commandScheduleId"/>
        <form:hidden path="commandSchedule.startTimeCronString"/>
        <cti:uniqueIdentifier var="formUniqueId" prefix="attrFormUniqueId_" />
        <input type="hidden" name="formUniqueId" value="${formUniqueId}">
        
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".runSchedule">
                <tags:cronExpressionData id="${formUniqueId}" state="${cronExpressionTagState}"/>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".duration">
                <span><i:inline key=".hours"/></span>
                <spring:bind path="runPeriodHours">
                    <%-- VIEW MODE --%>
                    <cti:displayForPageEditModes modes="VIEW">
                    ${status.value}
                    </cti:displayForPageEditModes>
                    <%-- EDIT/CREATE MODE --%>
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <c:set var="inputClass" value=""/>
                        <c:if test="${status.error}">
                            <c:set var="inputClass" value="error"/>
                        </c:if>
                        <form:input path="runPeriodHours" size="3" cssClass="${inputClass}"/>
                    </cti:displayForPageEditModes>
                </spring:bind>
                
                <span><i:inline key=".minutes"/></span>
                <spring:bind path="runPeriodMinutes">
                    <%-- VIEW MODE --%>
                    <cti:displayForPageEditModes modes="VIEW">
                    ${status.value}
                    </cti:displayForPageEditModes>
                    <%-- EDIT/CREATE MODE --%>
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <c:set var="inputClass" value=""/>
                        <c:if test="${status.error}">
                            <c:set var="inputClass" value="error"/>
                        </c:if>
                        <form:input path="runPeriodMinutes" size="3" cssClass="${inputClass}"/>
                    </cti:displayForPageEditModes>
                </spring:bind>
                
                
                <div><form:errors path="runPeriodHours" cssClass="error"/></div>
                <div><form:errors path="runPeriodMinutes" cssClass="error"/></div>
                <br>

            </tags:nameValue2>

            <tags:nameValue2 nameKey=".delayPeriod">
                <span><i:inline key=".seconds"/></span><tags:input path="delayPeriodSeconds" size="2"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <br>
        
        <div>
            <cti:button nameKey="save" type="submit" classes="f-blocker"/>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="delete" name="delete" type="submit" id="delete_confirm"/>
                <d:confirm on="#delete_confirm" nameKey="confirmDelete"/>
            </cti:displayForPageEditModes>
            <cti:button nameKey="cancel" href="home" />
        </div>
        
    </form:form>
</cti:standardPage>