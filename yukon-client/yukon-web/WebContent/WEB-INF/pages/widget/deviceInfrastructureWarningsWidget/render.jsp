<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="widgets.infrastructureWarnings">
<div class="scroll-md">
<table class="compact-results-table">
    <c:choose>
        <c:when test="${warnings.size() > 0}">
            <c:forEach var="warning" items="${warnings}">
                <tr>
                    <td>
                        <c:set var="warningColor" value="warning"/>
                        <c:if test="${warning.severity == 'HIGH'}">
                            <c:set var="warningColor" value="error"/>
                        </c:if>
                        <span class="${warningColor}"><i:inline key="${warning}"/></span>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="3"><span class="empty-list"><i:inline key=".noRecentWarnings" /></span></td>
            </tr>
        </c:otherwise>
    </c:choose>
</table>
</div>
</cti:msgScope>
