<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="include/billing_header.jsp" %>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache"%>
<%@ page import="com.cannontech.yukon.IDatabaseCache"%>
<%@ page import="com.cannontech.billing.FileFormatTypes"%>

<cti:standardPage module="amr" title="Billing">
<cti:standardMenu menuSelection="billing"/>

<cti:includeScript link="/JavaScript/calendar.js"/>

<h2>Billing</h2>

<form name = "MForm" action="<%=request.getContextPath()%>/servlet/BillingServlet" method="post">

<table width="80%" border="0" cellspacing="0" cellpadding="2" class = "TableCell">
    <tr> 
        <td width="20%" align="right" >File Format: </td>
        <td width="80%"> 
            <select name="fileFormat">
                <% /* Fill in the possible file format types*/
                int selectedFormat = billingBean.getFileFormat();
                java.util.Map<Integer,String> validFormats = FileFormatTypes.getValidFormats();
                for (final Integer key : validFormats.keySet()) {
                    if (key.equals(selectedFormat)) {
                        out.println("<OPTION VALUE='" + key + "' SELECTED>" + validFormats.get(key));        
                    } else {
                        out.println("<OPTION VALUE='" + key + "'>" + validFormats.get(key));
                    }
                }
                %>
            </select>
        </td>
    </tr>
    <tr> 
        <td align="right">Billing End Date:</td>
        <td >
            <input id="cal" type="text" name="endDate" value="<%= datePart.format(billingBean.getEndDate()) %>" size="8">
                <A HREF="javascript:openCalendar(document.getElementById('MForm').cal, 74, 0)"
                  onMouseOver="window.status='Select Billing End Date';return true;"
                  onMouseOut="window.status='';return true;">
                  <IMG SRC="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" WIDTH="20" HEIGHT="15" ALIGN="ABSMIDDLE" BORDER="0">
                </A>
        </td>
    </tr>
    <tr> 
        <td  align="right">Demand Days Previous:</td>
        <td > 
            <input type="text" name="demandDays" value="<%=billingBean.getDemandDaysPrev()%>" size = "8">
        </td>
    </tr>
    <tr> 
        <td align="right">Energy Days Previous:</td>
        <td> 
            <input type="text" name="energyDays" value="<%=billingBean.getEnergyDaysPrev()%>" size = "8">
        </td>
    </tr>
    <tr> 
        <td align="right">Remove Multiplier</td>
        <td> 
            <input type="checkbox" name="removeMultiplier" <%=(billingBean.getRemoveMult()?"checked":"")%> >
        </td>
    </tr>
    <tr> 
    <td align="right" valign="top">Billing Group:</td>
      <td> 
        <c:set value="${true}" var="isFirst"/>
        <select name="billGroup" size="10" multiple>
         <c:forEach items="${BILLING_BEAN.availableGroups}" var="item">
           <c:if test="${isFirst}">
            <option selected>${item}</option>
           </c:if>
           <c:if test="${!isFirst}">
            <option>${item}</option>
           </c:if>
           <c:set value="${false}" var="isFirst"/>
         </c:forEach>
        </select>
      </td>
    </tr>
    <tr> 
      <td  align = "right"> 
      <input type="submit" name="generate" value="Generate">
      </td>
      <td  align = "right"> 
      </td>
    </tr>
</table>
</form>
</cti:standardPage>