<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="setupDatabase">

<cti:dataGrid cols="2" tableClasses="twoColumnLayout">
    <cti:dataGridCell>
        <tags:sectionContainer title="Options" styleClass="pageActionArea">
        <form:form commandName="devDbSetupTask" action="setupDatabase" method="post" id="setupDbForm">
            <ul class="hideRevealTree">
                <li class="box">
                	<label>
                    	<span title="<i:inline key=".setupDevDatabase.option.roleProperties.title"/>">
                        	<tags:checkbox path="updateRoleProperties" descriptionNameKey=".setupDevDatabase.option.roleProperties"/>
                    	</span>
                	</label>
                </li>
                <li class="box">
                    <form:checkbox path="devAMR.create" id="createAMR"/>
                    <tags:hideReveal2 titleKey=".setupDevDatabase.option.amr" showInitially="false" slide="true">
                        <ul>
                            <li>
                                <tags:nameValueContainer2>
                  					<label>
                                    <tags:checkbox path="devAMR.createCartObjects" descriptionNameKey=".setupDevDatabase.option.amr.createCartObjects"/><br>
                                    </label>
                                    <label>
                                    <tags:checkbox path="devAMR.createRfnTemplates" descriptionNameKey=".setupDevDatabase.option.amr.createRfnTemplateMeters"/>
                                    </label>
                                    <tags:inputNameValue path="devAMR.numAdditionalMeters" nameKey=".setupDevDatabase.option.amr.numAdditionalMeters" size="4"/>
                                    <tags:selectNameValue path="devAMR.routeId" nameKey=".setupDevDatabase.option.amr.routeId" items="${allRoutes}" itemLabel="paoName" itemValue="liteID"/>
                                    <tags:inputNameValue path="devAMR.addressRangeMin" nameKey=".setupDevDatabase.option.amr.addressRangeMin" size="10"/>
                                    <tags:inputNameValue path="devAMR.addressRangeMax" nameKey=".setupDevDatabase.option.amr.addressRangeMax" size="10"/>
                                </tags:nameValueContainer2>
                            </li>
                            <li>
                                <tags:hideReveal2 titleKey=".setupDevDatabase.option.amr.meterTypes" showInitially="false" slide="true">
                                    <ul>
										<li>
											<label>
												<input id="f_check_all_meters" type="checkbox"/>
												<b>Check All</b>
											</label>
										</li>
                                        <c:forEach items="${devDbSetupTask.devAMR.meterTypes}" var="meterType" varStatus="status">
                                            <li>
                                            	<label>
                                                	<tags:checkbox path="devAMR.meterTypes[${status.index}].create" styleClass="f_check_single_meter"/>
                                                	<span>${devDbSetupTask.devAMR.meterTypes[status.index].paoType}</span>
                                            	</label>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </tags:hideReveal2>
                            </li>
                        </ul>
                    </tags:hideReveal2>
                </li>
                <li class="box">
                    <form:checkbox path="devCapControl.create" id="createCapControl"/>
                    <span>
                        <tags:hideReveal2 titleKey=".setupDevDatabase.option.capcontrol" showInitially="false" slide="true">
                            <ul>
                                <li>
                                    <tags:nameValueContainer2>
                                        <tags:inputNameValue path="devCapControl.numAreas" nameKey=".setupDevDatabase.option.capcontrol.object.areas" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numSubs" nameKey=".setupDevDatabase.option.capcontrol.object.subs" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numSubBuses" nameKey=".setupDevDatabase.option.capcontrol.object.subBuses" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numFeeders" nameKey=".setupDevDatabase.option.capcontrol.object.feeders" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numCapBanks" nameKey=".setupDevDatabase.option.capcontrol.object.capBanks" size="2"/>
                                        <tags:inputNameValue path="devCapControl.numRegulators" nameKey=".setupDevDatabase.option.capcontrol.object.regulators" size="2"/>
                                        <tags:inputNameValue path="devCapControl.offset" nameKey=".setupDevDatabase.option.capcontrol.object.offset" size="10"/>
                                    </tags:nameValueContainer2>
                                </li>
                                <li>
                                    <tags:hideReveal2 titleKey=".setupDevDatabase.option.capcontrol.cbcTypes" showInitially="false" slide="true">
                                        <ul>
                                            <li>
                                                <input type="radio" name="devCapControl.cbcType" value="${null}" <c:if test="${devDbSetupTask.devCapControl.cbcType == null}">checked="checked"</c:if>/>
                                                <label for="devCapControl.cbcType">None</label>
                                            </li>
                                            <c:forEach items="${devDbSetupTask.devCapControl.cbcTypes}" var="cbcType">
                                                <li>
                                                    <input type="radio" name="devCapControl.cbcType" id="cbcType_${cbcType.paoType}" value="${cbcType.paoType}"
                                                        <c:if test="${devDbSetupTask.devCapControl.cbcType.paoType == cbcType.paoType}">checked="checked"</c:if>/>
                                                    <label for="cbcType_${cbcType.paoType}">${cbcType.paoType}</label>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </tags:hideReveal2>
                                </li>
                                <li>
                                    <tags:hideReveal2 titleKey=".setupDevDatabase.option.capcontrol.regulatorTypes" showInitially="false" slide="true">
                                        <ul>
                                            <c:forEach items="${devDbSetupTask.devCapControl.regulatorTypes}" var="cbcType" varStatus="status">
                                                <li>
                                                	<label>
                                                    	<tags:checkbox path="devCapControl.regulatorTypes[${status.index}].create"/>
                                                    	<span>${devDbSetupTask.devCapControl.regulatorTypes[status.index].paoType}</span>
                                                	</label>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </tags:hideReveal2>
                                </li>
                            </ul>
                        </tags:hideReveal2>
                    </span>
                </li>
                <li class="box">
                    <form:checkbox path="devStars.create" id="createStars"/>
                    <span>
                        <tags:hideReveal2 titleKey=".setupDevDatabase.option.stars" showInitially="false" slide="true">
                            <ul>
                                <li>
                                    <tags:nameValueContainer2>
                                    	<tags:nameValue2 nameKey=".setupDevDatabase.option.stars.parentEnergyCompany">
	                                    	<form:select path="devStars.energyCompany" cssClass="f_ec_select">
	                                    		<c:forEach items="${allEnergyCompanies}" var="energyCompany">
	                                    			<form:option value="${energyCompany.energyCompanyId}">${energyCompany.name}</form:option>
	                                    		</c:forEach>
	                                    		<option value="0" id="createNewEnergyCompanyOpt">*Create New Energy Company</option>
	                                    	</form:select>
                                    	</tags:nameValue2>
                                        <tags:inputNameValue path="devStars.newEnergyCompanyName" nameKey=".setupDevDatabase.option.stars.createNewEC" size="10" inputClass="newEnergyCompanyInput" rowClass="newEnergyCompanyRow dn"/>
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
                                        <ul>
                                        	<li>
                                               	<label>
                                                   <input id="f_check_all_hardware" type="checkbox"/>
                                                   <b>Check All</b>
                                               	</label>
                                            </li>
                                            <c:forEach items="${devDbSetupTask.devStars.devStarsHardware.hardwareTypes}" var="hardwareType" varStatus="status">
                                                <li>
                                                	<label>
                                                    <tags:checkbox path="devStars.devStarsHardware.hardwareTypes[${status.index}].create" styleClass="f_check_single_hardware"/>
                                                    <span><i:inline key="${devDbSetupTask.devStars.devStarsHardware.hardwareTypes[status.index].hardwareType}" /></span>
                                                	</label>
                                                </li>
                                            </c:forEach>
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
            <cti:button id="setupDevDatabaseButtonId" nameKey="setupDevDatabase" type="submit" styleClass="setupDevDatabaseButton pageActionArea" disabled="${setupDbBtnDisabled}"/>
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
        <table class="devDbSetupExecution">
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
            <cti:button nameKey="cancel" styleClass="cancelDevDatabaseSetup pageActionArea"/>
        </div>
    </cti:dataGridCell>
