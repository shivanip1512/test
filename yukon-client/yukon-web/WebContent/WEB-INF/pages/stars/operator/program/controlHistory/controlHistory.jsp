<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="controlHistory">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
    <cti:includeScript link="/resources/js/pages/yukon.assets.controlhistory.js"/>
    <div data-account-id="${accountId}">
        <tags:sectionContainer2 nameKey="currentEnrollmentControlHistory">
            <div class="clearfix stacked js-block-this js-current-enrollment-history" style="min-height: 50px">
                <i:inline key="yukon.common.loading"/>
            </div>
        </tags:sectionContainer2>
        <tags:sectionContainer2 nameKey="previousEnrollmentControlHistory">
            <div class="clearfix stacked js-block-this js-past-enrollment-history" style="min-height: 50px">
                <i:inline key="yukon.common.loading"/>
            </div>
        </tags:sectionContainer2>
    </div>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>