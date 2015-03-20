<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="substation">

<%@ include file="/capcontrol/capcontrolHeader.jspf"%>

<flot:defaultIncludes/>

<cti:includeScript link="JQUERY_TREE" />
<cti:includeScript link="JQUERY_TREE_HELPERS" />
<cti:includeCss link="/resources/js/lib/dynatree/skin/ui.dynatree.css"/>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

<c:set var="substationId" value="${substation.id}"/>

<c:if test="${hasSubstationControl}">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="substationState"]');
    </script>
</c:if>

<script type="text/javascript">
$(function() {
    yukon.da.common.checkPageExpire();
    yukon.da.common.initSubstation();
});
</script>

<c:choose>
    <c:when test="${hasEditingRole}">
        <c:set var="editKey" value="edit"/>
    </c:when>
    <c:otherwise>
        <c:set var="editKey" value="info"/>
    </c:otherwise>
</c:choose>

<div class="js-page-additional-actions dn">
    <li class="divider" />
    <cti:url var="locationsUrl" value="/capcontrol/capbank/capBankLocations">
        <cti:param name="value" value="${substationId}" />
    </cti:url>
    <cm:dropdownOption key=".location.label" href="${locationsUrl}" icon="icon-interstate" />

    <c:if test="${showAnalysis}">
        <i:simplePopup titleKey=".analysisTrends" id="analysisTrendsOptions" on="#analysisTrendsButton">
            <%@ include file="analysisTrendsOptions.jspf" %>
        </i:simplePopup>
        <cm:dropdownOption key=".analysis.label" id="analysisTrendsButton" icon="icon-chart-line" />
    </c:if>

    <i:simplePopup titleKey=".recentEvents" id="recentEventsOptions" on="#recentEventsButton">
        <%@ include file="recentEventsOptions.jspf" %>
    </i:simplePopup>
    <cm:dropdownOption key=".recentEvents.label" id="recentEventsButton" icon="icon-application-view-columns" />

    <cti:url var="editUrl" value="/editor/cbcBase.jsf">
        <cti:param name="type" value="2" />
        <cti:param name="itemid" value="${substationId}" />
    </cti:url>
    <cm:dropdownOption  key="components.button.${editKey}.label" icon="icon-pencil" href="${editUrl}" />

    <c:if test="${hasSubstationControl}">
        <cm:dropdownOption linkId="substationState_${substationId}" key="defaults.actions" icon="icon-cog" href="javascript:void(0);" />
    </c:if>
</div>


<div class="dn" data-pao-id="${substationId}"></div>

<div class="column-12-12">
    <div class="column one">
        <tags:sectionContainer2 nameKey="infoContainer" hideEnabled="true" styleClass="stacked">
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <tags:nameValueContainer2 tableClass="name-collapse">
                        <tags:nameValue2 nameKey=".name" valueClass="wbba">
                            <span>${fn:escapeXml(substation.name)}</span>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".geoName" valueClass="wbba">
                            <span>${fn:escapeXml(substation.description)}</span>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".area" valueClass="wbba">
                            <span>
                                <c:choose>
                                    <c:when test="${areaId > 0}">
                                        <cti:deviceName deviceId="${areaId}"/>
                                    </c:when>
                                    <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
                                </c:choose>
                            </span>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="column two nogutter">
                    <tags:nameValueContainer2 tableClass="name-collapse">
                        <tags:nameValue2 nameKey=".state" rowClass="wsnw">
                            <span class="box state-box js-cc-state-updater" data-pao-id="${substationId}">&nbsp;</span>
                            <cti:dataUpdaterCallback function="yukon.da.updaters.stateColor" initialize="true" value="SUBSTATION/${substationId}/STATE_FLAGS"/>
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="STATE"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".mapLocationId">
                            <span>${substation.mapLocationId}</span>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".specialArea" valueClass="wbba">
                            <span>
                                <c:choose>
                                    <c:when test="${specialAreaId > 0}">
                                        <cti:classUpdater type="SUBSTATION" identifier="${substationId}/SA_ENABLED">
                                            <cti:deviceName deviceId="${specialAreaId}"/>: <cti:capControlValue paoId="${specialAreaId}" type="CBCSPECIALAREA" format="STATE"/>
                                        </cti:classUpdater>
                                    </c:when>
                                    <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
                                </c:choose>
                            </span>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </div>
            <capTags:warningImg paoId="${substationId}" type="SUBSTATION" alertBox="true"/>
        </tags:sectionContainer2>
    </div>

    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="statsContainer" hideEnabled="true" styleClass="stacked">
            <div class="column-12-12">
                <div class="column one">
                    <tags:nameValueContainer2 tableClass="name-collapse">
                        <tags:nameValue2 nameKey=".availableKvars">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_AVAILABLE" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".unavailableKvars">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_UNAVAILABLE" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="column two nogutter">
                    <tags:nameValueContainer2 tableClass="name-collapse">
                        <tags:nameValue2 nameKey=".closedKvars">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_CLOSED" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".trippedKvars">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_TRIPPED" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </div>
            <div class="clear">
                <tags:nameValueContainer2 tableClass="name-collapse">
                    <tags:nameValue2 nameKey=".pfactorEstimated" rowClass="powerFactor">
                        <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="PFACTOR" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
        </tags:sectionContainer2>
    </div>
</div>

<%@ include file="busTier.jspf" %>
<%@ include file="feederTier.jspf" %>
<%@ include file="bankTier.jspf" %>

</cti:standardPage>