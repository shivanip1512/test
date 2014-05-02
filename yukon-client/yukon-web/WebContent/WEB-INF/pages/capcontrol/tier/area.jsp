<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %> <%-- Used in Trends --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="area">

    <%@include file="/capcontrol/capcontrolHeader.jspf"%>

     <c:if test="${hasEditingRole}">
        <c:set var="editKey" value="edit"/>
    </c:if>
    <c:if test="${not hasEditingRole}">
        <c:set var="editKey" value="info"/>
    </c:if>

    <div class="dn f-page-additional-actions">
        <li class="divider" />
        <cti:url var="editUrl" value="/editor/cbcBase.jsf">
            <cti:param name="type" value="2" />
            <cti:param name="itemid" value="${bc_areaId}" />
        </cti:url>
        <cm:dropdownOption  key="components.button.${editKey}.label" icon="icon-pencil" href="${editUrl}" />
    </div>

    <div class="dn">

        <c:if test="${showAnalysis}">
            <i:simplePopup titleKey=".analysisTrends" id="analysisTrendsOptions" on="#analysisTrendsButton">
                 <%@ include file="analysisTrendsOptions.jspf" %>
            </i:simplePopup>
            <cti:button nameKey="analysis" id="analysisTrendsButton" icon="icon-chart-line"/>
        </c:if>

        <i:simplePopup titleKey=".recentEvents" id="recentEventsOptions" on="#recentEventsButton">
             <%@ include file="recentEventsOptions.jspf" %>
        </i:simplePopup>
        <cti:button nameKey="recentEvents" id="recentEventsButton" icon="icon-application-view-columns"/>
    </div>

    <flot:defaultIncludes/>

    <c:if test="${hasSubstationControl}">
        <script type="text/javascript">
            addCommandMenuBehavior('a[id^="substationState_"]');
        </script>
    </c:if>

    <c:if test="${hasAreaControl}">
        <script type="text/javascript">
            addCommandMenuBehavior('a[id^="areaState_"]');
        </script>
    </c:if>

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="infoContainer" hideEnabled="true">
                <div class="column-12-12 clearfix">
                    <div class="column one">
                        <tags:nameValueContainer2 tableClass="name-collapse">
                            <tags:nameValue2 nameKey=".name" valueClass="wbba">
                                <span>${fn:escapeXml(bc_areaName)}</span>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".geoName">
                                <span>${fn:escapeXml(description)}</span>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="column two nogutter">
                        <tags:nameValueContainer2 tableClass="name-collapse">
                            <tags:nameValue2 nameKey=".state" rowClass="wsnw">
                                <a id="areaState_${bc_areaId}" href="javascript:void(0);" class="subtle-link">
                                    <span id="areaState_box_${bc_areaId}" class="box state-box">&nbsp;</span>
                                    <cti:capControlValue paoId="${bc_areaId}" type="${type}" format="STATE" />
                                </a>
                                <cti:dataUpdaterCallback function="updateStateColorGenerator('areaState_box_${bc_areaId}')" initialize="true" value="${type}/${bc_areaId}/STATE"/>
                             </tags:nameValue2>
                             <tags:nameValue2 nameKey=".substations">
                                ${fn:length(subStations)}
                             </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                </div>
                <capTags:warningImg paoId="${bc_areaId}" type="${type}" alertBox="true"/>
            </tags:sectionContainer2>
        </div>

        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="statsContainer" hideEnabled="true">
                <div class="column-12-12">
                    <div class="column one">
                        <tags:nameValueContainer2 tableClass="name-collapse">
                            <tags:nameValue2 nameKey=".availableKvars">
                                <cti:capControlValue paoId="${bc_areaId}" type="${type}" format="KVARS_AVAILABLE"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".unavailableKvars">
                                <cti:capControlValue paoId="${bc_areaId}" type="${type}" format="KVARS_UNAVAILABLE"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="column two nogutter">
                        <tags:nameValueContainer2 tableClass="name-collapse">
                            <tags:nameValue2 nameKey=".closedKvars">
                                <cti:capControlValue paoId="${bc_areaId}" type="${type}" format="KVARS_CLOSED"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trippedKvars">
                                <cti:capControlValue paoId="${bc_areaId}" type="${type}" format="KVARS_TRIPPED"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                </div>
                <div class="clear">
                    <tags:nameValueContainer2 tableClass="name-collapse">
                        <tags:nameValue2 nameKey=".pfactorEstimated" rowClass="powerFactor">
                            <cti:capControlValue paoId="${bc_areaId}" type="${type}" format="PFACTOR"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </tags:sectionContainer2>
        </div>
    </div>

    <tags:boxContainer2 nameKey="substationsContainer"  arguments="${bc_areaName}">
        
        <table id="subTable" class="compact-results-table">
            <thead>
                <tr>
                    <c:set var="clazz" value="tar" />
                    <th width="16px">&nbsp;</th>
                    <th><i:inline key=".name"/></th>
                    <th><i:inline key=".state"/></th>
                    <th class="${clazz}"><i:inline key=".availableKvars"/></th>
                    <th class="${clazz}"><i:inline key=".unavailableKvars"/></th>
                    <th class="${clazz}"><i:inline key=".closedKvars"/></th>
                    <th class="${clazz}"><i:inline key=".trippedKvars"/></th>
                    <th class="${clazz}"><i:inline key=".pfactorEstimated"/></th>
                </tr>
            </thead>
    
            <c:forEach var="subStation" items="${subStations}">
            
                <c:set var="substationId" value="${subStation.ccId}"/>
                <cti:url value="/capcontrol/tier/feeders" var="feederLink">
                    <cti:param name="substationId" value="${substationId}"/>
                </cti:url>
            
                <tr data-pao-id="${substationId}">
                    <td>
                        <capTags:warningImg paoId="${substationId}" type="SUBSTATION"/>
                    </td>
                    <td>
                        <a href="${feederLink}" id="anc_${substationId}">
                            <spring:escapeBody>${subStation.ccName}</spring:escapeBody>
                        </a>
                        <span class="error textFieldLabel">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="SA_ENABLED_MSG" />
                        </span>
                    </td>

                    <td class="wsnw">
                        <c:if test="${hasSubstationControl}"><a id="substationState_${substationId}" href="javascript:void(0);" class="subtle-link"></c:if>
                        <c:if test="${not hasSubstationControl}"><span id="substationState_${substationId}"></c:if>
                            <span id="substationState_box_${substationId}" class="box state-box">&nbsp;</span>
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="STATE" />
                        <c:if test="${hasSubstationControl}"></a></c:if>
                        <c:if test="${not hasSubstationControl}"></span></c:if>
                        <cti:dataUpdaterCallback function="updateStateColorGenerator('substationState_box_${substationId}')" initialize="true" value="SUBSTATION/${substationId}/STATE"/>
                    </td>

                    <td class="tar"><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_AVAILABLE" /></td>
                    <td class="tar"><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_UNAVAILABLE" /></td>
                    <td class="tar"><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_CLOSED" /></td>
                    <td class="tar"><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_TRIPPED" /></td>
                    <td class="tar"><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="PFACTOR" /></td>
                </tr>
            </c:forEach>
            
        </table>
    
    </tags:boxContainer2>
    
</cti:standardPage>