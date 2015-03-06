<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol" page="regulator.${mode}">
    <cti:includeScript link="/JavaScript/yukon.da.regulator.js"/>
    <cti:url var="action" value="/capcontrol/regulators"/>
    <tags:setFormEditMode mode="${mode}"/>
    <form:form id="regulator-form" commandName="regulator" action="${action}" method="POST" data-pao-type-map="${paoTypeMap}">
        <cti:csrfToken/>
        <form:hidden path="id"/>
        <cti:displayForPageEditModes modes="VIEW">
            <form:hidden path="type" id="regulator-type"/>
        </cti:displayForPageEditModes>
        <tags:nameValueContainer2 tableClass="natural-width stacked-md">
            <tags:nameValue2 nameKey=".name">
                <tags:input path="name"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".description">
                <tags:input path="description"/>
            </tags:nameValue2>
            <tags:selectNameValue nameKey=".type" items="${regulatorTypes}" path="type" id="regulator-type" />
            <tags:selectNameValue nameKey=".config" items="${availableConfigs}" path="configId" itemValue="configurationId" />
            <cti:displayForPageEditModes modes="VIEW,EDIT">
            <tags:nameValue2 nameKey=".zone">
                <c:if test="${empty zone}">
                    <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                </c:if>
                <c:if test="${not empty zone}">
                    <cti:url var="zoneUrl" value="/capcontrol/ivvc/zone/detail">
                        <cti:param name="zoneId" value="${zone.id}" />
                    </cti:url>
                    <a href="${zoneUrl}">${fn:escapeXml(zone.name)}</a>
                </c:if>
            </tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.status">
                <tags:switchButton path="disabled" inverse="true" onNameKey="enabled" offNameKey="disabled"/>
            </tags:nameValue2>
            </cti:displayForPageEditModes>
        </tags:nameValueContainer2>
        <tags:sectionContainer2 nameKey="mappings">
            <table class="compact-results-table js-mappings-table dashed">
                <thead>
                    <tr>
                        <th><i:inline key="yukon.common.attribute"/></th>
                        <th><i:inline key="yukon.common.deviceName"/></th>
                        <th><i:inline key="yukon.common.pointName"/></th>
                        <cti:displayForPageEditModes modes="VIEW">
                            <th><i:inline key="yukon.common.events.pointValue"/></th>
                        </cti:displayForPageEditModes>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="mapping" items="${regulator.mappings}" varStatus="index">
                        <c:set var="idx" value="${index.index}"></c:set>
                        <tr data-mapping="${mapping.key}">
                            <td><i:inline key="${mapping.key}"/></td>
                            <td id="paoName${idx}"></td>
                            <td>
                                <tags:pickerDialog type="filterablePointPicker"
                                                   id="picker${idx}"
                                                   extraArgs="${mapping.key.filterType}"
                                                   extraDestinationFields="deviceName:paoName${idx};pointId:pointId${idx};"
                                                   allowEmptySelection="true"
                                                   selectionProperty="pointName"
                                                   initialId="${mapping.value != 0 ? mapping.value : ''}"
                                                   linkType="selection"
                                                   buttonStyleClass="fn"
                                                   viewOnlyMode="${mode == 'VIEW'}"/>
                               <form:errors path="mappings[${mapping.key}]" element="div" cssClass="error"/>
                               <input id="pointId${idx}" type="hidden" name="mappings[${mapping.key}]" value="${mapping.value}"/>
                            </td>
                            <cti:displayForPageEditModes modes="VIEW">
                                <td>
                                    <c:if test="${not empty mapping.value && mapping.value != 0}">
                                        <cti:pointStatus pointId="${mapping.value}" statusPointOnly="true"/>
                                        <cti:pointValue pointId="${mapping.value}" format="VALUE"/>
                                        <cti:pointValue pointId="${mapping.value}" format="UNIT" unavailableValue=""/>
                                        <cti:pointValue pointId="${mapping.value}" format="SHORT_QUALITY" unavailableValue=""/>
                                    </c:if>
                                </td>
                            </cti:displayForPageEditModes>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </tags:sectionContainer2>
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="editUrl" value="/capcontrol/regulators/${regulator.id}/edit" />
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <c:set var="disabled" value="false" />
                <c:if test="${not empty zone}">
                    <c:set var="disabled" value="true" />
                </c:if>
                <cti:button nameKey="delete" classes="delete js-delete" disabled="${disabled}" data-ok-event="yukon:da:regulator:delete" />
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${regulator.name}"/>
                <cti:url var="viewUrl" value="/capcontrol/regulators/${regulator.id}" />
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    <cti:url var="url" value="/capcontrol/regulators/${regulator.id}" />
    <form:form id="delete-regulator" method="DELETE" action="${url}"></form:form>
</cti:standardPage>
