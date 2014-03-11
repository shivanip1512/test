<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage page="meterEventsReport.jobs" module="amr">
    <tags:scheduledFileExportJobs searchResult="${scheduledJobsSearchResult}" jobType="${jobType}" baseUrl="jobs" editUrl="report" deleteUrl="delete" />
</cti:standardPage>