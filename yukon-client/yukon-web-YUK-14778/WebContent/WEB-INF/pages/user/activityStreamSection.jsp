<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.common.events">
    <div class="scroll-lg" id="divActivityStream">
        <c:if test="${empty userEvents.resultList}">
            <span class="empty-list"><i:inline key=".noResults"/></span>
        </c:if>
        <c:if test="${not empty userEvents.resultList}">
            <table class="compact-results-table row-highlighting">
                <thead>
                    <tr>
                        <th><i:inline key=".columnHeader.dateAndTime"/></th>
                        <th><i:inline key=".columnHeader.event"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach items="${userEvents.resultList}" var="event">
                        <cti:msg2 var="details" key="${event.messageSourceResolvable}" />
                        <tr title="${details}">
                            <td><cti:formatDate type="BOTH" value="${event.dateTime}" /></td>
                            <td><span>${details}</span></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
<%--     <cti:url value="/user/activityStream/${userEvents.endIndex}.html" var="nextUrl" /> --%>
<%--     <cti:url value="/user/activityStream/${userEvents.previousStartIndex}.html" var="prevUrl"/> --%>
<%--     <tags:pagingResultsControls prevUrl="${prevUrl}" nextUrl="${nextUrl}" result="${userEvents}"/> --%>
</cti:msgScope>