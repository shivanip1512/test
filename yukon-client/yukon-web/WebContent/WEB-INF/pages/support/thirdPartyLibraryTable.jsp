<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<table class="compact-results-table row-highlighting">
    <thead>
        <th width="20%"><i:inline key=".library"/></th>
        <th width="40%"><i:inline key=".projectUrl"/></th>            
        <th width="40%"><i:inline key=".license"/></th>
    </thead>
    <tbody>
        <c:forEach var="library" items="${libraryList}">
            <tr>
                <td>${library.project}</td>
                <td class="wbba">
                    <c:if test="${fn:startsWith(library.projectUrl, 'http')}">
                        <a href="${library.projectUrl}">${library.projectUrl}</a>
                    </c:if>
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