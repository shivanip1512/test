<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.loadGroupDetail.pageTitle" argument="${loadGroup.name}"/>
<cti:standardPage module="dr" page="loadGroupDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|loadGroups"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.loadGroupDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/loadGroup/list">
        	<cti:msg key="yukon.web.modules.dr.loadGroupDetail.breadcrumb.loadGroups"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.breadcrumb.loadGroup"
                htmlEscape="true" argument="${loadGroup.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.loadGroupDetail.loadGroup"
        htmlEscape="true" argument="${loadGroup.name}"/></h2>

    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
            		<cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendShed"/><br>
            		<cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestore"/><br>
                    <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.enable"/><br>
                    <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.disable"/><br>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.parents"/>
    <tags:abstractContainer type="box" title="${boxTitle}">

        <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.programs"/></p>
        <c:if test="${empty parentPrograms}">
            <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.noScenarios"/></p>
        </c:if>
        <c:if test="${!empty parentPrograms}">
	        <c:forEach var="parentProgram" items="${parentPrograms}">
                <c:url var="programURL" value="/spring/dr/program/detail">
                    <c:param name="programId" value="${parentProgram.paoIdentifier.paoId}"/>
                </c:url>
                <a href="${programURL}"><spring:escapeBody htmlEscape="true">${parentProgram.name}</spring:escapeBody></a><br>
	        </c:forEach>
        </c:if>

    </tags:abstractContainer>

</cti:standardPage>
