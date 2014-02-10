<%@page import="com.cannontech.core.roleproperties.YukonRoleProperty"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<c:set var="actionUrl" value="/stars/consumer/optout/deviceSelection"/>

<cti:standardPage module="consumer" page="optout">
    <cti:standardMenu />
    
    <h3><cti:msg key="yukon.dr.consumer.optout.header"/></h3>
    <div align="center">
        <c:if test="${!empty currentOptOutList && !optOutsAvailable}">
            <cti:msg key="yukon.dr.consumer.optout.noOptOutsAvailable"/>
        </c:if>
       	<c:if test="${optOutsAvailable}">
	        <cti:msg key="yukon.dr.consumer.optout.description"/>

    	    <br>
        	<br>

	        <form action="${actionUrl}" method="POST">
                <cti:csrfToken/>
		        <table>
		            <tr>
		                <td align="right">
		                    <cti:msg key="yukon.dr.consumer.optout.startDate"/>
		                </td>

		                <cti:formatDate  value="${currentDate}" type="DATE" var="formattedDate"/>

                        <cti:getProperty var="optOutTodayOnly" property="RESIDENTIAL_OPT_OUT_TODAY_ONLY" />

		                <td align="left">
                            <c:choose>
                                <c:when test="${optOutTodayOnly}">
                                    <input type="hidden" name="startDate" value="${formattedDate}" />
                                    ${formattedDate}
                                </c:when>
                                <c:otherwise>
                                    <dt:date name="startDate" value="${currentDate}" />
	       	                    </c:otherwise>
	       	                </c:choose>
		                </td>
		            </tr>

		            <tr>
		                <td align="right">
		                    <cti:msg key="yukon.dr.consumer.optout.duration"/>
		                </td>

		                <td align="left">
		                    <select name="durationInDays">

			                    <c:forEach var="optOutPeriod" items="${optOutPeriodList}">

			                       <c:set var="key" value="${(optOutPeriod == 1) ? 'yukon.dr.consumer.optout.day' : 'yukon.dr.consumer.optout.days' }"/>

		                           <option value="${optOutPeriod}">${optOutPeriod} <cti:msg key="${key}"/></option>

			                    </c:forEach>

		                    </select>
		                </td>
		            </tr>

		            <tr>
		                <td align="center" colspan="2">
		                    <br>
		                    <input type="submit" value="<cti:msg key='yukon.dr.consumer.optout.apply'/>" class="button">
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
		        <table id="deviceTable" class="results-table">
                    <thead>
    		        	<tr>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.device"/></th>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.program"/></th>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.status"/></th>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.dateActive"/></th>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.actions"/></th>
    		        	</tr>
                    </thead>
		        	<tfoot></tfoot>
                    <tbody>
    		        	<c:forEach var="optOut" items="${currentOptOutList}">
    			        	<tr>
    			        		<td>
                                    <spring:escapeBody htmlEscape="true">
                                        ${optOut.inventory.displayName}
                                    </spring:escapeBody>
                                </td>
                                        
    			        		<td>
    		                        <c:forEach var="program" items="${optOut.programList}">
    		                            <spring:escapeBody htmlEscape="true">
                                            ${program.programName}
                                        </spring:escapeBody> 
    		                        </c:forEach>
    		                    </td>
    			        		<td><cti:msg key="${optOut.state.formatKey}"/></td>
    			        		<td>
                                    <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                                </td>
    			        		<td>
                                    <c:if test="${optOut.state == 'SCHEDULED'}">
    			                        <form action="/stars/consumer/optout/confirmCancel" method="post">
                                            <cti:csrfToken/>
    				        				<input type="hidden" name="eventId" value="${optOut.eventId}">
    				        				<input type="submit" name="submit" value="<cti:msg key="yukon.dr.consumer.optout.cancel"/>" class="button">
    				        			</form>
    			        			</c:if>
    			        		</td>
    			        	</tr>
    		        	</c:forEach>
                    </tbody>
		        </table>
		    </c:when>
		    <c:otherwise>
                <br><cti:msg key="yukon.dr.consumer.optout.noCurrentOptOuts"/>
		    </c:otherwise>
        </c:choose>
        
        <br><br>
        <!-- Opt Out Limits -->
        <cti:msg key="yukon.dr.consumer.optout.optOutLimits"/>
        
        <table id="deviceTable" class="results-table">
            <thead>
                <tr>
                    <th><cti:msg key="yukon.dr.consumer.optout.device"/></th>
                    <th><cti:msg key="yukon.dr.consumer.optout.used"/></th>
                    <th><cti:msg key="yukon.dr.consumer.optout.remaining"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="inventory" items="${displayableInventories}">
                    <tr>
                        <td>
                            <spring:escapeBody htmlEscape="true">
                                ${inventory.displayName}
                            </spring:escapeBody>
                        </td>
                        <td>${optOutCounts[inventory.inventoryId].usedOptOuts}</td>
                        <td>
                            <c:choose>
                                <c:when test="${optOutCounts[inventory.inventoryId].remainingOptOuts == -1}">
                                    <cti:msg key="yukon.dr.consumer.optout.unlimitedRemaining"/>
                                </c:when>
                                <c:otherwise>
                                    ${optOutCounts[inventory.inventoryId].remainingOptOuts}
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <br><br>
        <!-- Opt Out History -->
        <cti:msg key="yukon.dr.consumer.optout.optOutHistory"/>
        <c:choose>
            <c:when test="${fn:length(previousOptOutList) > 0}">
		        <table id="deviceTable" class="results-table">
                    <thead>
    		        	<tr>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.device"/></th>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.program"/></th>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.dateScheduled"/></th>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.dateActive"/></th>
    		        		<th><cti:msg key="yukon.dr.consumer.optout.durationHeader"/></th>
    		        	</tr>
		        	</thead>
                    <tfoot></tfoot>
                    <tbody>
    		        	<c:forEach var="optOut" items="${previousOptOutList}">
    			        	<tr>
    			        		<td>
                                    <spring:escapeBody htmlEscape="true">
                                        ${optOut.inventory.displayName}
                                    </spring:escapeBody>
                                </td>
    			        		<td>
    		                        <c:forEach var="program" items="${optOut.programList}">
    		                            <spring:escapeBody htmlEscape="true">
                                            ${program.programName}
                                        </spring:escapeBody> 
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
                    </tbody>
		        </table>
		    </c:when>
		    <c:otherwise>
                <br><cti:msg key="yukon.dr.consumer.optout.noPreviousOptOuts"/>
		    </c:otherwise>
        </c:choose>
        
    </div>     
    
</cti:standardPage>    
    