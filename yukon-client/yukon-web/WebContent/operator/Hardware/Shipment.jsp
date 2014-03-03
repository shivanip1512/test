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
            <input type="hidden" name="action" value="ShipmentChange"> 
            <div align="center">
                <span class="title-header">SHIPMENT</span>
            </div>
            <br>
            <br>
            <table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                      <td class="HeaderCell">Shipping Information</td>
                </tr>
            </table>
            <table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Shipment Number:</div>
                        </td>
                    <td width="80%"> 
                        <input type="text" name="name" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentShipment.shipmentNumber}"/>'>
                    </td>
                </tr>
                <tr> 
                    <td width="20%" class="TableCell"> 
                      <div align="right">Warehouse:</div>
                    </td>
                    <td width="80%"> 
                          <select id="warehouse" name="warehouse" size="1" style="width:170">
                            <c:forEach var="house" items="${purchaseBean.availableWarehouses}">
                                 <c:choose>
                                     <c:when test="${house.warehouseID == purchaseBean.currentShipment.warehouseID}">
                                        <option value='<c:out value="${house.warehouseID}"/>' selected> <c:out value="${fn:escapeXml(house.warehouseName)}"/> </option>
                                     </c:when>
                                     <c:otherwise>
                                        <option value='<c:out value="${house.warehouseID}"/>'> <c:out value="${fn:escapeXml(house.warehouseName)}"/> </option>
                                     </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </td>
                  </tr>
                  <c:choose>
                    <c:when test="${purchaseBean.allowSerialNumberInput}">
                        <tr>
                            <td width="20%" class="TableCell"> 
                                <div align="right">Serial Number Range:</div>
                                </td>
                            <td width="80%"> 
                                <input type="text" name="serialStart" maxlength="19" size="19" value='<c:out value="${purchaseBean.currentShipment.serialNumberStart}"/>'>
                                <div> to </div>
                                <input type="text" name="serialEnd" maxlength="19" size="19" value='<c:out value="${purchaseBean.currentShipment.serialNumberEnd}"/>'>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td width="20%" class="TableCell"> 
                                <div align="right">Serial Number Range:</div>
                                </td>
                            <td width="80%"> 
                                <div align="left" class="fieldinfo"><c:out value="${purchaseBean.currentShipment.serialNumberStart}"/> to <c:out value="${purchaseBean.currentShipment.serialNumberEnd}"/></div>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                    
                <tr> 
                    <td width="20%" class="TableCell"> 
                          <div align="right">Date Ordered:</div>
                    </td>
                    <td width="80%"> 
                      <input id="orderingDate" type="text" name="orderingDate" maxlength="40" size="24" value='<cti:formatDate value="${purchaseBean.currentShipment.orderedDate}" type="DATE"/>'>
                    </td>
                  </tr>
                <tr> 
                    <td width="20%" class="TableCell"> 
                          <div align="right">Ship Date:</div>
                    </td>
                    <td width="80%"> 
                      <input id="shipDate" type="text" name="shipDate" maxlength="40" size="24" value='<cti:formatDate value="${purchaseBean.currentShipment.shipDate}" type="DATE"/>'>
                    </td>
                  </tr>
                  <tr> 
                    <td width="20%" class="TableCell"> 
                          <div align="right">Date Received:</div>
                    </td>
                    <td width="80%"> 
                      <input id="receivingDate" type="text" name="receivingDate" maxlength="40" size="24" value='<cti:formatDate value="${purchaseBean.currentShipment.receivedDate}" type="DATE"/>'>
                    </td>
                  </tr>
                  <tr> 
                    <td width="20%" class="TableCell"> 
                          <div align="right">Filing:</div>
                    </td>
                    <td width="80%"> 
                          <div align="left" class="fieldinfo">This shipment is part of schedule <c:out value="${fn:escapeXml(purchaseBean.currentSchedule.scheduleName)}"/></div>
                    </td>
                 </tr>
            </table>
            <br>
            <table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                      <td class="HeaderCell">Shipment Pricing and Charges</td>
                </tr>
            </table>
            <table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Price Per Unit (Actual):</div>
                        </td>
                    <td width="80%"> 
                        <input type="text" name="pricePerUnit" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentActualPricePerUnit}"/>'>
                    </td>
                </tr>
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Sales Tax:</div>
                        </td>
                    <td width="80%"> 
                        <input type="text" name="salesTax" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentSalesTax}"/>'>
                    </td>
                </tr>
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Shipping Charges:</div>
                        </td>
                    <td width="80%"> 
                        <input type="text" name="shippingCharges" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentShippingCharges}"/>'>
                    </td>
                </tr>
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Other Charges:</div>
                        </td>
                    <td width="80%"> 
                        <input type="text" name="otherCharges" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentOtherCharges}"/>'>
                    </td>
                </tr>
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">TOTAL:</div>
                        </td>
                    <td width="80%"> 
                        <input type="text" name="total" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentTotal}"/>'>
                    </td>
                </tr>
                   <tr>
                   </tr>
                   <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Amount Paid:</div>
                        </td>
                    <td width="80%"> 
                        <input type="text" name="amountPaid" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentAmountPaid}"/>'>
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
                    alert("Shipment number cannot be empty and must be unique.");
                    return false;
            }
            
            if (form.shipDate.value == "") 
            {
                alert("The ship date cannot be empty");
                return false;
            }
            
            if (form.serialStart != null && form.serialStart.value == "") 
            {
                alert("Serial range start field cannot be empty and should be a valid serial number.");
                return false;
            }
            
            if (form.serialEnd != null && form.serialEnd.value == "") 
            {
                alert("Serial range end field cannot be empty and should be a valid serial number.");
                return false;
            }
            
            if (form.warehouse.value == "")
            {
                alert("A warehouse is required for switch location assignment.  If no warehouses are available, then a new warehouse will need to be added to the appropriate energy company.");
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