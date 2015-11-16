<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="components.dialog.devicePoints">
    <input type="hidden" class="js-popup-title" value="${fn:escapeXml(pao.paoName)}">
    <table class="compact-results-table row-highlighting">
        <thead>
            <tr>
                <th><i:inline key=".pointName"/></th>
                <th></th>
                <th><i:inline key=".value"/></th>
                <th><i:inline key=".timestamp"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="point" items="${points}">
             <tr>
                 <td>${fn:escapeXml(point.pointName)}</td>
                 <td class="state-indicator">
                    <c:if test="${point.paoPointIdentifier.pointIdentifier.pointType.status}">
                        <cti:pointStatus pointId="${point.pointId}"/>
                    </c:if>
                 </td>
                 <td class="wsnw"><cti:pointValue pointId="${point.pointId}" format="SHORT"/></td>
                 <td class="wsnw"><tags:historicalValue pao="${device}" pointId="${point.pointId}"/></td>
             </tr>
         </c:forEach>
        </tbody>
    </table>
</cti:msgScope>