<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="operator" page="rtuDetail">

    <tags:setFormEditMode mode="${mode}" />

    <form:form commandName="rtu">
    
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="general">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
                            <tags:input path="name" size="25" maxlength="60" autofocus="autofocus"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".type">
                            <i:inline key="${rtu.paoType}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".status">
                            <tags:switchButton path="disableFlag" inverse="${true}"
                                offNameKey=".disabled.label" onNameKey=".enabled.label" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                
                <tags:sectionContainer2 nameKey="configuration">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".masterAddress">
                            <tags:input path="deviceAddress.masterAddress" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".slaveAddress">
                            <tags:input path="deviceAddress.slaveAddress" />
                         </tags:nameValue2>
                         <tags:nameValue2 nameKey=".postCommWait">
                            <tags:input path="deviceAddress.postCommWait" />
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".integrityScanRate">
                            <tags:switchButton path="editingIntegrity" toggleGroup="integrity" toggleAction="hide"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".interval" data-toggle-group="integrity">
                            <tags:intervalDropdown path="deviceScanRateMap['Integrity'].intervalRate"
                                intervals="${timeIntervals}"
                                id="scan1" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altInterval" data-toggle-group="integrity">
                            <tags:intervalDropdown path="deviceScanRateMap['Integrity'].alternateRate"
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
                        
                        <tags:nameValue2 nameKey=".exceptionScanRate">
                            <tags:switchButton path="editingException" toggleGroup="exception" toggleAction="hide"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".interval" data-toggle-group="exception">
                            <tags:intervalDropdown path="deviceScanRateMap['Exception'].intervalRate"
                                intervals="${timeIntervals}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altInterval" data-toggle-group="exception">
                            <tags:intervalDropdown path="deviceScanRateMap['Exception'].alternateRate"
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
                        
                        <tags:nameValue2 nameKey=".scanWindow">
                            <tags:switchButton path="scanWindow" toggleGroup="scanWindow" toggleAction="hide"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".scanType" data-toggle-group="scanWindow">
                            <tags:input path="deviceWindow.type" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".winOpen" data-toggle-group="scanWindow">
                            <tags:input path="winOpenTime" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".winClose" data-toggle-group="scanWindow">
                            <tags:input path="winCloseTime" />
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
            
            <div class="column two nogutter">
            
                <tags:sectionContainer2 nameKey="points"
                    styleClass="stacked-lg">
                    <div class="scroll-md">
                        <%@ include file="/WEB-INF/pages/capcontrol/pointsTable.jsp" %>
                    </div>
                    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                        <div class="action-area">
                            <tags:pointCreation paoId="${rtu.id}" />
                        </div>
                    </cti:checkRolesAndProperties>
                </tags:sectionContainer2>
                
                <c:set var="dnpConfig" value="${rtu.dnpConfig}"/>
                <c:if test="${not empty dnpConfig}">
                    <tags:sectionContainer2 nameKey="dnpConfiguration" styleClass="stacked-lg">
                        <tags:nameValueContainer2 tableClass="natural-width">
                            <tags:nameValue2 nameKey=".dnpConfig">
                                ${fn:escapeXml(dnpConfig.name)}
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".internalRetries">
                                ${dnpConfig.internalRetries}
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".timeOffset">
                                <i:inline key="yukon.web.modules.tools.configs.enum.dnpTimeOffset.${dnpConfig.timeOffset}"/>
                            </tags:nameValue2>                        
                            <tags:nameValue2 nameKey=".enableTimeSync">
                                ${dnpConfig.enableDnpTimesyncs}
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".omitTimeReq">
                                ${dnpConfig.omitTimeRequest}
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".unsolicit1">
                                ${dnpConfig.enableUnsolicitedMessageClass1}
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".unsolicit2">
                                ${dnpConfig.enableUnsolicitedMessageClass2}
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".unsolicit3">
                                ${dnpConfig.enableUnsolicitedMessageClass3}
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".nonUpdated">
                                ${dnpConfig.enableNonUpdatedOnFailedScan}
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </tags:sectionContainer2>
                </c:if>
                <c:set var="heartbeatConfig" value="${rtu.heartbeatConfig}"/>
                <c:if test="${not empty heartbeatConfig}">
                    <tags:sectionContainer2 nameKey="heartbeatConfiguration" styleClass="stacked-lg">
                        <tags:nameValueContainer2>
                            <c:if test="${empty dnpConfig}">
                                <tags:nameValue2 nameKey=".heartbeatConfig">
                                    ${fn:escapeXml(heartbeatConfig.name)}
                                </tags:nameValue2>
                            </c:if>
                            <c:set var="heartbeatClass" value="${dnpConfig == null ? 'dn' : ''}"/>
                            <tags:nameValue2 nameKey="yukon.web.modules.tools.configs.category.cbcHeartbeat.cbcHeartbeatMode"
                                rowClass="${heartbeatClass}">
                                <c:if test="${heartbeatConfig.mode != null}">
                                    <i:inline key="yukon.web.modules.tools.configs.enum.cbcHeartbeatMode.${heartbeatConfig.mode}"/>
                                </c:if>
                            </tags:nameValue2>
                            <c:set var="heartbeatModeClass" value="${heartbeatConfig.mode == null || heartbeatConfig.mode == 'DISABLED' ? 'dn' : ''}"/>
                            <tags:nameValue2 nameKey="yukon.web.modules.tools.configs.category.cbcHeartbeat.cbcHeartbeatPeriod"
                                rowClass="${heartbeatModeClass}">
                                ${heartbeatConfig.period}
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey="yukon.web.modules.tools.configs.category.cbcHeartbeat.cbcHeartbeatValue"
                                rowClass="${heartbeatModeClass}">
                                ${heartbeatConfig.value}
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </tags:sectionContainer2>
                </c:if>
            </div>
            
        </div>

    </form:form>
    
    
    <cti:tabs>
        <cti:msg2 var="childrenTab" key=".childrenTab"/>
        <cti:tab title="${childrenTab}">
            <c:forEach var="child" items="${rtu.childDevices}">
                <c:set var="paoId" value="${child.paoIdentifier.paoId}"/>
                <cti:button renderMode="image" icon="icon-expand" classes="js-show-hide" data-paoId="${paoId}"/>
                <cti:paoDetailUrl yukonPao="${child}">
                    ${fn:escapeXml(child.name)}
                </cti:paoDetailUrl>
                <div class="js-points-${paoId} scroll-md"></div>
            </c:forEach>
        
        </cti:tab>
        
    </cti:tabs>
    
    <cti:includeScript link="/resources/js/pages/yukon.assets.rtu.js" />
</cti:standardPage>