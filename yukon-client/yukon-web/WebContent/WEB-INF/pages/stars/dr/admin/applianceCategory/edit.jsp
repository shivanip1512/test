<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="energyCompanyAdmin" page="applianceCategoryEdit">

    <cti:standardMenu menuSelection="configEnergyCompany"/>

    <tags:simpleDialog id="acDialog"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/operator/Admin/ConfigEnergyCompany.jsp">
            <cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.breadcrumb.configEnergyCompany"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.breadcrumb.editApplianceCategory"/></cti:crumbLink>
    </cti:breadCrumbs>

    <cti:url var="createVirtualProgramUrl" value="/spring/stars/dr/admin/applianceCategory/editAssignedProgram"/>
    <form>
        <script type="text/javascript">
        function selectDevices(devices) {
            openSimpleDialog('acDialog', '${createVirtualProgramUrl}', 'Edit Assigned Programs');
        }
        </script>
        <input type="hidden" id="programsToAssign" name="programsToAssign"/>
    </form>

    <h2>WATER HEATER (WH1)</h2>
    <br>

    <%-- criteria needs to include only programs which are not already assigned and are in a control area --%>
    <cti:drPicker pickerId="programPicker"
        constraint="com.cannontech.common.search.criteria.LMProgramOrScenarioCriteria"
        paoIdField="programsToAssign" finalTriggerAction="selectDevices"
        selectionLinkName="Next"/>

    <table class="widgetColumns">
        <tr>
            <td class="widgetColumnCell" valign="top">
                <div class="widgetContainer">
                    <cti:msg var="boxTitle" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.info"/>
                    <tags:abstractContainer type="box" title="${boxTitle}">
                        <tags:nameValueContainer>
                            <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.type"/>
                            <tags:nameValue name="${fieldName}">
                                Water Heater
                            </tags:nameValue>

                            <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.icon"/>
                            <tags:nameValue name="${fieldName}">
                               <img src="<cti:url value="/WebConfig/yukon/Icons/Load.gif"/>"/>
                            </tags:nameValue>

                            <cti:msg var="fieldName" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.description"/>
                            <tags:nameValue name="${fieldName}">
                                Save money by turning down your water heater when you're not using it.
                            </tags:nameValue>
                        </tags:nameValueContainer>
                        <br>
                        <cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.isCustomerSelectable"/>
                    </tags:abstractContainer>
                </div>
            </td>
            <td class="widgetColumnCell" valign="top">
                <div class="widgetContainer">
                    <cti:msg var="boxTitle" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.actions"/>
                    <tags:abstractContainer type="box" title="${boxTitle}">
                        <ul>
                            <cti:url var="detailUrl" value="/spring/stars/dr/admin/applianceCategory/editDetails">
                                <cti:param name="applianceCategoryId" value="${param.applianceCategoryId}"/>
                            </cti:url>
                            <li>
                                <tags:simpleDialogLink dialogId="acDialog" actionUrl="${detailUrl}"
                                    labelKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.editDetailInformation"
                                    titleKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.editDetailInformation"/>
                            </li>
                            <li>
                                <a href="javascript:programPicker.showPicker();" class="simpleLink">
                                    <cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.addPrograms"/>
                                </a>
                            </li>
                            <li>
						        <tags:simpleDialogLink dialogId="acDialog" actionUrl="${createVirtualProgramUrl}"
						            labelKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.createVirtualProgram"
						            titleKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.createVirtualProgram"/>
                            </li>
                        </ul>
                    </tags:abstractContainer>
                </div>
            </td>
        </tr>
        <tr>
            <td class="widgetColumnCell" valign="top" colspan="2">
                <div class="widgetContainer">

    <c:set var="baseUrl" value="/spring/stars/dr/admin/applianceCategory/edit"/>

    <cti:msg var="filterLabel" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.filters"/>
    <tags:simplePopup id="filterDialog" title="${filterLabel}">
        <form:form action="${submitUrl}" commandName="backingBean" method="get">
	        <c:if test="${!empty param.sort}">
	            <input type="hidden" name="sort" value="${param.sort}"/>
	        </c:if>
	        <c:if test="${!empty param.descending}">
	            <input type="hidden" name="descending" value="${param.descending}"/>
	        </c:if>
	        <c:if test="${!empty param.itemsPerPage}">
	            <input type="hidden" name="itemsPerPage" value="${param.itemsPerPage}"/>
	        </c:if>

	        <table cellspacing="10">
	            <tr>
	                <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaList.filter.name"/>
	                <td>${fieldName}</td>
	                <%--
                    <td><form:input path="name" size="40"/></td>
	                --%>
                    <td><input type="text" size="40"/></td>
	            </tr>
	        </table>
	
	        <br>
	        <div class="actionArea">
	            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.controlAreaList.filter.submit"/>"/>
	            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlAreaList.filter.clear"/>"
	                onclick="javascript:clearFilter()"/>
	        </div>
        </form:form>
    </tags:simplePopup>

    <cti:msg var="boxTitle" key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.assignedPrograms"/>
    <tags:pagedBox title="${boxTitle}" searchResult="${assignedPrograms}"
        baseUrl="${baseUrl}" filterDialog="filterDialog">
        <table id="programList" class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th>
                    <tags:sortLink key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.programName"
                        baseUrl="${baseUrl}" fieldName="PROGRAM_NAME" isDefault="true"/>
                </th>
                <th>
                    <cti:msg key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.actions"/>
                </th>
                <th>
                    <tags:sortLink key="yukon.web.modules.stars.dr.admin.applianceCategory.edit.displayOrder"
                        baseUrl="${baseUrl}" fieldName="DISPLAY_ORDER" isDefault="false"/>
                </th>
            </tr>
            <c:forEach var="program" items="${assignedPrograms.resultList}">
                <c:set var="programId" value="1"/>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <cti:url var="programUrl" value="/spring/dr/program/detail">
                        <cti:param name="programId" value="${programId}"/>
                    </cti:url>
                    <td>
                        <a href="${programUrl}"><spring:escapeBody htmlEscape="true">${program.programPaoName}</spring:escapeBody></a>
                        <c:if test="${!empty program.programName && program.programName != program.programPaoName}">
                            (<spring:escapeBody htmlEscape="true">${program.programName}</spring:escapeBody>)
                        </c:if>
                    </td>
                    <td>
                        <cti:url var="editProgramUrl" value="/spring/stars/dr/admin/applianceCategory/editAssignedProgram">
                            <cti:param name="programId" value="${programId}"/>
                        </cti:url>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.editDialogTitle" 
                            dialogId="acDialog" 
                            actionUrl="${editProgramUrl}" 
                            logoKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.edit"/>
                        <cti:url var="removeProgramUrl" value="/spring/stars/dr/admin/applianceCategory/removeAssignedProgram">
                            <cti:param name="programId" value="${programId}"/>
                        </cti:url>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.removeDialogTitle" 
                            dialogId="acDialog" 
                            actionUrl="${removeProgramUrl}" 
                            logoKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.remove"/>
                    </td>
                    <td>
                        <cti:url var="moveProgramUpUrl" value="/spring/stars/dr/admin/applianceCategory/move">
                            <cti:param name="direction" value="up"/>
                            <cti:param name="programId" value="${programId}"/>
                        </cti:url>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.upDialogTitle" 
                            dialogId="acDialog" 
                            actionUrl="${moveProgramUpUrl}" 
                            logoKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.up"/>
                        <cti:url var="moveProgramDownUrl" value="/spring/stars/dr/admin/applianceCategory/move">
                            <cti:param name="direction" value="down"/>
                            <cti:param name="programId" value="${programId}"/>
                        </cti:url>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.downDialogTitle" 
                            dialogId="acDialog" 
                            actionUrl="${moveProgramDownUrl}" 
                            logoKey="yukon.web.modules.stars.dr.admin.applianceCategory.edit.down"/>
                        ${program.programOrder}
                    </td>
                </tr>
            </c:forEach>
        </table>
    </tags:pagedBox>
                </div>
            </td>
        </tr>
    </table>

</cti:standardPage>
