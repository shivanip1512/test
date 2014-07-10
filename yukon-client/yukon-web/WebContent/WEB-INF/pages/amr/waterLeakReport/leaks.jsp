<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.waterLeakReport.report">
<table id="leaks-table" class="compact-results-table has-actions">
    <thead>
        <tr>
            <c:if test="${leaks.hitCount > 0}">
                <th><input id="check-all-meters" type="checkbox"></th>
            </c:if>
            <tags:sort column="${nameColumn}"/>
            <tags:sort column="${numberColumn}"/>
            <tags:sort column="${typeColumn}"/>
            <tags:sort column="${rateColumn}"/>
            <c:if test="${hasVendorId}"><th><i:inline key=".tableHeader.cisDetails"/></th></c:if>
            <th></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="row" items="${leaks.resultList}">
            <c:set var="paoId" value="${row.meter.paoIdentifier.paoId}"/>
            <tr>
                
                <td>
                    <input type="checkbox" data-pao-id="${paoId}">
                </td>
                <td>
                    <cti:paoDetailUrl yukonPao="${row.meter}">${fn:escapeXml(row.meter.name)}</cti:paoDetailUrl>
                </td>
                <td>${fn:escapeXml(row.meter.meterNumber)}</td>
                <td><tags:paoType yukonPao="${row.meter}"/></td>
                <td><i:inline key=".leakRateLabel" arguments="${row.leakRate}"/></td>
                <c:if test="${hasVendorId}">
                    <td>
                        <cti:url var="accountUrl" value="cisDetails">
                            <cti:param name="paoId" value="${paoId}"/>
                        </cti:url>
                        <cti:msg2 var="accountTitle" key=".accountInfo.title"/> 
                        <a href="javascript:void(0);" data-popup="#account-${paoId}"><i:inline key=".viewCISDetails"/></a>
                        <div class="dn" id="account-${paoId}" data-url="${accountUrl}" data-title="${accountTitle}"></div>
                    </td>
                </c:if>
                <td>
                    <cm:singleDeviceMenu deviceId="${paoId}" triggerClasses="fr"/>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<tags:pagingResultsControls result="${leaks}" adjustPageCount="true"/>
</cti:msgScope>