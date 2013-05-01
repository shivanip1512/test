<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:verifyRolesAndProperties value="APPLICATION_BILLING" />

<cti:msgScope paths="modules.amr.billing.jobs">

<cti:msg2 key=".pageName" var="pageName" />
<cti:standardPage title="${pageName}" module="amr">
	<cti:standardMenu menuSelection="billing|schedules"/>
	<cti:breadCrumbs>
		<cti:msg2 key="yukon.web.components.button.home.label" var="homeLabel"/>
	    <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
	    <cti:crumbLink><cti:msg2 key=".pageTitle"/></cti:crumbLink>
	</cti:breadCrumbs>
	
	<tags:scheduledFileExportJobs searchResult="${filterResult}" jobType="${jobType}" baseUrl="jobs" editUrl="showForm" deleteUrl="delete" />
	
</cti:standardPage>
</cti:msgScope>