<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:standardPage module="dev" page="rfnTest">

<tags:sectionContainer title="RFN Status Archive Request">
    <form action="sendStatusArchiveRequest" method="post">
        <cti:csrfToken/>
        <tags:nameValueContainer>
            <span class="fr cp"><cti:icon icon="icon-help" data-popup="#results-help"/></span>
            <cti:msg2 var="helpTitle" key=".statusArchiveRequest.helpTitle"/>
            <div id="results-help" class="dn" data-width="600" data-height="360" data-title="${helpTitle}"><cti:msg2 key=".statusArchiveRequest.helpText"/></div><br/>
            <tags:nameValue name="Demand Reset Status Code">
                <select name="statusCode">
                    <c:forEach items="${dRStatusCodes}" var="dRStatusCode">
                        <option value="${dRStatusCode.ordinal()}">
                             ${dRStatusCode.name()}
                        </option>
                    </c:forEach>
                </select>
            </tags:nameValue>
            <tags:nameValue name="Number of Messages to send"><input name="messageCount" type="text" value="10"></tags:nameValue>
        </tags:nameValueContainer>
        <br>
        <div class="page-action-area">
            <cti:button nameKey="send" type="submit" classes="js-blocker"/>
        </div>
    </form>
</tags:sectionContainer>

</cti:standardPage>