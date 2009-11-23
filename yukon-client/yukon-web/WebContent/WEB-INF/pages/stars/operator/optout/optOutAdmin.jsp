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

    	function toggleCancelAllOptOutsProgramName(toggleEl) {
    		if (toggleEl.checked) {
				$('cancelAllOptOutsProgramName').enable();
    		} else {
    			$('cancelAllOptOutsProgramName').value = '';
    			$('cancelAllOptOutsProgramName').disable();
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
    
    <br><br>
    
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
		        <div align="center">
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
			    </div>
		    </ct:boxContainer>
		
		    <br><br>
	    </cti:checkProperty>
	
	    <!-- Cancel Current Opt Outs -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CANCEL_CURRENT">
		    <cti:msg var="cancleOptOuts" key="yukon.web.modules.dr.optOut.cancleOptOuts" />
		    <ct:boxContainer title="${cancleOptOuts}" hideEnabled="false">
		        <div align="center">
		        
		        	<c:if test="${not empty cancelCurrentOptOutsErrorMsg}">
		        		<div class="errorRed">${cancelCurrentOptOutsErrorMsg}</div>
		        		<br>
		        	</c:if>
		        
			        <form id="cancelAllOptOutsForm" action="/spring/stars/operator/optOut/admin/cancelAllOptOuts" method="post">
			        
			        	<table style="padding:10px;background-color:#EEE;">
			        		<tr><td>
			        			<b>Optional:</b>
			        		</td></tr>
			        		<tr><td style="padding-top:10px;">
			        			<label>
					        		<input type="checkbox" name="onlySingleProgram" onclick="toggleCancelAllOptOutsProgramName(this);"> 
					        		Only Cancel Opt Outs For A Single Program
					        	</label>
			        		</td></tr>
			        		<tr><td style="padding-top:10px;">
			        			Program Name: 
			        			<input type="text" id="cancelAllOptOutsProgramName" name="programName" value="" style="width:300px;" disabled>
			        		</td></tr>
			        	</table>
			        	<br><br>
			        
			        	<cti:msg var="cancelAllOptOutsButton" key="yukon.web.modules.dr.optOut.cancelAllOptOuts" />
			        	<ct:slowInput myFormId="cancelAllOptOutsForm" label="${cancelAllOptOutsButton}"/>
			        
			        </form>
			        <br>
			        <cti:msg key="yukon.web.modules.dr.optOut.cancelOptOutsWarning" />
			    </div>
		    </ct:boxContainer>
		
		    <br><br>
	    </cti:checkProperty>
	    
	    <!-- Opt Outs Count/Don't Count -->
	    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS">
		    <cti:msg var="optOutCountsAgainstLimit" key="yukon.web.modules.dr.optOut.optOutCountsAgainstLimit" />
		    <ct:boxContainer title="${optOutCountsAgainstLimit}" hideEnabled="false">
		        <div align="center">
			        <c:choose>
			            <c:when test="${optOutCounts}">
			                <cti:msg key="yukon.web.modules.dr.optOut.optOutsCount" /><br><br>
			                <form action="/spring/stars/operator/optOut/admin/dontCount" method="post">
			                    <input type="submit" name="disable" value="<cti:msg key="yukon.web.modules.dr.optOut.dontCountOptOuts" />">
			                </form>
			            </c:when>
			            <c:otherwise>
			                <cti:msg key="yukon.web.modules.dr.optOut.optOutsDontCount" /><br><br>
			                <form action="/spring/stars/operator/optOut/admin/count" method="post">
			                    <input type="submit" name="enable" value="<cti:msg key="yukon.web.modules.dr.optOut.countOptOuts" />">
			                </form>
			            </c:otherwise>
			        </c:choose>
			    </div>
	        </ct:boxContainer>
        </cti:checkProperty>
	</div>
            
</cti:standardPage>