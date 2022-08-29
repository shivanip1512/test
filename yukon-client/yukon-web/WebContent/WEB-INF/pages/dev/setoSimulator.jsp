<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="setoSimulator">
    <form:form action="sendEdgeDrDataNotification" modelAttribute="edgeDrDataNotification" method="POST">
        <cti:csrfToken />

        <div class="user-message warning">
            <br />This is the SETO Simulator page <br />Currently using SETO Web Hook URL: ${url}
        </div>
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="paoId">
                <tags:input path="paoId" />
            </tags:nameValue>
            <tags:nameValue name="payload">
                <tags:input path="payload" />
            </tags:nameValue>
            <tags:nameValue name="e2eId">
                <tags:input path="e2eId" />
            </tags:nameValue>
            <tags:nameValue name="errorCode">
                <input type=text name="error.errorCode" value="${edgeDrDataNotification.error.errorCode}"/>
            </tags:nameValue>
            <tags:nameValue name="errorMessage">
                <input type =text name="error.errorMessage" value="${edgeDrDataNotification.error.errorMessage}"/>
            </tags:nameValue>
        </tags:nameValueContainer>
        <div class="page-action-area">
            <cti:button label="Send" type="submit" />
            <cti:url value="/dev" var="setoBaseURL" />
            <cti:button nameKey="cancel" name="cancel" href="${setoBaseURL}" />
        </div>
    </form:form>
</cti:standardPage>