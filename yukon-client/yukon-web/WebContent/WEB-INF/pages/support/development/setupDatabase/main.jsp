<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="setupDatabase">

<cti:dataGrid cols="2" tableClasses="twoColumnLayout">
    <cti:dataGridCell>
        <tags:sectionContainer title="Options" styleClass="pageActionArea">
        <form:form commandName="devDbSetupTask" action="setupDatabase" method="post" id="setupDbForm">
            <ul class="indentedMedium">
                <li>
                    <tags:nameValueContainer2>
                        <tags:checkbox path="updateRoleProperties" descriptionNameKey=".setupDevDatabase.option.roleProperties"/>
                    </tags:nameValueContainer2>
                </li>
                <li class="dib fl">
                    <form:checkbox path="devAMR.create" id="createAMR"/>
                </li>
                <li class="dib indentedSmall">
                    <span>
                        <tags:hideReveal2 titleKey=".setupDevDatabase.option.amr" showInitially="false" slide="true" styleClass="dib">
                            <ul class="indentedMedium">
                                <li>
                                    <tags:nameValueContainer2>
                                        <tags:checkbox path="devAMR.createCartObjects" descriptionNameKey=".setupDevDatabase.option.amr.createCartObjects"/>
                                        <tags:inputNameValue path="devAMR.numAdditionalMeters" nameKey=".setupDevDatabase.option.amr.numAdditionalMeters" size="4"/>
                                        <tags:selectNameValue path="devAMR.routeId" nameKey=".setupDevDatabase.option.amr.routeId" items="${allRoutes}" itemLabel="paoName" itemValue="liteID"/>
                                        <tags:inputNameValue path="devAMR.addressRangeMin" nameKey=".setupDevDatabase.option.amr.addressRangeMin" size="10"/>
                                        <tags:inputNameValue path="devAMR.addressRangeMax" nameKey=".setupDevDatabase.option.amr.addressRangeMax" size="10"/>
                                    </tags:nameValueContainer2>
                                </li>
                                <li>
                                    <tags:hideReveal2 titleKey=".setupDevDatabase.option.amr.meterTypes" showInitially="false" slide="true">
                                        <ul class="indentedMedium">
                                            <li>
                                                <tags:nameValueContainer2>
                                                    <ul>
                                                        <c:forEach items="${devDbSetupTask.devAMR.meterTypes}" var="meterType" varStatus="status">
                                                            <li>
                                                                <tags:checkbox path="devAMR.meterTypes[${status.index}].create"/>
                                                                <span class="indentedSmall">${devDbSetupTask.devAMR.meterTypes[status.index].paoType}</span>
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </tags:nameValueContainer2>
                                            </li>
                                        </ul>
                                    </tags:hideReveal2>
                                </li>
                            </ul>
                        </tags:hideReveal2>
                    </span>
                </li>
                <li class="cl"></li>
                <li class="dib fl">
                    <form:checkbox path="devCapControl.create" id="createCapControl"/>
                </li>
                <li class="dib indentedSmall">
                    <span>
                        <tags:hideReveal2 titleKey=".setupDevDatabase.option.capcontrol" showInitially="false" slide="true" styleClass="dib">
                            <ul class="indentedMedium">
                                <li>
                                    <tags:nameValueContainer2>
                                        <tags:inputNameValue path="devCapControl.numAreas" nameKey=".setupDevDatabase.option.capcontrol.object.areas" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numSubs" nameKey=".setupDevDatabase.option.capcontrol.object.subs" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numSubBuses" nameKey=".setupDevDatabase.option.capcontrol.object.subBuses" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numFeeders" nameKey=".setupDevDatabase.option.capcontrol.object.feeders" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numCapBanks" nameKey=".setupDevDatabase.option.capcontrol.object.capBanks" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numCBCs" nameKey=".setupDevDatabase.option.capcontrol.object.cbcs" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numRegulators" nameKey=".setupDevDatabase.option.capcontrol.object.regulators" size="2"/>
                                        <tags:inputNameValue path="devCapControl.offset" nameKey=".setupDevDatabase.option.capcontrol.object.offset" size="10"/>
                                    </tags:nameValueContainer2>
                                </li>
                                <li>
                                    <tags:hideReveal2 titleKey=".setupDevDatabase.option.capcontrol.cbcTypes" showInitially="false" slide="true">
                                        <ul class="indentedMedium">
                                            <li>
                                                <tags:nameValueContainer2>
                                                    <ul>
                                                        <c:forEach items="${devDbSetupTask.devCapControl.cbcTypes}" var="cbcType" varStatus="status">
                                                            <li>
                                                                <tags:checkbox path="devCapControl.cbcTypes[${status.index}].create"/>
                                                                <span class="indentedSmall">${devDbSetupTask.devCapControl.cbcTypes[status.index].paoType}</span>
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </tags:nameValueContainer2>
                                            </li>
                                        </ul>
                                    </tags:hideReveal2>
                                </li>
                                <li>
                                    <tags:hideReveal2 titleKey=".setupDevDatabase.option.capcontrol.regulatorTypes" showInitially="false" slide="true">
                                        <ul class="indentedMedium">
                                            <li>
                                                <tags:nameValueContainer2>
                                                    <ul>
                                                        <c:forEach items="${devDbSetupTask.devCapControl.regulatorTypes}" var="cbcType" varStatus="status">
                                                            <li>
                                                                <tags:checkbox path="devCapControl.regulatorTypes[${status.index}].create"/>
                                                                <span class="indentedSmall">${devDbSetupTask.devCapControl.regulatorTypes[status.index].paoType}</span>
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </tags:nameValueContainer2>
                                            </li>
                                        </ul>
                                    </tags:hideReveal2>
                                </li>
                            </ul>
                        </tags:hideReveal2>
                    </span>
                </li>
                <li class="cl"></li>
                <li class="dib fl">
                    <form:checkbox path="devStars.create" id="createStars"/>
                </li>
                <li class="dib indentedSmall">
                    <span>
                        <tags:hideReveal2 titleKey=".setupDevDatabase.option.stars" showInitially="false" slide="true" styleClass="dib">
                            <ul class="indentedMedium">
                                <li>
                                    <tags:nameValueContainer2>
                                        <tags:selectNameValue path="devStars.energyCompany" nameKey=".setupDevDatabase.option.stars.parentEnergyCompany" items="${allEnergyCompanies}" itemLabel="name" itemValue="energyCompanyId"/>
                                        <tags:inputNameValue path="devStars.devStarsAccounts.numAccounts" nameKey=".setupDevDatabase.option.stars.numAccounts" size="4"/>
                                        <tags:inputNameValue path="devStars.devStarsHardware.numPerAccount" nameKey=".setupDevDatabase.option.stars.numHardwarePerAccount" size="2"/>
                                        <tags:inputNameValue path="devStars.devStarsHardware.numExtra" nameKey=".setupDevDatabase.option.stars.numExtra" size="2"/>
                                        <tags:inputNameValue path="devStars.devStarsAccounts.accountNumMin" nameKey=".setupDevDatabase.option.stars.accountNumMin" size="10"/>
                                        <tags:inputNameValue path="devStars.devStarsAccounts.accountNumMax" nameKey=".setupDevDatabase.option.stars.accountNumMax" size="10"/>
                                        <tags:inputNameValue path="devStars.devStarsHardware.serialNumMin" nameKey=".setupDevDatabase.option.stars.serialNumMin" size="10"/>
                                        <tags:inputNameValue path="devStars.devStarsHardware.serialNumMax" nameKey=".setupDevDatabase.option.stars.serialNumMax" size="10"/>
                                    </tags:nameValueContainer2>
                                </li>
                                <li>
                                    <tags:hideReveal2 titleKey=".setupDevDatabase.option.stars.hardwareTypes" showInitially="false" slide="true">
                                        <ul class="indentedMedium">
                                            <li>
                                                <tags:nameValueContainer2>
                                                    <ul>
                                                        <c:forEach items="${devDbSetupTask.devStars.devStarsHardware.hardwareTypes}" var="hardwareType" varStatus="status">
                                                            <li>
                                                                <tags:checkbox path="devStars.devStarsHardware.hardwareTypes[${status.index}].create"/>
                                                                <span class="indentedSmall"><i:inline key="${devDbSetupTask.devStars.devStarsHardware.hardwareTypes[status.index].hardwareType.displayKey}" /></span>
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </tags:nameValueContainer2>
                                            </li>
                                        </ul>
                                    </tags:hideReveal2>
                                </li>
                            </ul>
                        </tags:hideReveal2>
                    </span>
                </li>
            </ul>
            <c:set var="setupDbBtnDisabled" value="false"/>
            <c:set var="cancelBtnStyle" value="display: none;"/>
            <c:if test="${devDbSetupTask.running}">
                <c:set var="setupDbBtnDisabled" value="true"/>
                <c:set var="cancelBtnStyle" value=""/>
            </c:if>
            <cti:button id="setupDevDatabaseButtonId" key="setupDevDatabase" type="submit" styleClass="setupDevDatabaseButton pageActionArea" disabled="${setupDbBtnDisabled}"/>
        </form:form>
        </tags:sectionContainer>
    </cti:dataGridCell>
    <cti:dataGridCell>
        <c:set var="pbarAMRStyle" value=""/>
        <c:set var="pbarCCStyle" value=""/>
        <c:set var="pbarStarsStyle" value=""/>
        <c:if test="${(!devDbSetupTask.devAMR.create || !devDbSetupTask.hasRun) && !devDbSetupTask.running}">
            <c:set var="pbarAMRStyle" value="display: none;"/>
        </c:if>
        <c:if test="${(!devDbSetupTask.devCapControl.create || !devDbSetupTask.hasRun) && !devDbSetupTask.running}">
            <c:set var="pbarCCStyle" value="display: none;"/>
        </c:if>
        <c:if test="${(!devDbSetupTask.devStars.create || !devDbSetupTask.hasRun) && !devDbSetupTask.running}">
            <c:set var="pbarStarsStyle" value="display: none;"/>
        </c:if>
        <table class="pageActionArea">
            <tr id="setupDbAMRProgressBar" style="${pbarAMRStyle}">
                <td><i:inline key=".setupDevDatabase.option.amr" />
                </td>
                <td><tags:updateableProgressBar
                        countKey="DEVELOPMENT/AMR_SUCCESS_COUNT"
                        failureCountKey="DEVELOPMENT/AMR_FAILURE_COUNT"
                        totalCountKey="DEVELOPMENT/AMR_TOTAL_COUNT"/>
                </td>
            </tr>
            <tr id="setupDbCCProgressBar" style="${pbarCCStyle}">
                <td><i:inline key=".setupDevDatabase.option.capcontrol" />
                </td>
                <td><tags:updateableProgressBar
                        countKey="DEVELOPMENT/CC_SUCCESS_COUNT"
                        failureCountKey="DEVELOPMENT/CC_FAILURE_COUNT"
                        totalCountKey="DEVELOPMENT/CC_TOTAL_COUNT"/>
                </td>
            </tr>
            <tr id="setupDbStarsProgressBar" style="${pbarStarsStyle}">
                <td><i:inline key=".setupDevDatabase.option.stars" />
                </td>
                <td><tags:updateableProgressBar
                        countKey="DEVELOPMENT/STARS_SUCCESS_COUNT"
                        failureCountKey="DEVELOPMENT/STARS_FAILURE_COUNT"
                        totalCountKey="DEVELOPMENT/STARS_TOTAL_COUNT"/>
                </td>
            </tr>
        </table>
        <c:if test="${devDbSetupTask.hasRun}">
            <div class="pageActionArea">
                <i:inline key=".setupDevDatabase.log"/>
            </div>
        </c:if>
        <div id="cancelDevDatabaseSetupButtonId" style="${cancelBtnStyle}">
            <cti:button key="cancel" styleClass="cancelDevDatabaseSetup pageActionArea"/>
        </div>
    </cti:dataGridCell>
</cti:dataGrid>

<script type="text/javascript">
    YEvent.observeSelectorClick('.setupDevDatabaseButton', function(event) {
        $('setupDevDatabaseButtonId').disable();
        var displayCancelBtn = false;
        if ($('createAMR').checked) {
            $('setupDbAMRProgressBar').show();
            displayCancelBtn = true;
        }
        if ($('createCapControl').checked) {
            $('setupDbCCProgressBar').show();
            displayCancelBtn = true;
        }
        if ($('createStars').checked) {
            $('setupDbStarsProgressBar').show();
            displayCancelBtn = true;
        }
        if (displayCancelBtn) {
            $('cancelDevDatabaseSetupButtonId').show();
        }
    });
    YEvent.observeSelectorClick('.cancelDevDatabaseSetup', function(event) {
        new Ajax.Request("cancelExecution");
    });
</script>

</cti:standardPage>
