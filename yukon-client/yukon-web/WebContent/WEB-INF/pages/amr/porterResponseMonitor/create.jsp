<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">

    <form:form commandName="monitor" id="basicInfoForm" action="/amr/porterResponseMonitor/create" method="post">

        <input type="hidden" name="stateGroup" value="${monitor.stateGroup}">

        <tags:sectionContainer2 nameKey="setup">
            <tags:nameValueContainer2>
                <tags:inputNameValue nameKey=".name" path="name" size="50" maxlength="50" />
            </tags:nameValueContainer2>
        </tags:sectionContainer2>

        <div class="page-action-area">
            <cti:button nameKey="create" icon="icon-plus-green" type="submit" classes="f-blocker"/>
            <cti:button nameKey="cancel" type="submit" name="cancel"/>
        </div>

    </form:form>

</cti:standardPage>