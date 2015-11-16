<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.dr.estimatedLoad">
    <c:choose>
        <c:when test="${fn:length(allFormulas) == 0}">
            <span class="empty-list"><i:inline key=".noFormulas"/></span>
        </c:when>
        <c:otherwise>
            <div class="scroll-lg">
                <table class="compact-results-table">
                    <thead>
                        <th><i:inline key=".name"/></th>
                        <th><i:inline key=".calculationType"/></th>
                        <th><i:inline key=".formulaType"/></th>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="formula" items="${allFormulas}">
                            <tr>
                                <td><a href="formula/view?formulaId=${formula.formulaId}">${fn:escapeXml(formula.name)}</a></td>
                                <td><i:inline key="${formula.calculationType}"/></td>
                                <td><i:inline key="${formula.formulaType}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</cti:msgScope>