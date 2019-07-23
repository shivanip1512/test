<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="setup">
    
    <cti:msgScope paths="components, modules.dr.setup">
        <!-- Actions dropdown -->
        <div id="page-actions" class="dn">
            <cm:dropdownOption icon="icon-plus-green" key=".button.create.label" id="js-create-option" data-popup="#js-create-dr-objects-popup"/>
        </div>
        
        <div id="js-create-dr-objects-popup" class="dn" data-title="<i:inline key=".createObject.title"/>" data-width="300">
            <div class="column-12-12">
                <div class="column one">
                    <div class="stacked">
                        <cti:url var="createLoadGroupUrl" value="/dr/setup/loadGroup/create"/>
                        <a href="${createLoadGroupUrl}"><i:inline key=".loadGroup.title"/></a>
                    </div>
                </div>
                <div class="column two nogutter">
                    <div class="stacked">
                        <cti:url var="createMacroLoadGroupUrl" value="/dr/setup/macroLoadGroup/create"/>
                        <a href="${createMacroLoadGroupUrl}"><i:inline key=".macroLoadGroup.title"/></a>
                    </div>
                </div>
                <div class="column two nogutter">
                    <div class="stacked">
                        <cti:url var="createProgramConstraint" value="/dr/setup/constraint/create"/>
                        <a href="${createProgramConstraint}"><i:inline key=".constraint.title"/></a>
                    </div>
                </div>
            </div>
        </div>
    </cti:msgScope>
    
    <!-- Filter Inputs -->
    <hr>
        <div class="filter-section">
            <i:inline key="yukon.common.filterBy"/>
            <tags:simpleSelect name="js-filter-by" items="${filterByTypes}" itemLabelKey="formatKey" cssClass="js-filter-by"
                               selectedItem="${filterByType}"/>
            <div class="dib vat js-filter-options">
                <%@ include file="filterForm.jsp" %>
            </div>
        </div>
    <hr>
    
    <!-- Filtered Results -->
    <c:choose>
        <c:when test="${filteredResults.hitCount == 0}">
            <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:when>
        <c:otherwise>
            <c:if test="${filterByType == 'LOAD_GROUP'}">
                <%@ include file="filteredLoadGroups.jsp" %>
            </c:if>
            <c:if test="${filterByType == 'LOAD_PROGRAM'}">
                <%@ include file="filteredLoadPrograms.jsp" %>
            </c:if>
        </c:otherwise>
    </c:choose>
    
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.list.js" />
</cti:standardPage>