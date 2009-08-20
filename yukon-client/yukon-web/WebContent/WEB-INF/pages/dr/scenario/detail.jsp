<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.dr.scenario.detail.pageTitle" argument="${scenario.name}"/>
<cti:standardPage module="dr" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|scenario"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.dr.scenario.detail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/scenario/list">
        	<cti:msg key="yukon.web.dr.scenario.detail.breadcrumb.scenarios"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.dr.scenario.detail.breadcrumb.scenario" argument="${scenario.name}"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.dr.scenario.detail.scenario" argument="${scenario.name}"/></h2>

    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.dr.scenario.detail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="infoTitle" key="yukon.web.dr.scenario.detail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
            		<cti:msg key="yukon.web.dr.scenario.detail.actions.start"/><br>
            		<cti:msg key="yukon.web.dr.scenario.detail.actions.stop"/><br>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.dr.scenario.detail.heading.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">

	<table id="programList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.dr.scenario.detail.programs.heading.name"/></th>
		</tr>
		<c:forEach var="program" items="${programs}">
			<tr>
				<td>
				<c:url var="programURL" value="/spring/dr/program/detail">
					<c:param name="programId" value="${program.paoIdentifier.paoId}"/>
				</c:url>
				<a href="${programURL}">${program.name}</a>
				</td>
			</tr>
		</c:forEach>
	</table>

    </tags:abstractContainer>

</cti:standardPage>
