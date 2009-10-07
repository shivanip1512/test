<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.scenarioDetail.pageTitle" argument="${scenario.name}"/>
<cti:standardPage module="dr" page="scenarioDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|scenarios"/>

    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.scenarioDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/scenario/list">
        	<cti:msg key="yukon.web.modules.dr.scenarioDetail.breadcrumb.scenarios"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.scenarioDetail.breadcrumb.scenario"
                htmlEscape="true" argument="${scenario.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.scenarioDetail.scenario"
        htmlEscape="true" argument="${scenario.name}"/></h2>
    <br>

    <c:set var="scenarioId" value="${scenario.paoIdentifier.paoId}"/>
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.scenarioDetail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.scenarioDetail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${scenario}">
                        <cti:url var="startScenarioUrl" value="/spring/dr/program/startMultipleProgramsDetails">
                            <cti:param name="scenarioId" value="${scenarioId}"/>
                        </cti:url>
                        <a href="javascript:void(0)" class="simpleLink"
                            onclick="openSimpleDialog('drDialog', '${startScenarioUrl}', '<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.title"/>')">
                            <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.startIcon"/>
                            <cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.start"/><br>
                        </a>
    
                        <cti:url var="stopScenarioUrl" value="/spring/dr/program/stopMultipleProgramsDetails">
                            <cti:param name="scenarioId" value="${scenarioId}"/>
                        </cti:url>
                        <a href="javascript:void(0)" class="simpleLink"
                            onclick="openSimpleDialog('drDialog', '${stopScenarioUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.title"/>')">
                            <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.stopIcon"/>
                            <cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.stop"/><br>
                        </a>
                    </cti:checkPaoAuthorization>
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${scenario}" invert="true">
                        <cti:msg var="noScenarioControl" key="yukon.web.modules.dr.scenarioDetail.noControl"/>
                        <span title="${noScenarioControl}">
                            <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.startIcon.disabled"/>
                    		<cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.start"/><br>
                        </span>
                        <span title="${noScenarioControl}">
                            <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.stopIcon.disabled"/>
                    		<cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.stop"/><br>
                        </span>
                    </cti:checkPaoAuthorization>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.scenarioDetail.heading.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
        <c:set var="baseUrl" value="/spring/dr/scenario/detail"/>
        <%@ include file="../program/programList.jspf" %>
    </tags:abstractContainer>

</cti:standardPage>
