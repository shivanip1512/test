<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="bulk.verifyConfig">
    <cti:url var="verifyURL" value="/bulk/config/doVerifyConfigs"/>
    <tags:bulkActionContainer key="yukon.common.device.bulk.verifyConfig" deviceCollection="${deviceCollection}">
        <form method="post" action="${verifyURL}">
            <cti:csrfToken/>
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <cti:button nameKey="verify" type="submit" classes="primary action" busy="true"/>
        </form>
    </tags:bulkActionContainer>
</cti:standardPage>