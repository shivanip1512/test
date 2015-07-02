<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.programCreate">
<cti:includeScript link="/resources/js/pages/yukon.dr.curtailment.js"/>

<div class="column-24">
    <div class="column one">
    <table class="name-value-table natural-width">
        <tr>
            <td class="name"><i:inline key=".programType"/></td>
            <td class="value">
                <select id="program-type">
                    <c:forEach var="programType" items="${programTypes}">
                        <option value="${programType.id}">${programType.name}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <td class="name"><i:inline key=".programName"/></td>
            <td class="value"><input id="program-name"></td>
        </tr>
    </table>
    </div>
    <cti:url value="/dr/cc/programDetailCreate" var="createUrl"/>
    <div><cti:button nameKey="create" data-url="${createUrl}" id="create-program"/></div>
</div>
</cti:standardPage>