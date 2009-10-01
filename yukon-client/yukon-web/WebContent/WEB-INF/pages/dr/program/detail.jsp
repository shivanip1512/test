<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.programDetail.pageTitle" argument="${program.name}"/>
<cti:standardPage module="dr" page="programDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|programs"/>

    <cti:includeScript link="/JavaScript/demandResponseAction.js"/>
    
    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

<script type="text/javascript">
    function updateChangeGearEnabled(data) {
    
        var enabledSpan = $('changeGearEnabled');
        var disabledSpan = $('changeGearDisabled');
        
        if(data.state == 'true') {
            // enabled
            $(enabledSpan).show();
            $(disabledSpan).hide();
        } else {
            // disabled
            $(enabledSpan).hide();
            $(disabledSpan).show();
        }
    }
</script>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.programDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/program/list">
            <cti:msg key="yukon.web.modules.dr.programDetail.breadcrumb.programs"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.programDetail.breadcrumb.program"
                htmlEscape="true" argument="${program.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.programDetail.program"
        htmlEscape="true" argument="${program.name}"/></h2>
    <br>

    <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.info"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <tags:nameValueContainer>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.state"/>
                        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
                            <cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.start"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/START" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.stop"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/STOP" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.currentGear"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/CURRENT_GEAR" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.priority"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/PRIORITY" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.reduction"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/REDUCTION" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.loadCapacity"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/LOAD_CAPACITY" type="DR_PROGRAM"/>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.actions"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <span id="actionSpan_${loadGroupId}">
                        <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}">
                            <cti:url var="startProgramUrl" value="/spring/dr/program/startProgramDetails">
                                <cti:param name="programId" value="${programId}"/>
                            </cti:url>
                            <a href="javascript:void(0)" class="simpleLink"
                                onclick="openSimpleDialog('drDialog', '${startProgramUrl}', '<cti:msg key="yukon.web.modules.dr.program.startProgram.title"/>')">
                                <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon"/>
                                <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
                            </a><br>

                            <cti:url var="stopProgramUrl" value="/spring/dr/program/stopProgramDetails">
                                <cti:param name="programId" value="${programId}"/>
                            </cti:url>
                            <a href="javascript:void(0)" class="simpleLink"
                                onclick="openSimpleDialog('drDialog', '${stopProgramUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopProgram.title"/>')">
                                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
                                <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/><br>
                            </a>

                            <span id="changeGearEnabled">
                                <cti:url var="changeGearsUrl" value="/spring/dr/program/getChangeGearValue">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <a href="javascript:void(0)" class="simpleLink"
                                    onclick="openSimpleDialog('drDialog', '${changeGearsUrl}', '<cti:msg key="yukon.web.modules.dr.program.getChangeGearValue.title"/>')">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/><br>
                                </a>
                            </span>
                            
                            <cti:msg var="changeGearsDisabled" key="yukon.web.modules.dr.programDetail.actions.changeGears.disabled"/>
                            <span id="changeGearDisabled" title="${changeGearsDisabled}">
                                <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/><br>
                            </span>
                            <cti:dataUpdaterCallback function="updateChangeGearEnabled" initialize="true" state="DR_PROGRAM/${programId}/MANUAL_ACTIVE" />
                                                   
                            <cti:url var="sendEnableUrl" value="/spring/dr/program/sendEnableConfirm">
                                <cti:param name="programId" value="${programId}"/>
                                <cti:param name="isEnabled" value="true"/>
                            </cti:url>
                            <a id="enableLink_${programId}" href="javascript:void(0)" class="simpleLink"
                                onclick="openSimpleDialog('drDialog', '${sendEnableUrl}', '<cti:msg key="yukon.web.modules.dr.program.sendEnableConfirm.title"/>')">
                                <cti:logo key="yukon.web.modules.dr.programDetail.actions.enableIcon"/>
                                <cti:msg key="yukon.web.modules.dr.programDetail.actions.enable"/><br>
                            </a>
                            <cti:url var="sendDisableUrl" value="/spring/dr/program/sendEnableConfirm">
                                <cti:param name="programId" value="${programId}"/>
                                <cti:param name="isEnabled" value="false"/>
                            </cti:url>
                            <a id="disableLink_${programId}" href="javascript:void(0)" class="simpleLink"
                                onclick="openSimpleDialog('drDialog', '${sendDisableUrl}', '<cti:msg key="yukon.web.modules.dr.program.sendDisableConfirm.title"/>')">
                                <cti:logo key="yukon.web.modules.dr.programDetail.actions.disableIcon"/>
                                <cti:msg key="yukon.web.modules.dr.programDetail.actions.disable"/><br>
                            </a>
                            
                            <cti:dataUpdaterCallback function="updateEnabled('${programId}')" initialize="true" state="DR_PROGRAM/${programId}/ENABLED" />
                        </cti:checkPaoAuthorization>
                        <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}" invert="true">
                            <cti:msg var="noProgramControl" key="yukon.web.modules.dr.programDetail.noControl"/>
                            <div class="subtleGray" title="${noProgramControl}">
                                <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
                                <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
                            </div>
                            <div class="subtleGray" title="${noProgramControl}">
                                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
                                <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/>
                            </div>
                            <div class="subtleGray" title="${noProgramControl}">
                                <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                            </div>
                            <div class="subtleGray" title="${noProgramControl}">
                                <cti:logo key="yukon.web.modules.dr.programDetail.actions.disableIcon.disabled"/>
                                <cti:msg key="yukon.web.modules.dr.programDetail.actions.disable"/>
                            </div>
                        </cti:checkPaoAuthorization>
                    </span>
                </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.loadGroups"/>
    <c:set var="baseUrl" value="/spring/dr/program/detail"/>
    <%@ include file="../loadGroup/loadGroupList.jspf" %>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.parents"/>
    <tags:abstractContainer type="box" title="${boxTitle}">

        <c:if test="${empty parentControlArea}">
            <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.noControlArea"/></p>
        </c:if>
        <c:if test="${!empty parentControlArea}">
            <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.controlArea"/></p>
            <c:url var="controlAreaURL" value="/spring/dr/controlArea/detail">
                <c:param name="controlAreaId" value="${parentControlArea.paoIdentifier.paoId}"/>
            </c:url>
            <a href="${controlAreaURL}"><spring:escapeBody htmlEscape="true">${parentControlArea.name}</spring:escapeBody></a><br>
        </c:if>
        <br>

        <c:if test="${empty parentScenarios}">
            <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.noScenarios"/></p>
        </c:if>
        <c:if test="${!empty parentScenarios}">
            <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.scenarios"/></p>
            <c:forEach var="parentScenario" items="${parentScenarios}">
                <c:url var="scenarioURL" value="/spring/dr/scenario/detail">
                    <c:param name="scenarioId" value="${parentScenario.paoIdentifier.paoId}"/>
                </c:url>
                <a href="${scenarioURL}"><spring:escapeBody htmlEscape="true">${parentScenario.name}</spring:escapeBody></a><br>
            </c:forEach>
        </c:if>

    </tags:abstractContainer>

</cti:standardPage>
