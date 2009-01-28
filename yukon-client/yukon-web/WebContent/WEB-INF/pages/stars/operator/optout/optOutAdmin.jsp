<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:verifyRolesAndProperties value="ConsumerInfoRole"/>
    
<cti:standardPage module="dr" page="optOutAdmin">
    <cti:standardMenu />

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
			        <form action="/spring/stars/operator/optOut/admin/cancelAllOptOuts" method="post">
			            <input type="submit" name="cancel" value="<cti:msg key="yukon.web.modules.dr.optOut.cancelAllOptOuts" />">
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
			    </div
	        </ct:boxContainer>
        </cti:checkProperty>
	</div>
            
</cti:standardPage>