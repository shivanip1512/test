<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<c:set var="actionUrl" value="/spring/stars/consumer/optout/view2"/>

<cti:standardPage module="consumer" page="optout">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.optout.header"/></h3>
    <div align="center">
	   	<c:if test="${allOptedOut}">
	   		<cti:msg key="yukon.dr.consumer.optout.allOptedOut"/>
	   	</c:if>
       	<c:if test="${!allOptedOut}">
	        <cti:msg key="yukon.dr.consumer.optout.description"/>

    	    <br>
        	<br>

	        <form action="${actionUrl}" method="POST">
		        <table>
		            <tr>
		                <td align="right">
		                    <cti:msg key="yukon.dr.consumer.optout.startDate"/>
		                </td>

		                <cti:formatDate  value="${currentDate}" type="DATE" var="formattedDate"/>

		                <td align="left">
		                    <ct:dateInputCalendar fieldName="startDate" fieldValue="${formattedDate}"/>
		                </td>
		            </tr>

		            <tr>
		                <td align="right">
		                    <cti:msg key="yukon.dr.consumer.optout.duration"/>
		                </td>

		                <td align="left">
		                    <select name="durationInDays">

			                    <c:forEach var="x" begin="1" end="7" step="1">

			                       <c:set var="key" value="${(x == 1) ? 'yukon.dr.consumer.optout.day' : 'yukon.dr.consumer.optout.days' }"/>

		                           <option value="${x}">${x} <cti:msg key="${key}"/></option>

			                    </c:forEach>

		                    </select>
		                </td>
		            </tr>

		            <tr>
		                <td align="center" colspan="2">
		                    <br>
		                    <input type="submit" value="<cti:msg key='yukon.dr.consumer.optout.apply'/>"></input>
		                </td>
		            </tr>
		        </table>
	        </form>
       	</c:if>

        <br><br>
        <!-- Current Opt Outs -->
        <cti:msg key="yukon.dr.consumer.optout.currentOptOuts"/>
        
        <c:choose>
            <c:when test="${fn:length(currentOptOutList) > 0}">
		        <table id="deviceTable" class="miniResultsTable">
		        	<tr class="<ct:alternateRow odd="" even="altRow"/>">
		        		<th><cti:msg key="yukon.dr.consumer.optout.device"/></th>
		        		<th><cti:msg key="yukon.dr.consumer.optout.program"/></th>
		        		<th><cti:msg key="yukon.dr.consumer.optout.status"/></th>
		        		<th><cti:msg key="yukon.dr.consumer.optout.dateActive"/></th>
		        		<th><cti:msg key="yukon.dr.consumer.optout.actions"/></th>
		        	</tr>
		        	
		        	<c:forEach var="optOut" items="${currentOptOutList}">
			        	<tr class="<ct:alternateRow odd="" even="altRow"/>">
			        		<td>${optOut.inventory.displayName}</td>
			        		<td>
		                        <c:forEach var="program" items="${optOut.programList}">
		                            ${program.programName} 
		                        </c:forEach>
		                    </td>
			        		<td><cti:msg key="${optOut.state.formatKey}"/></td>
			        		<td>
                                <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                            </td>
			        		<td>
                                <c:if test="${optOut.state == 'SCHEDULED'}">
			                        <form action="/spring/stars/consumer/optout/cancel" method="post">
				        				<input type="hidden" name="eventId" value="${optOut.eventId}">
				        				<input type="submit" name="submit" value="<cti:msg key="yukon.dr.consumer.optout.cancel"/>">
				        			</form>
			        			</c:if>
			        		</td>
			        	</tr>
		        	</c:forEach>
		        </table>
		    </c:when>
		    <c:otherwise>
                <br><cti:msg key="yukon.dr.consumer.optout.noCurrentOptOuts"/>
		    </c:otherwise>
        </c:choose>
        
        <br><br>
        <!-- Opt Out Limits -->
        <cti:msg key="yukon.dr.consumer.optout.optOutLimits"/>
        
        <table id="deviceTable" class="miniResultsTable">
            <tr class="<ct:alternateRow odd="" even="altRow"/>">
                <th><cti:msg key="yukon.dr.consumer.optout.device"/></th>
                <th><cti:msg key="yukon.dr.consumer.optout.used"/></th>
                <th><cti:msg key="yukon.dr.consumer.optout.remaining"/></th>
            </tr>
            
            <c:forEach var="optOutCount" items="${optOutCountList}">
                <tr class="<ct:alternateRow odd="" even="altRow"/>">
                    <td>${optOutCount.inventory.displayName}</td>
                    <td>${optOutCount.usedOptOuts}</td>
                    <td>
                        <c:choose>
                            <c:when test="${optOutCount.remainingOptOuts == -1}">
                                <cti:msg key="yukon.dr.consumer.optout.unlimitedRemaining"/>
                            </c:when>
                            <c:otherwise>
                                ${optOutCount.remainingOptOuts}
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <br><br>
        <!-- Opt Out History -->
        <cti:msg key="yukon.dr.consumer.optout.optOutHistory"/>
        <c:choose>
            <c:when test="${fn:length(previousOptOutList) > 0}">
		        <table id="deviceTable" class="miniResultsTable">
		        	<tr class="<ct:alternateRow odd="" even="altRow"/>">
		        		<th><cti:msg key="yukon.dr.consumer.optout.device"/></th>
		        		<th><cti:msg key="yukon.dr.consumer.optout.program"/></th>
		        		<th><cti:msg key="yukon.dr.consumer.optout.dateScheduled"/></th>
		        		<th><cti:msg key="yukon.dr.consumer.optout.dateActive"/></th>
		        		<th><cti:msg key="yukon.dr.consumer.optout.durationHeader"/></th>
		        	</tr>
		        	
		        	<c:forEach var="optOut" items="${previousOptOutList}">
			        	<tr class="<ct:alternateRow odd="" even="altRow"/>">
			        		<td>${optOut.inventory.displayName}</td>
			        		<td>
		                        <c:forEach var="program" items="${optOut.programList}">
		                            ${program.programName} 
		                        </c:forEach>
		                    </td>
			        		<td>
			        		   <cti:formatDate value="${optOut.scheduledDate}" type="DATEHM"/>
			        		</td>
			        		<td>
			        		   <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                            </td>
			        		<td>
		                        <c:choose>
		                            <c:when test="${optOut.state == 'SCHEDULE_CANCELED'}">
		                                <cti:msg key="yukon.dr.consumer.optout.canceled"/>
		                            </c:when>
		                            <c:otherwise>
					        		    <cti:formatTimePeriod startDate="${optOut.startDate}" endDate="${optOut.stopDate}" type="DH"/>
		                            </c:otherwise>
		                        </c:choose>
			        		</td>
			        	</tr>
		        	</c:forEach>
		        </table>
		    </c:when>
		    <c:otherwise>
                <br><cti:msg key="yukon.dr.consumer.optout.noPreviousOptOuts"/>
		    </c:otherwise>
        </c:choose>
        
    </div>     
    
</cti:standardPage>    
    