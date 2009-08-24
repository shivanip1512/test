<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.dr.controlArea.detail.pageTitle" argument="${controlArea.name}"/>
<cti:standardPage module="dr" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|controlareas"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.dr.controlArea.detail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/controlArea/list">
        	<cti:msg key="yukon.web.dr.controlArea.detail.breadcrumb.controlAreas"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.dr.controlArea.detail.breadcrumb.controlArea" argument="${controlArea.name}"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.dr.controlArea.detail.controlArea" argument="${controlArea.name}"/></h2>

    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.dr.controlArea.detail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="infoTitle" key="yukon.web.dr.controlArea.detail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
            		<cti:msg key="yukon.web.dr.controlArea.detail.actions.start"/><br>
                    <cti:msg key="yukon.web.dr.controlArea.detail.actions.stop"/><br>
                    <cti:msg key="yukon.web.dr.controlArea.detail.actions.triggersChange"/><br>
                    <cti:msg key="yukon.web.dr.controlArea.detail.actions.dailyTimeChange"/><br>
                    <cti:msg key="yukon.web.dr.controlArea.detail.actions.disable"/><br>
                    <cti:msg key="yukon.web.dr.controlArea.detail.actions.enable"/><br>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.dr.controlArea.detail.heading.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">

	<table id="programList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.dr.controlArea.detail.programs.heading.name"/></th>
		</tr>
		<c:forEach var="program" items="${programs}">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
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
