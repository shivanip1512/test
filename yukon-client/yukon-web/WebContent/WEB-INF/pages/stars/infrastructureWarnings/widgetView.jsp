<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="widgets.infrastructureWarnings">

    <cti:url var="allWarningsUrl" value="/stars/infrastructureWarnings/detail"/>
    <%@ include file="summaryTable.jsp" %>

    <span class="fr"><a href="${allWarningsUrl}" target="_blank"><i:inline key="yukon.common.viewDetails"/></a></span>
    <%@ include file="infrastructureWarningsDetails.jsp" %>

</cti:msgScope>