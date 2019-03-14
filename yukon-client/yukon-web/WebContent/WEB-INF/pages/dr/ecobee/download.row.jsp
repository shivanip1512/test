<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.dr.ecobee.details">

<tr data-key="${key}" <c:if test="${hideRow}">style="opacity: 0;"</c:if>>
    <td class="name"><cti:formatDate type="MONTH_DAY_HM" value="${download.startDate}"/>:</td>
    <td class="value full-width">
        <cti:dataUpdaterCallback function="yukon.dr.ecobee.downloadStatus" initialize="true" value="ECOBEE_READ/${key}/STATUS"/>
        <div class="progress progress-ecobee <c:if test="${not download.complete}">active progress-striped</c:if>">
            <c:choose>
                <c:when test="${not download.complete}">
                    <c:set var="classes" value="progress-bar-info"/>
                </c:when>
                <c:when test="${download.complete and download.successful}">
                    <c:set var="classes" value="progress-bar-success"/>
                </c:when>
                <c:otherwise>
                    <c:set var="classes" value="progress-bar-danger"/>
                </c:otherwise>
            </c:choose>
            <div class="progress-bar ${classes}" 
                role="progressbar" 
                aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" 
                style="width: ${download.percentDone};">
            </div>
        </div>
        
        <span class="js-percent-done">${download.percentDone}</span>&nbsp;
        <c:set var="classes" value="${download.complete and download.successful ? '' : 'dn'}"/>
        <span class="js-download-ready success ${classes}"><i:inline key=".download.ready"/></span>
        <c:set var="classes" value="${download.complete and !download.successful ? '' : 'dn'}"/>
        <span class="js-download-failed error ${classes}"><i:inline key=".download.failed"/></span>
        
        <cti:url var="downloadUrl" value="/dr/ecobee/download">
            <cti:param name="key" value="${key}"/>
        </cti:url>
        <cti:msg2 key=".downloadReport" var="downloadReportToolTip"/>
        <cti:button href="${downloadUrl}" classes="fr js-ec-download" renderMode="buttonImage" icon="icon-page-white-excel" 
                    disabled="${download.complete && download.successful ? 'false' : 'true'}" title="${downloadReportToolTip}"/>
    </td>
</tr>
</cti:msgScope>