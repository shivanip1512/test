<%--
    Allows T/F parameters: showTabGeneration[default], showTabSetup, showTabSchedule
--%>
<%@ include file="../../../operator/Metering/include/billing_header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:verifyRolesAndProperties value="APPLICATION_BILLING"/>

<cti:standardPage module="amr" page="billing">

<cti:includeScript link="/JavaScript/yukon.cron.js" />
<cti:includeScript link="/JavaScript/yukon.ami.billing.dynamic.js"/>
<cti:includeScript link="/JavaScript/yukon.ami.billing.js"/>
<cti:includeScript link="/JavaScript/yukon.tag.scheduled_file_export_inputs.js"/>

<cti:tabbedContentSelector id="billing_tab_container" mode="section">

    <cti:msg key="yukon.web.billing.tab.generation.title" var="tabGen" />
    <cti:tabbedContentSelectorContent selectorName="${tabGen}" initiallySelected="${showTabGeneration}">
        <div id="billing_generation_settings" class="clearfix stacked">
            <jsp:include page="_settings.jsp"></jsp:include>
        </div>
        <tags:sectionContainer2 nameKey="billing.jobs">
            <div id="billing_schedules_jobs" data-reloadable>
                    <jsp:include page="../amr/scheduledBilling/_jobs.jsp"></jsp:include>
            </div>
        </tags:sectionContainer2>
    </cti:tabbedContentSelectorContent>
    
    <cti:checkRolesAndProperties value="DYNAMIC_BILLING_FILE_SETUP">
        <cti:msg key="yukon.web.billing.tab.setup.title" var="tab_setup" />
        <cti:tabbedContentSelectorContent selectorName="${tab_setup}" initiallySelected="${showTabSetup}">
            <div id="billing_setup_overview">
                <jsp:include page="../amr/dynamicBilling/_overview.jsp"></jsp:include>
            </div>
        </cti:tabbedContentSelectorContent>
    </cti:checkRolesAndProperties>
</cti:tabbedContentSelector>

</cti:standardPage>