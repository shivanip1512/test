<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="accountBean" class="com.cannontech.stars.web.bean.AccountBean" scope="page"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
<script language="JavaScript">
    function init() {}
        
    function back(form)
    {
        if (warnUnsavedChanges()) location.href='Update.jsp';
    }
                
</script>
	
	
	
	<%pageContext.setAttribute("currentAccountID", account.getAccountID());%>
	<c:set target="${accountBean}" property="currentAccount" value="${currentAccountID}" />
	
	<table width="760" border="0" cellspacing="0" cellpadding="0">
	  <tr>
	    <td>
            <%@ include file="include/HeaderBar.jspf" %>
	    </td>
	  </tr>
	  <tr>
	    <td>
	      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
	        <tr> 
	          <td width="101" bgcolor="#000000" height="1"></td>
	          <td width="1" bgcolor="#000000" height="1"></td>
	          <td width="657" bgcolor="#000000" height="1"></td>
	          <td width="1" bgcolor="#000000" height="1"></td>
	        </tr>
	        <tr> 
	          <td  valign="top" width="101">
		        <% String pageName = "AccountLog.jsp"; %>
		        <div align="right">
		            <%@ include file="include/Nav_Aux.jspf" %>
		        </div>
	          </td>
	          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
	          <td width="657" valign="top" bgcolor="#FFFFFF"> 
        
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
			        		<td class='TableCell' width='200'><cti:formatDate value="${event.eventBase.eventTimestamp}" type="BOTH"/></td>
			        	</tr>
			        </c:forEach>
		        </table>
		        <br>
				<br>
				<div align="center">
					<input type="button" name="Back" value="Back To Account" onclick="back(this.form)">
				</div>
                <br>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
    
</cti:standardPage>          
