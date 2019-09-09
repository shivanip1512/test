<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterProgramming.summary">

    <div id="page-actions" class="dn">
        <cti:url var="programOthersUrl" value="/collectionActions/home?redirectUrl=/bulk/meterProgramming/inputs"/>
        <cm:dropdownOption key=".programOtherDevices" href="${programOthersUrl}" icon="icon-cog-add"/>
    </div>

    <hr/>
    <div class="filter-section">
        <cti:url var="filterUrl" value="/amr/meterProgramming/summaryFilter"/>
        <form:form id="filter-form" action="${filterUrl}" method="get" modelAttribute="filter">
            <span class="vat"><i:inline key="yukon.common.filterBy"/></span>
            
            <cti:msg2 var="allPrograms" key=".allPrograms"/>
            <select id="programSelect" class="js-selected-programs" multiple="multiple">
                <c:forEach var="program" items="${programList}">
                    <option value="${program}" data-guid="${program.guid}" data-source="${program.source}">${program.name}</option>
                </c:forEach>
            </select>
            
            <div class="button-group ML15">
                <c:forEach var="status" items="${statusList}">
                    <tags:check path="statuses" key="${status.formatKey}" classes="M0" value="${status}"></tags:check>
                </c:forEach>
            </div>

            <cti:button nameKey="filter" classes="js-filter-programs action primary fn vab"/>
        </form:form>
    </div>
    <hr/>
    
        <cti:url var="dataUrl" value="/amr/meterProgramming/summaryFilter">
        </cti:url>
        <div id="results-container" data-url="${dataUrl}">
            <%@ include file="summaryResults.jsp" %>
        </div>

    <cti:includeScript link="/resources/js/pages/yukon.ami.meterProgramming.summary.js" />

</cti:standardPage>