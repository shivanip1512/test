<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="purchaseBean" class="com.cannontech.stars.web.bean.PurchaseBean" scope="session"/>


<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
    
    <cti:includeCss link="/include/PurpleStyles.css"/>
    <div class="headerbar">
        <%@ include file="include/PurchaseHeaderBar.jspf" %>
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
            <cti:csrfToken/>
            <input type="hidden" name="action" value="DeletePurchasePlanConfirmed"> 
            <div align="center">
                <input type="submit" name="Submit" value="Yes">
                 <input type="button" name="Back" value="No" onclick="back(this.form)">
            </div>
            <br>
            <table width="250" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                    <td align="center" class="HeaderCell">Delivery Schedules (will be deleted)</td>
                </tr>
            </table>
            <table width="250" border="1" cellspacing="0" cellpadding="5" align="center">
                <tr>
                    <c:forEach var="sched" items="${purchaseBean.availableSchedules}">
                        <div align="center">
                            <span class="TableCell"><c:out value="${fn:escapeXml(sched.scheduleName)}"/></span> 
                        </div>
                    </c:forEach>
                </tr>
            </table>
            <br>
            <table width="250" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                    <td align="center" class="HeaderCell">Plan Invoices (will be deleted)</td>
                </tr>
            </table>
            <table width="250" border="1" cellspacing="0" cellpadding="5" align="center">
            <tr>
                <c:forEach var="invoice" items="${purchaseBean.availableInvoices}">
                    <div align="center">
                        <span class="TableCell"><c:out value="${invoice.invoiceDesignation}"/></span> 
                    </div>
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
            location.href='PurchaseTrack.jsp';
        }
                
    </script>
</cti:standardPage>