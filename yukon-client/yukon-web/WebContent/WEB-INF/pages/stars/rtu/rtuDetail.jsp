<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="operator" page="rtuDetail.${mode}">

    <tags:setFormEditMode mode="${mode}" />
    
    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
        <cti:displayForPageEditModes modes="VIEW">
            <div id="page-actions" class="dn">
                <cti:url var="createUrl" value="/stars/rtu/create" />
                <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                                   id="create-option" href="${createUrl}"/>
                <cti:displayForPageEditModes modes="EDIT,VIEW">
                    <li class="divider"></li>
                    <cti:url var="editUrl" value="/stars/rtu/${rtu.id}/edit" />
                    <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}" />
                    
                    <!-- Commander -->
                    <cti:checkRolesAndProperties value="ENABLE_WEB_COMMANDER">
                        <li class="divider"></li>
                        <cm:dropdownOption key=".commander" id="commander-menu-option" icon="icon-ping"/>
                    </cti:checkRolesAndProperties>
                </cti:displayForPageEditModes>
            </div>
        </cti:displayForPageEditModes>
    </cti:checkRolesAndProperties>

    <cti:url var="action" value="/stars/rtu/save" />
    <form:form commandName="rtu" action="${action}" method="post">
        <cti:csrfToken />
        <form:hidden id="rtuId" path="id" />

        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="general">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
                            <tags:input path="name" size="25" maxlength="60" autofocus="autofocus"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".type">
                            <i:inline key="${rtu.paoType}" />
                            <tags:hidden path="paoType"/>
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
                         <tags:nameValue2 nameKey=".commChannel">
                            <tags:selectWithItems
                                id="comm-port"
                                path="deviceDirectCommSettings.portID"
                                items="${availablePorts}"
                                itemValue="liteID" itemLabel="paoName"
                                inputClass="with-option-hiding" />
                        </tags:nameValue2>
                         <tags:nameValue2 nameKey=".postCommWait">
                            <tags:input path="deviceAddress.postCommWait" />
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".integrityScanRate">
                            <tags:switchButton path="editingIntegrity" toggleGroup="integrity" toggleAction="hide"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".interval" data-toggle-group="integrity">
                            <tags:intervalDropdown path="deviceScanRateMap['Integrity'].intervalRate"
                                intervals="${fourClassTimeIntervals}"
                                id="scan1" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altInterval" data-toggle-group="integrity">
                            <tags:intervalDropdown path="deviceScanRateMap['Integrity'].alternateRate"
                                intervals="${altTimeIntervals}" />
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
                                intervals="${threeClassTimeIntervals}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".altInterval" data-toggle-group="exception">
                            <tags:intervalDropdown path="deviceScanRateMap['Exception'].alternateRate"
                                intervals="${altTimeIntervals}" />
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
                            ${rtu.deviceWindow.type}
                            <tags:hidden path="deviceWindow.type"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".winTimeRange" valueClass="full-width" data-toggle-group="scanWindow">
                            <tags:timeSlider dataToggleGroup="scanWindow" startPath="deviceWindow.winOpen" endPath="deviceWindow.winClose"
                                viewOnlyMode="${mode == 'VIEW'}" rangeEnabled="true" timeFormat="SECONDS" maxValue="2880"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
            
            <div class="column two nogutter">
                <%@ include file="../../common/dnpConfiguration.jsp" %>
            </div>
        </div>

        <cti:displayForPageEditModes modes="VIEW">
            <cti:tabs>
                <cti:msg2 var="childrenTab" key=".childrenTab"/>
                <cti:tab title="${childrenTab}">
                    <c:if test="${empty rtu.childDevices}">
                        <i:inline key=".noChildDevices"/>
                    </c:if>
                    <c:forEach var="child" items="${rtu.childDevices}">
                        <c:set var="paoId" value="${child.paoIdentifier.paoId}"/>
                        <cti:button renderMode="image" icon="icon-expand" classes="js-show-hide" data-paoId="${paoId}"/>
                        <cti:paoDetailUrl yukonPao="${child}">
                            ${fn:escapeXml(child.name)}
                        </cti:paoDetailUrl>
                        <div class="js-points-${paoId}" style="margin:10px 0px 10px 30px;"></div>
                    </c:forEach>
                </cti:tab>
        
                <cti:msg2 var="allPointsTab" key=".allPointsTab"/>
                <cti:tab title="${allPointsTab}" headerClasses="js-all-points-tab">
                    <c:set var="isPointCreate" value= "false" />
                    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                        <c:set var="isPointCreate" value= "true" />
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="MANAGE_POINTS" level="CREATE">
                        <c:set var="isPointCreate" value= "true" />
                    </cti:checkRolesAndProperties>
                    <c:if test="${isPointCreate}">
                        <div class="stacked-lg">
                            <span class="fr"><i:inline key=".rtuPoints"/><tags:pointCreation paoId="${rtu.id}" buttonClasses="vam"/></span>
                        </div>
                        <hr>
                    </c:if>
                    <cti:url var="dataUrl" value="/stars/rtu/${rtu.id}/allPoints"/>
                    <div class="js-all-points" data-url="${dataUrl}"></div>
                </cti:tab>
            </cti:tabs>
        </cti:displayForPageEditModes>
    
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action" />
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url var="viewUrl" value="/stars/rtu/${rtu.id}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button id="cancel-btn" nameKey="cancel" />
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    
    <cti:includeScript link="/resources/js/pages/yukon.assets.rtu.js" />
</cti:standardPage>