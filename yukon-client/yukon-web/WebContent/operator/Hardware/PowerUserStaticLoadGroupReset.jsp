<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="configBean" class="com.cannontech.stars.web.bean.ConfigBean" scope="page"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<%pageContext.setAttribute("currentUser", lYukonUser);%>
	<c:set target="${configBean}" property="currentUser" value="${currentUser}" />
	
	<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jsp" %>
	</div>
 	<br clear="all"> 
 	
	<div class="standardpurplesidebox"> 
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
           
    	</div>
    	
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager">
	    	<input type="hidden" name="action" value="StaticLoadGroupMapMasterReset">
	    	
	    	<div align="center">
				<c:choose>
					<c:when test="${configBean.hasStaticLoadGroupMapping && configBean.hasResetPermission}"> 
					 	<td> 
		                	<span class="ConfirmMsg">This will reset all address group mappings in this database to the defaults</span>
		                	<br>
		                	<span class="ConfirmMsg">specified in the static load group mapping table</span>
		                	<br>  
		                	<br>
		                	<span class="ErrorMsg">**This action has the potential to seize maximum resources of this system until completion.</span>
		                	<span class="ErrorMsg">  It may cause visible slowness or even lack of functionality for any users logged in.</span>
	                    	<span class="ErrorMsg">  It is recommended that this action only be taken during non-business hours.</span>
	                    </td> 
                    </c:when>
                    <c:otherwise> 	
	                	<span class="ErrorMsg">	You have reached this page as a result of incorrect navigation.  Please click Back on your browser.</span>
		            </c:otherwise>
			    </c:choose>
			</div>
	    	<table width="80%" border="2" cellspacing="0" cellpadding="0" align="center" class="TableCell">
			  	<tr>
					<c:choose>
						<c:when test="${configBean.hasStaticLoadGroupMapping}"> 
						 	<td> 
			                	<div align="center"> 
		                        	<input type="submit" name="Reset" value="Reset Now">
		                      </div>
		                    </td> 
	                    </c:when>
	                    <c:otherwise> 	
		                    
			            </c:otherwise>
			    	</c:choose>
				</tr>
 			</table>
        	
        </form>
    </div>
    
    <script language="JavaScript">
		function init()
		{
		}
		
	</script>
</cti:standardPage>          
