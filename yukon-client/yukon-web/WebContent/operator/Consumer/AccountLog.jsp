<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="accountBean" class="com.cannontech.stars.web.bean.AccountBean" scope="page"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<%pageContext.setAttribute("currentAccountID", account.getAccountID());%>
	<c:set target="${accountBean}" property="currentAccount" value="${currentAccountID}" />
	
	<cti:includeCss link="/include/PurpleStyles.css"/>
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jspf" %>
	</div>
 	<br clear="all"> 
 	
	<div class="standardpurplesidebox"> 
		<% String pageName = "AccountLog.jsp"; %>
		<div align="right">
			<%@ include file="include/Nav_Aux.jspf" %>
		</div>
	</div>

	<div class="standardcentralwhitebody">
		<br>
		<div align="center">
	    	<span class="TitleHeader">ACCOUNT ACTION LOG</span>
		</div>
		<br>
		<table width="600" border="1" cellspacing="0" cellpadding="5" align="center">
        	<tr>
        		<td class='HeaderCell' width='200'>Event</td>
        		<td class='HeaderCell' width='200'>User Name</td>
        		<td class='HeaderCell' width='200'>Time of Event</td>
        	</tr>
        	<c:forEach var="event" items="${accountBean.currentEvents}">
	        	<tr>
	        		<td class='TableCell' width='200'><c:out value="${event.actionText}"/></td>
	        		<td class='TableCell' width='200'><c:out value="${event.userName}"/></td>
	        		<td class='TableCell' width='200'><c:out value="${event.eventBase.eventTimestamp}"/></td>
	        	</tr>
	        </c:forEach>
        </table>
        <br>
		<br>
		<table width="500" border="0" cellspacing="0" cellpadding="5" align="center">
        	<tr>
           		<td width="30%"></td>
           		<td width="40%">  
					<div align="center">
						<input type="button" name="Back" value="Back To Account" onclick="back(this.form)">
					</div>
				</td>
            	<td width="30%"></td>
          	</tr>
		</table> 
    </div>
    
    <script language="JavaScript">
		function init() {}
		
		function back(form)
		{
			if (warnUnsavedChanges()) location.href='Update.jsp'
		}
				
	</script>
</cti:standardPage>          
