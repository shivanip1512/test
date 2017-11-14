<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>

<cti:standardPage module="tools" page="configs.summary">

    <div class="column-18-6 clearfix">
        <div class="column one">

            <form:form id="filter-form" action="view" method="get" commandName="filter">
                <cti:csrfToken/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".configurations">
                        <form:select multiple="true" path="configurationIds" size="6" style="min-width:200px;">
                            <form:option value="-999">Unassigned</form:option>
                            <form:option value="-998">Assigned to Any</form:option>
                            <c:forEach var="configuration" items="${configurations}">
                                <form:option value="${configuration.configurationId}">${configuration.name}</form:option>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".deviceGroups">
                        <cti:list var="groups">
                            <c:forEach var="subGroup" items="${deviceSubGroups}">
                                <cti:item value="${subGroup}"/>
                            </c:forEach>
                        </cti:list>
                        <tags:deviceGroupPicker inputName="deviceSubGroups" multi="true" inputValue="${groups}"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <br/>
                
                <div class="button-group dib">
                    <c:forEach var="lastAction" items="${lastActionOptions}">
                        <c:set var="checked" value="${false}"/>
                        <c:forEach var="action" items="${filter.actions}">
                            <c:if test="${action eq lastAction}">
                                <c:set var="checked" value="${true}"/>
                            </c:if>
                        </c:forEach>
                        <tags:check name="actions" key=".actionType.${lastAction}" classes="M0 no-color" value="${lastAction}" checked="${checked}"></tags:check>
                    </c:forEach>
                </div>
                
                <div class="button-group dib" style="margin-left:20px;">
                    <c:forEach var="syncOption" items="${syncOptions}">
                        <c:if test="${syncOption != 'NA'}">
                            <c:set var="checked" value="${false}"/>
                            <c:forEach var="sync" items="${filter.inSync}">
                                <c:if test="${sync eq syncOption}">
                                    <c:set var="checked" value="${true}"/>
                                </c:if>
                            </c:forEach>
                            <tags:check name="inSync" key=".syncType.${syncOption}" classes="M0 no-color" value="${syncOption}" checked="${checked}"></tags:check>
                        </c:if>
                    </c:forEach>
                </div>
                    
                <div class="button-group dib" style="margin-left:20px;">
                    <c:forEach var="status" items="${statusOptions}">
                        <c:if test="${status != 'NA'}">
                            <c:set var="checked" value="${false}"/>
                            <c:forEach var="lastStatus" items="${filter.statuses}">
                                <c:if test="${lastStatus eq status}">
                                    <c:set var="checked" value="${true}"/>
                                </c:if>
                            </c:forEach>
                            <tags:check name="statuses" key=".statusType.${status}" classes="M0 no-color" value="${status}" checked="${checked}"></tags:check>
                        </c:if>
                    </c:forEach>
                </div>
            
                <div class="action-area stacked">
                    <cti:button nameKey="filter" classes="primary action" type="submit" busy="true"/>
                </div>
            
            </form:form>
        
        </div>
    </div>
    
    <span class="fwn"><i:inline key=".filteredResults"/></span>
    <span class="badge">${results.hitCount}</span>&nbsp;<i:inline key=".devices"/>
    <span class="js-cog-menu">
        <cm:dropdown icon="icon-cog">
            <cm:dropdownOption key=".assignConfig" classes="js-collection-action" icon="icon-page-edit" data-collection-action="assignConfig"/> 
            <cm:dropdownOption key=".sendConfig" classes="js-collection-action" icon="icon-ping" data-collection-action="sendConfig"/> 
            <cm:dropdownOption key=".readConfig" classes="js-collection-action" icon="icon-read" data-collection-action="readConfig"/> 
            <cm:dropdownOption key=".verifyConfig" classes="js-collection-action" icon="icon-accept" data-collection-action="verifyConfig"/> 
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
    
    <div data-url="${dataUrl}" data-static>
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
                    <td><cti:paoDetailUrl yukonPao="${detail.device.paoIdentifier}" newTab="true">${detail.device.name}</cti:paoDetailUrl></td>
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
                                data-width="600" data-url="<cti:url value="/deviceConfiguration/summary/${deviceId}/displayError"/>"></div>
                                <a href="javascript:void(0);" data-popup=".js-failure-${deviceId}"><i:inline key=".statusType.${detail.status}"/></a>
                            </c:when>
                            <c:otherwise>
                                <i:inline key=".statusType.${detail.status}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${detail.inSync == 'OUT_OF_SYNC'}">
                                <div class="dn js-outofsync-${deviceId}" data-dialog data-cancel-omit="true" data-title="<cti:msg2 key=".outOfSync"/>" 
                                data-width="600" data-url="<cti:url value="/deviceConfiguration/summary/${deviceId}/outOfSync"/>"></div>
                                <a href="javascript:void(0);" data-popup=".js-outofsync-${deviceId}"><i:inline key=".syncType.${detail.inSync}"/></a>
                            </c:when>
                            <c:otherwise>
                                <i:inline key=".syncType.${detail.inSync}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td><cti:formatDate type="BOTH" value="${detail.actionStart}"/></td>
                    <td><cti:formatDate type="BOTH" value="${detail.actionEnd}"/></td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cti:msg2 var="historyTitle" key=".viewHistory.title" argument="${detail.device.name}"/>
                            <div class="dn js-view-history-${deviceId}" data-dialog data-cancel-omit="true" data-title="${historyTitle}" 
                            data-width="600" data-load-event="yukon:config:viewHistory"
                            data-url="<cti:url value="/deviceConfiguration/summary/${deviceId}/viewHistory"/>"></div>
                            <cm:dropdownOption key=".viewHistory" data-popup=".js-view-history-${deviceId}" icon="icon-application-view-columns"/>
                            <cm:dropdownOption key=".send" icon="icon-ping" classes="js-send-config" data-device-id="${deviceId}"/>
                            <cm:dropdownOption key=".read" icon="icon-read" classes="js-read-config" data-device-id="${deviceId}"/>
                            <cm:dropdownOption key=".verify" icon="icon-accept" classes="js-verify-config" data-device-id="${deviceId}"/>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${results}" adjustPageCount="true" thousands="true"/>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.device.config.summary.js" />
    <cti:includeCss link="/resources/js/lib/sortable/sortable.css"/>
    <cti:includeScript link="/resources/js/lib/sortable/sortable.js"/>

</cti:standardPage>