</cti:dataGrid>

<script type="text/javascript">
	jQuery(document).ready(function() {
	    jQuery("input#f_check_all_meters:checkbox").checkAll("input.f_check_single_meter:checkbox");
	    jQuery("input#f_check_all_hardware:checkbox").checkAll("input.f_check_single_hardware:checkbox");
		
	    jQuery('.setupDevDatabaseButton').click(function() {
	    	jQuery('#setupDevDatabaseButtonId').attr("disabled",true);
	        var displayCancelBtn = false;
	        if (jQuery('#createAMR').is(':checked')) {
	        	jQuery('#setupDbAMRProgressBar').show();
	            displayCancelBtn = true;
	        }
	        if (jQuery('#createCapControl').is(':checked')) {
	        	jQuery('#setupDbCCProgressBar').show();
	            displayCancelBtn = true;
	        }
	        if (jQuery('#createStars').is(':checked')) {
	        	jQuery('#setupDbStarsProgressBar').show();
	            displayCancelBtn = true;
	        }
	        if (displayCancelBtn) {
	        	jQuery('#cancelDevDatabaseSetupButtonId').show();
	        }
	        jQuery('#setupDbForm').submit();
	    });
	    
	    jQuery(".f_ec_select").change(function() {
	    	if (jQuery('#createNewEnergyCompanyOpt').is(':selected')) {
	    		jQuery('.newEnergyCompanyRow').show(800, function() {
	    			jQuery('.newEnergyCompanyInput').focus();
	    		});
	    	} else {
	    		jQuery('.newEnergyCompanyRow').hide(500);
	    	}
	    });
	    
	    jQuery('.cancelDevDatabaseSetup').click(function(event) {
	        jQuery.ajax("cancelExecution");
	    });
	    
	});
</script>

</cti:standardPage>
