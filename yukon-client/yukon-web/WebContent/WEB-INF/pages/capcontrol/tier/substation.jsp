<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="pageName" value="substation.${mode}"/>
<c:if test="${orphan}">
    <c:set var="pageName" value="substation.orphan.${mode}"/>
</c:if>
<cti:standardPage module="capcontrol" page="${pageName}">
<cti:includeScript link="/resources/js/pages/yukon.da.substation.js"/>

<tags:setFormEditMode mode="${mode}"/>

<%@ include file="/capcontrol/capcontrolHeader.jspf"%>

<c:set var="substationId" value="${substation.id}"/>

<cti:checkRolesAndProperties value="SUBSTATION_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
    ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,
    NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
    <c:set var="hasSubstationCommandsAndActionsAccess" value="true"/>
</cti:checkRolesAndProperties>

<c:if test="${hasSubstationCommandsAndActionsAccess}">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="substationState"]');
    </script>
</c:if>

<c:if test="${!orphan}">
    <script type="text/javascript">
    $(function() {
       yukon.da.common.checkPageExpire();
    });

    </script>
</c:if>
<script type="text/javascript">
$(function() {
    yukon.da.common.initSubstation();
});
</script>

<div class="js-page-additional-actions dn">
    <cti:displayForPageEditModes modes="VIEW,EDIT">
        <cti:checkRolesAndProperties value="SYSTEM_WIDE_CONTROLS">
            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <li class="divider" />
            </cti:checkRolesAndProperties>
        </cti:checkRolesAndProperties>

        <cti:url var="locationsUrl" value="/capcontrol/capbank/capBankLocations">
            <cti:param name="value" value="${substationId}" />
        </cti:url>
        <cm:dropdownOption key=".location.label" href="${locationsUrl}" icon="icon-interstate" />
    
        <c:if test="${showAnalysis}">
            <i:simplePopup styleClass="js-analysis-trends" titleKey=".analysisTrends" id="analysisTrendsOptions" on="#analysisTrendsButton">
                <%@ include file="analysisTrendsOptions.jspf" %>
            </i:simplePopup>
            <cm:dropdownOption key=".analysis.label" id="analysisTrendsButton" icon="icon-chart-line" />
        </c:if>
    
        <i:simplePopup styleClass="js-actions-recentEvents" titleKey=".recentEvents" id="recentEventsOptions" on="#recentEventsButton">
            <%@ include file="recentEventsOptions.jspf" %>
        </i:simplePopup>
        <cm:dropdownOption key=".recentEvents.label" id="recentEventsButton" icon="icon-application-view-columns" />
    
        <c:if test="${!orphan}">
            <c:if test="${hasSubstationCommandsAndActionsAccess}">
                <cm:dropdownOption linkId="substationState_${substationId}" key=".substation.actions" icon="icon-cog" href="javascript:void(0);" />
            </c:if>
        </c:if>

        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cm:dropdownOption key=".edit.buses" icon="icon-add-remove" data-popup=".js-edit-buses-popup" />
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>

    <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cti:url var="editUrl" value="/capcontrol/substations/${substationId}/edit" />
            <cm:dropdownOption  key="components.button.edit.label" icon="icon-pencil" href="${editUrl}" />
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>
    
    <li class="divider" />
    <cm:dropdownOption classes="js-show-comments" key=".menu.viewComments" icon="icon-comment" data-pao-id="${substationId}" 
        data-pao-name="${fn:escapeXml(substation.name)}"/>
    <cti:url var="recentEventsUrl" value="/capcontrol/search/recentEvents?value=${substationId}" />
    <cm:dropdownOption href="${recentEventsUrl}" key=".menu.viewRecentEvents" icon="icon-calendar-view-month"/>
</div>

<div class="dn" data-pao-id="${substationId}"></div>

<div class="column-14-10">
<div class="column one">
<cti:url var="action" value="/capcontrol/substations"/>
<cti:displayForPageEditModes modes="CREATE">
    <c:if test="${not empty parent}">
        <cti:url var="action" value="/capcontrol/substations?parentId=${parent.liteID}"/>
    </c:if>
