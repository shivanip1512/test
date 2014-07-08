<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="movedCapBanks">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    <script type="text/javascript">
    $(function() {
        $('#movedCBTable').on('click', 'a.moveLink', function (event) {
            var a = event.currentTarget;
            yukon.da.getMovedBankMenu(a.id, event);
        });
    });
    </script>

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="" />
    
    <tags:sectionContainer2 nameKey="movedContainer">
        <c:choose>
            <c:when test="${searchResult.hitCount == 0}">
                <span class="empty-list"><i:inline key=".noRecentMoves"/></span>
            </c:when>
            <c:otherwise>
                <div data-url="<cti:url value="/capcontrol/move/movedCapBanks"/>" data-static>
                    <table id="movedCBTable" class="compact-results-table">
                        <tr>
                            <th><i:inline key=".recentFeeder"/></th>
                            <th><i:inline key=".originalFeeder"/></th>
                            <th><i:inline key=".capBank"/></th>
                        </tr>
                        <c:forEach var="movedCapbank" items="${movedCaps}">
                            <tr id="tr_cap_${movedCapbank.capbank.ccId}">
                                <td id="${fn:escapeXml(movedCapbank.capbank.ccName)}">
                                    <a href="javascript:void(0);" class="moveLink" id="${movedCapbank.capbank.ccId}">${fn:escapeXml(movedCapbank.currentFeederName)}</a>
                                </td>
                                <td>${fn:escapeXml(movedCapbank.originalFeederName)}</td>
                                <td>${fn:escapeXml(movedCapbank.capbank.ccName)}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                <tags:pagingResultsControls result="${searchResult}"/>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer2>
</cti:standardPage>