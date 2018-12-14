<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="movedCapBanks">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    <cti:includeScript link="/resources/js/pages/yukon.da.moved.banks.js" />

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="" />

    <c:choose>
        <c:when test="${empty movedBanks}">
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
                    <c:forEach var="movedCapbank" items="${movedBanks}">
                        <tr data-bank-id="${movedCapbank.ccId}">
                            <td>${fn:escapeXml(movedCapbank.ccName)}</td>
                            <td>
                                <cti:url var="originalSubUrl" value="/capcontrol/substations/${movedCapbank.originalSubstationId}" />
                                <a href="${originalSubUrl}">${fn:escapeXml(movedCapbank.originalFeederName)}</a>
                            </td>
                            <td>
                                <cti:url var="currentSubUrl" value="/capcontrol/substations/${movedCapbank.currentSubstationId}" />
                                <a href="${currentSubUrl}">${fn:escapeXml(movedCapbank.currentFeederName)}</a>
                            </td>
                            <td>
                                <cti:checkRolesAndProperties value="CAPBANK_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
                                    NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
                                    <cti:msg2 var="returnText" key=".command.RETURN_CAP_TO_ORIGINAL_FEEDER"/>
                                    <cti:msg2 var="assignText" key=".command.assignBankHere"/>
                                    <div class="button-group fr">
                                        <cti:button classes="js-return" label="${returnText}" icon="icon-bullet-go-left" />
                                        <cti:button classes="js-assign" label="${assignText}" icon="icon-bullet-go-down" />
                                    </div>
                                </cti:checkRolesAndProperties>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</cti:standardPage>