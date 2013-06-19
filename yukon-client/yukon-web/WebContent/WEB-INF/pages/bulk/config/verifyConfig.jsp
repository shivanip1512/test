<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="tools" page="bulk.verifyConfig">

    <tags:bulkActionContainer key="yukon.common.device.bulk.verifyConfig" deviceCollection="${deviceCollection}">
        <form id="verifyConfigForm" method="get" action="/bulk/config/doVerifyConfigs">
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <cti:button nameKey="verify" type="submit" classes="f_disableAfterClick primary action" busy="true"/>
        </form>
    </tags:bulkActionContainer>
</cti:standardPage>