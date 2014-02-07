<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="scenarioDetail">

    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/hideReveal.js"/>
    <cti:includeScript link="/JavaScript/drEstimatedLoad.js"/>
    
    <c:set var="scenarioId" value="${scenario.paoIdentifier.paoId}"/>

    <input id="assetId" type="hidden" value="${scenarioId}"/>
    <cti:includeScript link="/JavaScript/drAssetDetails.js"/>

    <div class="column-12-12">
        <div class="column one">
        <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
            <tags:sectionContainer2 nameKey="heading.info">
            <tags:nameValueContainer2>
                <cti:dataUpdaterCallback
                    function="Yukon.EstimatedLoad.displaySummaryValue"
                    identifier="ESTIMATED_LOAD/${scenarioId}/SCENARIO"/>
               <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.connectedLoad">
                        <div data-pao="${scenarioId}">
                            <cti:icon icon="icon-error" classes="dn"/>
                            <cti:icon icon="icon-spinner"/>
                            <span class="f-connected-load">
                                <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                            </span>
                        </div>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.diversifiedLoad">
                        <div data-pao="${scenarioId}">
                            <cti:icon icon="icon-error" classes="dn"/>
                            <cti:icon icon="icon-spinner"/>
                            <span class="f-diversified-load">
                                <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                            </span>
                        </div>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.kwSavings">
                        <div data-pao="${scenarioId}">
                            <cti:icon icon="icon-error" classes="dn"/>
                            <cti:icon icon="icon-spinner"/>
                            <span class="f-kw-savings">
                                <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                            </span>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </cti:checkRolesAndProperties>
        </div>
        
        <div class="fr column two nogutter">
            <cti:checkRolesAndProperties value="SHOW_ASSET_AVAILABILITY">
                <%-- Display the Asset Availability Info --%>
                <tags:sectionContainer2 nameKey="assetAvailability">
                    <c:choose>
                        <c:when test="${assetTotal <= 0}">
                            <span class="empty-list">
                                <cti:msg2 key="yukon.web.modules.operator.hardware.assetAvailability.noDevices"/>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <dr:assetAvailabilityStatus assetId="${scenarioId}"
                                assetAvailabilitySummary="${assetAvailabilitySummary}" 
                                pieJSONData="${pieJSONData}" showDetails="true"/>
                        </c:otherwise>
                    </c:choose>
                </tags:sectionContainer2>
            </cti:checkRolesAndProperties>
            
                <%--
                    Control Area Actions section each action has a simpleDialogLink that
                    pops open a dialog for the action.  The available actions are based
                    on the dynamically updated SHOW_ACTION value
                --%>
                <!-- Page Dropdown Actions -->
                <div id="f-page-actions" class="dn">
                    
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${scenario}">

                        <tags:dynamicChoose updaterString="DR_SCENARIO/${scenarioId}/SHOW_ACTION" suffix="${scenarioId}">
                            
                            <tags:dynamicChooseOption optionId="hasNoPrograms">
                                <cti:msg2 var="scenarioHasNoAssignedPrograms" key=".noAssignedPrograms"/>
                                <li>
                                    <a class="clearfix" title="${scenarioHasNoAssignedPrograms}">
                                        <cti:icon icon="icon-control-play-blue" classes="disabled"/>
                                        <span class="dib disabled"><cti:msg2 key=".actions.start"/></span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${scenarioHasNoAssignedPrograms}">
                                        <cti:icon icon="icon-control-stop-blue" classes="disabled"/>
                                        <span class="dib disabled"><cti:msg2 key=".actions.stop"/></span>
                                    </a>
                                </li>
                            </tags:dynamicChooseOption>
                            
                            <tags:dynamicChooseOption optionId="enabled">
                                <cti:url var="startScenarioUrl" value="/dr/program/start/multipleDetails">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title" 
                                        dialogId="drDialog" actionUrl="${startScenarioUrl}" icon="icon-control-play-blue"
                                        labelKey=".actions.start"/>
                                </li>
                                <cti:url var="stopScenarioUrl" value="/dr/program/stop/multipleDetails">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title" 
                                        dialogId="drDialog" actionUrl="${stopScenarioUrl}" icon="icon-control-stop-blue"
                                        labelKey=".actions.stop"/>
                                </li>
                                <li class="divider"></li>
                                <cti:url var="changeScenarioGearsUrl" value="/dr/program/changeGearMultiplePopup">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                                        dialogId="drDialog" actionUrl="${changeScenarioGearsUrl}" icon="icon-cog-edit"
                                        labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                </li>
                                <cti:url var="sendEnableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                    <cti:param name="enable" value="true"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableProgramsConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendEnableProgramsUrl}" icon="icon-accept"
                                        labelKey=".actions.enablePrograms"/>
                                </li>
                                <cti:url var="sendDisableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                                    <cti:param name="scenarioId" value="${scenarioId}"/>
                                    <cti:param name="enable" value="false"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableProgramsConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendDisableProgramsUrl}" icon="icon-delete"
                                        labelKey=".actions.disablePrograms"/>
                                </li>
                            </tags:dynamicChooseOption>
                        </tags:dynamicChoose>
                    </cti:checkPaoAuthorization>

                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${scenario}" invert="true">
                        <cti:msg2 var="noScenarioControl" key=".noControl"/>
                        <li>
                            <a class="clearfix" title="${noScenarioControl}"> 
                                <cti:icon icon="icon-control-play-blue" classes="disabled" /> 
                                <span class="dib disabled">
                                    <cti:msg2 key=".actions.start" />
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noScenarioControl}"> 
                                <cti:icon icon="icon-control-stop-blue" classes="disabled" /> 
                                <span class="dib disabled">
                                    <cti:msg2 key=".actions.stop" />
                                </span>
                            </a>
                        </li>
                    </cti:checkPaoAuthorization>
            </div>
        </div>
    </div>

    <div class="column-24">
        <div class="column one nogutter">
            <c:set var="baseUrl" value="/dr/scenario/detail"/>
            <%@ include file="../program/programList.jspf" %>
        </div>
    </div>

</cti:standardPage>
