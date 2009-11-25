<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

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
    
    <c:if test="${not empty emptyProgramName}">
		<div class="errorRed"><cti:msg key="yukon.web.modules.dr.byProgramName.emptyProgramName"/></div>
   		<br>
   	</c:if>
   	<c:if test="${not empty programNotFound}">
   		<div class="errorRed"><cti:msg key="yukon.web.modules.dr.byProgramName.programNotFound"/></div>
   		<br>
   	</c:if>
    
    <div style="width: 50%;">
    
	    <!-- System Information -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_STATUS">
		    <cti:msg var="systemInfo" key="yukon.web.modules.dr.optOut.systemInfo" />
		    <cti:msg var="totalAccounts" key="yukon.web.modules.dr.optOut.totalAccounts" />
		    <cti:msg var="todaysOptOuts" key="yukon.web.modules.dr.optOut.todaysOptOuts" />
		    <cti:msg var="futureOptOuts" key="yukon.web.modules.dr.optOut.futureOptOuts" />
		    <ct:boxContainer title="${systemInfo}" hideEnabled="false">
		        <ct:nameValueContainer>
		            <ct:nameValue name="${totalAccounts}">
		                ${totalNumberOfAccounts}
		            </ct:nameValue>
		            
		            <ct:nameValue name="${todaysOptOuts}">
		                ${currentOptOuts}
		            </ct:nameValue>
		            <ct:nameValue name="${futureOptOuts}">
		                ${scheduledOptOuts}
		            </ct:nameValue>
		        </ct:nameValueContainer>
		    </ct:boxContainer>
		    
            <br><br>
        </cti:checkProperty>
	
	    <!-- Disable Opt Outs today -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_ENABLE">
		    <cti:msg var="disableOptOuts" key="yukon.web.modules.dr.optOut.disableOptOutsTitle" />
		    <ct:boxContainer title="${disableOptOuts}" hideEnabled="false">
		        <c:choose>
		            <c:when test="${optOutsEnabled}">
				        <cti:msg key="yukon.web.modules.dr.optOut.optOutEnabled" /><br><br>
				        <form action="/spring/stars/operator/optOut/admin/disable" method="post">
		                    <input type="submit" name="enable" value="<cti:msg key="yukon.web.modules.dr.optOut.disableOptOuts" />">
				        </form>
		            </c:when>
		            <c:otherwise>
				        <cti:msg key="yukon.web.modules.dr.optOut.optOutDisabled" /><br><br>
				        <form action="/spring/stars/operator/optOut/admin/enable" method="post">
		                    <input type="submit" name="disable" value="<cti:msg key="yukon.web.modules.dr.optOut.enableOptOuts" />">
				        </form>
		            </c:otherwise>
		        </c:choose>
		    </ct:boxContainer>
		
		    <br><br>
	    </cti:checkProperty>
	
	    <!-- Cancel Current Opt Outs -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CANCEL_CURRENT">
		    <cti:msg var="cancelOptOuts" key="yukon.web.modules.dr.optOut.cancelOptOuts.title" />
		    <ct:boxContainer title="${cancelOptOuts}" hideEnabled="false">
		        
		        <cti:msg key="yukon.web.modules.dr.optOut.cancelOptOuts.cancelOptOutsWarning" />
		        <br><br>
		        
		        <form id="cancelAllOptOutsForm" action="/spring/stars/operator/optOut/admin/cancelAllOptOuts" method="post">
		        
		        	<table style="padding:10px;background-color:#EEE;">
		        		<tr><td>
		        			<b>Optional:</b>
		        		</td></tr>
		        		<tr><td style="padding-top:10px;">
		        			<label>
				        		<input type="checkbox" name="onlySingleProop	opgram" onclick="toggleProgramNameEnabled(this, $('cancelAllOptOutsProgramName'));"> 
				        		<cti:msg key="yukon.web.modules.dr.optOut.cancelOptOuts.byProgramName.instruction"/>
				        	</label>
		        		</td></tr>
		        		<tr><td style="padding-top:10px;">
		        			<cti:msg key="yukon.web.modules.dr.byProgramName.label"/>:
		        			<input type="text" id="cancelAllOptOutsProgramName" name="programName" value="" style="width:300px;" disabled>
		        		</td></tr>
		        	</table>
		        	<br>
		        
		        	<cti:msg var="cancelAllOptOutsButton" key="yukon.web.modules.dr.optOut.cancelOptOuts.cancelAllOptOutsButton" />
		        	<ct:slowInput myFormId="cancelAllOptOutsForm" label="${cancelAllOptOutsButton}"/>
		        
		        </form>
		        
		        
		        
		    </ct:boxContainer>
		
		    <br><br>
	    </cti:checkProperty>
	    
	    <!-- Opt Outs Count/Don't Count -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS">
		    <cti:msg var="optOutCountsAgainstLimit" key="yukon.web.modules.dr.optOut.countOptOuts.title" />
		    <ct:boxContainer title="${optOutCountsAgainstLimit}" hideEnabled="false">
		        
		        <b>Current Limits</b>
		        <br><br>
	        	<table class="compactResultsTable">
	        		<tr>
	        			<th>Program Name</th>
	        			<th>Counts Against Todays Opt Out Limits</th>
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
		                
                	<table style="padding:10px;background-color:#EEE;">
		        		<tr><td>
		        			<b>Optional:</b>
		        		</td></tr>
		        		<tr><td style="padding-top:10px;">
		        			<label>
				        		<input type="checkbox" name="onlySingleProgram" onclick="toggleProgramNameEnabled(this, $('disabledCountOptOutsProgramName'));"> 
				        		<cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.byProgramName.instruction"/>
				        	</label>
		        		</td></tr>
		        		<tr><td style="padding-top:10px;">
		        			<cti:msg key="yukon.web.modules.dr.byProgramName.label"/>:
		        			<input type="text" id="disabledCountOptOutsProgramName" name="programName" value="" style="width:300px;" disabled>
		        		</td></tr>
		        	</table>
		        	<br>
		        	
                    <input type="submit" name="count" value="<cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.countOptOutsButton" />">
                    <input type="submit" name="dontCount" value="<cti:msg key="yukon.web.modules.dr.optOut.countOptOuts.dontCountOptOutsButton" />">
                </form>
			        
	        </ct:boxContainer>
        </cti:checkProperty>
	</div>
            
</cti:standardPage>