<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

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
            <div class="vat"><i:inline key="yukon.common.filterBy"/></div>
            <div class="dib">
                <cti:url value="/dr/setup/filter" var="filterUrl"/>
                <form:form id="setupFilter" method="get" modelAttribute="lmSetupFilter" action="${filterUrl}">
                    <cti:msg2 var="namePlaceholder" key="yukon.common.name"/>
                    <cti:msg2 var="filterLbl" key="yukon.common.filter"/>
                    <cti:msg2 var="selectLbl" key="yukon.web.modules.dr.setup.loadGroup.filter.types"/>
                    
                    <c:set var="displayGearFilters" value="${isFilterByGearSelected ? '' : 'dn'}"/>
                    <c:set var="displayLoadGroupFilters" value="${isFilterByLoadGroupSelected ? '' : 'dn'}"/>
                    <c:set var="displayLoadProgramFilters" value="${isFilterByLoadProgramSelected ? '' : 'dn'}"/>
                    
                    <tags:selectWithItems items="${filterByTypes}" path="filterByType" id="js-filter-by-type" inputClass="vat MR5"/>
                    <tags:input path="name" placeholder="${namePlaceholder}" inputClass="vat MR5" id="js-name" autocomplete="nofill"/>
                    <div class="js-load-group-types-container dib ${displayLoadGroupFilters} MR10">
                        <tags:selectWithItems items="${loadGroupTypes}" path="types" dataPlaceholder="${selectLbl}" 
                                              id="js-load-group-types"/>
                    </div>
                    <div class="js-load-program-types-container vat dib ${displayLoadProgramFilters} MR5 MB5">
                        <tags:selectWithItems items="${loadProgramTypes}" path="types" dataPlaceholder="${selectLbl}"
                                              id="js-load-program-types"/>
                    </div>
                    <div class="js-operational-states-container dib vat ${displayLoadProgramFilters} MR5">
                        <cti:msg2 var="selectOperationalStatesLbl" key="yukon.web.modules.dr.setup.loadGroup.operationalStates.selectLbl"/>
                        <tags:selectWithItems items="${operationalStates}" path="operationalStates" dataPlaceholder="${selectOperationalStatesLbl}"
                                                               id="js-operational-states"/>
                    </div>
                    <div class="js-gear-types-container dib ${displayGearFilters} MR5">
                        <tags:selectWithItems items="${gearTypes}" path="gearTypes" dataPlaceholder="${selectLbl}"
                                                               id="js-gear-types"/>
                    </div>
                    <div class="js-program-container dib vat ${displayGearFilters} MR5">
                            <form:hidden path="programIds" id="js-programIds"/>
                           <tags:pickerDialog id="programPicker"
                                                            type="programPicker"
                                                            destinationFieldId="js-programIds"
                                                            linkType="selection"
                                                            nameKey="selectPrograms"
                                                            selectionProperty="paoName"
                                                            multiSelectMode="true"
                                                            initialIds="${lmSetupFilter.programIds}"
                                                            endEvent="yukon:gear:filter:programSelected"
                                                            allowEmptySelection="true"/>
                    </div>
                    <cti:button label="${filterLbl}" classes="primary action fr MR0 ML5" type="submit"/>
                </form:form>
            </div>
        </div>
    <hr>
    
    <!-- Filtered Results -->
    <c:choose>
        <c:when test="${filteredResults.hitCount == 0}">
            <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:when>
        <c:when test="${isFilterByGearSelected}">
            <%@ include file="gearFilteredResults.jsp" %>
        </c:when>
        <c:when test="${isFilterByLoadProgramSelected}">
            <%@ include file="loadProgramFilteredResults.jsp" %>
        </c:when>
        <c:when test="${isFilterByControlAreaSelected}">
            <%@ include file="controlAreaFilteredResults.jsp" %>
        </c:when>
        <c:when test="${isFilterByControlScenarioSelected}">
            <%@ include file="controlScenarioFilteredResults.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="filteredResults.jsp" %>
        </c:otherwise>
    </c:choose>

    <dt:pickerIncludes />
    <cti:includeScript link="YUKON_TIME_FORMATTER" />
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.programGear.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.list.js" />
</cti:standardPage>