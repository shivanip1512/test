<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:set var="pageName" value="cbc.${mode}"/>
<c:if test="${orphan}">
    <c:set var="pageName" value="cbc.orphan.${mode}"/>
</c:if>
<cti:standardPage module="capcontrol" page="${pageName}">

<%@ include file="/capcontrol/capcontrolHeader.jspf" %>

    <tags:setFormEditMode mode="${mode}" />
    
    <div class="js-page-additional-actions dn">
    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
        <li class="divider" />
        <cm:dropdownOption key="yukon.web.components.button.copy.label" icon="icon-disk-multiple"
        data-popup="#copy-cbc"/>
        <cti:url var="editUrl" value="/capcontrol/cbc/${cbc.id}/edit" />
        <cm:dropdownOption key="components.button.edit.label" icon="icon-pencil" href="${editUrl}" />
    </cti:checkRolesAndProperties>
</div>

    <cti:url var="action" value="/capcontrol/cbc" />
    <form:form id="cbc-edit-form" commandName="cbc" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="id" />
        <%-- TODO --%>
        <div class="column-12-12 clearfix">
            <div class="column one">
                <cti:msg2 var="generalTab" key=".tab.general" />
                <tags:sectionContainer title="${generalTab}">
                    <tags:nameValueContainer2 tableClass="natural-width">
                        <tags:nameValue2 nameKey=".name">
                            <tags:input path="name" size="25" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".type">
                            <cti:displayForPageEditModes modes="CREATE">
                                <tags:selectWithItems id="pao-type" items="${paoTypes}" path="paoType"/>
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="VIEW,EDIT">
                                <form:hidden id="pao-type" path="paoType" />
                                <i:inline key="${cbc.paoType}" />
                            </cti:displayForPageEditModes>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".parent">
                            <c:if test="${empty cbc.parent }">
                                <span class="empty-list">No Parent</span>
                            </c:if>
                            <c:if test="${not empty cbc.parent}">
                                <cti:url var="editParent" value="/capcontrol/capbanks/${cbc.parent.liteID}"/>
                                <a href="${editParent}">${fn:escapeXml(cbc.parent.paoName)}</a>
                            </c:if>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".status">
                            <tags:switchButton path="disableFlag" inverse="${true}"
                                offNameKey=".disabled.label" onNameKey=".enabled.label" />
                        </tags:nameValue2>

                    </tags:nameValueContainer2>
                </tags:sectionContainer>

                <tags:sectionContainer2 nameKey="config">

                    <tags:nameValueContainer2 tableClass="natural-width">
                        <tags:nameValue2 nameKey=".serialNumber">
                            <tags:input path="deviceCBC.serialNumber"
                                size="25" />
                        </tags:nameValue2>
                        <c:set var="twoWayClass" value="${cbc.twoWay? '' : 'dn'} js-two-way"/>
                        <c:set var="oneWayClass" value="${cbc.twoWay? 'dn' : ''} js-one-way"/>
                        <tags:nameValue2 nameKey=".masterAddr" rowClass="${twoWayClass}">
                            <tags:input path="deviceAddress.masterAddress" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".slaveAddr" rowClass="${twoWayClass}">
                            <tags:input path="deviceAddress.slaveAddress" />
                         </tags:nameValue2>

                        <tags:nameValue2 nameKey=".commChannel" rowClass="${twoWayClass}">
                            <tags:selectWithItems
                                id="comm-port"
                                path="deviceDirectCommSettings.portID"
                                items="${availablePorts}"
                                itemValue="liteID" itemLabel="paoName"
                                inputClass="with-option-hiding" />
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey="yukon.common.ipaddress" rowClass="${twoWayClass}" data-tcp-port="true">
                            <tags:input path="ipAddress"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".port" rowClass="${twoWayClass}" data-tcp-port="true">
                            <tags:input path="port"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".postCommWait" rowClass="${twoWayClass}">
                            <tags:input path="deviceAddress.postCommWait" />
                        </tags:nameValue2>
                        <%-- TODO Toggle next few fields --%>
                        <tags:nameValueGap2/>
                        <tags:nameValueGap2/>
                        <tags:nameValue2 nameKey=".integrityScanRate" rowClass="${twoWayClass}">
                            <tags:switchButton path="editingIntegrity" toggleGroup="integrity" toggleAction="hide"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".interval" data-toggle-group="integrity">
                            <tags:intervalStepper path="deviceScanRateMap['Integrity'].intervalRate"
                                intervals="${timeIntervals}"
                                id="scan1" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altInterval" data-toggle-group="integrity">
                            <tags:intervalStepper path="deviceScanRateMap['Integrity'].alternateRate"
                                intervals="${timeIntervals}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".scanGroup" data-toggle-group="integrity">
                            <div class="button-group">
                                <c:forEach var="scanGroup"
                                    items="${scanGroups}">
                                    <tags:radio
                                        path="deviceScanRateMap['Integrity'].scanGroup"
                                        value="${scanGroup.dbValue}"
                                        key="${scanGroup}"
                                        classes="yes M0" />
                                </c:forEach>
                            </div>
                        </tags:nameValue2>

                        <%-- TODO Toggle next few fields --%>
                        <tags:nameValueGap2/>
                        <tags:nameValueGap2/>
                        <tags:nameValue2 nameKey=".exceptionScanRate" rowClass="${twoWayClass}">
                            <tags:switchButton path="editingException" toggleGroup="exception" toggleAction="hide"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".interval" data-toggle-group="exception">
                            <tags:intervalStepper path="deviceScanRateMap['Exception'].intervalRate"
                                intervals="${timeIntervals}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altInterval" data-toggle-group="exception">
                            <tags:intervalStepper path="deviceScanRateMap['Exception'].alternateRate"
                                intervals="${timeIntervals}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".scanGroup" data-toggle-group="exception">
                            <div class="button-group">
                                <c:forEach var="scanGroup" items="${scanGroups}">
                                    <tags:radio path="deviceScanRateMap['Exception'].scanGroup"
                                        key="${scanGroup}" value="${scanGroup.dbValue}"
                                        classes="yes M0" />
                                </c:forEach>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".controlRoute" rowClass="${oneWayClass}">
                            <tags:selectWithItems path="deviceCBC.routeID"
                                items="${availableRoutes}"
                                itemLabel="paoName" itemValue="liteID"
                                inputClass="with-option-hiding" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
            <cti:displayForPageEditModes modes="CREATE">
                <form:hidden path="dnpConfigId"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="VIEW,EDIT">
            <div class="column two nogutter">
                <tags:sectionContainer2 nameKey="points"
                    styleClass="stacked-lg">
                    <div id="CBCCtlEditorScrollDiv" class="scroll-md">
                        <%@ include file="pointsTable.jsp" %>
                    </div>
                    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                        <div class="action-area">
                            <tags:pointCreation paoId="${cbc.id}" />
                        </div>
                    </cti:checkRolesAndProperties>
                </tags:sectionContainer2>

                <tags:sectionContainer2 nameKey="dnpConfiguration" styleClass="stacked-lg ${twoWayClass}">
                    <tags:nameValueContainer2 tableClass="natural-width">
                        <tags:nameValue2 nameKey=".dnpConfig">
                            <cti:displayForPageEditModes modes="CREATE">
                                ${fn:escapeXml(dnpConfig.name)}
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="VIEW,EDIT">
                                <tags:selectWithItems id="dnp-config"
                                    items="${configs}"
                                    path="dnpConfigId"
                                    itemLabel="name"
                                    itemValue="configurationId" />
                            </cti:displayForPageEditModes>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <tags:nameValueContainer2
                        tableClass="natural-width js-dnp-fields js-block-this">
                        <tags:nameValue2 nameKey=".internalRetries"
                            valueClass="js-dnp-field js-dnp-internalRetries">
                            ${dnpConfig.internalRetries}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".useLocal"
                            valueClass="js-dnp-field js-dnp-localTime">
                            ${dnpConfig.localTime}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".enableTimeSync"
                            valueClass="js-dnp-field js-dnp-enableDnpTimesyncs">
                            ${dnpConfig.enableDnpTimesyncs}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".omitTimeReq"
                            valueClass="js-dnp-field js-dnp-omitTimeRequest">
                            ${dnpConfig.omitTimeRequest}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".unsolicit1"
                            valueClass="js-dnp-field js-dnp-enableUnsolicitedMessageClass1">
                            ${dnpConfig.enableUnsolicitedMessageClass1}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".unsolicit2"
                            valueClass="js-dnp-field js-dnp-enableUnsolicitedMessageClass2">
                            ${dnpConfig.enableUnsolicitedMessageClass2}
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".unsolicit3"
                            valueClass="js-dnp-field js-dnp-enableUnsolicitedMessageClass3">
                            ${dnpConfig.enableUnsolicitedMessageClass3}
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
            </cti:displayForPageEditModes>
        </div>

        <div class="page-action-area">
   
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action" />
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
            <c:set var="deleteDisabled" value="false" />
                <c:if test="${not empty cbc.parent}">
                    <c:set var="deleteDisabled" value="true" />
                    <c:set var="deleteTitle" value="Can not delete while attached to a cap bank" />
                </c:if>

                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:cbc:delete"
                    disabled="${deleteDisabled}" title="${deleteTitle}"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${cbc.name}"/>
            
                <cti:url var="viewUrl" value="/capcontrol/cbc/${cbc.id}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>

        </div>
    </form:form>

    <cti:msg2 var="copyText" key="components.button.copy.label"/>
    <div id="copy-cbc" class="dn" data-title="Copy CBC" data-dialog data-ok-text="${copyText}" data-event="yukon:da:cbc:copy">
        <tags:setFormEditMode mode="EDIT" />
        <cti:url var="copyUrl" value="/capcontrol/cbc/${cbc.id}/copy"/>
        <form action="${copyUrl}" method="POST">
            <cti:csrfToken/>
            <tags:nameValueContainer>
                <tags:nameValue name="New Name">
                    <cti:msg2 var="newName" key="yukon.common.copyof" argument="${cbc.name}"/>
                    <input name="newName" value="${newName}">
                </tags:nameValue>
                <tags:nameValue name="Copy Points">
                    <tags:switchButton name="copyPoints" offNameKey=".no.label" onNameKey=".yes.label" checked="${true}"/>
                </tags:nameValue>
            </tags:nameValueContainer>
        </form>
    </div>

    <cti:url var="url" value="/capcontrol/cbc/${cbc.id}"/>
    <form:form id="delete-cbc" method="DELETE" action="${url}">
        <cti:csrfToken/>
    </form:form>

    <cti:toJson id="two-way-types" object="${twoWayTypes}"/>
    <cti:toJson id="tcp-comm-ports" object="${tcpCommPorts}"/>
    <cti:includeScript link="/resources/js/pages/yukon.da.cbc.js" />
</cti:standardPage>