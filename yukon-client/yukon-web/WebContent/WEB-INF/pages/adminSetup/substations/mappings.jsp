<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:url var="routeUrl" value="/admin/substations/routeMapping/viewRoute" />
<cti:standardPage page="substationToRouteMapping" module="adminSetup">
    <cti:msg2 var="removeRouteTitle" key="yukon.web.modules.adminSetup.substationToRouteMapping.removeRoutes"/>
    <cti:msg2 var="chooseSubstationsTitle" key="yukon.web.modules.adminSetup.substationToRouteMapping.chooseSubstation"/>
    
    <div id="saveStatusMessage"></div>

    <cti:includeScript link="/resources/js/pages/yukon.substation.route.mapping.js" />

    <cti:url var="action" value="/admin/substations/routeMapping/save"/>
    <form:form id="substation-form" name="substationForm" modelAttribute="substationRouteMapping" action="${action}">
        <cti:csrfToken/>

        <form:select id="selectedRoutes" path="selectedRoutes" multiple="multiple" cssClass="dn">
        </form:select>
        
        <tags:nameValueContainer2 tableClass="with-form-controls">
            <tags:nameValue2 nameKey=".substation">
                <c:choose>
                    <c:when test="${not empty list}">
                        <form:select id="substation" path="substationId" cssClass="fl">
                            <c:forEach var="substation" items="${list}">
                                <c:if test="${substationRouteMapping.substationId == substation.id}">
                                    <c:set var="selectedSubstationName" value="${substation.name}"/>
                                </c:if>
                                <form:option value="${substation.id}" title="${substation.name}" label="${substation.name}"/>
                            </c:forEach>
                        </form:select>
                    </c:when>
                    <c:otherwise>
                        <span><i:inline key=".noSubstations"/></span>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>

            <tr>
                <tags:nameValue2 excludeColon = "true">
                    <form:input id="substationName" name="substationName" path="substationName" placeholder="Substation Name"/>
                    <td>
                        <cti:button nameKey="create" icon="icon-plus-green" id="b-add" type="submit" name="addSubstation"/>
                        <c:if test="${hasVendorId}">
                            <cti:url var="mspAddUrl" value="/admin/substations/routemapping/multispeak/choose"/>
                            <div class="dn" id="add-msp-substation" data-title="${chooseSubstationsTitle}" data-url="${mspAddUrl}">
                            </div> 
                            <cti:button nameKey="mspSubstation" icon="icon-add" id="b-add" data-popup="#add-msp-substation"/>
                        </c:if>
                    </td>
                </tags:nameValue2>
            </tr>
        </tags:nameValueContainer2>
        <br>
        
        <d:confirm on="#removeSubstation" nameKey="confirmDelete" argument="${selectedSubstationName}"/>
        <d:confirm on="#removeAllSubstations" nameKey="confirmDelete" argument="All Substations"/>
        
        <c:if test="${not empty list}">
            <tags:sectionContainer2 nameKey="substationRouteMapping" styleClass="select-box">
                <div class="column-12-12 clearfix">
                    <!-- Available Routes -->
                    <div class="column one">
                        <h3><i:inline key="yukon.common.available"/></h3>
                        <div class=" bordered-div" style="overflow:auto; height:370px;">
                            <div id="unassigned" class="select-box-available" style="min-height:150px;">
                                <c:forEach var="item" items="${substationRouteMapping.avList}">
                                    <div class="select-box-item clearfix cm"
                                         data-id="${item.id}">${fn:escapeXml(item.name)}
                                        <cti:button icon="icon-plus-green" renderMode="buttonImage" 
                                                    classes="select-box-item-add js-add-route"/>
                                        <div class="select-box-item-movers" style="display:none;">
                                            <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                                                        classes="left select-box-item-up js-move-up"/>
                                            <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                                                        classes="right select-box-item-down js-move-down"/>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Assigned Routes -->
                    <div class="column two nogutter">
                        <h3><i:inline key="yukon.common.assigned"/></h3>
                        <div class="bordered-div" style="overflow:auto; height:370px;">
                            <div id="assigned" class="select-box-selected js-with-movables" style="min-height:150px;" 
                                 data-item-selector=".select-box-item">
                                <c:forEach var="item" items="${substationRouteMapping.routeList}" varStatus="status">
                                    <div class="select-box-item cm"
                                         data-id="${item.id}">${fn:escapeXml(item.name)}
                                        <input type="hidden" value="${route.id}" class="route-id">
                                    
                                        <cti:button icon="icon-cross" renderMode="buttonImage" 
                                                    classes="select-box-item-remove js-remove-route"/>
                                        <div class="select-box-item-movers">
                                            <c:set var="disabled" value="${status.first}"/>
                                            <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                                                        classes="left select-box-item-up js-move-up" disabled="${disabled}"/>
                                            <c:set var="disabled" value="${status.last}"/>
                                            <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                                                        classes="right select-box-item-down js-move-down" disabled="${disabled}"/>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>
            </tags:sectionContainer2>
            <div class="page-action-area">
                <cti:button id="saveAllRoutes" nameKey="save" busy="true" type="submit" classes="primary action"/>
                <cti:button id="removeSubstation" nameKey="delete" classes="delete" name="removeSubstation" type="submit"/>
                <cti:button id="cancel" nameKey="cancel" />
            </div>
        </c:if>
    </form:form>
    <div id="addRoute-popup"></div>
</cti:standardPage>
