<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<c:set var="pageName" value="capbank.${mode}"/>
<c:if test="${orphan}">
    <c:set var="pageName" value="capbank.orphan.${mode}"/>
</c:if>

<cti:standardPage module="capcontrol" page="${pageName}">

<%@ include file="/capcontrol/capcontrolHeader.jspf" %>

    <tags:setFormEditMode mode="${mode}" />

    <cti:url var="action" value="/capcontrol/capbanks" />
    <form:form id="capbank-edit-form" commandName="capbank" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="id" />
        <div class="column-12-12 clearfix">
            <div class="column one">
                <cti:tabs>
                    <cti:msg2 var="infoTab" key=".infoTab"/>
                    <cti:tab title="${infoTab}">
                        <tags:nameValueContainer2 tableClass="natural-width">
                            <tags:nameValue2 nameKey=".name">
                                <tags:input path="name" size="25" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".status">
                                <tags:switchButton path="disabled" inverse="${true}"
                                    offNameKey=".disabled.label" onNameKey=".enabled.label" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".address">
                                <tags:input path="location"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".mapLocationID">
                                <tags:input path="CapBank.mapLocationID"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".parent">
                                <c:if test="${empty parent }">
                                    <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                                </c:if>
                                <c:if test="${not empty parent}">
                                    <cti:url var="editParent" value="/capcontrol/feeders/${parent.liteID}"/>
                                        <a href="${editParent}">${parent.paoName}</a>
                                </c:if>
                            </tags:nameValue2>
                            <tags:yukonListEntrySelectNameValue nameKey=".switchManufacturer" path="CapBank.switchManufacture" energyCompanyId="${energyCompanyId}" listName="SWITCH_MANUFACTURER" useTextAsValue="${true}"/>
                            <tags:yukonListEntrySelectNameValue nameKey=".controllerType" path="CapBank.controllerType" energyCompanyId="${energyCompanyId}" listName="CONTROLLER_TYPE" useTextAsValue="${true}"/>
                            <tags:yukonListEntrySelectNameValue nameKey=".switchType" path="CapBank.typeOfSwitch" energyCompanyId="${energyCompanyId}" listName="TYPE_OF_SWITCH" useTextAsValue="${true}"/>
                        </tags:nameValueContainer2>
                    </cti:tab>
                    <cti:msg2 var="addInfoTab" key=".additionalInfoTab"/>
                    <cti:tab title="${addInfoTab}">
                        <tags:sectionContainer2 nameKey="configurationSection">
                            <tags:nameValueContainer2 tableClass="natural-width">
                                <tags:nameValue2 nameKey=".cbAddInfo.maintAreaId">
                                    <tags:input path="capbankAdditionalInfo.maintAreaID" />
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.poleNumber">
                                    <tags:input path="capbankAdditionalInfo.poleNumber" />
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.config">
                                    <tags:selectWithItems path="capbankAdditionalInfo.capBankConfig" 
                                    items="${configList}" itemValue="displayName"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.potentialTransformer">
                                    <tags:selectWithItems path="capbankAdditionalInfo.potentTransformer" 
                                    items="${potentialTransformerList}" itemValue="displayName"/>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                        <tags:sectionContainer2 nameKey="communicationSection">
                            <tags:nameValueContainer2 tableClass="natural-width">  
                                <tags:nameValue2 nameKey=".cbAddInfo.medium">
                                    <tags:selectWithItems id="commMedium" path="capbankAdditionalInfo.commMedium" 
                                    items="${communicationMediumList}" itemValue="displayName"/>
                                    <tags:input id="customCommMedium" path="capbankAdditionalInfo.commMediumCustom" inputClass="dn"/>
                                    <i:inline key=".custom"/><input id="customMediumCheckbox" type="checkbox" class="js-custom-medium" <c:if test="${capbank.capbankAdditionalInfo.customCommMedium}">checked="checked"</c:if>>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.commStrength">
                                    <tags:input path="capbankAdditionalInfo.commStrengh" />
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.externalAntenna">
                                    <tags:switchButton path="capbankAdditionalInfo.extAnt" offNameKey=".no.label" onNameKey=".yes.label" toggleGroup="externalAntenna" toggleAction="hide" />
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.antennaType" data-toggle-group="externalAntenna">
                                    <tags:selectWithItems path="capbankAdditionalInfo.antennaType" 
                                    items="${antennaTypeList}" itemValue="displayName"/>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                        <tags:sectionContainer2 nameKey="locationSection">
                            <tags:nameValueContainer2 tableClass="natural-width">
                                <tags:nameValue2 nameKey=".cbAddInfo.latitude">
                                    <tags:input path="capbankAdditionalInfo.latit" />
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.longitude">
                                    <tags:input path="capbankAdditionalInfo.longtit" />
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                        <tags:sectionContainer2 nameKey="maintenanceSection">
                             <tags:nameValueContainer2 tableClass="natural-width">  
                                <tags:nameValue2 nameKey=".cbAddInfo.lastVisit">
                                    <dt:dateTime path="capbankAdditionalInfo.lastMaintVisit"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.lastInspection">
                                    <dt:dateTime path="capbankAdditionalInfo.lastInspVisit"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.lastOpCountReset">
                                    <dt:dateTime path="capbankAdditionalInfo.opCountResetDate"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.requestPending">
                                    <tags:switchButton path="capbankAdditionalInfo.reqPend" offNameKey=".no.label" onNameKey=".yes.label"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.otherComments">
                                    <tags:input path="capbankAdditionalInfo.otherComments" size="40"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.opTeamComments">
                                    <tags:input path="capbankAdditionalInfo.opTeamComments" size="40"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.cbcInstallDate">
                                    <dt:dateTime path="capbankAdditionalInfo.cbcBattInstallDate"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.drivingDirections">
                                    <tags:input path="capbankAdditionalInfo.driveDir" size="40" />
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                    </cti:tab>
                </cti:tabs>
            </div>
        
            <cti:displayForPageEditModes modes="EDIT,VIEW">
                <div class="column two nogutter">
                    <cti:tabs>
                        <cti:msg2 var="attachedPointsTab" key=".attachedPointsTab"/>
                        <cti:tab title="${attachedPointsTab}">
                            <div class="scroll-md">
                                <%@ include file="pointsTable.jsp" %>
                            </div>
                            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                <div class="action-area">
                                    <tags:pointCreation paoId="${capbank.id}" />
                                </div>
                            </cti:checkRolesAndProperties>
                        </cti:tab>
                    </cti:tabs>
                  </div>
              </cti:displayForPageEditModes>
         </div>

        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action" />
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">

                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:capbank:delete"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${capbank.name}"/>

            </cti:displayForPageEditModes>
            
            <cti:url var="viewUrl" value="/capcontrol/capbanks/${capbank.id}"/>
            <cti:button nameKey="cancel" href="${viewUrl}"/>

        </div>
    </form:form>

    <cti:url var="url" value="/capcontrol/capbanks/${capbank.id}"/>
    <form:form id="delete-capbank" method="DELETE" action="${url}">
        <cti:csrfToken/>
    </form:form>
    <cti:includeScript link="/resources/js/pages/yukon.da.capbank.js" />
</cti:standardPage>