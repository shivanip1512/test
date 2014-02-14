<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="rf.details">
    
    <cti:includeScript link="/JavaScript/yukon/yukon.dr.rf.performance.js"/>
    
    <div class="clearfix column-16-8 stacked">
        <div class="column one">
            <form action="/dr/rf/details">
                <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                    <tags:nameValue2 nameKey=".dateRange">
                            <dt:dateRange startValue="${from}" endValue="${to}" startName="from" endName="to" wrapperClasses="dib fl">
                                <div class="dib fl" style="margin-right: 5px;"><i:inline key="yukon.common.to"/></div>
                            </dt:dateRange>
                            <cti:button nameKey="update" type="submit" busy="true" classes="action primary"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".numTests">${fn:length(tests)}</tags:nameValue2>
                </tags:nameValueContainer2>
            </form>
        </div>
        <div class="column two nogutter">
        </div>
    </div>
    
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key=".eventTime"/></th>
                <th><i:inline key=".results"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${tests}" var="test">
                <tr>
                    <td><cti:formatDate type="FULL" value="${test.timeMessageSent}"/></td>
                    <td>
                        <dr:rfPerformanceStats test="${test.eventStats}"/>
                        <c:if test="${test.numUnknowns > 0}"><a href="javascript:void(0);" class="fr f-unknown" data-test="${test.messageId}"><i:inline key=".showUnknown"/></a></c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <div id="unknown-popup" title="<cti:msg2 key=".unknownBreakdown"/>" data-reloadable></div>
</cti:standardPage>