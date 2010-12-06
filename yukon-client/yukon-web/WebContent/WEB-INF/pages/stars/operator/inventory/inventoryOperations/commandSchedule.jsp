<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="commandSchedule.${mode}">
    <tags:setFormEditMode mode="${mode}"/>
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>

    <c:set var="action" value="/spring/stars/operator/inventory/inventoryOperations/updateSchedule"/>
    <cti:msg2 var="confirmDeleteTitle" key=".confirmDeleteTitle"/>
    
    <script>
        function confirmDelete() {
            $('confirmDeletePopup').show();
        }
    </script>
    
    <form:form id="updateForm" commandName="schedule" action="${action}">
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
                <spring:bind path="hours">
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
                        <form:input path="hours" size="3" cssClass="${inputClass}"/>
                    </cti:displayForPageEditModes>
                </spring:bind>
                
                <span><i:inline key=".minutes"/></span>
                <spring:bind path="minutes">
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
                        <form:input path="minutes" size="3" cssClass="${inputClass}"/>
                    </cti:displayForPageEditModes>
                </spring:bind>
                
                
                <div><form:errors path="hours" cssClass="errorMessage"/></div>
                <div><form:errors path="minutes" cssClass="errorMessage"/></div>
                <br>

            </tags:nameValue2>

            <tags:nameValue2 nameKey=".delayPeriod">
                <span><i:inline key=".seconds"/></span><tags:input path="seconds" size="2"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <br>
        
        <div>
            <tags:slowInput2 key="save" formId="updateForm"/>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button key="delete" type="button" onclick="confirmDelete();"/>
            </cti:displayForPageEditModes>
            <cti:button key="cancel" type="submit" name="cancel"/>
        </div>
        
        <tags:simplePopup title="${confirmDeleteTitle}" id="confirmDeletePopup" styleClass="mediumSimplePopup">
            <i:inline key=".confirmDeleteMessage"/>
            <div class="actionArea">
                <cti:button key="ok" type="submit" name="delete"/>
                <cti:button key="cancel" type="button" onclick="javascript:$('confirmDeletePopup').hide();"/>
            </div>
        </tags:simplePopup>
    
    </form:form>
</cti:standardPage>