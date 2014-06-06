<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="value" required="true" type="com.cannontech.web.dr.model.EcobeeQueryStats" %>

<cti:msgScope paths="yukon.web.modules.dr.ecobee.details">

<div class="progress query-statistics" style="width: 80px;float:left;">
    <div class="progress-bar progress-bar-info" 
        role="progressbar" 
        aria-valuenow="0.0%" 
        aria-valuemin="0" 
        aria-valuemax="100" 
        style="width: ${value.limitPercent};"></div>
</div>
<span class="fl query-counts" 
    style="margin-left: 10px;" 
    title="<cti:msg2 key=".stats.tooltip"/>">
    <span class="query-total dib">
        <span>${value.countsTotal}</span>
        <span>&nbsp;/&nbsp;</span>
        <span>${value.limit}</span>
    </span>
    <c:set var="classes" value="label-success"/>
    <c:if test="${value.countsTotal / value.limit > 0.8}">
        <c:set var="classes" value="label-warning"/>
    </c:if>
    <c:if test="${value.countsTotal / value.limit > 0.95}">
        <c:set var="classes" value="label-danger"/>
    </c:if>
</span>
<span class="label ${classes} fr lhn">${value.limitPercent}</span>

</cti:msgScope>