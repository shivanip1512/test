<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="i18n" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<cti:standardPage module="operator" page="controlHistory">

    <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>" >
	<link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/StarsConsumerStyles.css"/>" >

	<div align="center"><i18n:inline key=".header" /></div>
	<br>
	<table border="1" cellspacing="0" cellpadding="0" align="center" class="enrolledPrograms">
		<tr bgcolor="#FFFFFF">
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".enrolledPrograms" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center">Last Control</div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".controlHistorySummary" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center">Recent Control History</div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".completeControlHistory" /></div>
			</td>
		</tr>

		<c:forEach var="program" items="${programs}">
			<tr>
				<td>
					<table>
						<tr>
							<td align="center">
								<img src="/WebConfig/${program.applianceCategoryLogo}" />
								<br/><br/> 
								${program.programName}
							</td>
						</tr>
					</table>
				</td>
				<td>
					<table align="center" width="99%">
					    <c:forEach var="invBaseControlHistory" items="${controlHistoryPastMonthEventMap[program]}">
					        
					        <tr>
					            <th width="15%"><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.deviceLabel"/></th>
					            <th width="25%"><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.startDate"/></th>
					            <th width="25%"><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.endDate"/></th>
					            <th width="35%"><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.controlDuration"/></th>
					        </tr>
					        
					        <tr>
					        	<td width="15%" valign="top" rowspan="${rowspan + 1}">
					            	<spring:escapeBody htmlEscape="true">${invBaseControlHistory.inventoryBase.deviceLabel}</spring:escapeBody>
					        	</td>
				        	</tr>

					        <c:set var="eventList" value="${invBaseControlHistory.controlHistoryEventList}"/>
					        <c:set var="eventListSize" value="${fn:length(eventList)}"/>
					        <c:set var="rowspan" value="${eventListSize > 0 ? eventListSize : 1}"/>

					        <c:choose>
					            <c:when test="${eventListSize > 0}">
					            	<tr>
					            		<td colspan="3">
					            			<table align="center" width="99%">
								                <c:forEach var="event" items="${eventList}">
								                    <tr class="<tags:alternateRow odd='altRow' even=''/>">
								                        <td width="29.4%"><cti:formatDate  value="${event.startDate}" type="BOTH"/></td>
								                        <td width="29.4%"><cti:formatDate  value="${event.endDate}" type="BOTH"/></td>
								                        <td width="41.2%"><cti:formatDuration type="HM" startDate="${event.startDate}" endDate="${event.endDate}" /></td>
								                    </tr>
								                </c:forEach>
								                <tr>
								                    <td colspan="4"><div style="height: 0.5em;"></div></td>
								                </tr>
											</table>	
					            		</td>
					            	</tr>
					            </c:when>
					            <c:otherwise>
					                <tr class="${rowClass}">
					                    <td>---</td>
					                    <td>---</td>
					                    <td>---</td>
					                </tr>
					            </c:otherwise>
					        </c:choose>
					        
					    </c:forEach>
					</table>
				</td>

				<td width="150" valign="top">
					<c:forEach var="invBaseControlHistory" items="${controlHistoryAllEventMap[program]}">
						<c:set var="controlHistoryEventList" value="${invBaseControlHistory.controlHistoryEventList}"/>
					    <c:set var="controlHistoryEventListSize" value="${fn:length(controlHistoryEventList)}"/>
					    <c:choose>
					    	<c:when test="${controlHistoryEventListSize > 0}">
					    		The device, ${invBaseControlHistory.inventoryBase.deviceLabel}, was last controlled from
					    		<cti:formatDate value="${controlHistoryEventList[0].startDate}" type="BOTH"/>
					    		to
					    		<cti:formatDate value="${controlHistoryEventList[0].endDate}" type="BOTH"/>
					    		for a total of 
					    		<cti:formatDuration type="HM" startDate="${controlHistoryEventList[0].startDate}" endDate="${controlHistoryEventList[0].endDate}" />
					    	</c:when>
					    	<c:otherwise>
					    		The device, ${invBaseControlHistory.inventoryBase.deviceLabel}, has not been controlled for this program.
					    	</c:otherwise>
					    </c:choose>
					    <br><br>
					</c:forEach>
				</td>
					
				
				<td width="150" valign="top" style="padding-left: 10px">
				  <tags:nameValueContainer>
					<tags:nameValue name="Today">${weekProgramDurationMap[program]}</tags:nameValue>
					<tags:nameValue name="Past Month">${monthProgramDurationMap[program]}</tags:nameValue>
					<tags:nameValue name="Annual">${yearProgramDurationMap[program]}</tags:nameValue>
				  </tags:nameValueContainer>
				</td>

				
				</td>
				<td width="130">
					<form method="POST" action="ContHist.jsp">
						<input type="hidden" name="prog" value="${program.programId}" />
						<table width="100" border="0" cellspacing="0" cellpadding="3" align="center">
							<tr>
								<td width="180" valign="top" align="center">
									<select name="Period">
										<option value="Past Week">Past Week</option>
										<option value="Past Month">Past Month</option>
										<option value="Past Forever Ever">All</option>
									</select>
								</td>
							</tr>
							<tr>
								<td width="180" valign="top" align="center">
									<input type="submit" name="Input2" value="View">
								</td>
							</tr>
						</table>
					</form>
				</td>
			</tr>

		</c:forEach>
	</table>
	<br>
	
	
	<div align="center"><i18n:inline key=".header" /></div>
	<br>
	<table border="1" cellspacing="0" cellpadding="0" align="center" class="enrolledPrograms">
		<tr bgcolor="#FFFFFF">
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".enrolledPrograms" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".todaysControlHistory" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".controlHistorySummary" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".completeControlHistory" /></div>
			</td>
		</tr>

		<c:forEach var="program" items="${programs}">
			<tr>
				<td>
					<table>
						<tr>
							<td align="center">
								<img src="/WebConfig/${program.applianceCategoryLogo}" />
								<br/><br/> 
								${program.programName}
							</td>
						</tr>
					</table>
				</td>

				<td valign="top">
	
					<c:forEach var="invBaseControlHistory" items="${controlHistoryAllEventMap[program]}">
					  <c:set var="controlHistoryEventList" value="${invBaseControlHistory.controlHistoryEventList}"/>
					  <c:set var="controlHistoryEventListSize" value="${fn:length(controlHistoryEventList)}"/>
					  <table align="center" width="99%">
						<tr>
						  <th colspan="3">
						    <spring:escapeBody htmlEscape="true">${invBaseControlHistory.inventoryBase.deviceLabel} -</spring:escapeBody>
					      </th>
						</tr>
					
						<tr>
						  <td colspan="3">
						    <span style="margin-left: 20px">
 						      <c:choose>
						    	<c:when test="${controlHistoryEventListSize > 0}">
						    		The device, ${invBaseControlHistory.inventoryBase.deviceLabel}, was last controlled from
						    		<cti:formatDate value="${controlHistoryEventList[0].startDate}" type="BOTH"/>
						    		to
						    		<cti:formatDate value="${controlHistoryEventList[0].endDate}" type="BOTH"/>
						    		for a total of 
						    		<cti:formatDuration type="HM" startDate="${controlHistoryEventList[0].startDate}" endDate="${controlHistoryEventList[0].endDate}" />
						    	</c:when>
						    	<c:otherwise>
						    		The device, ${invBaseControlHistory.inventoryBase.deviceLabel}, has never been controlled for this program.
						    	</c:otherwise>
						    </c:choose>
						    </span>
						    <br><br>
						  </td>
						</tr>
						<tr>
						  <td>
						  
						<div style="margin-left: 20px">
						<table align="center" width="99%">
						
						  <tr>
					        <th width="25%"><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.startDate"/></th>
					        <th width="25%"><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.endDate"/></th>
					        <th width="35%"><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.controlDuration"/></th>
					      </tr>
					        
					    <c:choose>
					            <c:when test="${controlHistoryEventListSize > 0}">
					            	<tr>
					            		<td colspan="3">
					            			<table style="border-collapse: collapse;" align="center" width="99%">
								                <c:forEach var="event" items="${controlHistoryEventList}">
								                    <tr class="<tags:alternateRow odd='altRow' even=''/>">
								                        <td width="29.4%"><cti:formatDate  value="${event.startDate}" type="BOTH"/></td>
								                        <td width="29.4%"><cti:formatDate  value="${event.endDate}" type="BOTH"/></td>
								                        <td width="41.2%"><cti:formatDuration type="HM" startDate="${event.startDate}" endDate="${event.endDate}" /></td>
								                    </tr>
								                </c:forEach>
								                <tr>
								                    <td colspan="4"><div style="height: 0.5em;"></div></td>
								                </tr>
											</table>	
					            		</td>
					            	</tr>
					            </c:when>
					            <c:otherwise>
					                <tr class="${rowClass}">
					                    <td>---</td>
					                    <td>---</td>
					                    <td>---</td>
					                </tr>
					            </c:otherwise>
					        </c:choose>
					        </table>
					    </div>
					     </td>
					    </tr>
					  </table>
  				    </c:forEach>
				</td>
					
				
				<td width="150" valign="top">
					<table width="150" border="0" cellspacing="0" bgcolor="white"
						cellpadding="2" align="center">
						<tr>
							<td height="23" class="TableCell" width="117">Today</td>
							<td height="23" class="TableCell" width="95">${weekProgramDurationMap[program]}</td>
						</tr>
						<tr>
							<td height="23" class="TableCell" width="117">Past Month</td>
							<td height="23" class="TableCell" width="95">${monthProgramDurationMap[program]}</td>
						</tr>
						<tr>
							<td height="23" class="TableCell" width="117">Annual</td>
							<td height="23" class="TableCell" width="95">${yearProgramDurationMap[program]}</td>
						</tr>
					</table>
				</td>
				<td width="130">
					<form method="POST" action="ContHist.jsp">
						<input type="hidden" name="prog" value="${program.programId}" />
						<table width="100" border="0" cellspacing="0" cellpadding="3" align="center">
							<tr>
								<td width="180" valign="top" align="center">
									<select name="Period">
										<option value="Past Week">Past Week</option>
										<option value="Past Month">Past Month</option>
										<option value="Past Forever Ever">All</option>
									</select>
								</td>
							</tr>
							<tr>
								<td width="180" valign="top" align="center">
									<input type="submit" name="Input2" value="View">
								</td>
							</tr>
						</table>
					</form>
				</td>
			</tr>

		</c:forEach>
	</table>
	<br>
	
  <c:forEach var="program" items="${programs}">
	
	<tags:sectionContainer title="${program.programName}" titleIcon="/WebConfig/${program.applianceCategoryLogo}" >
      <c:forEach var="invBaseControlHistory" items="${controlHistoryPastMonthEventMap[program]}">
		<c:set var="controlHistoryEventList" value="${invBaseControlHistory.controlHistoryEventList}"/>
		<c:set var="controlHistoryEventListSize" value="${fn:length(controlHistoryEventList)}"/>

	    <table align="center" width="99%">
		  <tr>
		    <td>

		    <table align="center" width="99%" >
			  <tr>
				<th colspan="3">
				  <spring:escapeBody htmlEscape="true">${invBaseControlHistory.inventoryBase.deviceLabel} -</spring:escapeBody>
				  </th>
				</tr>
							
				<tr>
				  <td colspan="3">
				    <span style="margin-left: 20px">
					  <c:choose>
					    <c:when test="${controlHistoryEventListSize > 0}">
					      <cti:formatDate var="startDate" value="${controlHistoryEventList[0].startDate}" type="BOTH"/>
					      <cti:formatDate var="endDate"  value="${controlHistoryEventList[0].endDate}" type="BOTH"/>
					      <cti:formatDuration var="duration" type="HM" startDate="${controlHistoryEventList[0].startDate}" endDate="${controlHistoryEventList[0].endDate}" />
					    	
					      <cti:msg key="yukon.web.modules.operator.controlHistory.mostRecentPastControlEntry" 
					               arguments="${invBaseControlHistory.inventoryBase.deviceLabel},${startDate},${startDate},${duration}" />
					    </c:when>
					    <c:otherwise>
					      <cti:msg key="yukon.web.modules.operator.controlHistory.noPastControl" 
					               arguments="${invBaseControlHistory.inventoryBase.deviceLabel}" />
					    </c:otherwise>
					  </c:choose>
				    </span>
				    <br><br>
				  </td>
				</tr>
		
				<tr>
			  	  <td width="70%" valign="top">
				    <div style="margin-left: 20px">
				      <dr:controlHistory controlHistoryEventList="${invBaseControlHistory.controlHistoryEventList}" 
				                         controlHistoryTotalDuration="${pastDayDurationMap[program][invBaseControlHistory.inventoryBase] * 1000}" />
			        </div>
			      </td>
				  <td width="30%" valign="top" bordercolor="black" style="border-left: thin">
					<tags:nameValueContainer>
					  <tags:nameValue name="Today"><cti:formatDuration type="HM" value="${pastDayDurationMap[program][invBaseControlHistory.inventoryBase] * 1000}"/></tags:nameValue>
					  <tags:nameValue name="Past Month"><cti:formatDuration type="HM" value="${pastMonthDurationMap[program][invBaseControlHistory.inventoryBase] * 1000}"/></tags:nameValue>
					  <tags:nameValue name="Annual"><cti:formatDuration type="HM" value="${pastYearDurationMap[program][invBaseControlHistory.inventoryBase] * 1000}"/></tags:nameValue>
					</tags:nameValueContainer>
				  </td>
			    </tr>
			    <br>
			  </table>
		    </td>

			
		  </tr>
		</table>

	  </c:forEach>
	</tags:sectionContainer>
  </c:forEach>	
		
	<div align="center"><i18n:inline key=".header" /></div>
	<br>
	<table border="1" cellspacing="0" cellpadding="0" align="center" class="enrolledPrograms">
		<tr bgcolor="#FFFFFF">
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".enrolledPrograms" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".todaysControlHistory" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".controlHistorySummary" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".completeControlHistory" /></div>
			</td>
		</tr>

		<c:forEach var="program" items="${programs}">
			<tr>
				<td>
					<table>
						<tr>
							<td align="center">
								<img src="/WebConfig/${program.applianceCategoryLogo}" />
								<br/><br/> 
								<spring:escapeBody htmlEscape="true">${program.programName}</spring:escapeBody>
							</td>
						</tr>
					</table>
				</td>

				<td valign="top">
	
					<c:forEach var="invBaseControlHistory" items="${controlHistoryPastMonthEventMap[program]}">
					  <c:set var="controlHistoryEventList" value="${invBaseControlHistory.controlHistoryEventList}"/>
					  <c:set var="controlHistoryEventListSize" value="${fn:length(controlHistoryEventList)}"/>
					  <table align="center" width="99%">
						<tr>
						  <th colspan="3">
						    <spring:escapeBody htmlEscape="true">${invBaseControlHistory.inventoryBase.deviceLabel} -</spring:escapeBody>
					      </th>
						</tr>
					
						<tr>
						  <td colspan="3">
						    <span style="margin-left: 20px">
 						      <c:choose>
						    	<c:when test="${controlHistoryEventListSize > 0}">
						    	  <cti:formatDate var="startDate" value="${controlHistoryEventList[0].startDate}" type="BOTH"/>
						    	  <cti:formatDate var="endDate"  value="${controlHistoryEventList[0].endDate}" type="BOTH"/>
						    	  <cti:formatDuration var="duration" type="HM" startDate="${controlHistoryEventList[0].startDate}" endDate="${controlHistoryEventList[0].endDate}" />
						    	
						    	  <cti:msg key="yukon.web.modules.operator.controlHistory.mostRecentPastControlEntry" 
						    	           arguments="${invBaseControlHistory.inventoryBase.deviceLabel},${startDate},${startDate},${duration}" />
						    	</c:when>
						    	<c:otherwise>
						    	  <cti:msg key="yukon.web.modules.operator.controlHistory.noPastControl" 
						    	           arguments="${invBaseControlHistory.inventoryBase.deviceLabel}" />
						    	</c:otherwise>
						    </c:choose>
						    </span>
						    <br><br>
						  </td>
						</tr>
						<tr>
						  <td>
						  
						<div style="margin-left: 20px">
						  <dr:controlHistory 
						    controlHistoryEventList="${invBaseControlHistory.controlHistoryEventList}" 
						    controlHistoryTotalDuration="${pastDayDurationMap[program][invBaseControlHistory.inventoryBase] * 1000}" />
					    </div>
					    </td>
					  </tr>
					  <br>
					</table>
  				  </c:forEach>
				</td>
					
				<td width="150" valign="top" style="padding-left: 10px">
				  <tags:nameValueContainer>
					<tags:nameValue name="Today">${weekProgramDurationMap[program]}</tags:nameValue>
					<tags:nameValue name="Past Month">${monthProgramDurationMap[program]}</tags:nameValue>
					<tags:nameValue name="Annual">${yearProgramDurationMap[program]}</tags:nameValue>
				  </tags:nameValueContainer>
				</td>
				
				<td width="130">
					<form method="POST" action="ContHist.jsp">
						<input type="hidden" name="prog" value="${program.programId}" />
						<table width="100" border="0" cellspacing="0" cellpadding="3" align="center">
							<tr>
								<td width="180" valign="top" align="center">
									<select name="Period">
										<option value="Past Week">Past Week</option>
										<option value="Past Month">Past Month</option>
										<option value="Past Forever Ever">All</option>
									</select>
								</td>
							</tr>
							<tr>
								<td width="180" valign="top" align="center">
									<input type="submit" name="Input2" value="View">
								</td>
							</tr>
						</table>
					</form>
				</td>
			</tr>

		</c:forEach>
	</table>
	<br>


	<div align="center"><i18n:inline key=".header" /></div>
	<br>
	<table border="1" cellspacing="0" cellpadding="0" align="center" class="enrolledPrograms">
		<tr bgcolor="#FFFFFF">
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".enrolledPrograms" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".todaysControlHistory" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".controlHistorySummary" /></div>
			</td>
			<td class="HeaderCell">
				<div align="center"><i18n:inline key=".completeControlHistory" /></div>
			</td>
		</tr>

		<c:forEach var="program" items="${programs}">
			<tr>
				<td>
					<table>
						<tr>
							<td align="center">
								<img src="/WebConfig/${program.applianceCategoryLogo}" />
								<br/><br/> 
								<spring:escapeBody htmlEscape="true">${program.programName}</spring:escapeBody>
							</td>
						</tr>
					</table>
				</td>

				<td valign="top">
	
					<c:forEach var="invBaseControlHistory" items="${controlHistoryPastMonthEventMap[program]}">
					  <c:set var="controlHistoryEventList" value="${invBaseControlHistory.controlHistoryEventList}"/>
					  <c:set var="controlHistoryEventListSize" value="${fn:length(controlHistoryEventList)}"/>
					  <table align="center" width="99%">
						<tr>
						  <th colspan="3">
						    <spring:escapeBody htmlEscape="true">${invBaseControlHistory.inventoryBase.deviceLabel} -</spring:escapeBody>
					      </th>
						</tr>
					
						<tr>
						  <td colspan="3">
						    <span style="margin-left: 20px">
 						      <c:choose>
						    	<c:when test="${controlHistoryEventListSize > 0}">
						    	  <cti:formatDate var="startDate" value="${controlHistoryEventList[0].startDate}" type="BOTH"/>
						    	  <cti:formatDate var="endDate"  value="${controlHistoryEventList[0].endDate}" type="BOTH"/>
						    	  <cti:formatDuration var="duration" type="HM" startDate="${controlHistoryEventList[0].startDate}" endDate="${controlHistoryEventList[0].endDate}" />
						    	
						    	  <cti:msg key="yukon.web.modules.operator.controlHistory.mostRecentPastControlEntry" 
						    	           arguments="${invBaseControlHistory.inventoryBase.deviceLabel},${startDate},${startDate},${duration}" />
						    	</c:when>
						    	<c:otherwise>
						    	  <cti:msg key="yukon.web.modules.operator.controlHistory.noPastControl" 
						    	           arguments="${invBaseControlHistory.inventoryBase.deviceLabel}" />
						    	</c:otherwise>
						    </c:choose>
						    </span>
						    <br><br>
						  </td>
						</tr>
						<tr>
						  <td>
						  
						<div style="margin-left: 20px">
						  <dr:controlHistory 
						    controlHistoryEventList="${invBaseControlHistory.controlHistoryEventList}" 
						    controlHistoryTotalDuration="${pastDayDurationMap[program][invBaseControlHistory.inventoryBase] * 1000}" />
					    </div>
					    </td>
					  </tr>
					  <br>
					</table>
  				  </c:forEach>
				</td>
					
				<td width="150" valign="top" style="padding-left: 10px">
				  <tags:nameValueContainer>
					<tags:nameValue name="Today">${weekProgramDurationMap[program]}</tags:nameValue>
					<tags:nameValue name="Past Month">${monthProgramDurationMap[program]}</tags:nameValue>
					<tags:nameValue name="Annual">${yearProgramDurationMap[program]}</tags:nameValue>
				  </tags:nameValueContainer>
				</td>
				
				<td width="130">
					<form method="POST" action="ContHist.jsp">
						<input type="hidden" name="prog" value="${program.programId}" />
						<table width="100" border="0" cellspacing="0" cellpadding="3" align="center">
							<tr>
								<td width="180" valign="top" align="center">
									<select name="Period">
										<option value="Past Week">Past Week</option>
										<option value="Past Month">Past Month</option>
										<option value="Past Forever Ever">All</option>
									</select>
								</td>
							</tr>
							<tr>
								<td width="180" valign="top" align="center">
									<input type="submit" name="Input2" value="View">
								</td>
							</tr>
						</table>
					</form>
				</td>
			</tr>

		</c:forEach>
	</table>
	<br>
	
</cti:standardPage>