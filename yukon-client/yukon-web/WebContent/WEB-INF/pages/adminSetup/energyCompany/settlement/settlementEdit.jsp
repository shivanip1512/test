<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="settlements.${mode}">
<cti:includeScript link="/resources/js/common/yukon.field.helper.js"/>

    <tags:setFormEditMode mode="${mode}"/>
    
<script>
    function toggleAvailableRatesInputs(availableRatesCheckBox) {

    var parentTr = availableRatesCheckBox.up('tr');
    var rateConfigTrs = parentTr.nextSiblings();

    for (var i = 0; i < rateConfigTrs.length; i++) {
    	var rateConfigTr = rateConfigTrs[i];
    	if (availableRatesCheckBox.checked) {
            rateConfigTr.removeClassName('disabled');

            rateConfigTrs.each(function(elem) {
                elem.select("input:text").each(function(elem2) {
                    elem2.disabled = false;
                })
             });
            
        } else {
            rateConfigTr.addClassName('disabled');

            rateConfigTrs.each(function(elem) {
                elem.select("input:text").each(function(elem2) {
                    elem2.disabled = true;
                })
             });
        }
    }
}

$(function() {
    var availableRateCheckBoxTds = $$(".availableRateCheckBoxTd");

    for (var i = 0; i < availableRateCheckBoxTds.length; i++) {
        var availableRateCheckBoxTd = availableRateCheckBoxTds[i];

        toggleAvailableRatesInputs(availableRateCheckBoxTd.down('input'));
    }
});

</script>

    <cti:url value="edit" var="editUrl">
        <cti:param name="ecId" value="${ecId}" />
    </cti:url>

    <form:form modelAttribute="settlementDto" method="post" action="${edit}">
        <cti:csrfToken/>
        <tags:sectionContainer2 nameKey="configurations">
            <table class="name-value-table natural-width">
                <c:forEach var="liteSettlementConfig" items="${settlementDto.editableLiteSettlementConfigs}" varStatus="status">
                    <tr>
                        <td class="name"><label>${fn:escapeXml(liteSettlementConfig.fieldName)}:</label></td>
                        <td class="value">
                            <span class="focusableFieldHolder">
                                <tags:input path="editableLiteSettlementConfigs[${status.index}].fieldValue"/>
                            </span>
                            <tags:hidden path="editableLiteSettlementConfigs[${status.index}].configID"/>
                            <span class="focused-field-description">${liteSettlementConfig.description}</span>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </tags:sectionContainer2>
        
        <tags:sectionContainer2 nameKey="availableRates">
            <c:forEach var="availableRate" items="${settlementDto.availableRates}" varStatus="availRatesStatus">
                <tags:hidden path="availableRates[${availRatesStatus.index}].entryId"/>
                <table class="name-value-table natural-width">
                    <tr>
                        <td colspan="3" class="availableRateCheckBoxTd"><tags:checkbox path="availableRates[${availRatesStatus.index}].enabled" 
                            onclick="toggleAvailableRatesInputs(this);" /> ${fn:escapeXml(availableRate.availableRateName)}
                        </td>
                    </tr>
                    <c:forEach var="liteSettlementConfig" items="${availableRate.rateConfigurations}" varStatus="rateConfigStatus">
                        <tr>
                            <td width="30"></td>
                            <td class="name"><label>${fn:escapeXml(liteSettlementConfig.fieldName)}:</label></td>
                            <td class="value">
                                <span class="focusableFieldHolder">
                                     <form:input path="availableRates[${availRatesStatus.index}].rateConfigurations[${rateConfigStatus.index}].fieldValue"/>
                                </span>
                                <tags:hidden path="availableRates[${availRatesStatus.index}].rateConfigurations[${rateConfigStatus.index}].configID"/>
                                <tags:hidden path="availableRates[${availRatesStatus.index}].rateConfigurations[${rateConfigStatus.index}].fieldName"/>
                                <tags:hidden path="availableRates[${availRatesStatus.index}].rateConfigurations[${rateConfigStatus.index}].description"/>
                                <tags:hidden path="availableRates[${availRatesStatus.index}].rateConfigurations[${rateConfigStatus.index}].refEntryID"/>
                                <span class="focused-field-description">${liteSettlementConfig.description}</span>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                <br>
            </c:forEach>
        </tags:sectionContainer2>
    
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="save" type="submit" name="save"/>
                <cti:button nameKey="cancel" type="submit" name="cancel"/>
            </cti:displayForPageEditModes>
        </div>
    </form:form>

</cti:standardPage>
