<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterProgramming.summary">

    <div id="page-actions" class="dn">
        <cti:url var="programOthersUrl" value="/collectionActions/home?redirectUrl=/bulk/meterProgramming/inputs"/>
        <cm:dropdownOption key=".programOtherDevices" href="${programOthersUrl}" icon="icon-cog-add"/>
    </div>

    <hr/>
    <div class="filter-section stacked-md">
        <cti:url var="filterUrl" value="/amr/meterProgramming/summaryFilter"/>
        <form:form id="filter-form" action="${filterUrl}" method="get" modelAttribute="filter">
            <span class="fl">
                <span class="vat"><i:inline key="yukon.common.filterBy"/></span>
                
                <cti:msg2 var="allPrograms" key=".allPrograms"/>&nbsp;
                <select id="programSelect" class="js-selected-programs" multiple="multiple" data-placeholder="${allPrograms}">
                    <c:forEach var="program" items="${programList}">
                        <c:set var="selectedText" value=""/>
                        <c:if test="${fn:contains(selectedPrograms, program.name)}">
                            <c:set var="selectedText" value="selected='selected'"/>
                        </c:if>
                        <option value="${program}" data-guid="${program.guid}" data-source="${program.source}" ${selectedText}>${program.name}</option>
                    </c:forEach>
                </select>
                
                <div class="button-group ML15">
                    <c:forEach var="status" items="${statusList}">
                        <tags:check path="statuses" key="${status.formatKey}" classes="M0" value="${status}"></tags:check>
                    </c:forEach>
                </div>
            </span>
            
            <tags:deviceGroupPicker inputName="deviceGroupName" classes="fl ML15"/>
            
            <cti:button nameKey="filter" classes="js-filter-programs action primary fr"/>
        </form:form>
    </div><br/>
    <hr/>
    
        <cti:url var="dataUrl" value="/amr/meterProgramming/summaryFilter">
            <c:forEach var="program" items="${filter.programs}" varStatus="status">
                <cti:param name="programs[${status.index}].name" value="${program.name}"/>
                <cti:param name="programs[${status.index}].guid" value="${program.guid}"/>
                <cti:param name="programs[${status.index}].source" value="${program.source}"/>
            </c:forEach>
            <c:forEach var="status" items="${filter.statuses}">
                <cti:param name="statuses" value="${status}"/>
            </c:forEach>
        </cti:url>
        <div id="results-container" data-url="${dataUrl}">
            <%@ include file="summaryResults.jsp" %>
        </div>

    <cti:includeScript link="/resources/js/pages/yukon.ami.meterProgramming.summary.js" />

</cti:standardPage>