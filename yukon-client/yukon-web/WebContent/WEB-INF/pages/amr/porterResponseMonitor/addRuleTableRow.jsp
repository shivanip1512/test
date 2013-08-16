<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:standardPageFragment module="amr"	pageName="porterResponseMonitor" fragmentName="addRule">

	<table class="compactResultsTable">
		<tr id="rule_${newMapKey}" class="ruleTableRow">
			<td class="orderColumn">
                <input name="rules[${newMapKey}].ruleOrder" type="text" value="${nextOrder}" class="ruleOrder" maxlength="2" size="2"/>
            </td>
            <td class="checkBox outcomeColumn">
                <label>
                    <input name="rules[${newMapKey}].success" type="checkbox"/>
                    <i:inline key=".rule.success"/>
                </label>
            </td>
			<td class="errorsColumn">
                <input name="rules[${newMapKey}].errorCodes" type="text" maxlength="4" size="4"/>
            </td>
			<td class="matchColumn">
                <select name="rules[${newMapKey}].matchStyle">
				    <c:forEach items="${matchStyleChoices}" var="style">
					   <option value="${style}"><i:inline key="${style.formatKey}" /></option>
				    </c:forEach>
                </select>
            </td>
			<td class="stateColumn">
                <select name="rules[${newMapKey}].state">
                    <c:forEach items="${statesList}" var="state">
                        <option value="${state.liteID}" title="${state.stateText}">${fn:escapeXml(state.stateText)}</option>
                    </c:forEach>
                </select>
                <cti:button classes="f-remove fr" icon="icon-cross" renderMode="buttonImage"/>
            </td>
		</tr>
        <tr style="display: none" id="rule_${newMapKey}_undo" class="undo-row">
            <td colspan="5">
                <i:inline key=".rulesTable.removedRow"/>
                <a href="javascript:void(0)" class="undo"><i:inline key=".rulesTable.undoLink"/></a>
            </td>
        </tr>
    </table>

</tags:standardPageFragment>