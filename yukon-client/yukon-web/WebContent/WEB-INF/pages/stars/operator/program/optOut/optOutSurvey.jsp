<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="optOut.confirm">

<form:form id="form" modelAttribute="optOutBackingBean" method="POST"
    action="/stars/operator/program/optOut/optOutQuestions">
    <cti:csrfToken/>
    <tags:sectionContainer2 nameKey="optOuts">
        <input type="hidden" name="accountId" value="${accountId}"/>

        <%@ include file="../../../consumer/optout/survey.jspf" %>

        <div class="page-action-area">
            <cti:button nameKey="save" type="submit" classes="js-blocker"/>
            <cti:button nameKey="cancel" type="submit"/>
        </div>
    </tags:sectionContainer2>
</form:form>

</cti:standardPage>
