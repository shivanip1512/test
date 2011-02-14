<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="adminSetup" page="applianceCategory.PROGRAMS">

<cti:includeScript link="/JavaScript/iconChooser.js"/>
<tags:setFormEditMode mode="${mode}"/>

<tags:simpleDialog id="acDialog"/>

<cti:url var="clearFilterUrl" value="programs">
    <cti:param name="ecId" value="${param.ecId}"/>
    <c:if test="${!empty param.itemsPerPage}">
        <cti:param name="itemsPerPage" value="${param.itemsPerPage}"/>
    </c:if>
    <c:if test="${!empty param.sort}">
        <cti:param name="sort" value="${param.sort}"/>
    </c:if>
    <c:if test="${!empty param.descending}">
        <cti:param name="descending" value="${param.descending}"/>
    </c:if>
</cti:url>

<script type="text/javascript">
function clearFilter() {
    window.location = '${clearFilterUrl}';
}
</script>

<i:simplePopup id="filterDialog" titleKey=".filters">
    <form:form action="programs" commandName="backingBean" method="get">
        <tags:sortFields backingBean="${backingBean}"/>
        <input type="hidden" name="ecId" value="${param.ecId}">

        <table cellspacing="10">
            <tr>
                <td><i:inline key=".programName"/></td>
                <td><form:input id="programNameInput" path="name" size="40"/></td>
            </tr>
        </table>

        <br>
        <div class="actionArea">
            <input type="submit" value="<cti:msg2 key=".filterButton"/>"/>
            <input type="button" value="<cti:msg2 key=".showAllButton"/>"
                onclick="javascript:clearFilter()"/>
        </div>
    </form:form>
</i:simplePopup>

<table class="widgetColumns">
    <tr>
        <td class="widgetColumnCell" valign="top" colspan="2">
            <div class="widgetContainer">
                <cti:msg2 var="boxTitle" key=".assignedPrograms"/>
                <tags:pagedBox title="${boxTitle}" searchResult="${assignedPrograms}"
                    baseUrl="programs" filterDialog="filterDialog" defaultFilterInput="programNameInput"
                    isFiltered="${isFiltered}" showAllUrl="${clearFilterUrl}">
                    <c:if test="${empty assignedPrograms.resultList}">
                        <i:inline key=".noAssignedPrograms"/>
                    </c:if>
                    <c:if test="${!empty assignedPrograms.resultList}">
                        <table id="programList" class="compactResultsTable rowHighlighting">
                            <tr>
                                <th>
                                    <tags:sortLink key=".programName" baseUrl="programs"
                                        fieldName="PROGRAM_NAME" isDefault="true"/>
                                </th>
                                <th>
                                    <tags:sortLink key=".applianceCategoryName" baseUrl="programs"
                                        fieldName="APPLIANCE_CATEGORY_NAME"/>
                                </th>
                            </tr>
                            <c:forEach var="assignedProgram" items="${assignedPrograms.resultList}">
                                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                    <td>
                                        <dr:assignedProgramName assignedProgram="${assignedProgram}"/>
                                    </td>
                                    <td>
                                        <cti:url var="viewUrl" value="view">
                                            <cti:param name="ecId" value="${param.ecId}"/>
                                            <cti:param name="applianceCategoryId" value="${assignedProgram.applianceCategoryId}"/>
                                        </cti:url>
                                        <a href="${viewUrl}">
                                        <spring:escapeBody htmlEscape="true">
                                            ${applianceCategoriesById[assignedProgram.applianceCategoryId].name}
                                        </spring:escapeBody>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </c:if>
                </tags:pagedBox>
            </div>
        </td>
    </tr>
</table>

</cti:standardPage>
