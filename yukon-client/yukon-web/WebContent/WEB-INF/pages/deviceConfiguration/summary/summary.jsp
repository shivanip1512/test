<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="configs.summary">
    <div class="column-18-6 clearfix">
        <div class="column one filter-container">
            <span class="fr cp"><cti:icon icon="icon-help" data-popup="#summary-help"/></span>
            <cti:msg2 var="helpTitle" key=".helpTitle"/>
            <div id="summary-help" class="dn" data-dialog data-cancel-omit="true" data-title="${helpTitle}"><cti:msg2 key=".helpText"/></div>
            <form:form id="filter-form" action="filter" method="get" modelAttribute="filter">
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
                
                <div>
                    <div class="column one tac">
                        <div><i:inline key=".lastAction"/></div>
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
                    </div>
                    <div class="column two tac">
                        <div><i:inline key=".lastActionStatus"/></div>
                        <div class="button-group dib">
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
                    </div>
                    <div class="column three tac">
                        <div><i:inline key=".lastVerificationStatus"/></div>
                        <div class="button-group dib">
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
                    </div>
                </div><br/>
            
                <div class="action-area stacked">
                    <cti:button nameKey="filter" classes="primary action" type="submit" busy="true"/>
                </div>
            
            </form:form>
        </div>
    </div>

    <br/>
    <cti:url var="dataUrl" value="/deviceConfiguration/summary/filter">
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
        <%@ include file="resultsTable.jsp" %>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.device.config.summary.js" />
    <cti:includeCss link="/resources/js/lib/sortable/sortable.css"/>
    <cti:includeScript link="/resources/js/lib/sortable/sortable.js"/>

</cti:standardPage>