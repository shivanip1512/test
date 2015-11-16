<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.tools.commander">
<cm:dropdown icon="icon-map-sat" key=".nearby" menuClasses="js-nearby-menu" triggerClasses="vab js-nearby-btn" type="button">
    <c:forEach var="pd" items="${nearby}">
        <cti:paoTypeIcon var="iconName" yukonPao="${pd.pao}"/>
        <c:set var="iconName" value="${empty iconName ? 'icon-blank' : iconName}"/>
        <cm:dropdownOption data-pao-id="${pd.pao.liteID}" icon="${iconName}">
            ${fn:escapeXml(pd.pao.paoName)} - <fmt:formatNumber maxFractionDigits="2" value="${pd.distance}"/> <cti:msg2 key="${pd.unit}"/>
        </cm:dropdownOption>
    </c:forEach>
    <li class="divider"></li>
    <cti:url var="mapUrl" value="/tools/map">
        <cti:mapParam value="${nearbyCollection.collectionParameters}"/>
    </cti:url>
    <cm:dropdownOption icon="icon-map-sat" key="components.button.map.label" href="${mapUrl}" newTab="true"/>
</cm:dropdown>
</cti:msgScope>