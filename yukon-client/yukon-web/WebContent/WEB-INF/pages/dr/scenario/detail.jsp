<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="scenarioDetail" title="${pageTitle}">

    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconSetup/>
    
    <c:set var="scenarioId" value="${scenario.paoIdentifier.paoId}"/>
    <tags:layoutHeadingPrefixPart>
        <dr:favoriteIcon paoId="${scenarioId}" isFavorite="${isFavorite}"/>
    </tags:layoutHeadingPrefixPart>

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

                        <tags:dynamicChoose updaterString="DR_SCENARIO/${scenarioId}/SHOW_ACTION" suffix="${scenarioId}">
                            <tags:dynamicChooseOption optionId="hasNoPrograms">
                            
                                <cti:msg var="scenarioHasNoAssignedPrograms"
                                       key="yukon.web.modules.dr.scenarioDetail.noAssignedPrograms"/>
                                <span title="${scenarioHasNoAssignedPrograms}">
                                    <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.startIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.start"/>
                                </span>
                                <br>
                                <span title="${scenarioHasNoAssignedPrograms}">
                                    <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.stopIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.stop"/>
                                </span>
                                            
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="enabled">
                            
                                <cti:url var="startScenarioUrl" value="/spring/dr/program/start/multipleDetails">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title"
                                               dialogId="drDialog"
                                               actionUrl="${startScenarioUrl}"
                                               logoKey="yukon.web.modules.dr.scenarioDetail.actions.startIcon"
                                               labelKey="yukon.web.modules.dr.scenarioDetail.actions.start"/>
                                <br>
        
                                <cti:url var="stopScenarioUrl" value="/spring/dr/program/stop/multipleDetails">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title"
                                               dialogId="drDialog"
                                               actionUrl="${stopScenarioUrl}"
                                               logoKey="yukon.web.modules.dr.scenarioDetail.actions.stopIcon"
                                               labelKey="yukon.web.modules.dr.scenarioDetail.actions.stop"/>
                                <br>
                                
                                <cti:url var="changeScenarioGearsUrl" value="/spring/dr/program/changeGearMultiplePopup">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                                            dialogId="drDialog" 
                                            actionUrl="${changeScenarioGearsUrl}" 
                                            logoKey="yukon.web.modules.dr.programDetail.actions.changeGearsIcon"
                                            labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                <br>
                                
                                <cti:url var="sendEnableProgramsUrl" value="/spring/dr/program/sendEnableDisableProgramsConfirm">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                    <cti:param name="enable" value="true"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableProgramsConfirm.title"
                                    dialogId="drDialog" actionUrl="${sendEnableProgramsUrl}"
                                    logoKey="yukon.web.modules.dr.scenarioDetail.actions.enableProgramsIcon"
                                    labelKey="yukon.web.modules.dr.scenarioDetail.actions.enablePrograms"/>
                                <br>
                                <cti:url var="sendDisableProgramsUrl" value="/spring/dr/program/sendEnableDisableProgramsConfirm">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                    <cti:param name="enable" value="false"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableProgramsConfirm.title"
                                    dialogId="drDialog" actionUrl="${sendDisableProgramsUrl}"
                                    logoKey="yukon.web.modules.dr.scenarioDetail.actions.disableProgramsIcon"
                                    labelKey="yukon.web.modules.dr.scenarioDetail.actions.disablePrograms"/>
                                <br>
                                
                            </tags:dynamicChooseOption>
                        </tags:dynamicChoose>
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
