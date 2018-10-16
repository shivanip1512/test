<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="regulator.${mode}">
<cti:includeScript link="/resources/js/pages/yukon.da.regulator.js"/>
<%@ include file="/capcontrol/capcontrolHeader.jspf" %>
<tags:setFormEditMode mode="${mode}"/>

<cti:toJson object="${hours}" id="range-hours"/>

<cti:displayForPageEditModes modes="VIEW">
<div class="dn js-auto-map-dialog" data-dialog data-width="650"
    data-title="<cti:msg2 key=".automap.title"/>"
    data-ok-text="<cti:msg2 key="yukon.common.map"/>"
    data-cancel-text="<cti:msg2 key="yukon.common.close"/>"
    data-event="yukon:da:regulator:automap">
    <table class="full-width stacked">
        <tr>
            <td><span class="label label-info"><i:inline key="yukon.common.note"/></span></td>
            <td><i:inline key=".automap.description" arguments="${regulator.name}"/></td>
        </tr>
    </table>
    <div class="stacked dn js-automap-results">
        <span class="name"><i:inline key="yukon.common.result"/>:&nbsp;</span>
        <span class="label label-success js-automap-result"></span>
    </div>
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key="yukon.common.attribute"/></th>
                <th><i:inline key="yukon.common.point"/></th>
                <th class="dn js-result-header"><i:inline key="yukon.common.result"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody class="js-mappings">
            <c:forEach var="mapping" items="${regulator.mappings}">
                <tr data-mapping="${mapping.key}">
                    <td><i:inline key="${mapping.key}"/></td>
                    <td>${fn:escapeXml(regulator.name)}-${fn:escapeXml(mapping.key.mappingString)}</td>
                    <td class="js-result"></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<cti:url var="editUrl" value="/capcontrol/regulators/${regulator.id}/edit"/>
<%-- Page Actions --%>
<div class="js-page-additional-actions dn">
    <cti:checkRolesAndProperties value="SYSTEM_WIDE_CONTROLS">
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <li class="divider" />
        </cti:checkRolesAndProperties>

    </cti:checkRolesAndProperties>
    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
        <cm:dropdownOption icon="icon-pencil" key="yukon.common.edit" href="${editUrl}"/>
    </cti:checkRolesAndProperties>
    <li class="divider"></li>
    <cti:url var="url" value="/capcontrol/regulators/${regulator.id}/build-mapping-file"/>
    <cm:dropdownOption href="${url}" icon="icon-page-white-excel" key=".attribute.map.file"/>
    <cm:dropdownOption data-popup=".js-auto-map-dialog" icon="icon-table-relationship" key=".attribute.map"/>
</div>
</cti:displayForPageEditModes>

<cti:toJson id="pao-type-map" object="${paoTypeMap}"/>

