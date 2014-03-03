<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="purchaseBean" class="com.cannontech.stars.web.bean.PurchaseBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">

    <cti:msg key="yukon.common.calendarcontrol.months" var="months"/>
    <cti:msg key="yukon.common.calendarcontrol.days" var="days"/>
    <cti:msg key="yukon.common.calendarcontrol.clear" var="clear"/>
    <cti:msg key="yukon.common.calendarcontrol.close" var="close"/>

    <cti:includeCss link="/include/PurpleStyles.css"/>
    <div class="headerbar">
        <%@ include file="include/PurchaseHeaderBar.jspf" %>
    </div>
     <br clear="all"> 
     
    <%pageContext.setAttribute("liteEC",liteEC);%>
    <c:set target="${purchaseBean}" property="energyCompany" value="${liteEC}" />
    <%pageContext.setAttribute("currentUser", lYukonUser);%>
    <c:set target="${purchaseBean}" property="currentUser" value="${currentUser}" />
     
    <div class="standardpurplesidebox"> 
    </div>

    <div class="standardcentralwhitebody">
        <div align="center"> <br>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            <br clear="all">
        </div>
        
        <form name="PForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager" onsubmit="return validate(this)">
            <cti:csrfToken/>
            <input type="hidden" name="action" value="TimePeriodChange"> 
            <div align="center">
                <span class="title-header">TIME PROJECTION FOR DELIVERY</span>
            </div>
            <br>
            <br>
            <table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                      <td class="HeaderCell">Prediction Data</td>
                </tr>
            </table>
            <table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Period Name:</div>
                        </td>
                    <td width="80%"> 
                        <input type="text" name="name" maxlength="30" size="24" value='<c:out value="${fn:escapeXml(purchaseBean.currentTimePeriod.timePeriodName)}"/>'>
                    </td>
                </tr>
                <tr> 
                    <td width="20%" class="TableCell"> 
                      <div align="right">Quantity:</div>
                    </td>
                    <td width="80%"> 
                      <input type="text" name="quantity" maxlength="40" size="24" value='<c:out value="${purchaseBean.currentTimePeriod.quantity}"/>'>
                    </td>
                  </tr>
                  <tr> 
                    <td width="20%" class="TableCell"> 
                          <div align="right">Expected Ship Date:</div>
                    </td>
                    <td width="80%"> 
                      <input id="shipDate" type="text" name="shipDate" maxlength="40" size="24" value='<cti:formatDate value="${purchaseBean.currentTimePeriod.predictedShipDate}" type="DATE"/>'>
                    </td>
                  </tr>
                  <tr> 
                    <td width="20%" class="TableCell"> 
                          <div align="right">Filing:</div>
                    </td>
                    <td width="80%"> 
                          <div align="left" class="fieldinfo">This time projection is for schedule <c:out value="${fn:escapeXml(purchaseBean.currentSchedule.scheduleName)}"/></div>
                    </td>
                 </tr>
            </table>
            <br>
            <br>
            <table width="500" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                       <td width="10%"></td>
                       <td width="40%">  
                        <input type="submit" name="Submit" value="Submit">
                    </td>
                    <td width="50%">
                        <input type="reset" name="Reset" value="Reset" onclick="location.reload()">
                          <input type="button" name="Back" value="Back To Schedule" onclick="back(this.form)">
                      </td>
                  </tr>
            </table> 
        </form>
    </div>
    
    <script language="JavaScript">
        function init() {}
        
        function validate(form) 
        {
            if (form.name.value == "") 
            {
                    alert("Time period name cannot be empty and must be unique.");
                    return false;
            }
            
            if (form.shipDate.value == "") 
            {
                alert("The ship date cannot be empty");
                return false;
            }
            
            return true;
        }
        
        function back(form)
        {
            location.href='DeliverySchedule.jsp';
        }
                
    </script>
</cti:standardPage>