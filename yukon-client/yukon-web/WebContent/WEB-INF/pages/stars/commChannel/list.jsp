<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="commChannel">
    <!-- Actions dropdown -->
    <div id="page-actions" class="dn">
         <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE"> 
            <cti:msg2 key="yukon.web.modules.operator.commChannel.create" var="popupTitle"/>
            <cti:url var="createUrl" value="/stars/device/commChannel/create" />
            <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" data-popup="#js-create-comm-channel-popup"/>
         </cti:checkRolesAndProperties> 
    </div>

    <c:choose>
        <c:when test="${not empty commChannelList}">
            <cti:url var="listUrl" value = "/stars/device/commChannel/list"/>
            <div data-url="${listUrl}" data-static>
                <table class="compact-results-table row-highlighting">
                    <thead>
                        <tr>
                            <tags:sort column="${name}" />
                            <tags:sort column="${type}" />
                            <tags:sort column="${status}" />
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="commChannel" items="${commChannelList}">
                            <c:set var="cssClass" value="error" />
                            <cti:msg2 var="commChannelStatus" key="yukon.common.disabled"/>
                            <c:if test="${commChannel.enable}">
                                <c:set var="cssClass" value="success" />
                                <cti:msg2 var="commChannelStatus" key="yukon.common.enabled"/>
                            </c:if>
                            <tr>
                                <td width="50%">
                                    <cti:paoDetailUrl paoId="${commChannel.deviceId}">${fn:escapeXml(commChannel.deviceName)}</cti:paoDetailUrl>
                                </td>
                                <td><i:inline key="${commChannel.deviceType}"/></td>
                                <td class="${cssClass}">${commChannelStatus}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:otherwise>
    </c:choose>

    <cti:msg2 var="saveText" key="components.button.save.label"/>
    <div id="js-create-comm-channel-popup" 
         class="dn"
         data-title="${popupTitle}"
         data-dialog
         data-ok-text="${saveText}" 
         data-width="500"
         data-height="400"
         data-event="yukon:assets:commChannel:create" 
         data-url="${createUrl}"/>

    <cti:includeScript link="/resources/js/common/yukon.comm.channel.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.commChannel.js"/>
</cti:standardPage>