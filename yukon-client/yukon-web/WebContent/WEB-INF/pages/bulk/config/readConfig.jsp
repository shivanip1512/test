<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="tools" page="bulk.readConfig">

    <tags:bulkActionContainer key="yukon.common.device.bulk.readConfig" deviceCollection="${deviceCollection}">
        <cti:url var="readConfigUrl" value="/bulk/config/doReadConfig" />
        <form id="readConfigForm" method="post" action="${readConfigUrl}">
            <cti:csrfToken/>
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}"/>
            <cti:button nameKey="read" type="submit" classes="primary action" busy="true"/>
        </form>
    </tags:bulkActionContainer>
</cti:standardPage>