<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="configs.summary">

    <span class="fr cp"><cti:icon icon="icon-help" data-popup="#summary-help"/></span>
    <cti:msg2 var="helpTitle" key=".helpTitle"/>
    <div id="summary-help" class="dn" data-dialog data-cancel-omit="true" data-title="${helpTitle}"><cti:msg2 key=".helpText"/></div>
            
    <hr/>
    <div class="filter-section">
        <form:form id="filter-form" action="filter" method="get" modelAttribute="filter">
            <span class="fl">
                <span class="vat"><i:inline key="yukon.common.filterBy"/>&nbsp;</span>
                <cti:msg2 var="selectConfigPlaceholder" key=".selectConfigurations"/>
                <span style="margin-right:20px;">
                    <form:select id="selectedConfigurations" path="configurationIds" size="1" cssClass="w300" data-placeholder="${selectConfigPlaceholder}">
	                    <form:option value="-999"><i:inline key=".configurations.unassigned"/></form:option>
	                    <form:option value="-998"><i:inline key=".configurations.all"/></form:option>
	                    <c:forEach var="configuration" items="${configurations}">
	                        <form:option value="${configuration.configurationId}">${fn:escapeXml(configuration.name)}</form:option>
	                    </c:forEach>
	                </form:select>
                </span>
                <tags:selectWithItems path="stateSelection" items="${states}" inputClass="vam"/>
            </span>
                
            <cti:list var="groups">
                <c:forEach var="subGroup" items="${deviceSubGroups}">
                    <cti:item value="${subGroup}"/>
                </c:forEach>
            </cti:list>
            <tags:deviceGroupPicker inputName="deviceSubGroups" multi="true" inputValue="${groups}" classes="fl"/>
            
            <cti:button nameKey="filter" classes="js-filter-configs primary action fl MB5" busy="true"/>
        
        </form:form>
    </div><br/>
    <hr/>
    
    <cti:url var="dataUrl" value="/deviceConfiguration/summary/filter">
        <c:forEach var="config" items="${filter.configurationIds}">
            <cti:param name="configurationIds" value="${config}"/>
        </c:forEach>
        <c:forEach var="subGroup" items="${filter.groups}">
            <cti:param name="deviceSubGroups" value="${subGroup.fullName}"/>
        </c:forEach>
        <cti:param name="stateSelection" value="${filter.stateSelection}"/>
    </cti:url>
    <div id="results-container" data-url="${dataUrl}">
        <%@ include file="resultsTable.jsp" %>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.device.config.summary.js" />
    <cti:includeCss link="/resources/js/lib/sortable/sortable.css"/>
    <cti:includeScript link="/resources/js/lib/sortable/sortable.js"/>

</cti:standardPage>