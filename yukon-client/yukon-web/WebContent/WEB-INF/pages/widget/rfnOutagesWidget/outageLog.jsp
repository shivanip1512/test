<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.rfnOutagesWidget">

<tags:sectionContainer2 nameKey="outageLog">
    <div class="scroll-sm">
        <c:choose>
            <c:when test="${empty logs}"><i><i:inline key=".noLogs"/></i></c:when>
            <c:otherwise>
                <table class="compact-results-table row-highlighting">
                    <thead>
                        <tr>
                            <th><i:inline key=".start"/></th>
                            <th><i:inline key=".end"/></th>
                            <th><i:inline key=".duration"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach items="${logs}" var="log">
                            <tr>
                                <c:choose>
                                    <c:when test="${!log.invalid}">
                                        <td><cti:formatDate value="${log.start}" type="BOTH"/></td>
                                        <td><cti:formatDate value="${log.end}" type="BOTH"/></td>
                                        <td>
                                            <cti:formatDuration var="duration" type="DHMS_REDUCED" startDate="${log.start}" endDate="${log.end}"/>
                                            <c:choose>
                                                <c:when test="${!empty duration}">
                                                    ${duration}
                                                </c:when>
                                                <c:otherwise>
                                                    <i:inline key=".durationLessThan1Second"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td><i:inline key=".unknown"/></td>
                                        <td><cti:formatDate value="${log.end}" type="BOTH"/></td>
                                        <td><i:inline key=".unknown"/></td>
                                    </c:otherwise>
                                </c:choose>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</tags:sectionContainer2>
</cti:msgScope>