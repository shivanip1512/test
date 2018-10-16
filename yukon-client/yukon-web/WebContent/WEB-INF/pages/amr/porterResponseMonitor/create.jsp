<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">
<cti:url value="/amr/porterResponseMonitor/create" var="createPorterURL"/>
    <form:form modelAttribute="monitor" id="basicInfoForm" action="${createPorterURL}" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="stateGroup" value="${monitor.stateGroup}">
        <tags:hidden path="groupName"/>
        <tags:hidden path="attribute"/>
        <tags:hidden path="evaluatorStatus"/>

        <tags:sectionContainer2 nameKey="setup">
            <tags:nameValueContainer2>
                <tags:inputNameValue nameKey=".name" path="name" size="60" maxlength="60" />
            </tags:nameValueContainer2>
        </tags:sectionContainer2>

        <div class="page-action-area">
            <cti:button nameKey="create" type="submit" classes="primary action" busy="true"/>
            <cti:url value="/meter/start" var="cancelUrl"/>
            <cti:button nameKey="cancel" href="${cancelUrl}" busy="true" data-disable-group="actionButtons" />
        </div>

    </form:form>

</cti:standardPage>