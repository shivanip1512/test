<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="meterBean" class="com.cannontech.stars.web.bean.NonYukonMeterBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<cti:includeCss link="/include/PurpleStyles.css"/>
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jspf" %>
	</div>
 	<br clear="all"> 
 	
	<%pageContext.setAttribute("liteEC",liteEC);%>
	<c:set target="${meterBean}" property="energyCompany" value="${liteEC}" />
	<%pageContext.setAttribute("currentUser", lYukonUser);%>
	<c:set target="${meterBean}" property="currentUser" value="${currentUser}" />
	<c:set target="${meterBean}" property="currentMeterID" value="${param.MetRef}" />
	
 	<div class="standardpurplesidebox"> 
		<% String pageName = "MeterProfile.jsp"; %>
		<div align="right">
			<%@ include file="include/Nav.jspf" %>
		</div>
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% String header = "METER PROFILE"; %>
            <%@ include file="include/SearchBar.jspf" %>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
    		<br clear="all">
    	</div>
    	
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager" onsubmit="return validate(this)">
	    	<input type="hidden" name="action" value="MeterProfileSave">
 	    	<table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
            	<tr> 
                	<td width="300" valign="top" bgcolor="#FFFFFF"> 
                    	<table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        	<tr> 
                          		<td>
	                          		<span class="SubtitleHeader">METER</span> 
	                            	<hr>
	                            	<span class="MainText">Edit Meter Information: <br>
	                            	<br>
	                            	</span> 
	                            	<table width="300" border="0" cellspacing="0" cellpadding="1" class="TableCell">
		                              	<tr> 
		                                	<td align="right" width="88" class="SubtitleHeader">Type:</td>
		                                	<td width="210"> 
			                                  	<select name="MeterType" onchange="setContentChanged(true)">
				                                   	<c:forEach var="meterType" items="${meterBean.availableMeterTypes.yukonListEntries}">
														<option value='<c:out value="${meterType.entryID}"/>'> <c:out value="${meterType.entryText}"/> </option>
													</c:forEach>
													<option value="0"> <c:out value="(none)"/> </option>
			                                  	</select>
		                                	</td>
		                              	</tr>
		                              	<tr> 
			                                <td align="right" width="88" class="SubtitleHeader">*Meter 
			                                  Number:</td>
			                                <td width="210"> 
			                                  	<input type="text" name="MeterNumber" size="24" onchange="setContentChanged(true)" value='<c:out value="${meterBean.currentMeter.meterHardwareBase.meterNumber}"/>'>
			                                </td>
		                              	</tr>
		                          	</table>
                       			</td>
                   			</tr>
                   		</table>
					<td width="300" valign="top" bgcolor="#FFFFFF"> 
	                	<table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
	                    	<tr> 
	                        	<td><span class="SubtitleHeader">GENERAL INVENTORY INFO</span> 
		                            <hr>
		                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
		                            	<tr> 
		                                	<td width="88" class="TableCell"> 
		                                  		<div align="right">Label:</div>
		                                	</td>
		                                	<td width="210"> 
		                                  		<input type="text" name="DeviceLabel" maxlength="30" size="24" value='<c:out value="${meterBean.currentMeter.inventoryBase.deviceLabel}"/>' onChange="setContentChanged(true)">
		                                	</td>
		                              	</tr>
		                              	<tr> 
		                                	<td width="88" class="TableCell"> 
		                                  		<div align="right">Alt Tracking #:</div>
		                                	</td>
		                                	<td width="210"> 
		                                  		<input type="text" name="AltTrackNo" maxlength="30" size="24" value='<c:out value="${meterBean.currentMeter.inventoryBase.alternateTrackingNumber}"/>' onChange="setContentChanged(true)">
		                                	</td>
		                              	</tr>
		                              	<tr> 
		                                	<td width="88" class="TableCell"> 
		                                  		<div align="right">Voltage:</div>
		                                	</td>
		                                	<td width="210"> 
		                                  		<select name="Voltage" onChange="setContentChanged(true)">
			                                   		 <c:forEach var="voltage" items="${meterBean.voltages.yukonListEntries}">
														<option value='<c:out value="${voltage.entryID}"/>'> <c:out value="${voltage.entryText}"/> </option>
													</c:forEach>
													<option value="0"> <c:out value="(none)"/> </option>
		                                  		</select>
		                                	</td>
		                              	</tr>
		                              	<tr> 
		                                	<td width="88" class="TableCell"> 
		                                  		<div align="right">Notes:</div>
		                                	</td>
		                                	<td width="210"> 
		                                  		<textarea name="Notes" rows="3" wrap="soft" cols="28" value='<c:out value="${meterBean.currentMeter.inventoryBase.notes}"/>' class="TableCell" onChange="setContentChanged(true)"></textarea>
		                                	</td>
		                              	</tr>
		                            </table>
		                 		</td>
		                	</tr>
		          		</table>
			    	</td>
	      		</tr>
	      		
	     	</table>
	     	
	 	   	<table width="600" border="0" cellspacing="0" cellpadding="5" align="right">
	        	<tr>
	           		<td width="60%">  
						<input type="button" name="Submit" value="Save Meter" onclick="validate(this.form)">
					</td>
	            	<td width="40%">
	                	<input type="reset" name="Reset" value="Reset" onclick="location.reload()">
	                  	<input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='Inventory.jsp'">
	              	</td>
	          	</tr>
			</table>
    		<br>
    	</form>
    </div>
    
    <script language="JavaScript">
		function init() {}
		
		function validate(form) 
		{
			
			if (form.MeterNumber.value == "") 
			{
					alert("Meter number cannot be empty");
					return false;
			}
			
			return true;
		}
		
	</script>
</cti:standardPage>          
