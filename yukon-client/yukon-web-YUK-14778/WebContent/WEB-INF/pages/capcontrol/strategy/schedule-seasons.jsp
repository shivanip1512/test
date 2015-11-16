<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="modules.capcontrol">

<%-- SEASONS FOR SCHEDULE --%>
<c:forEach var="season" items="${seasons}" varStatus="status">
    <tr>
        <td>${season.seasonName}</td>
        <td>
            <input type="hidden" name="seasonAssignments[${status.index}].seasonName" value="${season.seasonName}">
            <select name="seasonAssignments[${status.index}].strategyId">
                <option value="-1"><cti:msg2 key="yukon.common.none.choice"/></option>
                <c:forEach var="strategy" items="${strategies}">
                    <option value="${strategy.id}">${fn:escapeXml(strategy.name)}</option>
                </c:forEach>
            </select>
        </td>
    </tr>
</c:forEach>

</cti:msgScope>