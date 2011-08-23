<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<tags:standardPageFragment module="amr"	pageName="porterResponseMonitor" fragmentName="addRule">

	<table class="compactResultsTable">
		<tr id="rule_${newMapKey}" class="ruleTableRow">
			<td><input name="rules[${newMapKey}].ruleOrder" type="text" value="${nextOrder}" class="ruleOrder" size="1" /></td>
            <td class="checkBox outcomeColumn">
                <input name="rules[${newMapKey}].success" type="checkbox"/>
                <span><i:inline key=".rule.success"/></span>
            </td>
			<td><input name="rules[${newMapKey}].errorCodes" type="text" size="8" /></td>
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
                        <option value="${state.liteID}" title="${state.stateText}">
                            <spring:escapeBody htmlEscape="true">
                                ${state.stateText}
                            </spring:escapeBody>
                        </option>
                    </c:forEach>
                </select>
            </td>
			<td class="removeColumn">
                <cti:img nameKey="delete" styleClass="removeRow pointer"/>
            </td>
		</tr>
        <tr id="rule_${newMapKey}_undo" class="undoRow" style="display: none">
            <td colspan="1">
                <cti:img nameKey="delete"/>
            </td>
            <td colspan="4" class="removed"><i:inline key=".rulesTable.removedRow"/></td>
            <td colspan="1" class="removed">
                <span class="undoRemoveBtn"><i:inline key=".rulesTable.undoLink"/></span>
            </td>
        </tr>
    </table>

</tags:standardPageFragment>