<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

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
        <cti:crumbLink url="/spring/dr/home">
            <cti:msg key="yukon.web.modules.dr.scenarioDetail.breadcrumb.drHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/scenario/list">
            <cti:msg key="yukon.web.modules.dr.scenarioDetail.breadcrumb.scenarios"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.scenarioDetail.breadcrumb.scenario"
                htmlEscape="true" argument="${scenario.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <c:set var="scenarioId" value="${scenario.paoIdentifier.paoId}"/>
    <h2><dr:favoriteIcon paoId="${scenarioId}" isFavorite="${isFavorite}"/>
        <cti:msg key="yukon.web.modules.dr.scenarioDetail.scenario"
        htmlEscape="true" argument="${scenario.name}"/></h2>
    <br>

    <table class="widgetColumns">
        <tr>
            <td class="widgetColumnCell" valign="top">
                <div class="widgetContainer">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.scenarioDetail.heading.info"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                </tags:abstractContainer>
                </div>
            </td>
            <td class="widgetColumnCell" valign="top">
                <div class="widgetContainer">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.scenarioDetail.heading.actions"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${scenario}">
                        <cti:url var="startScenarioUrl" value="/spring/dr/program/startMultipleProgramsDetails">
                            <cti:param name="scenarioId" value="${scenarioId}"/>
                        </cti:url>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${startScenarioUrl}"
                                                       logoKey="yukon.web.modules.dr.scenarioDetail.actions.startIcon"
                                                       labelKey="yukon.web.modules.dr.scenarioDetail.actions.start"/>
                        <br>

                        <cti:url var="stopScenarioUrl" value="/spring/dr/program/stopMultipleProgramsDetails">
                            <cti:param name="scenarioId" value="${scenarioId}"/>
                        </cti:url>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${stopScenarioUrl}"
                                                       logoKey="yukon.web.modules.dr.scenarioDetail.actions.stopIcon"
                                                       labelKey="yukon.web.modules.dr.scenarioDetail.actions.stop"/>
                        <br>
                    </cti:checkPaoAuthorization>
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${scenario}" invert="true">
                        <cti:msg var="noScenarioControl" key="yukon.web.modules.dr.scenarioDetail.noControl"/>
                        <span title="${noScenarioControl}">
                            <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.startIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.start"/>
                        </span>
                        <br>
                        <span title="${noScenarioControl}">
                            <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.stopIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.stop"/>
                        </span>
                        <br>
                    </cti:checkPaoAuthorization>
                </tags:abstractContainer>
                </div>
            </td>
        </tr>
        <tr>
            <td class="widgetColumnCell" colspan="2">
                <div class="widgetContainer">
                    <c:set var="baseUrl" value="/spring/dr/scenario/detail"/>
                    <%@ include file="../program/programList.jspf" %>
                </div>
            </td>
        </tr>
    </table>

</cti:standardPage>
