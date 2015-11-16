<%@ attribute name="headerKey" required="true" %>
<%@ attribute name="attributeReadings" required="true" type="com.cannontech.common.device.model.PreviousReadings" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:if test="${not empty attributeReadings.previous36 || not empty attributeReadings.previous3Months}">
    <cti:msgScope paths="widgets.touWidget">
    
        <cti:msg2 key="${headerKey}" var="headerName"/>
        <tags:sectionContainer title="${headerName}">
    		<tags:nameValueContainer2>
                <cti:msg2 key='${attributeReadings.attribute}' var="currentAttribute"/>
    			<tags:nameValue2 nameKey=".currentReading" argument="${currentAttribute}">
    				<span id="${attributeReadings.attribute.key}_latestReading">
                        <cti:pointValueFormatter format="FULL" value="${attributeReadings.previous36[0]}" />
                    </span>
    			</tags:nameValue2>
                <cti:msg2 key='${attributeReadings.attribute}' var="previousAttribute"/>
                <tags:nameValue2 nameKey=".previousReadings" argument="${previousAttribute}">
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
    			<tags:nameValue2 nameKey=".totalConsumption">
    				<span id="${attributeReadings.attribute.key}_total">0.000</span>
    			</tags:nameValue2>
    		</tags:nameValueContainer2>
        </tags:sectionContainer>
    </cti:msgScope>
</c:if>