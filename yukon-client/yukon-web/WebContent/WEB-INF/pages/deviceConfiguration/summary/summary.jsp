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
                            <form:option value="-998">All Configurations</form:option>
                            <c:forEach var="configuration" items="${configurations}">
                                <form:option value="${configuration.configurationId}">${configuration.name}</form:option>
                            </c:forEach>
                        </form:select>
                        <select name="selection">
                            <option value="ALL">ALL</option>
                            <option value="IN_PROGRESS">IN_PROGRESS</option>
                            <option value="NEEDS_UPLOAD">NEEDS_UPLOAD</option>
                            <option value="NEEDS_VALIDATION">NEEDS_VALIDATION</option>
                        </select>
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
    </cti:url>
    <div data-url="${dataUrl}" data-static>
        <%@ include file="resultsTable.jsp" %>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.device.config.summary.js" />
    <cti:includeCss link="/resources/js/lib/sortable/sortable.css"/>
    <cti:includeScript link="/resources/js/lib/sortable/sortable.js"/>

</cti:standardPage>