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
    <tags:setFormEditMode mode="${mode}"/>

    <cti:toJson id="pao-type-map" object="${paoTypeMap}"/>

    <cti:url var="action" value="/capcontrol/regulators"/>
    <form:form id="regulator-form" commandName="regulator" action="${action}" method="POST">

        <cti:csrfToken/>
        <form:hidden id="regulator-id" path="id"/>

        <div class="column-12-12 clearfix stacked-md ">
            <div class="column one">
                <tags:sectionContainer2 nameKey="info">
                    <tags:nameValueContainer2 tableClass="natural-width with-form-controls">

                        <tags:nameValue2 nameKey=".name">
                            <tags:input path="name"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".description">
                            <tags:input path="description"/>
                        </tags:nameValue2>

                        <c:if test="${empty zone}">
                            <tags:selectNameValue nameKey=".type" items="${regulatorTypes}" path="type" id="regulator-type" />
                        </c:if>
                        <c:if test="${not empty zone}">
                            <tags:nameValue2 nameKey=".type">
                                <form:hidden path="type" id="regulator-type"/>
                                <i:inline key="${regulator.type}"/>
                            </tags:nameValue2>
                        </c:if>

                        <tags:selectNameValue nameKey=".config" items="${availableConfigs}" path="configId" itemValue="configurationId" />

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
                                        <cti:param name="zoneId" value="${zone.id}" />
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
                        <input type="hidden" value="0" id="regulator-events-last-update">
                        <div class="empty-list js-ivvc-events-empty">
                            <i:inline key=".emptylist"/>
                        </div>
                        <div class="scroll-md dn js-ivvc-events-holder" style="border-bottom: 1px solid #ccc">
                            <table id="regulator-events" class="has-alerts full-width dashed">
                                <thead></thead>
                                <tfoot></tfoot>
                                <tbody>
                                </tbody>
                            </table>
                        </div>
                    </tags:sectionContainer2>
                </cti:displayForPageEditModes>
            </div>
            <table class="dn">
                <tr id="regulator-events-template-row">
                    <td><cti:icon icon="js-event-icon"/></td>
                    <td class="js-message"><%-- <strong>Unknown Event</strong> --%></td>
                    <td class="js-user"><%-- Unknown User --%></td>
                    <td class="js-timestamp"><%-- 1/1/1970 12:00:00 AM --%></td>
                </tr>
            </table>
        </div>

        <tags:sectionContainer2 nameKey="mappings">

            <table class="compact-results-table js-mappings-table dashed">
                <thead>
                    <tr>
                        <th><i:inline key="yukon.common.attribute"/></th>
                        <th><i:inline key="yukon.common.deviceName"/></th>
                        <th><i:inline key="yukon.common.pointName"/></th>
                        <cti:displayForPageEditModes modes="VIEW">
                            <th></th>
                            <th><i:inline key="yukon.common.events.pointValue"/></th>
                        </cti:displayForPageEditModes>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="mapping" items="${regulator.mappings}" varStatus="index">
                        <c:set var="idx" value="${index.index}"></c:set>

                        <tr data-mapping="${mapping.key}">

                            <%-- Attribute --%>
                            <td><i:inline key="${mapping.key}"/></td>

                            <%-- Device Name --%>
                            <td id="paoName${idx}"></td>

                            <%-- Point Name --%>
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
                                <%-- Point Value --%>
                                <td class="state-indicator">
                                    <cti:pointStatus pointId="${mapping.value}" statusPointOnly="true"/>
                                </td>
                                <td>
                                    <c:if test="${not empty mapping.value && mapping.value != 0}">
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
                <c:if test="${empty zone}">
                    <c:set var="deleteDisabled" value="false" />
                    <c:set var="deleteTitle" value="" />
                </c:if>
                <c:if test="${not empty zone}">
                    <c:set var="deleteDisabled" value="true" />
                    <cti:msg2 var="deleteTitle" key=".delete.hovertext"/>
                </c:if>

                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:regulator:delete"
                    disabled="${deleteDisabled}" title="${deleteTitle}"/>
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
