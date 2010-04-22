<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>
<%@ taglib tagdir="/WEB-INF/tags/dr/forms" prefix="drForms" %>

<cti:standardPage module="operator" page="appliance.edit">
	
	<tags:setFormEditMode mode="${mode}"/>

	<cti:includeScript link="/JavaScript/yukonGeneral.js"/>

    <script type="text/javascript">

    	alignTableColumnsByTable('#enrollmentTable', '#hardwareSummaryTable');
	
    </script>
    
    <table>
      <tr>
        <td rowspan="3" valign="top">
            <tags:formElementContainer nameKey="applianceInfo">
                <form id="deleteForm" action="/spring/stars/operator/appliances/applianceDelete" method="post">
                    <input type="hidden" name="accountId" value="${accountId}">
                    <input type="hidden" name="applianceId" value="${starsAppliance.applianceID}" >
                </form>
                
                <form id="cancelForm" action="/spring/stars/operator/appliances/applianceList" method="post">
                    <input type="hidden" name="accountId" value="${accountId}">
                    <input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
                </form>
                
                <drForms:starsApplianceForm formId="updateForm" formUrl="/spring/stars/operator/appliances/applianceUpdate" />

                <br>

                <cti:displayForPageEditModes modes="EDIT">
                    <tags:slowInput2 myFormId="updateForm" key="save"/>
                    <tags:slowInput2 myFormId="deleteForm" key="delete"/>
                </cti:displayForPageEditModes>
                <tags:slowInput2 myFormId="cancelForm" key="cancel"/>
                        
            </tags:formElementContainer>
        </td>
		<td rowspan="3" width="50px"></td>
        <td valign="top">
            <c:if test="${not empty displayableInventoryEnrollment}">
                <tags:formElementContainer nameKey="enrollment">
                    <tags:nameValueContainer2 id="enrollmentTable">
                        <tags:nameValue2 nameKey=".program">
                            <spring:escapeBody htmlEscape="true">${displayableInventoryEnrollment.programName.displayName}</spring:escapeBody>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".loadGroup">
                            <spring:escapeBody htmlEscape="true">${displayableInventoryEnrollment.loadGroupName}</spring:escapeBody>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".relay">
                            <spring:escapeBody htmlEscape="true">${displayableInventoryEnrollment.relay}</spring:escapeBody>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
            </c:if>
        </td>
      </tr>
      <tr><td></td></tr>
      <tr>
        <td valign="top">
            <c:if test="${not empty hardware}">
                <tags:formElementContainer nameKey="hardwareSummary">
                    <tags:nameValueContainer2 id="hardwareSummaryTable">
                        <tags:nameValue2 nameKey=".category">
                            <spring:escapeBody htmlEscape="true">${hardware.categoryName}</spring:escapeBody>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".hardwareType">
                            <spring:escapeBody htmlEscape="true">${hardware.displayType}</spring:escapeBody>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serialNumber">
                            <spring:escapeBody htmlEscape="true">${hardware.serialNumber}</spring:escapeBody>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
            </c:if>          
        </td>
      </tr>
 	</table>
</cti:standardPage>