<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="${reportTitle}" module="amr">

    <cti:standardMenu menuSelection="meters" />

    <cti:breadCrumbs>
        <cti:crumbLink url="/dashboard" title="Home" />
        <cti:crumbLink url="/amr/bulkimporter/home" title="Bulk Importer" />
        <cti:crumbLink>${reportTitle}</cti:crumbLink>
    </cti:breadCrumbs>

    <cti:simpleReportUrlFromNameTag var="bodyUrl"
                                    htmlOutput="false"
                                    viewType="extView"
                                    viewJsp="BODY"
                                    definitionName="${definitionName}"
                                    reportType="${reportType}" />
                                    
    <jsp:include page="${bodyUrl}" />

</cti:standardPage>