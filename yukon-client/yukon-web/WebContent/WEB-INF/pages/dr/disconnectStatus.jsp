<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="disconnectStatus">

    <cti:url var="dataUrl" value="/dr/program/disconnectStatus?programId=${programId}"/>
    <div id="disconnect-status-table" data-url="${dataUrl}" data-static>
        <table class="compact-results-table has-actions">
            <thead>
                <tags:sort column="${device}" />
                <tags:sort column="${status}" />
                <cti:checkRolesAndProperties value="ALLOW_DISCONNECT_CONTROL">
                    <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
                </cti:checkRolesAndProperties>
            </thead>
            <tbody>
                <c:forEach var="disconnectStatus" items="${disconnectStatusList.resultList}">
                    <c:set var="pao" value="${disconnectStatus.key}"/>
                    <c:set var="pointData" value="${disconnectStatus.value}"/>
                    <c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
                    <tr>
                        <td>
                            <cti:paoDetailUrl yukonPao="${pao}" newTab="true">${fn:escapeXml(pao.paoName)}</cti:paoDetailUrl>
                        </td>
                        <td>                    
                            <span class="js-status-${paoId}"><cti:pointValueFormatter format="VALUE" value="${pointData}" /></span>&nbsp;
                            <span class="js-time-${paoId}"><cti:formatDate type="BOTH" value="${pointData.pointDataTimeStamp}"/></span>
                        </td>
                        <cti:checkRolesAndProperties value="ALLOW_DISCONNECT_CONTROL">
                            <td>
                                <cm:dropdown icon="icon-cog">
                                    <cm:dropdownOption key=".manualRestore" classes="js-restore" icon="icon-control-play-blue" 
                                        data-device-id="${paoId}" data-command="restore"/>
                                    <cm:dropdownOption key=".resendShed" classes="js-resend-shed" icon="icon-control-stop-blue" 
                                        data-device-id="${paoId}" data-command="resendShed"/>
                                </cm:dropdown>
                            </td>
                        </cti:checkRolesAndProperties>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${disconnectStatusList}" adjustPageCount="true" thousands="true"/>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.dr.disconnectStatus.js"/>
    
</cti:standardPage>