</cti:displayForPageEditModes>
<form:form modelAttribute="substation" action="${action}" method="POST">
    <form:hidden path="id"/>
    <cti:csrfToken/>
                <cti:msg2 var="infoTab" key=".infoTab"/>
                <tags:sectionContainer title="${infoTab}">
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <tags:nameValueContainer2 tableClass="name-collapse">
                        <tags:nameValue2 nameKey=".name">
                            <tags:input path="name" maxlength="50" autofocus="autofocus"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".geoName">
                            <tags:input path="geoAreaName" maxlength="60"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".mapLocationId">
                            <tags:input path="CapControlSubstation.mapLocationID" maxlength="64"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".specialArea">
                            <span>
                                <c:choose>
                                    <c:when test="${specialAreaId > 0}">
                                        <cti:classUpdater type="SUBSTATION" identifier="${substationId}/SA_ENABLED">
                                            <cti:url var="editSpecialArea" value="/capcontrol/areas/${specialAreaId}"/>
                                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="SA_ENABLED_ALL_MSG"
                                            defaultBlank="true" initialize="true"/>
                                        </cti:classUpdater>
                                    </c:when>
                                    <c:otherwise><i:inline key="yukon.common.none.choice"/></c:otherwise>
                                </c:choose>
                            </span>
                        </tags:nameValue2>
                    </tags:nameValueContainer2> 
                </div>
                <div class="column two nogutter">
                    <tags:nameValueContainer2 tableClass="name-collapse">
                       <tags:nameValue2 nameKey=".parent">
                            <c:if test="${empty parent}">
                                <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                            </c:if>
                            <c:if test="${not empty parent}">
                                <cti:url var="editParent" value="/capcontrol/areas/${parent.liteID}"/>
                                    <a href="${editParent}">${fn:escapeXml(parent.paoName)}</a>
                            </c:if>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".state">
                            <tags:switchButton path="disabled" inverse="true" 
                                offNameKey=".disabled" onNameKey=".enabled" offClasses="M0"/>
                        </tags:nameValue2>
                       <tags:nameValue2 nameKey=".voltReduction">
                            <form:hidden id="var-point-input" path="CapControlSubstation.voltReductionPointId" />
                            <tags:pickerDialog
                                id="varPointPicker"
                                type="voltReductionPointPicker"
                                linkType="selectionLabel"
                                selectionProperty="pointName"
                                destinationFieldId="var-point-input"
                                buttonStyleClass="M0"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"
                                includeRemoveButton="${true}"
                                removeValue="0" />
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty substation.capControlSubstation.voltReductionPointId || substation.capControlSubstation.voltReductionPointId == 0}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </div>
            <cti:displayForPageEditModes modes="EDIT,VIEW">
                <capTags:warningImg paoId="${substationId}" type="SUBSTATION" alertBox="true"/>
            </cti:displayForPageEditModes>
        </tags:sectionContainer>
    </div>
    
<cti:displayForPageEditModes modes="EDIT,VIEW">
    <div class="column two nogutter">
      <cti:tabs containerName="substation_${substationId}_Tab1">
         <cti:msg2 var="statisticsTab" key=".statisticsTab"/>
         <cti:tab title="${statisticsTab}">
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
            </cti:tab>
           <cti:msg2 var="attachedPointsTab" key=".attachedPointsTab"/>
            <cti:tab title="${attachedPointsTab}">
                <div class="scroll-md">
                    <%@ include file="/WEB-INF/pages/capcontrol/pointsTable.jsp" %>
                </div>
                <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                    <div class="action-area">
                        <tags:pointCreation paoId="${substationId}" />
                    </div>
                </cti:checkRolesAndProperties>
            </cti:tab>
        </cti:tabs>
    </div>
    </cti:displayForPageEditModes>
</div>

  <div class="page-action-area">

        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:button nameKey="save" type="submit" classes="primary action"/>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="EDIT">

            <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:substation:delete" />
            <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${substation.name}"/>

            <cti:url var="viewUrl" value="/capcontrol/substations/${substationId}"/>
            <cti:button nameKey="cancel" href="${viewUrl}"/>

        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="CREATE">
            <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
        </cti:displayForPageEditModes>
    </div>
</form:form>
    
<cti:url var="url" value="/capcontrol/substations/${substationId}"/>
<form:form id="delete-substation" method="DELETE" action="${url}">
    <cti:csrfToken/>
</form:form>
    
<cti:displayForPageEditModes modes="EDIT,VIEW">
    <div style="padding-top:15px;">
        <%@ include file="busTier.jspf" %>
        <%@ include file="feederTier.jspf" %>
        <%@ include file="bankTier.jspf"%>
    </div>
</cti:displayForPageEditModes>
    
<%-- EDIT BUSES POPUP --%>
<div class="dn js-edit-buses-popup"
    data-dialog
    data-parent-id="${substationId}"
    data-title="<cti:msg2 key=".edit.buses"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/substations/${substationId}/buses/edit"/>"
    data-event="yukon:vv:children:save"
    data-height="300"
    data-width="750"></div>
    
</cti:standardPage>