<%@ attribute name="headerKey" required="true" %>
<%@ attribute name="attributeReadings" required="true" type="com.cannontech.common.device.model.PreviousReadings" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:msg2 key="${headerKey}" var="headerName" />
<cti:msgScope paths="widgets.touWidget">
<style>
<!--
table.compactResultsTable tr.vertical-middle td,
table.compactResultsTable tr.vertical-middle td.name {
	vertical-align: middle;
}
-->
</style>

<c:if test="${not empty attributeReadings.previous36 || not empty attributeReadings.previous3Months}">
	<table class="compactResultsTable">
		<tr>
            <th colspan="2">${headerName}</th>
        </tr>
		<tr class="vertical-middle">
			<td>
				<ct:nameValueContainer>
                <cti:msg2 var="currentReading" key=".currentReading" argument="${attributeReadings.attribute.description}" />
					<ct:nameValue name="${currentReading}" nameColumnWidth="40%">
						<span id="${attributeReadings.attribute.key}_latestReading"><cti:pointValueFormatter format="FULL"
							                                                             value="${attributeReadings.previous36[0]}" /></span>
					</ct:nameValue>
			 		<ct:nameValueGap gapHeight="6px" />
					<cti:msg2 var="previousReadings" key=".previousReadings" argument="${attributeReadings.attribute.description}" />
                    <ct:nameValue name="${previousReadings}" >
					 	<select id="${attributeReadings.attribute.key}_previousReading" 
					 	        onchange="calculatePreviousReadingDifference('${attributeReadings.attribute.key}',${attributeReadings.previous36[0].value}) ">
					        <cti:formatDate type="DATE" var="cutOff"
							    value="${attributeReadings.cutoffDate}" />
                            <cti:msg2 var="recentReadings" key=".recentReadings" argument="${cutOff}"/>
							<optgroup id="firstOptGroup" label="${recentReadings}">
								<c:forEach items="${attributeReadings.previous36}" var="reading">
							    	<option value="${reading.value}">
							            <cti:pointValueFormatter format="FULL"
							                value="${reading}" />
							        </option>
							    </c:forEach>
							</optgroup>
							<c:if test="${not empty attributeReadings.previous3Months}">
							    <cti:formatDate type="DATE" var="cutOff"
							        value="${attributeReadings.cutoffDate}" />
                                <cti:msg2 var="dailyReadings" key=".dailyReadings" argument="${cutOff}"/>
							    <optgroup label="${dailyReadings}">
							        <c:forEach items="${attributeReadings.previous3Months}" var="reading">
										<option value="${reading.value}">
							                <cti:pointValueFormatter format="FULL"
							                        value="${reading}" />
							            </option>
							        </c:forEach>
							    </optgroup>
							</c:if>
					    </select>
					</ct:nameValue>
					<ct:nameValueGap gapHeight="6px" />
                    <cti:msg2 var="totalConsumption" key=".totalConsumption"/>
					<ct:nameValue name="${totalConsumption}">
						<span id="${attributeReadings.attribute.key}_total">0.000</span>
					</ct:nameValue>
					<ct:nameValueGap gapHeight="6px" />
				</ct:nameValueContainer>
			</td>
		</tr>
	</table>
	<br />
</c:if>
</cti:msgScope>
