<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:verifyRolesAndProperties value="APPLICATION_BILLING" />

<cti:msgScope paths="modules.amr.billing.jobs">
    <tags:scheduledFileExportJobs jobs="${jobs}" jobType="${jobType}"/>
</cti:msgScope>