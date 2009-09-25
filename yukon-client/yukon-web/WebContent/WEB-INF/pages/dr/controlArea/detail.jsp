<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.controlAreaDetail.pageTitle" argument="${controlArea.name}"/>
<cti:standardPage module="dr" page="controlAreaDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|controlareas"/>

    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <cti:includeScript link="/JavaScript/demandResponseAction.js"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.controlAreaDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/controlArea/list">
        	<cti:msg key="yukon.web.modules.dr.controlAreaDetail.breadcrumb.controlAreas"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.breadcrumb.controlArea"
                htmlEscape="true" argument="${controlArea.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.controlAreaDetail.controlArea"
        htmlEscape="true" argument="${controlArea.name}"/></h2>
    <br>

    <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
                    <tags:nameValueContainer>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.state"/>
                        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STATE"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.priority"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/PRIORITY"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.startStop"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/START"/>
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.separator"/>
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STOP"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.loadCapacity"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/LOAD_CAPACITY"/>
                        </tags:nameValue>

	                    <c:if test="${!empty controlArea.triggers}">
	                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.triggers"/>
	                        <tags:nameValue name="${fieldName}">
                            <table id="controlAreaList" class="resultsTable activeResultsTable">
                                <tr>
                                    <th><cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.valueThreshold"/></th>
                                    <th><cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.peakProjection"/></th>
                                    <th><cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.atku"/></th>
                                </tr>
                                <c:forEach var="trigger" items="${controlArea.triggers}">
                                    <c:set var="triggerNumber" value="${trigger.triggerNumber}"/>                               
                                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                        <td>
                                           <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/VALUE_THRESHOLD"/>
                                        </td>
                                        <td>
                                            <c:if test="${trigger.thresholdType}">
                                                <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/PEAK_PROJECTION"/>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${trigger.thresholdType}">
                                               <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/ATKU"/>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
	                        </tags:nameValue>
	                    </c:if>
                    </tags:nameValueContainer>
                    <c:if test="${empty controlArea.triggers}">
                        <br/>
                        <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.noTriggers"/>
                    </c:if>
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
                    <span id="actionSpan_${loadGroupId}">
                		<cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.start"/><br>
                        <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.stop"/><br>

                        <c:if test="${!empty controlArea.triggers}">
                            <cti:url var="sendTriggerChangeUrl" value="/spring/dr/controlArea/getTriggerChangeValues">
                                <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            </cti:url>
                            <a href="javascript:void(0)"
                                onclick="openSimpleDialog('drDialog', '${sendTriggerChangeUrl}', '<cti:msg key="yukon.web.modules.dr.controlArea.getChangeTriggerValues.title"/>')">
                                <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/><br>
                            </a>
                        </c:if>
                        
                        <cti:url var="sendChangeTimeWindowUrl" value="/spring/dr/controlArea/getChangeTimeWindowValues">
                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        </cti:url>
                        <a href="javascript:void(0)"
                            onclick="openSimpleDialog('drDialog', '${sendChangeTimeWindowUrl}', '<cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title"/>')">
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/><br>
                        </a>
                        
                        <cti:url var="sendEnableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            <cti:param name="isEnabled" value="true"/>
                        </cti:url>
                        <a id="enableLink_${controlAreaId}" href="javascript:void(0)"
                            onclick="openSimpleDialog('drDialog', '${sendEnableUrl}', '<cti:msg key="yukon.web.modules.dr.controlArea.sendEnableConfirm.title"/>')">
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.enable"/>
                        </a>
                        <cti:url var="sendDisableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            <cti:param name="isEnabled" value="false"/>
                        </cti:url>
                        <a id="disableLink_${controlAreaId}" href="javascript:void(0)"
                            onclick="openSimpleDialog('drDialog', '${sendDisableUrl}', '<cti:msg key="yukon.web.modules.dr.controlArea.sendDisableConfirm.title"/>')">
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.disable"/>
                        </a><br>
                        <cti:dataUpdaterCallback function="updateEnabled('${controlAreaId}')" initialize="true" state="DR_CONTROLAREA/${controlAreaId}/ENABLED" />
                        
                        <c:if test="${!empty controlArea.triggers}">
                            <cti:url var="sendResetPeakUrl" value="/spring/dr/controlArea/sendResetPeakConfirm">
                                <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            </cti:url>
                            <a href="javascript:void(0)"
                                onclick="openSimpleDialog('drDialog', '${sendResetPeakUrl}', '<cti:msg key="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title"/>')">
                                <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                            </a>
                        </c:if>
                    </span>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
        <c:set var="baseUrl" value="/spring/dr/controlArea/detail"/>
        <%@ include file="../program/programList.jspf" %>
    </tags:abstractContainer>

</cti:standardPage>
