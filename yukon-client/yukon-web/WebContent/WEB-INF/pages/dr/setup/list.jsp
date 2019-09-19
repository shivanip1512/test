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
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="CREATE">
                <cm:dropdownOption icon="icon-plus-green" key=".button.create.label" id="js-create-option" data-popup="#js-create-dr-objects-popup"/>
            </cti:checkRolesAndProperties>
        </div>
        
        <div id="js-create-dr-objects-popup" class="dn" data-title="<i:inline key=".createObject.title"/>" data-width="400">
            <table style="width:100%">
                <tr>
                    <td>
                        <cti:url var="createControlAreaUrl" value="/dr/setup/controlArea/create"/>
                        <a href="${createControlAreaUrl}"><i:inline key=".controlArea.title"/></a>
                    </td>
                    <td>
                        <cti:url var="createControlScenario" value="/dr/setup/controlScenario/create"/>
                        <a href="${createControlScenario}"><i:inline key=".controlScenario.title"/></a>
                    </td>
                </tr>
                <tr>
                    <td>
                        <cti:url var="createLoadProgramUrl" value="/dr/setup/loadProgram/create"/>
                        <a href="${createLoadProgramUrl}"><i:inline key=".loadProgram.title"/></a>
                    </td>
                    <td>
                        <cti:url var="createLoadGroupUrl" value="/dr/setup/loadGroup/create"/>
                        <a href="${createLoadGroupUrl}"><i:inline key=".loadGroup.title"/></a>
                    </td>

                </tr>
                <tr>
                    <td>
                        <cti:url var="createMacroLoadGroupUrl" value="/dr/setup/macroLoadGroup/create"/>
                        <a href="${createMacroLoadGroupUrl}"><i:inline key=".macroLoadGroup.title"/></a>
                    </td>
                    <td>
                        <cti:url var="createProgramConstraint" value="/dr/setup/constraint/create"/>
                        <a href="${createProgramConstraint}"><i:inline key=".constraint.title"/></a>
                    </td>

                </tr>
            </table>
        </div>
    </cti:msgScope>
    
    <!-- Filter Inputs -->
    <hr>
        <div class="filter-section">
            <span class="vat"><i:inline key="yukon.common.filterBy"/>&nbsp;</span>
            <div class="dib">
                <cti:url value="/dr/setup/filter" var="filterUrl"/>
                <form:form id="setupFilter" method="get" modelAttribute="lmSetupFilter" action="${filterUrl}">
                    <cti:msg2 var="namePlaceholder" key="yukon.common.name"/>
                    <cti:msg2 var="filterLbl" key="yukon.common.filter"/>
                    <cti:msg2 var="selectLbl" key="yukon.web.modules.dr.setup.loadGroup.filter.types"/>
                    <tags:selectWithItems items="${filterByTypes}" path="filterByType" id="js-filter-by-type" inputClass="vat"/>
                    <tags:input path="name" placeholder="${namePlaceholder}" inputClass="vat" id="js-name"/>
                    <div class="js-load-group-types-container dib dn">
                        <tags:selectWithItems items="${loadGroupTypes}" path="types" dataPlaceholder="${selectLbl}" 
                                              id="js-load-group-types"/>
                    </div>
                    <div class="js-load-program-types-container dib dn">
                        <tags:selectWithItems items="${loadProgramTypes}" path="types" dataPlaceholder="${selectLbl}"
                                              id="js-load-program-types"/>
                    </div>
                    <cti:button label="${filterLbl}" classes="primary action fr" type="submit"/>
                </form:form>
            </div>
        </div>
    <hr>
    
    <!-- Filtered Results -->
    <c:choose>
        <c:when test="${filteredResults.hitCount == 0}">
            <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:when>
        <c:otherwise>
            <%@ include file="filteredResults.jsp" %>
        </c:otherwise>
    </c:choose>
    
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.list.js" />
</cti:standardPage>