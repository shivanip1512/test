<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg2 var="namePlaceholder" key="yukon.common.name"/>
<cti:msg2 var="filterLbl" key="yukon.common.filter"/>

<c:if test="${filterByType == 'LOAD_GROUP'}">
    <cti:url var="filterLoadGroupsUrl" value="/dr/setup/filterLoadGroups"/>
    <form:form action="${filterLoadGroupsUrl}" method="get" modelAttribute="loadGroupBase" cssClass="js-filter-load-groups-form">
        <div class="dib">
        <tags:input path="name" placeholder="${namePlaceholder}"/>
        <cti:msg2 var="allSwitchTypesLbl" key="yukon.web.modules.dr.setup.loadGroup.allSwitchTypes"/>
        <tags:selectWithItems defaultItemLabel="${allSwitchTypesLbl}" id="js-switch-types" items="${switchTypes}" path="type"
                              inputClass="js-init-chosen" />
        </div>
        <cti:button label="${filterLbl}" classes="fr primary action" type="submit"/>
    </form:form>
</c:if>
<c:if test="${filterByType == 'LOAD_PROGRAM'}">
    <cti:url var="filterProgramsUrl" value="/dr/setup/filterLoadPrograms"/>
    <form:form action="${filterProgramsUrl}" method="get" modelAttribute="loadProgram">
        <tags:input path="name" placeholder="${namePlaceholder}"/>
        <cti:button label="${filterLbl}" classes="primary action fr" type="submit"/>
    </form:form>
</c:if>


<script>
    if ($("#js-switch-types").exists()) {
        $("#js-switch-types").chosen({width: "250px"});
    }
</script>
