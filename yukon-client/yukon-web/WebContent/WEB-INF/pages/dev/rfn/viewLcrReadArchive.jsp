<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.lcrReadArchiveRequest">

<tags:sectionContainer title="RFN LCR Read Archive Request Test">
    <form action="sendLcrReadArchiveRequest" method="post">
        <cti:csrfToken/>
        <tags:nameValueContainer>
            <tags:nameValue name="Serial Number"><input name="serialFrom" type="text" value="1000"> to <input name="serialTo" type="text" value="1000"></tags:nameValue>
            <tags:nameValueContainer>
                <tags:nameValue name="Schema">
                    <select name="drReport">
                        <c:forEach var="drReport" items="${drReports}">
                            <option value="${drReport}">${drReport.description}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue>
                <tags:nameValue name="Days of Data"><input name="days" type="number" value="1"></tags:nameValue>
            </tags:nameValueContainer>
        </tags:nameValueContainer>
        <div class="page-action-area">
            <cti:button nameKey="broadcast" href="sendPerformanceVerification"/>
            <cti:button nameKey="send" type="submit" classes="js-blocker"/>
        </div>
    </form>
</tags:sectionContainer>

</cti:standardPage>