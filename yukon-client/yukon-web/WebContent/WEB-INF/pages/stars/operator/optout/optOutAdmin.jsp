<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:verifyRolesAndProperties value="CONSUMER_INFO"/>
    
<cti:standardPage module="dr" page="optOutAdmin">
    <cti:standardMenu menuSelection="optout|admin"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.optOut.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.optOut.breadcrumb"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <script type="text/javascript">

    	function toggleProgramNameEnabled(toggleEl, txtEl) {
    		if (toggleEl.checked) {
    			txtEl.enable();
    		} else {
    			txtEl.value = '';
    			txtEl.disable();
    		}
    	}
    </script>

    <table style="width: 100%">
        <tr>
            <td width="50%">
                <h3><cti:msg key="yukon.web.modules.dr.optOut.title" /></h3>
            </td>
            <td align="right">
	            <form name="custSearchForm" method="POST" action="<cti:url value="/servlet/SOAPClient"/>">
		            <input type="hidden" name="action" value="SearchCustAccount" />
		            <cti:msg key="yukon.web.modules.dr.optOut.search" />
		            <div>
		                <select name="SearchBy" onchange="document.custSearchForm.SearchValue.value=''">
		                    <c:forEach items="${customerSearchList}" var="entry">
		                        <option value="${entry.entryID}" >${entry.entryText}</option>
		                    </c:forEach>
		                </select>
		    
		                <input type="text" name="SearchValue" size="15" value=''>
		                <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" alt="search" onClick="Javascript:document.custSearchForm.submit();">
		            </div> 
	           </form>
            </td>
        </tr>
    </table>
    <br>
    
    <div style="width: 50%;">
    
	    <!-- System Information -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_STATUS">
		    <cti:msg var="systemInfo" key="yukon.web.modules.dr.optOut.systemInfo" />
		    <cti:msg var="totalAccounts" key="yukon.web.modules.dr.optOut.totalAccounts" />
		    <cti:msg var="todaysOptOuts" key="yukon.web.modules.dr.optOut.todaysOptOuts" />
		    <cti:msg var="futureOptOuts" key="yukon.web.modules.dr.optOut.futureOptOuts" />
		    <tags:boxContainer title="${systemInfo}" hideEnabled="false">
		        <tags:nameValueContainer>
		            <tags:nameValue name="${totalAccounts}">
		                ${totalNumberOfAccounts}
		            </tags:nameValue>
		            
		            <tags:nameValue name="${todaysOptOuts}">
		                ${currentOptOuts}
		            </tags:nameValue>
		            <tags:nameValue name="${futureOptOuts}">
		                ${scheduledOptOuts}
		            </tags:nameValue>
		        </tags:nameValueContainer>
		    </tags:boxContainer>
		    
            <br><br>
        </cti:checkProperty>
	
	    <!-- Disable Opt Outs today -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_ENABLE">
		    <cti:msg var="disableOptOuts" key="yukon.web.modules.dr.optOut.disableOptOutsTitle" />
		    <tags:boxContainer title="${disableOptOuts}" hideEnabled="false">
		        <c:choose>
		            <c:when test="${optOutsEnabled}">
				        <cti:msg key="yukon.web.modules.dr.optOut.optOutEnabled" /><br><br>
				        <form action="/spring/stars/operator/optOut/admin/disable" method="post">
		                    <input type="submit" name="enable" value="<cti:msg key="yukon.web.modules.dr.optOut.disableOptOuts" />" class="formSubmit">
				        </form>
		            </c:when>
		            <c:otherwise>
				        <cti:msg key="yukon.web.modules.dr.optOut.optOutDisabled" /><br><br>
				        <form action="/spring/stars/operator/optOut/admin/enable" method="post">
		                    <input type="submit" name="disable" value="<cti:msg key="yukon.web.modules.dr.optOut.enableOptOuts" />" class="formSubmit">
				        </form>
		            </c:otherwise>
		        </c:choose>
		    </tags:boxContainer>
		
		    <br><br>
	    </cti:checkProperty>
	
	    <!-- Cancel Current Opt Outs -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CANCEL_CURRENT">
		    <cti:msg var="cancelOptOuts" key="yukon.web.modules.dr.optOut.cancelOptOuts.title" />
		    <tags:boxContainer title="${cancelOptOuts}" hideEnabled="false">
		    
		    	<div style="font-size:11px;"><b><cti:msg key="yukon.web.modules.dr.optOut.cancelOptOuts.note.label"/></b> <cti:msg key="yukon.web.modules.dr.optOut.cancelOptOuts.note.text"/></div>
		        <br>
		        
		        <cti:msg key="yukon.web.modules.dr.optOut.cancelOptOuts.cancelOptOutsWarning" />
		        <br><br>
		        
		        <form id="cancelAllOptOutsForm" action="/spring/stars/operator/optOut/admin/cancelAllOptOuts" method="post">
		        
		        	<table style="padding:10px;background-color:#EEE;width:100%;">
		        		<tr><td>
		        			<b><cti:msg key="yukon.web.modules.dr.optOut.cancelOptOuts.byProgramName.instruction.label"/></b> <cti:msg key="yukon.web.modules.dr.optOut.cancelOptOuts.byProgramName.instruction.text"/>
		        		</td></tr>
		        		<tr><td style="padding-top:10px;">
		        		
		        			<%-- PROGRAM PICKER --%>
		        			<input type="hidden" id="cancelOptOutsProgramPaoId"> <%-- dummy destination for selected programId, unused. We actually want to submit the program name to do a lookup for webpublishingProgramId --%>
		        			<input type="hidden" id="cancelOptOutsProgramName" name="programName" value="">
		        			
		        			<tags:pickerDialog  type="lmDirectProgramPaoPermissionCheckingByEnergyCompanyIdPicker"
				                                 id="cancelOptOutsProgramPicker" 
				                                 destinationFieldId="cancelOptOutsProgramPaoId"
				                                 styleClass="simpleLink"
				                                 immediateSelectMode="true"
				                                 extraDestinationFields="paoName:cancelOptOutsProgramName;paoName:cancelOptOutsProgramNameDisplaySpan"
				                                 extraArgs="${energyCompanyId}">
				             	<cti:img key="add"/> <cti:msg key="yukon.web.modules.dr.chooseProgram"/>
                             </tags:pickerDialog>
                             
                             <span id="cancelOptOutsProgramNameDisplaySpan" style="font-weight:bold;"></span>
                             
		        		</td></tr>
		        	</table>
		        	<br>
		        
		        	<cti:msg var="cancelAllOptOutsButton" key="yukon.web.modules.dr.optOut.cancelOptOuts.cancelAllOptOutsButton" />
		        	<tags:slowInput myFormId="cancelAllOptOutsForm" label="${cancelAllOptOutsButton}"/>
		        
		        </form>
		        
		        
		        
		    </tags:boxContainer>
		
		    <br><br>
	    </cti:checkProperty>
	    
	    <!-- Opt Outs Count/Don't Count -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS">
		    <cti:msg var="optOutCountsAgainstLimit" key="yukon.web.modules.dr.optOut.countOptOuts.title" />
		    <tags:boxContainer title="${optOutCountsAgainstLimit}" hideEnabled="false">
		        
		        <div style="font-size:11px;"><b><cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.note.label"/></b> <cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.note.text"/></div>
		        <br>
		        
		        <b><cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.currentLimits"/></b>
		        <br><br>
	        	<table class="compactResultsTable">
	        		<tr>
	        			<th><cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.currentLimits.header.programName"/></th>
	        			<th><cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.currentLimits.header.counts"/></th>
	        		</tr>
	        		
	        		<c:forEach var="program" items="${programNameCountsMap}">
	        			<tr>
	        				<td>${program.key}</td>
	        				<td><cti:msg key="${program.value.formatKey}"/></td>
	        			</tr>
	        		</c:forEach>
	        	
	        	</table>
	        	<br>
	        
	        	<form action="/spring/stars/operator/optOut/admin/setCounts" method="post">
		                
                	<table style="padding:10px;background-color:#EEE;width:100%;">
		        		<tr><td>
		        			<b><cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.byProgramName.label"/></b> <cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.byProgramName.instruction"/>
		        		</td></tr>
		        		<tr><td style="padding-top:10px;">
		        			
		        			<%-- PROGRAM PICKER --%>
		        			<input type="hidden" id="disabledCountProgramPaoId"> <%-- dummy destination for selected programId, unused. We actually want to submit the program name to do a lookup for webpublishingProgramId --%>
		        			<input type="hidden" id="disabledCountProgramName" name="programName" value="">
		        			
		        			<tags:pickerDialog  type="lmDirectProgramPaoPermissionCheckingByEnergyCompanyIdPicker"
				                                 id="disabledCountProgramPicker" 
				                                 destinationFieldId="disabledCountProgramPaoId"
				                                 styleClass="simpleLink"
				                                 immediateSelectMode="true"
				                                 extraDestinationFields="paoName:disabledCountProgramName;paoName:disabledCountProgramNameDisplaySpan"
				                                 extraArgs="${energyCompanyId}">
				                <cti:img key="add"/> <cti:msg key="yukon.web.modules.dr.chooseProgram"/>
                             </tags:pickerDialog>
                             
                             <span id="disabledCountProgramNameDisplaySpan" style="font-weight:bold;"></span>
                             
		        		</td></tr>
		        	</table>
		        	<br>
		        	
                    <input type="submit" name="count" value="<cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.countOptOutsButton" />" class="formSubmit">
                    <input type="submit" name="dontCount" value="<cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.dontCountOptOutsButton" />" class="formSubmit">
                </form>
			        
	        </tags:boxContainer>
        </cti:checkProperty>
	</div>
            
</cti:standardPage>