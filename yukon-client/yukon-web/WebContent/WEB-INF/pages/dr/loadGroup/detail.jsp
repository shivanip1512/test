<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:standardPage module="dr" page="loadGroupDetail">
    
    <tags:simpleDialog id="drDialog"/>

    <c:set var="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>

    <input id="assetId" type="hidden" value="${loadGroupId}"/>

    <c:if test="${loadGroup.paoIdentifier.paoType != 'MACRO_GROUP'}">
        <div class="column-12-12 clearfix">
            <div class="column one">
                <%-- Load Group Info section --%>
                <tags:sectionContainer2 nameKey="heading.info">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".info.state">
                            <dr:loadGroupState loadGroupId="${loadGroupId}"/>
                        </tags:nameValue2>
                    
                        <tags:nameValue2 nameKey=".info.lastAction">
                            <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/LAST_ACTION"/>
                        </tags:nameValue2>
                    
                        <tags:nameValue2 nameKey=".info.controlStatistics">
                            <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/CONTROL_STATISTICS"/>
                        </tags:nameValue2>
                       
                        <cti:checkRolesAndProperties value="DR_VIEW_REDUCTION">
                            <tags:nameValue2 nameKey=".info.reduction">
                                <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/REDUCTION"/>
                            </tags:nameValue2>
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
                    Load Group Actions section each action has a simpleDialogLink that
                    pops open a dialog for the action.  The available actions are based
                    on the dynamically updated SHOW_ACTION value.
                --%>
                <!-- Page Dropdown Actions -->
                <c:if test="${loadGroup.paoIdentifier.paoType != 'LM_GROUP_NEST'}">
                    <div id="page-actions" class="dn">
    
                        <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${loadGroup}">
                            <%-- Actions are enabled only if the user has CONTROL_COMMAND for LM objects --%>
                            <tags:dynamicChoose updaterString="DR_LOADGROUP/${loadGroupId}/SHOW_ACTION" suffix="${loadGroupId}">
                                <tags:dynamicChooseOption optionId="unknown">
                                    <%-- Actions are disabled when Load Management doesn't know about the Load group --%>
                                    <cti:msg2 var="loadGroupUnknown" key=".unknown"/>
                                    <c:if test="${allowShed}">
                                        <cm:dropdownOption icon="icon-control-play-blue" key=".actions.sendShed" disabled="true" title="${loadGroupUnknown}"/>
                                    </c:if>
                                    <cm:dropdownOption icon="icon-control-stop-blue" key=".actions.sendRestore" disabled="true" title="${loadGroupUnknown}"/>
                                    <cm:dropdownOption icon="icon-delete" key=".actions.disable" disabled="true" title="${loadGroupUnknown}"/>
                                </tags:dynamicChooseOption>
                                
                                <cti:msgScope paths=",modules.dr.loadGroup">
                                <cti:msg2 var="noEnableDisable" key=".actions.noEnableDisable"/> 
                                    <tags:dynamicChooseOption optionId="enabled">
                                        <%-- Actions shown when the Load Group is enabled --%>
                                        <c:if test="${allowShed}">
                                            <cti:url var="sendShedUrl" value="/dr/loadGroup/sendShedConfirm">
                                                <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                            </cti:url>
                                            <li><tags:simpleDialogLink titleKey=".sendShedConfirm.title" 
                                                    dialogId="drDialog" actionUrl="${sendShedUrl}" 
                                                    icon="icon-control-play-blue"
                                                    labelKey=".actions.sendShed"/></li>
                                        </c:if>
                                        <cti:url var="sendRestoreUrl" value="/dr/loadGroup/sendRestoreConfirm">
                                            <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                        </cti:url>
                                        <li><tags:simpleDialogLink titleKey=".sendRestoreConfirm.title" 
                                                dialogId="drDialog" actionUrl="${sendRestoreUrl}" 
                                                icon="icon-control-stop-blue"
                                                labelKey=".actions.sendRestore"/></li>
                                                
                                         <c:choose>
    										<c:when test="${enableDisableProgramsAllowed}">       
                                        		<cti:url var="sendDisableUrl" value="/dr/loadGroup/sendEnableConfirm">
                                            		<cti:param name="loadGroupId" value="${loadGroupId}"/>
                                            		<cti:param name="isEnabled" value="false"/>
                                        		</cti:url>
                                        		<li><tags:simpleDialogLink titleKey=".sendDisableConfirm.title" 
                                                	dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                                                	labelKey=".actions.disable"/></li>
                                           	</c:when>
                                           	<c:otherwise>
                                            	<cm:dropdownOption icon="icon-delete" key=".actions.disable" disabled="true" title="${noEnableDisable}"/>
                                           	</c:otherwise>
                                          </c:choose>     
                                                
                                    </tags:dynamicChooseOption>
        
                                    <tags:dynamicChooseOption optionId="disabled">
                                        <%-- Actions shown when the Load Group is disabled --%>
                                        <c:choose>
    										<c:when test="${enableDisableProgramsAllowed}">  
                                        		<cti:url var="sendEnableUrl" value="/dr/loadGroup/sendEnableConfirm">
                                            		<cti:param name="loadGroupId" value="${loadGroupId}"/>
                                            		<cti:param name="isEnabled" value="true"/>
                                        		</cti:url>
                                        		<li><tags:simpleDialogLink titleKey=".sendEnableConfirm.title" 
                                                		dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
                                                		labelKey=".actions.enable"/></li>
                                            </c:when>
                                            <c:otherwise>
                                            	<cm:dropdownOption icon="icon-accept" key=".actions.enable" disabled="true" title="${noEnableDisable}"/>
                                            </c:otherwise>
                                         </c:choose>     
                                    </tags:dynamicChooseOption>
                                </cti:msgScope>
                            </tags:dynamicChoose>
                        </cti:checkPaoAuthorization>
                        
                        <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${loadGroup}" invert="true">
                            <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
                            <cti:msg2 var="noLoadGroupControl" key=".noControl"/>
                            <c:if test="${allowShed}">
                                <cm:dropdownOption icon="icon-control-play-blue" key=".actions.sendShed" disabled="true" title="${noLoadGroupControl}"/>
                            </c:if>
                            <cm:dropdownOption icon="icon-control-stop-blue" key=".actions.sendRestore" disabled="true" title="${noLoadGroupControl}"/>
                            <cm:dropdownOption icon="icon-delete" key=".actions.disable" disabled="true" title="${noLoadGroupControl}"/>
                        </cti:checkPaoAuthorization>
                    </div>
                </c:if>
            </div>
        </div>
    </c:if>

    <%-- Child Load Groups for the Macro Load Group --%>
    <c:if test="${loadGroup.paoIdentifier.paoType == 'MACRO_GROUP'}">
        <i:inline key=".note.macroLoadGroup"/>
        <c:set var="baseUrl" value="/dr/loadGroup/detail"/>
        <%@ include file="../loadGroup/loadGroupList.jspf" %>
    </c:if>

    <%-- Parent Programs and macro load groups --%>
    <div class="column-12-12 clearfix">
    
        <div class="column one">
            <tags:sectionContainer2 nameKey="parents.programs">
                <c:if test="${empty parentPrograms}">
                    <span class="empty-list"><i:inline key=".parents.noPrograms"/></span>
                </c:if>
                <c:if test="${!empty parentPrograms}">
                    <c:forEach var="parentProgram" items="${parentPrograms}">
                        <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentProgram}">
                            <c:url var="programURL" value="/dr/program/detail">
                                <c:param name="programId" value="${parentProgram.paoIdentifier.paoId}"/>
                            </c:url>
                            <a href="${programURL}">${fn:escapeXml(parentProgram.name)}</a><br>
                        </cti:checkPaoAuthorization>
                        <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentProgram}" invert="true">
                            <span title="<cti:msg2 key=".parents.noPermission"/>">${fn:escapeXml(parentProgram.name)}</span>
                        </cti:checkPaoAuthorization>
                    </c:forEach>
                </c:if>
            </tags:sectionContainer2>
        </div>
        
        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="parents.macroLoadGroups">
                <c:if test="${empty parentLoadGroups}">
                    <span class="empty-list"><i:inline key=".parents.noMacroLoadGroups"/></span>
                </c:if>
                <c:if test="${!empty parentLoadGroups}">
                    <c:forEach var="parentLoadGroup" items="${parentLoadGroups}">
                        <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentLoadGroup}">
                            <c:url var="loadGroupURL" value="/dr/loadGroup/detail">
                                <c:param name="loadGroupId" value="${parentLoadGroup.paoIdentifier.paoId}"/>
                            </c:url>
                            <a href="${loadGroupURL}">${fn:escapeXml(parentLoadGroup.name)}</a><br>
                        </cti:checkPaoAuthorization>
                        <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentLoadGroup}" invert="true">
                            <span title="<cti:msg2 key=".parents.noPermission"/>">${fn:escapeXml(parentLoadGroup.name)}</span>
                        </cti:checkPaoAuthorization>
                    </c:forEach>
                </c:if>
            </tags:sectionContainer2>
        </div>
        
    </div>

    <cti:includeScript link="JQUERY_FLOTCHARTS_PIE"/>
    <!--[if lte IE 8]><cti:includeScript link="JQUERY_EXCANVAS"/><![endif]-->
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.asset.details.js"/>
    
</cti:standardPage>