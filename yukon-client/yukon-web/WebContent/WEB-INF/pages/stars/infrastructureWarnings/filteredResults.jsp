<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="widgets.infrastructureWarnings">

    <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
    <span class="badge">${warnings.hitCount}</span>&nbsp;<i:inline key=".warnings"/>
    
    <c:if test="${warnings.hitCount > 0}">
        <span class="js-cog-menu">
            <cm:dropdown icon="icon-cog">
                <cm:dropdownOption key="yukon.common.collectionActions" icon="icon-cog-go" classes="js-collection-action" data-url="/bulk/collectionActions"/> 
                <cm:dropdownOption icon="icon-csv" key="yukon.common.download" classes="js-download-warnings"/>  
                <cm:dropdownOption icon="icon-map-sat" key="yukon.common.mapDevices" classes="js-collection-action" data-url="/tools/map"/>
            </cm:dropdown>
        </span>
    </c:if>

    <c:choose>
        <c:when test="${warnings.hitCount > 0}">
    
            <cti:url var="dataUrl" value="/stars/infrastructureWarnings/filteredResultsTable">
                <c:forEach var="type" items="${selectedTypes}">
                    <cti:param name="types" value="${type}"/>
                </c:forEach>
            </cti:url>
            <div data-url="${dataUrl}">
                <%@ include file="filteredTable.jsp" %>
            </div>
    
        </c:when>
        <c:otherwise>
            <br/><br/><span class="empty-list"><i:inline key=".noWarnings" /></span>
        </c:otherwise>
    </c:choose>

</cti:msgScope>
