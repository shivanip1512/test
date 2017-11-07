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
    <span class="badge">${results.size()}</span>&nbsp;<i:inline key=".devices"/>

    <table class="compact-results-table row-highlighting has-actions">
        <thead>
            <tr>
                <th><i:inline key=".deviceName"/></th>
                <th><i:inline key=".type"/></th>
                <th><i:inline key=".deviceConfiguration"/></th>
                <th><i:inline key=".lastAction"/></th>
                <th><i:inline key=".lastActionStatus"/></th>
                <th><i:inline key=".inSync"/></th>
                <th><i:inline key=".lastActionStart"/></th>
                <th><i:inline key=".lastActionEnd"/></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="detail" items="${results}">
                <c:set var="deviceId" value="${detail.device.paoIdentifier.paoId}"/>
                <tr>
                    <td><cti:paoDetailUrl yukonPao="${detail.device.paoIdentifier}" newTab="true">${detail.device.name}</cti:paoDetailUrl></td>
                    <td>${detail.device.paoIdentifier.paoType.paoTypeName}</td>
                    <cti:url var="configUrl" value="/deviceConfiguration/config/view?configId=${detail.deviceConfig.configurationId}"/>
                    <td><a href="${configUrl}">${detail.deviceConfig.name}</a></td>
                    <c:if test="${detail.action != null}">
                        <td><i:inline key=".actionType.${detail.action}"/></td>
                    </c:if>
                    <c:if test="${detail.action == null}">
                        <td></td>
                    </c:if>
                    <td><i:inline key=".statusType.${detail.status}"/></td>
                    <td><i:inline key=".syncType.${detail.inSync}"/></td>
                    <td><cti:formatDate type="BOTH" value="${detail.actionStart}"/></td>
                    <td><cti:formatDate type="BOTH" value="${detail.actionEnd}"/></td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <div class="dn js-view-history-${deviceId}" data-dialog data-title="<cti:msg2 key=".viewHistory"/>" 
                            data-url="<cti:url value="/deviceConfiguration/summary/${deviceId}/viewHistory"/>"></div>
                            <cm:dropdownOption key=".viewHistory" data-popup=".js-view-history-${deviceId}"/>
                            <cm:dropdownOption key=".send" />
                            <cm:dropdownOption key=".read" />
                            <cm:dropdownOption key=".verify" />
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</cti:standardPage>