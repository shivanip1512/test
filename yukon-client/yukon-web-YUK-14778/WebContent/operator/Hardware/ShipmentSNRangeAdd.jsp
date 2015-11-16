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
     
    <%pageContext.setAttribute("liteEC",liteEC);%>
    <c:set target="${purchaseBean}" property="energyCompany" value="${liteEC}" />
    <%pageContext.setAttribute("currentUser", lYukonUser);%>
    <c:set target="${purchaseBean}" property="currentUser" value="${currentUser}" />
     
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
            <input type="hidden" name="action" value="ShipmentSNRangeAdd"> 
            <div align="center">
                <span class="title-header">SERIAL RANGE DETAIL</span>
            </div>
            <br>
            <table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Serial Number Range:</div>
                        </td>
                    <td width="80%"> 
                        <div align="left" class="fieldinfo"><c:out value="${purchaseBean.currentShipment.serialNumberStart}"/> to <c:out value="${purchaseBean.currentShipment.serialNumberEnd}"/></div>
                    </td>
                </tr>
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Member:</div>
                        </td>
                    <td width="80%"> 
                        <div align="left" ><c:out value="${fn:escapeXml(purchaseBean.serialNumberMember.name)}"/></div>
                    </td>
                </tr>
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Warehouse:</div>
                        </td>
                    <td width="80%"> 
                        <div align="left" ><c:out value="${fn:escapeXml(purchaseBean.serialNumberWarehouse.warehouseName)}"/></div>
                    </td>
                </tr>
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Device Type:</div>
                        </td>
                    <td width="80%"> 
                        <div align="left" ><c:out value="${fn:escapeXml(purchaseBean.serialNumberDeviceType.entryText)}"/></div>
                    </td>
                </tr>
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Device State:</div>
                        </td>
                    <td width="80%"> 
                        <div align="left" ><c:out value="${fn:escapeXml(purchaseBean.serialNumberDeviceState.entryText)}"/></div>
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
            return true;
        }
        
        function back(form)
        {
            location.href='DeliverySchedule.jsp';
        }
                
    </script>
</cti:standardPage>