<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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
                        <tags:check name="actions" key=".actionType.${lastAction}" classes="M0" value="${lastAction}" checked="${checked}"></tags:check>
                    </c:forEach>
                </div>
                
                <div class="button-group dib" style="margin-left:20px;">
                    <c:forEach var="syncOption" items="${syncOptions}">
                        <c:set var="checked" value="${false}"/>
                        <c:forEach var="sync" items="${filter.inSync}">
                            <c:if test="${sync eq syncOption}">
                                <c:set var="checked" value="${true}"/>
                            </c:if>
                        </c:forEach>
                        <tags:check name="inSync" key=".syncType.${syncOption}" classes="M0" value="${syncOption}" checked="${checked}"></tags:check>
                    </c:forEach>
                </div>
                    
                <div class="button-group dib" style="margin-left:20px;">
                    <c:forEach var="status" items="${statusOptions}">
                        <c:set var="checked" value="${false}"/>
                        <c:forEach var="lastStatus" items="${filter.statuses}">
                            <c:if test="${lastStatus eq status}">
                                <c:set var="checked" value="${true}"/>
                            </c:if>
                        </c:forEach>
                        <tags:check name="statuses" key=".statusType.${status}" classes="M0" value="${status}" checked="${checked}"></tags:check>
                    </c:forEach>
                </div>
            
                <div class="action-area stacked">
                    <cti:button nameKey="filter" classes="primary action" type="submit" />
                </div>
            
            </form:form>
        
        </div>
    </div>


    <table class="compact-results-table">
        <thead>
            <tr>
                <th>Device Name</th>
                <th>Type</th>
                <th>Device Configuration</th>
                <th>Last Action</th>
                <th>Last Action Status</th>
                <th>In Sync</th>
                <th>Last Action Start</th>
                <th>Last Action End</th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="detail" items="${results}">
                <tr>
                    <td>${detail.device.name}</td>
                    <td>${detail.device.paoIdentifier.paoType}</td>
                    <td>${detail.deviceConfig.name}</td>
                    <td>${detail.action}</td>
                    <td>${detail.status}</td>
                    <td>${detail.inSync}</td>
                    <td><cti:formatDate type="BOTH" value="${detail.actionStart}"/></td>
                    <td><cti:formatDate type="BOTH" value="${detail.actionEnd}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</cti:standardPage>