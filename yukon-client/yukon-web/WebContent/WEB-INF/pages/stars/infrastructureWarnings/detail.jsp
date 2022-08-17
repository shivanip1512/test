<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="infrastructureWarnings" smartNotificationsEvent="INFRASTRUCTURE_WARNING">

    <cti:msgScope paths="widgets.infrastructureWarnings">
    
        <div class="column-12-12">
            <div class="column one">
                <c:set var="fromDetailPage" value="true"/>
                <%@ include file="summaryTable.jsp" %>
            </div>
            <div class="column two nogutter filter-container">
                <cti:url var="action" value="/stars/infrastructureWarnings/filteredResults" />
                <form id="warnings-form" action="${action}" method="GET">
                    <span class="fr cp"><cti:icon icon="icon-help" data-popup="#results-help"/></span>
                    <cti:msg2 var="helpTitle" key=".detail.helpTitle"/>
                    <div id="results-help" class="dn" data-width="600" data-height="400" data-title="${helpTitle}"><cti:msg2 key=".detail.helpText"/></div><br/>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".deviceTypes">
                            <div class="button-group stacked">
                                <c:forEach var="type" items="${deviceTypes}">
                                    <c:set var="selected" value="${false}"/>
                                    <c:if test="${fn:contains(selectedTypes, type)}">
                                        <c:set var="selected" value="${true}"/>
                                    </c:if>
                                    <cti:msg2 var="deviceType" key=".category.${type}"/>
                                    <tags:check name="types" classes="M0" value="${type}" label="${deviceType}" checked="${selected}"></tags:check>
                                </c:forEach>
                            </div>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                
                    <div class="fr">
                        <cti:button nameKey="filter" classes="js-filter-results primary action"/>
                    </div>
                </form>
            </div>
        </div>
        
        <div id="results-table">
        </div>

        <div class="dn" id="js-pao-notes-popup"></div>
        <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
        <cti:includeScript link="/resources/js/pages/yukon.infrastructurewarnings.detail.js"/>
    
    </cti:msgScope>
    
</cti:standardPage>