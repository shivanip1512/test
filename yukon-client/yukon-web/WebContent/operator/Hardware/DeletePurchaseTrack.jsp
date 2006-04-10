<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="purchaseBean" class="com.cannontech.stars.web.bean.PurchaseBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
	<div class="headerbar">
		<%@ include file="include/PurchaseHeaderBar.jsp" %>
	</div>
 	<br clear="all"> 
 	
	<div class="standardpurplesidebox"> 
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <br clear="all">
    	</div>
    	
		<form name="PForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager" onsubmit="return validate(this)">
	    	<input type="hidden" name="action" value="DeletePurchasePlanConfirmed"> 
	    	<div align="center">
	    		<input type="submit" name="Submit" value="Yes">
                 <input type="button" name="Back" value="No" onclick="back(this.form)">
			</div>
			<br>
			<table width="200" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                    <td class="HeaderCell">Delivery Schedules (will be deleted)</td>
                </tr>
            </table>
            <table width="200" border="1" cellspacing="0" cellpadding="5" align="center">
                <tr>
                    <c:forEach var="sched" items="${purchaseBean.availableSchedules}">
                         <span class="headeremphasis"><c:out value="${sched.scheduleName}"></span> 
                    </c:forEach>
                </tr>
            </table>
            <br>
            <table width="200" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                    <td class="HeaderCell">Plan Invoices (will be deleted)</td>
                </tr>
            </table>
            <table width="200" border="1" cellspacing="0" cellpadding="5" align="center">
            <tr>
                <c:forEach var="sched" items="${purchaseBean.availableSchedules}">
                     <span class="headeremphasis"><c:out value="${sched.scheduleName}"></span> 
                </c:forEach>
            </tr>
            </table>
        </form>
    </div>
    
    <script language="JavaScript">
		function init() {}
		
		function validate(form) 
		{
			return true;
		}
		
		function back(form)
		{
			if (warnUnsavedChanges()) location.href='PurchaseTrack.jsp'
		}
				
	</script>
</cti:standardPage>          
