<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dr" page="recentEventParticipation.details">
    <script>
    $(function () {
        $('#download-btn').click(function (ev) {
        	document.auditReport.action = yukon.url("/dr/recenteventparticipation/details/export");
             document.auditReport.submit();
        });
    });
    </script>
    <form id="auditReport" name="auditReport" action="<cti:url value="/dr/recenteventparticipation/details"/>">
        <div class="clearfix column-16-8 stacked">
            <div class="column one">
                <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                    <tags:nameValue2 nameKey=".dateRange">
                        <dt:dateRange startValue="${from}" endValue="${to}" startName="from" endName="to" wrapperClasses="dib fl">
                            <div class="dib fl MR5">
                                <i:inline key="yukon.common.to" />
                            </div>
                        </dt:dateRange>
                        <cti:button nameKey="update" type="submit" busy="true" classes="action primary" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <div class="action-area">
                    <c:if test="${totalEvents > 0}">
                        <c:choose>
                            <c:when test="${maxCsvRows > searchResult.hitCount}">
                                <cti:button nameKey="download" href="${csvLink}" icon="icon-page-excel" />
                            </c:when>
                            <c:otherwise>
                                <cti:button nameKey="download" id="download-btn" icon="icon-page-excel" />
                                <d:confirm on="#download-btn" nameKey="confirmExport" />
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </div>
            </div>
        </div>
        <%-- Recent Events Participation Table --%>
        <cti:url var="dataUrl" value="/dr/recenteventparticipation/recentEventsTable">
            <cti:param name="from" value="${from}" />
            <cti:param name="to" value="${to}" />
        </cti:url>
        <div data-url="${dataUrl}">
            <%@ include file="recentEventsTable.jsp"%>
        </div>
    </form>

</cti:standardPage>