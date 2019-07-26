<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="support" page="thirdPartyLibraries">

    <tags:boxContainer2 nameKey="sourceCredits">
        <table class="compact-results-table row-highlighting">
            <thead>
                <th width="40%"><i:inline key=".library"/></th>
                <th width="60%"><i:inline key=".license"/></th>
            </thead>
            <tbody>
                <c:forEach var="library" items="${libraries}">
                    <tr>
                        <td class="wbba">
                            <c:choose>
                                <c:when test="${fn:startsWith(library.projectUrl, 'http')}">
                                    <a href="${library.projectUrl}">${library.project}</a>
                                </c:when>
                                <c:otherwise>
                                    ${library.project}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="wbba">
                            <c:forEach var="license" items="${library.licenseUrls}">
                                <c:if test="${fn:startsWith(license, 'http')}">
                                    <a href="${license}">${license}</a>
                                </c:if>
                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
           </tbody>
        </table>
    </tags:boxContainer2>

</cti:standardPage>