<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.programCreate">
<cti:msgScope paths="yukon.web.modules.commercialcurtailment.ccurtSetup">
<cti:includeScript link="/JavaScript/yukon.curtailment.js"/>

<div class="column-24">
    <h3><i:inline key=".programCreate"/></h3>
    <div class="column one">
    <table class="name-value-table natural-width">
        <tr>
            <td class="name">Program Type</td>
            <td class="value">
                <select id="program-type">
                    <c:forEach var="programType" items="${programTypes}">
                        <option value="${programType.id}">${programType.name}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <td class="name">Program Name</td>
            <td class="value"><input id="program-name" value=""></td>
        </tr>
    </table>
    </div>
    <cti:url value="/dr/cc/programDetailCreate" var="createUrl"/>
    <div><cti:button nameKey="create" data-url="${createUrl}" id="create-program"/></div>
</div>
</cti:msgScope>
</cti:standardPage>