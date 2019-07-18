<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg2 var="namePlaceholder" key="yukon.common.name"/>
<cti:msg2 var="filterLbl" key="yukon.common.filter"/>

<c:if test="${filterByType == 'LOAD_GROUP'}">
    <cti:url var="filterLoadGroupsUrl" value="/dr/setup/filterLoadGroups"/>
    <form:form action="${filterLoadGroupsUrl}" method="get" modelAttribute="loadGroupFilter" cssClass="js-filter-load-groups-form">
        <tags:input path="name" placeholder="${namePlaceholder}" inputClass="vat"/>
        <cti:msg2 var="selectSwitchTypesLbl" key="yukon.web.modules.dr.setup.loadGroup.filter.selectSwitchTypes"/>
        <tags:selectWithItems items="${switchTypes}" path="switchTypes" dataPlaceholder="${selectSwitchTypesLbl}" id="js-switch-types" />
        <cti:button label="${filterLbl}" classes="fr primary action vam ML15" type="submit"/>
    </form:form>
</c:if>
<c:if test="${filterByType == 'LOAD_PROGRAM'}">
    <cti:url var="filterProgramsUrl" value="/dr/setup/filterLoadPrograms"/>
    <form:form action="${filterProgramsUrl}" method="get" modelAttribute="loadProgram">
        <tags:input path="name" placeholder="${namePlaceholder}"/>
        <cti:button label="${filterLbl}" classes="primary action fr" type="submit"/>
    </form:form>
</c:if>
