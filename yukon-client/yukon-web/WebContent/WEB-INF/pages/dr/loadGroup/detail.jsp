<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.dr.loadGroup.detail.pageTitle" argument="${loadGroup.name}"/>
<cti:standardPage module="dr" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|loadGroups"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.dr.loadGroup.detail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/loadGroup/list">
        	<cti:msg key="yukon.web.dr.loadGroup.detail.breadcrumb.loadGroups"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.dr.loadGroup.detail.breadcrumb.loadGroup" argument="${loadGroup.name}"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.dr.loadGroup.detail.loadGroup" argument="${loadGroup.name}"/></h2>

    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.dr.loadGroup.detail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="infoTitle" key="yukon.web.dr.loadGroup.detail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
            		<cti:msg key="yukon.web.dr.loadGroup.detail.actions.sendShed"/><br>
            		<cti:msg key="yukon.web.dr.loadGroup.detail.actions.sendRestore"/><br>
                    <cti:msg key="yukon.web.dr.loadGroup.detail.actions.enable"/><br>
                    <cti:msg key="yukon.web.dr.loadGroup.detail.actions.disable"/><br>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.dr.loadGroup.detail.heading.parents"/>
    <tags:abstractContainer type="box" title="${boxTitle}">

        <p><cti:msg key="yukon.web.dr.loadGroup.detail.parents.programs"/></p>
        <c:if test="${empty parentPrograms}">
            <p><cti:msg key="yukon.web.dr.loadGroup.detail.parents.noScenarios"/></p>
        </c:if>
        <c:if test="${!empty parentPrograms}">
	        <c:forEach var="parentProgram" items="${parentPrograms}">
                <c:url var="programURL" value="/spring/dr/program/detail">
                    <c:param name="programId" value="${parentProgram.paoIdentifier.paoId}"/>
                </c:url>
                <a href="${programURL}">${parentProgram.name}</a><br>
	        </c:forEach>
        </c:if>

    </tags:abstractContainer>

</cti:standardPage>
