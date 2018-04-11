<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    
<cti:msgScope paths="modules.tools.configs.summary">
    
    <span class="fwn"><i:inline key=".filteredResults"/></span>
    <span class="badge">${results.hitCount}</span>&nbsp;<i:inline key=".devices"/>
    <c:choose>
        <c:when test="${results.hitCount > 0}">
            <span class="js-cog-menu">
                <cm:dropdown icon="icon-cog">
                    <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                        <cm:dropdownOption key=".assignConfig" classes="js-collection-action" icon="icon-page-edit" data-collection-action="ASSIGN"/> 
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                        <cm:dropdownOption key=".sendConfig" classes="js-collection-action" icon="icon-ping" data-collection-action="SEND"/> 
                        <cm:dropdownOption key=".readConfig" classes="js-collection-action" icon="icon-read" data-collection-action="READ"/> 
                    </cti:checkRolesAndProperties>
                    <cm:dropdownOption key=".verifyConfig" classes="js-collection-action" icon="icon-accept" data-collection-action="VERIFY"/> 
                    <cm:dropdownOption key=".download" classes="js-config-download" icon="icon-page-white-excel"/>
                </cm:dropdown>
            </span>
            
            <cti:url var="dataUrl" value="/deviceConfiguration/summary/view">
                <c:forEach var="config" items="${filter.configurationIds}">
                    <cti:param name="configurationIds" value="${config}"/>
                </c:forEach>
                <c:forEach var="subGroup" items="${filter.groups}">
                    <cti:param name="deviceSubGroups" value="${subGroup.fullName}"/>
                </c:forEach>
                <c:forEach var="action" items="${filter.actions}">
                    <cti:param name="actions" value="${action}"/>
                </c:forEach>
                <c:forEach var="sync" items="${filter.inSync}">
                    <cti:param name="inSync" value="${sync}"/>
                </c:forEach>
                <c:forEach var="status" items="${filter.statuses}">
                    <cti:param name="statuses" value="${status}"/>
                </c:forEach>
            </cti:url>
            
            <table class="compact-results-table row-highlighting has-actions">
                <thead>
                    <tr>
                        <tags:sort column="${deviceName}" />
                        <tags:sort column="${type}" />  
                        <tags:sort column="${deviceConfiguration}" />                 
                        <tags:sort column="${lastAction}" />                  
                        <tags:sort column="${lastActionStatus}" />                  
                        <tags:sort column="${inSync}" />                  
                        <tags:sort column="${lastActionStart}" />                 
                        <tags:sort column="${lastActionEnd}" />                  
                        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="detail" items="${results.resultList}">
                        <c:set var="deviceId" value="${detail.device.paoIdentifier.paoId}"/>
                        <tr>
                            <td><cti:paoDetailUrl yukonPao="${detail.device.paoIdentifier}" newTab="true">${fn:escapeXml(detail.device.name)}</cti:paoDetailUrl></td>
                            <td class="wsnw">${detail.device.paoIdentifier.paoType.paoTypeName}</td>
                            <cti:url var="configUrl" value="/deviceConfiguration/config/view?configId=${detail.deviceConfig.configurationId}"/>
                            <td><a href="${configUrl}">${detail.deviceConfig.name}</a></td>
                            <c:if test="${detail.action != null}">
                                <td><i:inline key=".actionType.${detail.action}"/></td>
                            </c:if>
                            <c:if test="${detail.action == null}">
                                <td></td>
                            </c:if>
                            <td>
                                 <c:choose>
                                    <c:when test="${detail.status == 'FAILURE'}">
                                        <div class="dn js-failure-${deviceId}" data-dialog data-cancel-omit="true" data-title="<cti:msg2 key=".failure"/>" 
                                        data-width="600" data-url="<cti:url value="/deviceConfiguration/summary/${detail.errorCode}/displayError"/>"></div>
                                        <a href="javascript:void(0);" data-popup=".js-failure-${deviceId}"><i:inline key=".statusType.${detail.status}"/></a>
                                    </c:when>
                                    <c:otherwise>
                                        <i:inline key=".statusType.${detail.status}"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${detail.inSync == 'OUT_OF_SYNC'  && detail.status != 'IN_PROGRESS'}">
                                        <div class="dn js-outofsync-${deviceId}" data-dialog data-cancel-omit="true" data-title="<cti:msg2 key=".outOfSync"/>" 
                                        data-width="600" data-url="<cti:url value="/deviceConfiguration/summary/${deviceId}/outOfSync"/>"></div>
                                        <a href="javascript:void(0);" data-popup=".js-outofsync-${deviceId}" ><i:inline key=".syncType.${detail.inSync}"/></a>
                                    </c:when>
                                    <c:otherwise>
                                        <i:inline key=".syncType.${detail.inSync}"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><cti:formatDate type="BOTH" value="${detail.actionStart}" nullText="N/A"/></td>
                            <td><cti:formatDate type="BOTH" value="${detail.actionEnd}" nullText="N/A"/></td>
                            <td>
                                <cm:dropdown icon="icon-cog">
                                    <cti:msg2 var="historyTitle" key=".viewHistory.title" argument="${detail.device.name}"/>
                                    <div class="dn js-view-history-${deviceId}" data-dialog data-cancel-omit="true" data-title="${historyTitle}" 
                                    data-width="600" data-load-event="yukon:config:viewHistory"
                                    data-url="<cti:url value="/deviceConfiguration/summary/${deviceId}/viewHistory"/>"></div>
                                    <cm:dropdownOption key=".viewHistory" data-popup=".js-view-history-${deviceId}" icon="icon-application-view-columns"/>
                                    <cti:checkRolesAndProperties value="SEND_READ_CONFIG">                    
                                        <c:set var="sendClass" value="${detail.displaySend ? 'js-device-action' : ''}"/> 
                                        <cm:dropdownOption key=".send" icon="icon-ping" classes="${sendClass}" data-device-id="${deviceId}" 
                                            data-action="sendConfig" disabled="${!detail.displaySend}"/>
                                        <c:set var="readClass" value="${detail.displayRead ? 'js-device-action' : ''}"/>
                                        <cm:dropdownOption key=".read" icon="icon-read" classes="${readClass}" data-device-id="${deviceId}" 
                                            data-action="readConfig" disabled="${!detail.displayRead}"/>
                                    </cti:checkRolesAndProperties>
                                    <c:set var="verifyClass" value="${detail.displayVerify ? 'js-device-action' : ''}"/>
                                    <cm:dropdownOption key=".verify" icon="icon-accept" classes="${verifyClass}" data-device-id="${deviceId}" 
                                        data-action="verifyConfig" disabled="${!detail.displayVerify}"/>
                                </cm:dropdown>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <tags:pagingResultsControls result="${results}" adjustPageCount="true" thousands="true"/>
        </c:when>
        <c:otherwise>
            <br/><br/><span class="empty-list"><i:inline key="yukon.common.search.noResultsFound" /></span>
        </c:otherwise>
    </c:choose>
    
</cti:msgScope>
