<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="movedCapBanks">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="" />

    <c:choose>
        <c:when test="${searchResult.hitCount == 0}">
            <span class="empty-list"><i:inline key=".noRecentMoves"/></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th><i:inline key=".capBank"/></th>
                        <th><i:inline key=".originalFeeder"/></th>
                        <th><i:inline key=".recentFeeder"/></th>
                        <th>&nbsp;</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="movedCapbank" items="${searchResult.resultList}">
                        <tr>
                            <td>${fn:escapeXml(movedCapbank.capbank.ccName)}</td>
                            <td>
                                <cti:url var="originalSubUrl" value="/capcontrol/tier/feeders">
                                    <cti:param name="substationId" value="${movedCapbank.originalSubstationId}" />
                                </cti:url>
                                <a href="${originalSubUrl}">${fn:escapeXml(movedCapbank.originalFeederName)}</a>
                            </td>
                            <td>
                                <cti:url var="currentSubUrl" value="/capcontrol/tier/feeders">
                                    <cti:param name="substationId" value="${movedCapbank.currentSubstationId}" />
                                </cti:url>
                                <a href="${currentSubUrl}">${fn:escapeXml(movedCapbank.currentFeederName)}</a>
                            </td>
                            <td>
                                <c:set var="bankId" value="${movedCapbank.capbank.ccId}"/>
                                <cti:url var="assignUrl" value="/capcontrol/command/${bankId}/assign-here" />
                                <cti:url var="returnUrl" value="/capcontrol/command/${bankId}/move-back" />
                                <cti:msg2 var="returnText" key=".command.RETURN_CAP_TO_ORIGINAL_FEEDER"/>
                                <cti:msg2 var="assignText" key=".command.assignBankHere"/>
                                <div class="button-group fr">
                                    <cti:button href="${returnUrl}" label="${returnText}" icon="icon-bullet-go-left" />
                                    <cti:button href="${assignUrl}" label="${assignText}" icon="icon-bullet-go-down" />
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</cti:standardPage>