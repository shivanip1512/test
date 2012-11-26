<%@ attribute name="headerKey" required="true" %>
<%@ attribute name="attributeReadings" required="true" type="com.cannontech.common.device.model.PreviousReadings" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

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
				<tags:nameValueContainer2>
					<tags:nameValue2 nameKey=".currentReading" argument="<cti:msg2 key='${attributeReadings.attribute}'/>">
						<span id="${attributeReadings.attribute.key}_latestReading">
                            <cti:pointValueFormatter format="FULL" value="${attributeReadings.previous36[0]}" />
                        </span>
					</tags:nameValue2>
			 		<tags:nameValueGap2 gapHeight="6px" />
                    <tags:nameValue2 nameKey=".previousReadings" argument="<cti:msg2 key='${attributeReadings.attribute}'/>">
					 	<select id="${attributeReadings.attribute.key}_previousReading" 
					 	        onchange="calculatePreviousReadingDifference('${attributeReadings.attribute.key}',${attributeReadings.previous36[0].value}) ">
					        <cti:formatDate type="DATE" var="cutOff" value="${attributeReadings.cutoffDate}" />
                            <cti:msg2 var="recentReadings" key=".recentReadings" argument="${cutOff}"/>
							<optgroup id="firstOptGroup" label="${recentReadings}">
								<c:forEach items="${attributeReadings.previous36}" var="reading">
							    	<option value="${reading.value}">
							            <cti:pointValueFormatter format="FULL" value="${reading}" />
							        </option>
							    </c:forEach>
							</optgroup>
							<c:if test="${not empty attributeReadings.previous3Months}">
							    <cti:formatDate type="DATE" var="cutOff" value="${attributeReadings.cutoffDate}" />
                                <cti:msg2 var="dailyReadings" key=".dailyReadings" argument="${cutOff}"/>
							    <optgroup label="${dailyReadings}">
							        <c:forEach items="${attributeReadings.previous3Months}" var="reading">
										<option value="${reading.value}">
							                <cti:pointValueFormatter format="FULL" value="${reading}" />
							            </option>
							        </c:forEach>
							    </optgroup>
							</c:if>
					    </select>
					</tags:nameValue2>
					<tags:nameValueGap2 gapHeight="6px" />
					<tags:nameValue2 nameKey=".totalConsumption">
						<span id="${attributeReadings.attribute.key}_total">0.000</span>
					</tags:nameValue2>
					<tags:nameValueGap2 gapHeight="6px" />
				</tags:nameValueContainer2>
			</td>
		</tr>
	</table>
	<br />
</c:if>
</cti:msgScope>
