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

<cti:includeScript link="/JavaScript/yukon.ami.billing.js"/>
<cti:includeScript link="/JavaScript/yukon.ami.billing.dynamic.js"/>

<cti:tabs id="billing_tab_container">

    <cti:msg key="yukon.web.billing.tab.generation.title" var="tabGen" />
    <cti:tab selectorName="${tabGen}" initiallySelected="${showTabGeneration}">
        <div id="billing_generation_settings" class="clearfix stacked">
            <jsp:include page="_settings.jsp"></jsp:include>
        </div>
        <tags:sectionContainer2 nameKey="billing.jobs">
            <div id="billing_schedules_jobs">
                <jsp:include page="../amr/scheduledBilling/_jobs.jsp"></jsp:include>
            </div>
        </tags:sectionContainer2>
    </cti:tab>
    
    <cti:checkRolesAndProperties value="DYNAMIC_BILLING_FILE_SETUP">
        <cti:msg key="yukon.web.billing.tab.setup.title" var="tab_setup" />
        <cti:tab selectorName="${tab_setup}" initiallySelected="${showTabSetup}">
            <div id="billing_setup_overview">
                <jsp:include page="../amr/dynamicBilling/_overview.jsp"></jsp:include>
            </div>
        </cti:tab>
    </cti:checkRolesAndProperties>
</cti:tabs>

</cti:standardPage>