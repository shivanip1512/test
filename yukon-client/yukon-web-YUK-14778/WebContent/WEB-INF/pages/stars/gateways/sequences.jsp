<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways, modules.operator.gateways.detail">
<c:choose>
    <c:when test="${empty sequences}">
        <div class="empty-list"><i:inline key=".sequencing.none"/></div>
    </c:when>
    <c:otherwise>
        <table class="compact-results-table dashed">
            <thead>
                <tr>
                    <th><i:inline key=".dataType"/></th>
                    <th><i:inline key=".sequenceStart"/></th>
                    <th><i:inline key=".sequenceEnd"/></th>
                    <th><i:inline key=".completeness"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="sequence" items="${sequences}">
                    <c:forEach var="block" items="${sequence.blocks}" varStatus="status">
                        <tr>
                            <c:if test="${status.first}">
                                <td class="js-gw-sq-type"><i:inline key=".sequenceType.${sequence.type}"/></td>
                            </c:if>
                            <c:if test="${not status.first}">
                                <td class="js-gw-sq-type"></td>
                            </c:if>
                            <td class="js-gw-sq-start">${block.start}</td>
                            <td class="js-gw-sq-end">${block.end}</td>
                            <c:if test="${status.first}">
                                <td class="js-gw-sq-completeness">
                                    <div class="progress dib vat">
                                        <c:set var="clazz" value="progress-bar-success"/>
                                        <c:if test="${sequence.completionPercentage < 90 and sequence.completionPercentage >= 75}">
                                            <c:set var="clazz" value="progress-bar-warning"/>
                                        </c:if>
                                        <c:if test="${sequence.completionPercentage <= 75}">
                                            <c:set var="clazz" value="progress-bar-danger"/>
                                        </c:if>
                                        <div class="progress-bar ${clazz}" style="width: ${sequence.completionPercentage}%"></div>
                                    </div>&nbsp;
                                    <fmt:formatNumber pattern="###.##%" value="${sequence.completionPercentage / 100}"/>
                                </td>
                            </c:if>
                            <c:if test="${not status.first}">
                                <td class="js-gw-sq-completeness"></td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
</cti:msgScope>