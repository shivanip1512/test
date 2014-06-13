<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="consumer" page="controlhistory">
    <cti:standardMenu />
    <cti:msg2 var="sectionTitle" key=".header"/>
    <tags:sectionContainer title="${sectionTitle}">
        <div id="programs">
            <dr:controlHistorySummary displayableProgramList="${displayablePrograms}" 
                completeHistoryUrl="/stars/consumer/controlhistory/completeHistoryView"
                past="false"/>
        </div>
    </tags:sectionContainer>
</cti:standardPage>