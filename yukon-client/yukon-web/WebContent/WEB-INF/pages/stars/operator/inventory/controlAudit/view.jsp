<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="controlAudit">
<cti:includeScript link="HIGH_STOCK"/>
<cti:includeScript link="/resources/js/pages/yukon.assets.control.audit.js"/>

<style>
.badge-controlled { background-color: #093; }
.badge-uncontrolled { background-color: #fb8521; }
.badge-unknown { background-color: #4d90fe; }
.badge-unsupported { background-color: #888; }
</style>

<c:set var="auditId" value="${not empty audit ? audit.taskId : ''}"/>
<div class="stacked-md" data-audit-id="${auditId}">
    <tags:selectedInventory inventoryCollection="${inventoryCollection}"/>
</div>

<div class="column-12-12 clearfix">
    <div class="column one">
        <cti:url var="url" value="/stars/operator/inventory/controlAudit/start"/>
        <form:form modelAttribute="settings" action="${url}">
            <cti:csrfToken/>
            <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
            <dt:dateTime path="from" value="${settings.from}" wrapperClass="fn M0"/>
            <span class="form-control vat"><i:inline key="yukon.common.to"/></span>
            <dt:dateTime path="to" value="${settings.to}" wrapperClass="fn"/>
            <c:set var="clazz" value="${empty auditId ? 'dn' : ''}"/>
            <div class="buffered progress-lg ${clazz}">
                <span class="name"><i:inline key="yukon.common.progress"/>:&nbsp;</span>
                <div class="progress dib vam js-progress">
                    <div class="progress-bar progress-bar-info progress-bar-striped active"
                         role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
                <span class="js-percent-text"></span>
            </div>
            <div class="page-action-area">
                <cti:button type="submit" nameKey="runAudit" classes="primary action" busy="true"/>
            </div>
        </form:form>
    </div>
    <div class="column two nogutter">
        <c:set var="clazz" value="${empty auditId ? 'dn' : ''}"/>
        <div style="max-height: 220px;" class="js-pie-chart js-initialize ${clazz}"></div>
    </div>
</div>

<div id="audit-result-details">
<c:if test="${audit.complete}">
<%@ include file="result.details.jsp" %>
</c:if>
</div>

</cti:standardPage>