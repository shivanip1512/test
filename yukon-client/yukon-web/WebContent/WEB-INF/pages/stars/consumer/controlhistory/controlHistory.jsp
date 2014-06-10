<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="consumer" page="controlhistory">
    <cti:standardMenu />
    <div id="programs">
        <dr:controlHistorySummary displayableProgramList="${displayablePrograms}" 
            completeHistoryUrl="/stars/consumer/controlhistory/completeHistoryView"
            titleKey="" past="false"/>
    </div>
</cti:standardPage>