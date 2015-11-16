<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.bankMove">

<h3><i:inline key=".selectedFeeder.title" arguments="${feederName}" /></h3>

<div style="max-height: 112px;overflow: auto;overflow-x: hidden;">
    <c:if test="${empty capBankList}">
        <span class="empty-list"><i:inline key=".noBanks"/></span>
    </c:if>
    <c:if test="${not empty capBankList}">
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th><i:inline key=".bankName"/></th>
                    <th><i:inline key=".controlOrder"/></th>
                    <th><i:inline key=".closeOrder"/></th>
                    <th><i:inline key=".tripOrder"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="cap" items="${capBankList}">
                    <tr>
                        <td>${fn:escapeXml(cap.ccName)}</td>
                        <td>${cap.controlOrder}</td>
                        <td>${cap.closeOrder}</td>
                        <td>${cap.tripOrder}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>
</cti:msgScope>