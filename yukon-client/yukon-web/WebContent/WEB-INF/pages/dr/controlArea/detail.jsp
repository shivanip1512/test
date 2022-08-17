<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="controlAreaDetail">
    
    <tags:simpleDialog id="drDialog"/>
    
    <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>

    <input id="assetId" type="hidden" value="${controlAreaId}"/>

    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="heading.info">
                <tags:nameValueContainer2 tableClass="stacked">
                    <tags:nameValue2 nameKey=".info.state">
                        <dr:controlAreaState controlAreaId="${controlAreaId}"/>
                    </tags:nameValue2>
                    <cti:checkRolesAndProperties value="DR_VIEW_PRIORITY">
                        <tags:nameValue2 nameKey=".info.priority">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/PRIORITY"/>
                        </tags:nameValue2>
                    </cti:checkRolesAndProperties>
                    
                    <tags:nameValue2 nameKey=".info.startStop">
                        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/START"/>
                        <i:inline key=".info.separator"/>
                        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STOP"/>
                    </tags:nameValue2>
                    
                    <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                        <cti:dataUpdaterCallback
                            function="yukon.dr.estimatedLoad.displayValue"
                            value="ESTIMATED_LOAD/${controlAreaId}/CONTROL_AREA"/>
                        <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.connectedLoad">
                            <div data-pao="${controlAreaId}">
                                <span class="js-connected-load"></span>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.diversifiedLoad">
                            <div data-pao="${controlAreaId}">
                                <span class="js-diversified-load"></span>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.kwSavings">
                            <div data-pao="${controlAreaId}">
                                <cti:icon icon="icon-loading-bars" classes="js-est-load-calculating push-down-4"/>
                                <cti:button classes="js-est-load-error-btn dn fn vat M0" renderMode="buttonImage" 
                                    icon="icon-error" data-popup="[data-control-area-id=${controlAreaId}]"/>
                                <span class="js-kw-savings dib push-down-3"></span>
                                <cti:url var="url" value="/dr/estimatedLoad/summary-error">
                                    <cti:param name="paoId" value="${controlAreaId}"/>
                                </cti:url>
                                <div data-url="${url}" 
                                    data-control-area-id="${controlAreaId}" data-height="235" data-width="575" 
                                    class="dn"/>
                            </div>
                        </tags:nameValue2>
                    </cti:checkRolesAndProperties>
              </tags:nameValueContainer2>

              <tags:nameValueContainer2>
                    <c:if test="${empty controlArea.triggers}">
                        <span class="empty-list"><i:inline key=".info.noTriggers"/></span>
                    </c:if>
                    <c:if test="${!empty controlArea.triggers}">
                        <c:forEach var="trigger" items="${controlArea.triggers}">
                            <cti:checkRolesAndProperties value="DR_VIEW_CONTROL_AREA_TRIGGER_INFO">
                                <c:set var="triggerNumber" value="${trigger.triggerNumber}"/>
                                <tags:nameValue2 rowClass="strong-label-small" nameKey=".info.trigger" argument="${triggerNumber}"></tags:nameValue2>
                                <tags:nameValue2 nameKey=".info.loadCapacity">
                                    <tags:nameValue2 nameKey=".info.valueThreshold">
                                        <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/VALUE_THRESHOLD"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".info.peakProjection">
                                        <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/PEAK_PROJECTION"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".info.atku">
                                        <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/ATKU"/>
                                    </tags:nameValue2>
                                </tags:nameValue2>
                            </cti:checkRolesAndProperties>
                        </c:forEach>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        
        <div class="column two nogutter">
            <cti:checkRolesAndProperties value="SHOW_ASSET_AVAILABILITY">
                <tags:sectionContainer2 nameKey="assetAvailability">
                    <%-- Display the Asset Availability Info --%>
                    <div class="js-asset-availability js-block-this">
                        <i:inline key="yukon.common.loading"/>
                    </div>
                </tags:sectionContainer2>
            </cti:checkRolesAndProperties>
        </div>
        
    </div>

    <c:set var="baseUrl" value="/dr/controlArea/detail?controlAreaId=${controlAreaId}"/>
    <%@ include file="../program/programList.jspf" %>
    
    <%--
        Control Area Actions section each action has a simpleDialogLink that
        pops open a dialog for the action.  The available actions are based
        on the dynamically updated SHOW_ACTION value
    --%>
    <!-- Page Dropdown Actions -->
    <div id="page-actions" class="dn">
    
        <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${controlArea}">
            <div data-start-action="on" data-pao-id="${controlAreaId}">
                <li>
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.startMultiplePrograms.title" 
                           dialogId="drDialog" actionUrl="${startControlAreaUrl}" 
                           icon="icon-control-play-blue" labelKey=".controlAreaDetail.actions.start"/>
                </li>
            </div>
            <div data-start-action="off" class="dn" data-pao-id="${controlAreaId}">
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true">
                    <cti:msg2 key=".controlAreaDetail.actions.start"/>
                </cm:dropdownOption>
            </div>
            <div data-stop-action="on" data-pao-id="${controlAreaId}">
                <li>
                    <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopMultiplePrograms.title" 
                               dialogId="drDialog" actionUrl="${stopControlAreaUrl}"
                               icon="icon-control-stop-blue" labelKey=".controlAreaDetail.actions.stop"/>
                </li>
            </div>
            <div data-stop-action="off" class="dn" data-pao-id="${controlAreaId}">
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true">
                    <cti:msg2 key=".controlAreaDetail.actions.stop"/>
                </cm:dropdownOption>
            </div>
            <li class="divider"></li>
            <div data-change-triggers-action="on" data-pao-id="${controlAreaId}">
                <c:choose>
                    <c:when test="${controlArea.hasThresholdTrigger}">
                        <cti:url var="sendTriggerChangeUrl" value="/dr/controlArea/getTriggerChangeValues">
                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        </cti:url>
                        <li>
                            <tags:simpleDialogLink titleKey=".getChangeTriggerValues.title" 
                                dialogId="drDialog" actionUrl="${sendTriggerChangeUrl}" icon="icon-wrench"
                                labelKey=".actions.triggersChange"/>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                        <cm:dropdownOption icon="icon-wrench" disabled="true" title="${triggersChangeDisabled}">
                            <cti:msg2 key=".actions.triggersChange" />
                        </cm:dropdownOption>
                    </c:otherwise>
                </c:choose>
            </div>
            <div data-change-triggers-action="off" class="dn" data-pao-id="${controlAreaId}">
                <cm:dropdownOption icon="icon-wrench" disabled="true">
                    <cti:msg2 key=".actions.triggersChange" />
                </cm:dropdownOption>
            </div>
            <div data-change-time-action="on" data-pao-id="${controlAreaId}">
                <cti:url var="sendChangeTimeWindowUrl" value="/dr/controlArea/getChangeTimeWindowValues">
                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                </cti:url>
                <li>
                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title" 
                        dialogId="drDialog" actionUrl="${sendChangeTimeWindowUrl}" icon="icon-time"
                        labelKey=".actions.dailyTimeChange"/>
                </li>
            </div>
            <div data-change-time-action="off" class="dn" data-pao-id="${controlAreaId}">
                <cm:dropdownOption icon="icon-time" disabled="true">
                    <cti:msg2 key=".actions.dailyTimeChange" />
                </cm:dropdownOption>
            </div>
            <c:choose>
				<c:when test="${enableDisableProgramsAllowed}">
					<div data-enable-control-area-action="on" data-pao-id="${controlAreaId}">
            			<cti:url var="sendEnableUrl" value="/dr/controlArea/sendEnableConfirm">
		            		<cti:param name="controlAreaId" value="${controlAreaId}"/>
			                <cti:param name="isEnabled" value="true"/>
			             </cti:url>
			             <li>
        		     	<tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendEnableConfirm.title" 
                			dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
            	           	labelKey=".actions.enable"/>
	            	    </li>
			 		</div>
		            <div data-disable-control-area-action="on" data-pao-id="${controlAreaId}">
        		        <cti:url var="sendDisableUrl" value="/dr/controlArea/sendEnableConfirm">
                		    <cti:param name="controlAreaId" value="${controlAreaId}"/>
		                    <cti:param name="isEnabled" value="false"/>
		                </cti:url>
		                <li>
        		            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendDisableConfirm.title" 
                		        dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                        		labelKey=".actions.disable"/>
		                </li>
		            </div>
            		<div data-disable-control-area-action="off" class="dn" data-pao-id="${controlAreaId}">
		                <cm:dropdownOption icon="icon-delete" disabled="true">
        	        	    <cti:msg2 key=".actions.disable" />
            		    </cm:dropdownOption>
		            </div>
        	    </c:when>
            	<c:otherwise>
		            <div data-enable-control-area-action="off" class="dn" data-pao-id="${controlAreaId}">
        		        <cm:dropdownOption icon="icon-accept" disabled="true">
        	        	    <cti:msg2 key=".actions.enable" />
		                </cm:dropdownOption>
		            </div>
		            <div data-disable-control-area-action="off" class="dn" data-pao-id="${controlAreaId}">
        		        <cm:dropdownOption icon="icon-delete" disabled="true">
                		    <cti:msg2 key=".actions.disable" />
		                </cm:dropdownOption>
		            </div>
	            </c:otherwise>
            </c:choose>
            
            <c:choose>
				<c:when test="${changeGearAllowed}">
		            <div data-change-gears-action="on" data-pao-id="${controlAreaId}">
        		        <cti:url var="changeGearsUrl" value="/dr/program/changeGearMultiplePopup">
                		    <cti:param name="controlAreaId" value="${controlAreaId}"/>
		                </cti:url>
	                <li>
    	                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                            dialogId="drDialog" actionUrl="${changeGearsUrl}" icon="icon-cog-edit"
                            labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
        	        </li>
            		</div>
		            <div class="dn" data-change-gears-action="off" data-pao-id="${controlAreaId}">
        		        <cm:dropdownOption icon="icon-cog-edit" disabled="true">
                		    <cti:msg2 key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
		                </cm:dropdownOption>
		            </div>
        	    </c:when>
				<c:otherwise>
					 <div class="dn" data-change-gears-action="off" data-pao-id="${controlAreaId}">
                		<cm:dropdownOption icon="icon-cog-edit" disabled="true">
		                    <cti:msg2 key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
		                </cm:dropdownOption>
		            </div>
        	    </c:otherwise>
            </c:choose>
            <li class="divider"></li>
            <c:choose>
				<c:when test="${enableDisableProgramsAllowed}">
		            <div data-enable-programs-action="on" data-pao-id="${controlAreaId}">
		                <cti:url var="sendEnableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
		                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
		                    <cti:param name="enable" value="true"/>
		                </cti:url>
		                <li>
        		            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableProgramsConfirm.title" 
                		        dialogId="drDialog" actionUrl="${sendEnableProgramsUrl}" icon="icon-accept"
                        		labelKey=".actions.enablePrograms"/>
		                </li>
		            </div>
        		    <div data-enable-programs-action="off" class="dn" data-pao-id="${controlAreaId}">
                		<cm:dropdownOption icon="icon-accept" disabled="true">
		                    <cti:msg2 key=".actions.enablePrograms" />
		                </cm:dropdownOption>
        		    </div>
		            <div data-disable-programs-action="on" data-pao-id="${controlAreaId}">
        		        <cti:url var="sendDisableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
	    	                <cti:param name="controlAreaId" value="${controlAreaId}"/>
    	    	            <cti:param name="enable" value="false"/>
		                </cti:url>
        	        <li>
            	        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableProgramsConfirm.title" 
                	        dialogId="drDialog" actionUrl="${sendDisableProgramsUrl}" icon="icon-delete"
                    	    labelKey=".actions.disablePrograms"/>
	                </li>
    		        </div>
            		<div data-disable-programs-action="off" class="dn" data-pao-id="${controlAreaId}">
		                <cm:dropdownOption icon="icon-delete" disabled="true">
		                    <cti:msg2 key=".actions.disablePrograms" />
		                </cm:dropdownOption>
		            </div>
        	    </c:when>
				<c:otherwise>
					<div data-enable-programs-action="off" class="dn" data-pao-id="${controlAreaId}">
		                <cm:dropdownOption icon="icon-accept" disabled="true">
		                    <cti:msg2 key=".actions.enablePrograms" />
		                </cm:dropdownOption>
		            </div>
        		    <div data-disable-programs-action="off" class="dn" data-pao-id="${controlAreaId}">
                		<cm:dropdownOption icon="icon-delete" disabled="true">
	    	                <cti:msg2 key=".actions.disablePrograms" />
		                </cm:dropdownOption>
            		</div>
	            </c:otherwise>
            </c:choose>
            
            <div data-reset-peak-action="on" data-pao-id="${controlAreaId}">
                <c:choose>
                    <c:when test="${!empty controlArea.triggers}">
                        <cti:url var="sendResetPeakUrl" value="/dr/controlArea/sendResetPeakConfirm">
                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        </cti:url>
                        <li>
                            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title" 
                                dialogId="drDialog" actionUrl="${sendResetPeakUrl}" icon="icon-control-repeat-blue"
                                labelKey=".actions.resetPeak"/>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li>
                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                            <cm:dropdownOption icon="icon-control-repeat-blue" disabled="true" title="${resetDisabled}">
                                <cti:msg2 key=".actions.resetPeak" />
                            </cm:dropdownOption>
                        </li>
                    </c:otherwise>
                </c:choose>
            </div>
            <div data-reset-peak-action="off" class="dn" data-pao-id="${controlAreaId}">
                <cm:dropdownOption icon="icon-control-repeat-blue" disabled="true">
                    <cti:msg2 key=".actions.resetPeak" />
                </cm:dropdownOption>
            </div>
        </cti:checkPaoAuthorization>
    
        <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${controlArea}" invert="true">
            <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
            <cti:msg var="noAssignedPrograms" key="yukon.web.modules.dr.controlAreaDetail.noControl"/>
            <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${noAssignedPrograms}">
                <cti:msg2 key=".controlAreaDetail.actions.start"/>
            </cm:dropdownOption>
            <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${noAssignedPrograms}">
                <cti:msg2 key=".controlAreaDetail.actions.stop"/>
            </cm:dropdownOption>
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-wrench" disabled="true" title="${noAssignedPrograms}">
                <cti:msg2 key=".actions.triggersChange" />
            </cm:dropdownOption>
            <cm:dropdownOption icon="icon-time" disabled="true" title="${noAssignedPrograms}">
                <cti:msg2 key=".actions.dailyTimeChange" />
            </cm:dropdownOption>
            <cm:dropdownOption icon="icon-delete" disabled="true" title="${noAssignedPrograms}">
                <cti:msg2 key=".actions.disable" />
            </cm:dropdownOption>
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-control-repeat-blue" disabled="true" title="${noAssignedPrograms}">
                <cti:msg2 key=".actions.resetPeak" />
            </cm:dropdownOption>
        </cti:checkPaoAuthorization>
    </div>
    <cti:dataUpdaterCallback function="yukon.dr.dataUpdater.showAction.updateControlAreaMenu(${controlAreaId},${changeGearAllowed},${enableDisableProgramsAllowed})" 
        initialize="true" state="DR_CONTROLAREA/${controlAreaId}/SHOW_ACTION"/>
        
    <cti:includeScript link="/resources/js/pages/yukon.dr.estimated.load.js"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_PIE"/>
    <!--[if lte IE 8]><cti:includeScript link="JQUERY_EXCANVAS"/><![endif]-->
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.asset.details.js"/>
    
</cti:standardPage>