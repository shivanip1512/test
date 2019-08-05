<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="programDetail">

    <tags:simpleDialog id="drDialog"/>
    
    <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
    
    <input id="assetId" type="hidden" value="${programId}"/>

    <div class="column-12-12 clearfix">
        <div class="column one">
            <%-- Program Info section --%>
            <tags:sectionContainer2 nameKey="heading.info">
                <tags:nameValueContainer2>
                    
                    <tags:nameValue2 nameKey=".info.state" nameColumnWidth="175px">
                        <dr:programState programId="${programId}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".info.start">
                        <cti:dataUpdaterValue identifier="${programId}/START" type="DR_PROGRAM"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".info.stop">
                        <cti:dataUpdaterValue identifier="${programId}/STOP" type="DR_PROGRAM"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".info.currentGear">
                        <cti:dataUpdaterValue identifier="${programId}/CURRENT_GEAR" type="DR_PROGRAM"/>
                    </tags:nameValue2>
                    
                    <cti:checkRolesAndProperties value="DR_VIEW_PRIORITY">
                        <tags:nameValue2 nameKey=".info.priority">
                            <cti:dataUpdaterValue identifier="${programId}/PRIORITY" type="DR_PROGRAM"/>
                        </tags:nameValue2>
                    </cti:checkRolesAndProperties>
                    
                    <cti:checkRolesAndProperties value="DR_VIEW_REDUCTION">
                        <tags:nameValue2 nameKey=".info.reduction">
                            <cti:dataUpdaterValue identifier="${programId}/REDUCTION" type="DR_PROGRAM"/>
                        </tags:nameValue2>
                    </cti:checkRolesAndProperties>
                    
                    <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                        <cti:dataUpdaterCallback
                            function="yukon.dr.estimatedLoad.displayValue"
                            value="ESTIMATED_LOAD/${programId}/PROGRAM"
                            initialize="true"/>
                        <cti:msgScope paths="modules.dr.estimatedLoad">
                            <tags:nameValue2 nameKey=".info.connectedLoad">
                                <div data-pao="${programId}">
                                    <span class="js-connected-load"></span>
                                </div>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".info.diversifiedLoad">
                                <div data-pao="${programId}">
                                    <span class="js-diversified-load"></span>
                                </div>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".info.kwSavings">
                                <div data-pao="${programId}" class="js-est-load-kw-savings">
                                    <cti:icon icon="icon-loading-bars" classes="js-est-load-calculating push-down-4"/>
                                    <cti:button classes="js-est-load-error-btn dn fn vat M0" renderMode="buttonImage" 
                                        icon="icon-error" data-popup="[data-program-id=${programId}]"/>
                                    <span class="js-kw-savings dib push-down-3"></span>
                                    <cti:url var="url" value="/dr/estimatedLoad/program-error">
                                        <cti:param name="programId" value="${programId}"/>
                                    </cti:url>
                                    <div data-url="${url}" 
                                        data-program-id="${programId}" data-height="235" data-width="575" 
                                        class="dn"/>
                                </div>
                            </tags:nameValue2>
                        </cti:msgScope>
                    </cti:checkRolesAndProperties>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <cti:checkRolesAndProperties value="SHOW_ASSET_AVAILABILITY">
                <%-- Display the Asset Availability Info --%>
                <tags:sectionContainer2 nameKey="assetAvailability">
                    <div class="js-asset-availability js-block-this">
                        <i:inline key="yukon.common.loading"/>
                    </div>
                </tags:sectionContainer2>
            </cti:checkRolesAndProperties>
            <%-- 
                Program Actions section each action has a simpleDialogLink that
                pops open a dialog for the action.  The available actions are based
                on the dynamically updated SHOW_ACTION value.
            --%>
            <!-- Page Dropdown Actions -->
            <div id="page-actions" class="dn">
                <cti:msgScope paths=",modules.dr.program">
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}">
                        
                        <div data-start-action="on" data-pao-id="${programId}">
                            <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                <cti:param name="programId" value="${programId}"/>
                            </cti:url>
                            <li><tags:simpleDialogLink titleKey=".startProgram.title" 
                                    dialogId="drDialog" actionUrl="${startProgramUrl}" icon="icon-control-play-blue"
                                    labelKey=".actions.start"/></li>
                        </div>
                        <div class="dn" data-start-action="off" data-pao-id="${programId}">
                            <cm:dropdownOption icon="icon-control-play-blue" disabled="true" key=".actions.start"/>
                        </div>
                        <div data-stop-action="on" data-pao-id="${programId}">
                            <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                <cti:param name="programId" value="${programId}"/>
                            </cti:url>
                            <li><tags:simpleDialogLink titleKey=".stopProgram.title" 
                                    dialogId="drDialog" actionUrl="${stopProgramUrl}" icon="icon-control-stop-blue"
                                    labelKey=".actions.stop"/></li>
                        </div>
                        <div class="dn" data-stop-action="off" data-pao-id="${programId}">
                            <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" key=".actions.stop"/>
                        </div>
						<c:choose>
							<c:when test="${changeGearAllowed}">
								 <div data-change-gears-action="on" data-pao-id="${programId}">
                          		 	<cti:url var="changeGearsUrl" value="/dr/program/getChangeGearValue">
                                		<cti:param name="programId" value="${programId}"/>
                            	</cti:url>
                            	<li>
                            		<tags:simpleDialogLink titleKey=".getChangeGearValue.title" 
                                    	dialogId="drDialog" actionUrl="${changeGearsUrl}" icon="icon-cog-edit"
	                                    labelKey=".actions.changeGears"/>
	                            </li>
		                        </div>
		                        <div class="dn" data-change-gears-action="off" data-pao-id="${programId}">
        		                    <cm:dropdownOption icon="icon-cog-edit" disabled="true" key=".actions.changeGears"/>
		                        </div>
							</c:when>
							<c:otherwise>
								<div class="dn" data-change-gears-action="off" data-pao-id="${programId}">
                           			 <cm:dropdownOption icon="icon-cog-edit" disabled="true" key=".actions.changeGears"/>
                      		  </div> 
							</c:otherwise>
						</c:choose>

						<c:choose>
							<c:when test="${enableDisableProgramsAllowed}">
								<div data-enable-program-action="on" data-pao-id="${programId}">
									<cti:url var="sendEnableUrl" value="/dr/program/sendEnableConfirm">
										<cti:param name="programId" value="${programId}" />
										<cti:param name="isEnabled" value="true" />
									</cti:url>
									<li><tags:simpleDialogLink titleKey=".sendEnableConfirm.title"
							        		dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
											labelKey=".actions.enable" /></li>
								</div>
								<div data-disable-program-action="on" data-pao-id="${programId}">
									<cti:url var="sendDisableUrl" value="/dr/program/sendEnableConfirm">
										<cti:param name="programId" value="${programId}" />
										<cti:param name="isEnabled" value="false" />
									</cti:url>
									<li><tags:simpleDialogLink titleKey=".sendDisableConfirm.title"
									        dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
											labelKey=".actions.disable" /></li>
								</div>
								<div class="dn" data-disable-program-action="off" data-pao-id="${programId}">
									<cm:dropdownOption icon="icon-delete" disabled="true" key=".actions.disable" />
								</div>
							 </c:when>
							<c:otherwise>
								<div class="dn" data-enable-program-action="off" data-pao-id="${programId}">
									<cm:dropdownOption icon="icon-accept" disabled="true" key=".actions.enable" />
								</div>
								<div class="dn" data-disable-program-action="off" data-pao-id="${programId}">
									<cm:dropdownOption icon="icon-delete" disabled="true" key=".actions.disable" />
								</div>
							</c:otherwise>
						</c:choose>
					</cti:checkPaoAuthorization>
				</cti:msgScope>
				<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}" invert="true">
					<%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
					<cti:msg2 var="noProgramControl" key=".noControl" />
					<cm:dropdownOption icon="icon-control-play-blue" disabled="true" key=".actions.start" title="${noProgramControl}" />
					<cm:dropdownOption icon="icon-control-stop-blue" disabled="true" key=".actions.stop" title="${noProgramControl}" />
					<cm:dropdownOption icon="icon-cog-edit" disabled="true" key=".actions.changeGears" title="${noProgramControl}" />
					<cm:dropdownOption icon="icon-delete" disabled="true" key=".actions.disable" title="${noProgramControl}" />
				</cti:checkPaoAuthorization>
                
                <!-- Meter Disconnect Status -->
                <c:if test="${program.paoIdentifier.paoType == 'LM_METER_DISCONNECT_PROGRAM'}">
                    <cti:url var="url" value="/dr/program/disconnectStatus">
                        <cti:param name="programId" value="${programId}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-disconnect" key=".actions.disconnectStatus" href="${url}"/>
                    <cti:url var="url" value="/dr/program/controlStatus">
                        <cti:param name="programId" value="${programId}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-read" key=".actions.controlStatus" href="${url}"/>
                </c:if>
			</div>
		</div>
	</div>

    <%-- Child Load Groups for the Program --%>
    <c:set var="baseUrl" value="/dr/program/detail"/>
    <%@ include file="../loadGroup/loadGroupList.jspf" %>
    
    <c:set var="showControlAreas" value="false"/>
    <c:set var="showScenarios" value="false"/>
    <cti:checkRolesAndProperties value="SHOW_CONTROL_AREAS">
        <c:set var="showControlAreas" value="true"/>
    </cti:checkRolesAndProperties>
    <cti:checkRolesAndProperties value="SHOW_SCENARIOS">
        <c:set var="showScenarios" value="true"/>
    </cti:checkRolesAndProperties>

    <div class="column-12-12">
        <div class="column one">
            <c:if test="${showControlAreas}">
                <%-- 
                    Parent Control Area for the Program - has a link to Control Area detail if
                    the user has permission to view the Control Area.
                --%>
                <tags:sectionContainer2 nameKey="parents.controlArea">
                    <c:if test="${empty parentControlArea}">
                        <span class="empty-list"><cti:msg2 key=".parents.noControlArea"/></span>
                    </c:if>
                    <c:if test="${!empty parentControlArea}">
                        <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentControlArea}">
                            <c:url var="controlAreaURL" value="/dr/controlArea/detail">
                                <c:param name="controlAreaId" value="${parentControlArea.paoIdentifier.paoId}"/>
                            </c:url>
                            <a href="${controlAreaURL}">${fn:escapeXml(parentControlArea.name)}</a><br>
                        </cti:checkPaoAuthorization>
                        <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentControlArea}" invert="true">
                            <cti:msg2 var="noParentPermission" key=".parents.noControlAreaPermission"/>
                            <span title="${noParentPermission}">${fn:escapeXml(parentControlArea.name)}</span>
                        </cti:checkPaoAuthorization>
                    </c:if>
                </tags:sectionContainer2>
            </c:if>
        </div>
        <div class="column two nogutter">
            <c:if test="${showScenarios}">
                <tags:sectionContainer2 nameKey="parents.scenarios">
                    <%-- 
                        Parent Scenario(s) for the Program - has a link to Scenario detail if
                        the user has permission to view the Scenario.
                    --%>
                    <c:if test="${empty parentScenarios}">
                        <span class="empty-list"><cti:msg2 key=".parents.noScenarios"/></span>
                    </c:if>
                    <c:if test="${!empty parentScenarios}">
                        <c:forEach var="parentScenario" items="${parentScenarios}">
                            <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentScenario}">
                                <c:url var="scenarioURL" value="/dr/scenario/detail">
                                    <c:param name="scenarioId" value="${parentScenario.paoIdentifier.paoId}"/>
                                </c:url>
                                <a href="${scenarioURL}">${fn:escapeXml(parentScenario.name)}</a><br>
                            </cti:checkPaoAuthorization>
                            <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentScenario}" invert="true">
                                <cti:msg2 var="noParentPermission" key=".parents.noScenarioPermission"/>
                                <span title="${noParentPermission}">${fn:escapeXml(parentScenario.name)}</span>
                            </cti:checkPaoAuthorization>
                        </c:forEach>
                    </c:if>
                </tags:sectionContainer2>
            </c:if>
        </div>
    </div>
	<cti:dataUpdaterCallback function="yukon.dr.dataUpdater.showAction.updateProgramMenu(${programId},${changeGearAllowed},${enableDisableProgramsAllowed})" 
		initialize="true" state="DR_PROGRAM/${programId}/SHOW_ACTION" />
        
    <cti:includeScript link="/resources/js/pages/yukon.dr.asset.details.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.estimated.load.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.dataUpdater.showAction.js"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_PIE"/>
    <!--[if lte IE 8]><cti:includeScript link="JQUERY_EXCANVAS"/><![endif]-->
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>

</cti:standardPage>