<cti:url var="action" value="/capcontrol/regulators"/>
<form:form id="regulator-form" modelAttribute="regulator" action="${action}" method="POST">
    
    <cti:csrfToken/>
    <form:hidden id="regulator-id" path="id"/>
    
    <div class="column-12-12 clearfix stacked-md">
        <div class="column one">
            <tags:sectionContainer2 nameKey="info">
                <c:set var="clazz" value=""/>
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <c:set var="clazz" value="with-form-controls"/>
                </cti:displayForPageEditModes>
                <tags:nameValueContainer2 tableClass="natural-width ${clazz}">
                    
                    <tags:nameValue2 nameKey=".name">
                        <tags:input path="name" maxlength="60"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".description">
                        <tags:input path="description" maxlength="60"/>
                    </tags:nameValue2>
                    
                    <c:if test="${empty zone}">
                        <tags:selectNameValue nameKey=".type" items="${regulatorTypes}" path="type" id="regulator-type"/>
                    </c:if>
                    <c:if test="${not empty zone}">
                        <tags:nameValue2 nameKey=".type">
                            <form:hidden path="type" id="regulator-type"/>
                            <i:inline key="${regulator.type}"/>
                        </tags:nameValue2>
                    </c:if>
                    
                    <tags:selectNameValue nameKey=".config" items="${availableConfigs}" path="configId" 
                        itemValue="configurationId"/>
                    
                    <tags:nameValue2 nameKey="yukon.common.status">
                        <tags:switchButton path="disabled" inverse="true"
                            offClasses="M0" onNameKey=".enabled" offNameKey=".disabled"/>
                    </tags:nameValue2>
                    
                    <cti:displayForPageEditModes modes="VIEW,EDIT">
                        
                        <tags:nameValue2 nameKey=".zone">
                            <c:if test="${empty zone}">
                                <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                            </c:if>
                            <c:if test="${not empty zone}">
                                <cti:url var="zoneUrl" value="/capcontrol/ivvc/zone/detail">
                                    <cti:param name="zoneId" value="${zone.id}"/>
                                </cti:url>
                                <a href="${zoneUrl}">${fn:escapeXml(zone.name)}</a>
                            </c:if>
                        </tags:nameValue2>
                    </cti:displayForPageEditModes>
                    
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        
        <div class="column two nogutter">
            <cti:displayForPageEditModes modes="VIEW">
                <%--Events Table Here --%>
                <tags:sectionContainer2 nameKey="events">
                    <tags:stepper id="ivvc-events-range" key=".events.shown" classes="fr stacked">
                        <c:forEach var="range" items="${ranges}">
                            <c:set var="selected" value="${range eq lastRange ? 'selected' : ''}"/>
                            <option value="${range}" ${selected}><cti:msg2 key="${range}"/></option>
                        </c:forEach>
                    </tags:stepper>
                    <input type="hidden" value="0" id="regulator-events-last-update">
                    <div class="empty-list js-ivvc-events-empty">
                        <i:inline key=".events.emptylist"/>
                    </div>
                    <div class="scroll-md dn js-ivvc-events-holder stacked-md clear">
                        <table id="regulator-events" class="compact-results-table has-alerts full-width dashed stacked striped">
                            <thead>
                                <th></th>
                                <th><i:inline key=".ivvc.busView.eventMessage"/></th>
                                <th><i:inline key=".ivvc.busView.user"/></th>
                                <th><i:inline key=".ivvc.busView.timestamp"/></th>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                    <div class="js-events-timeline clear" data-regulator-id="${regulator.id}"></div>
                </tags:sectionContainer2>
               
            </cti:displayForPageEditModes>
        </div>
    </div>
    
    <h2><i:inline key=".mappings"/></h2>
    <div class="js-mappings-container"><%@ include file="mapping-table.jsp" %></div>
        
    <div class="page-action-area">
        
        <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
        </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:button nameKey="save" type="submit" classes="primary action"/>
        </cti:displayForPageEditModes>
        
        <cti:displayForPageEditModes modes="EDIT">
            <c:if test="${empty zone}">
                <c:set var="deleteDisabled" value="false"/>
                <c:set var="deleteTitle" value=""/>
            </c:if>
            <c:if test="${not empty zone}">
                <c:set var="deleteDisabled" value="true"/>
                <cti:msg2 var="deleteTitle" key=".delete.hovertext"/>
            </c:if>
            
            <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:regulator:delete"
                disabled="${deleteDisabled}" title="${deleteTitle}"/>
            <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${regulator.name}"/>
            
            <cti:url var="viewUrl" value="/capcontrol/regulators/${regulator.id}"/>
            <cti:button nameKey="cancel" href="${viewUrl}"/>
        
        </cti:displayForPageEditModes>
        
        <cti:displayForPageEditModes modes="CREATE">
            <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
        </cti:displayForPageEditModes>
    </div>
</form:form>

<cti:url var="url" value="/capcontrol/regulators/${regulator.id}"/>
<form:form id="delete-regulator" method="DELETE" action="${url}">
    <cti:csrfToken/>
</form:form>

<table class="dn js-templates">
    <tr data-event-id="?" class="js-event-template">
        <td><cti:icon icon="js-event-icon"/></td>
        <td class="js-message"></td>
        <td class="js-user"></td>
        <td class="js-timestamp"></td>
    </tr>
</table>

</cti:standardPage>