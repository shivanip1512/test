<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="loadGroupDetail">
    <cti:includeScript link="YUKON_FLOTCHARTS"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_PIE"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_SELECTION"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_AXIS_LABEL"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_RESIZE"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_TIME"/>
    <!--[if lte IE 8]><cti:includeScript link="JQUERY_EXCANVAS"/><![endif]-->
    <cti:includeScript link="/JavaScript/yukon.ui.progressbar.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/flotChart.css"/>
    
    <tags:simpleDialog id="drDialog"/>

    <c:set var="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>

    <input id="assetId" type="hidden" value="${loadGroupId}"/>
    <cti:includeScript link="/JavaScript/yukon.dr.asset.details.js"/>

    <c:if test="${loadGroup.paoIdentifier.paoType != 'MACRO_GROUP'}">
        <div class="column-12-12">
        <div class="column one">
                <%-- Load Group Info section --%>
                <tags:sectionContainer2 nameKey="heading.info">
                    <tags:nameValueContainer>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.state"/>
                        <cti:checkRolesAndProperties value="LOAD_GROUP_STATE">
                            <tags:nameValue name="${fieldName}">
                                <dr:loadGroupState loadGroupId="${loadGroupId}"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="LOAD_GROUP_LAST_ACTION">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.lastAction"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/LAST_ACTION"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="LOAD_GROUP_CONTROL_STATISTICS">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.controlStatistics"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/CONTROL_STATISTICS"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="LOAD_GROUP_REDUCTION">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.reduction"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/REDUCTION"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                    </tags:nameValueContainer>
                </tags:sectionContainer2>
            </div>

            <div class="column two nogutter">
                <cti:checkRolesAndProperties value="SHOW_ASSET_AVAILABILITY">
                    <%-- Display the Asset Availability Info --%>
                    <tags:sectionContainer2 nameKey="assetAvailability">
                        <div class="f-asset-availability f-block-this">
                            <i:inline key="yukon.common.loading"/>
                        </div>
                    </tags:sectionContainer2>
                </cti:checkRolesAndProperties>
                
                <%--
                    Load Group Actions section each action has a simpleDialogLink that
                    pops open a dialog for the action.  The available actions are based
                    on the dynamically updated SHOW_ACTION value
                --%>
                <!-- Page Dropdown Actions -->
                <div id="page-actions" class="dn">

                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${loadGroup}">
                        <%-- Actions are enabled only if the user has CONTROL_COMMAND for LM objects --%>

                        <tags:dynamicChoose updaterString="DR_LOADGROUP/${loadGroupId}/SHOW_ACTION" suffix="${loadGroupId}">
                            
                            <tags:dynamicChooseOption optionId="unknown">
                                <%-- Actions are disabled when Load Management doesn't know about the Load group --%>
                                <cti:msg var="loadGroupUnknown" key="yukon.web.modules.dr.loadGroupDetail.unknown"/>
                                <li>
                                    <a class="clearfix" title="${programUnknown}">
                                        <cti:icon icon="icon-control-play-blue" classes="disabled"/>
                                        <span class="dib disabled"><cti:msg2 key=".actions.sendShed"/></span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${programUnknown}">
                                        <cti:icon icon="icon-control-stop-blue" classes="disabled"/>
                                        <span class="dib disabled"><cti:msg2 key=".actions.sendRestore"/></span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${programUnknown}">
                                        <cti:icon icon="icon-delete" classes="disabled"/>
                                        <span class="dib disabled"><cti:msg2 key=".actions.disable"/></span>
                                    </a>
                                </li>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="enabled">
                                <%-- Actions shown when the Load Group is enabled --%>
                                <cti:url var="sendShedUrl" value="/dr/loadGroup/sendShedConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendShedConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendShedUrl}" icon="icon-control-play-blue"
                                        labelKey="yukon.web.modules.dr.loadGroupDetail.actions.sendShed"/>
                                </li>
                                <cti:url var="sendRestoreUrl" value="/dr/loadGroup/sendRestoreConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendRestoreUrl}" icon="icon-control-stop-blue"
                                        labelKey="yukon.web.modules.dr.loadGroupDetail.actions.sendRestore"/>
                                </li>
                                <cti:url var="sendDisableUrl" value="/dr/loadGroup/sendEnableConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendDisableConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                                        labelKey="yukon.web.modules.dr.loadGroupDetail.actions.disable"/>
                                </li>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="disabled">
                                <%-- Actions shown when the Load Group is disabled --%>
                                <cti:url var="sendEnableUrl" value="/dr/loadGroup/sendEnableConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendEnableConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
                                        labelKey="yukon.web.modules.dr.loadGroupDetail.actions.enable"/>
                                </li>
                            </tags:dynamicChooseOption>
                        </tags:dynamicChoose>
                    </cti:checkPaoAuthorization>
                    
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${loadGroup}" invert="true">
                        <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
                        <cti:msg var="noLoadGroupControl" key="yukon.web.modules.dr.loadGroupDetail.noControl"/>
                        <li>
                            <a class="clearfix" title="${noLoadGroupControl}">
                                <cti:icon icon="icon-control-play-blue" classes="disabled"/>
                                <span class="dib disabled"><cti:msg2 key=".actions.sendShed"/></span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noLoadGroupControl}">
                                <cti:icon icon="icon-control-stop-blue" classes="disabled"/>
                                <span class="dib disabled"><cti:msg2 key=".actions.sendRestore"/></span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noLoadGroupControl}">
                                <cti:icon icon="icon-delete" classes="disabled"/>
                                <span class="dib disabled"><cti:msg2 key=".actions.disable"/></span>
                            </a>
                        </li>
                    </cti:checkPaoAuthorization>
                </div>
            </div>
        </c:if>

        <%-- Child Load Groups for the Macro Load Group --%>
        <c:if test="${loadGroup.paoIdentifier.paoType == 'MACRO_GROUP'}">
                <div class="widget-container">
                    <i:inline key=".note.macroLoadGroup"/>
                    <c:set var="baseUrl" value="/dr/loadGroup/detail"/>
                    <%@ include file="../loadGroup/loadGroupList.jspf" %>
                </div>
        </c:if>

        <%-- Parent Programs and macro load groups --%>
        <div class="column-12-12">
            <div class="column one">
                    <tags:boxContainer2 nameKey="parents.programs">
                        <c:if test="${empty parentPrograms}">
                            <span class="empty-list"><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.noPrograms"/></span>
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
                                    <cti:msg var="noParentPermission" key="yukon.web.modules.dr.loadGroupDetail.parents.noPermission"/>
                                    <span title="${noParentPermission}">
                                        ${fn:escapeXml(parentProgram.name)}
                                    </span>
                                </cti:checkPaoAuthorization>
                            </c:forEach>
                        </c:if>
                    </tags:boxContainer2>
            </div>
            <div class="column two nogutter">
                    <tags:boxContainer2 nameKey="parents.macroLoadGroups">
                        <c:if test="${empty parentLoadGroups}">
                            <span class="empty-list"><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.noMacroLoadGroups"/></span>
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
                                    <cti:msg var="noParentPermission" key="yukon.web.modules.dr.loadGroupDetail.parents.noPermission"/>
                                    <span title="${noParentPermission}">
                                        ${fn:escapeXml(parentLoadGroup.name)}
                                    </span>
                                </cti:checkPaoAuthorization>
                            </c:forEach>
                        </c:if>
                    </tags:boxContainer2>
            </div>
        </div>
</cti:standardPage>