<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<cti:standardPage title="Multispeak Setup" module="multispeak">
<cti:standardMenu />
<%@ page import="com.cannontech.multispeak.client.*" %>

<jsp:useBean id="multispeakBean" class="com.cannontech.multispeak.client.MultispeakBean" scope="session"/>
<c:if test="${param.vendor != null}">
<c:set target="${multispeakBean}" property="selectedVendorID"><c:out value="${param.vendor}" default="1"/></c:set>
</c:if>
<c:if test="${param.init != null}">
<c:set var="MSP_RESULT_MSG" value="" scope="session" />
<c:set var="ERROR_MESSAGE" value="" scope="session" />
</c:if>

<script language="JavaScript">
function enableEndpointValue(selected, interface) {
 
 if( selected )
    {
        document.getElementById("mspPing"+interface).style.cursor = 'pointer';
        document.getElementById("mspMethods"+interface).style.cursor = 'pointer';
        document.getElementById("mspPing"+interface).href = 'JavaScript:serviceSubmit(\"'+interface+'\", \"pingURL\");'
        document.getElementById("mspMethods"+interface).href = 'JavaScript:document.form1.submit()'
    }
    else
    {
        document.getElementById("mspPing"+interface).style.cursor = 'default';
        document.getElementById("mspMethods"+interface).style.cursor = 'default';
        document.getElementById("mspPing"+interface).href = 'javascript:;'
        document.getElementById("mspMethods"+interface).href = 'javascript:;'
    }
    document.getElementById("mspEndpoint"+interface).disabled = !selected;
}

function serviceSubmit(service, value)
{
  document.form1.actionService.value=service;
  document.form1.actionEndpoint.value=document.getElementById("mspEndpoint"+service).value;
  document.form1.ACTION.value=value;
  document.form1.submit();
}

function dispStatusMsg(msgStr) { //v1.0
  status=msgStr;
  document.statVal = true;
}

function vendorChanged(vendor)
{
    javascript:window.location='<%=request.getContextPath()%>/msp_setup.jsp?vendor='+vendor+'&init';
}
</script>
    
<br>
<div class="mainTable">
  <h2 class="setup">Welcome to</h2>
  <h3 class="setup">Yukon Multispeak Interface Setup</h3>
  <h4 class='ErrorMsg'><c:out value="${sessionScope.ERROR_MESSAGE}" default=""/></h4>

<cti:titledContainer title="Multispeak Interface Setup">
<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/MultispeakServlet">
<input type="hidden" name="ACTION" value="updateMSP">
<input type="hidden" name="actionEndpoint">
<input type="hidden" name="actionService">
<input type="hidden" name="REDIRECT" value='<c:out value="${pageContext.request.requestURI}"/>' >
<input type="hidden" name="vendorID" value='<c:out value="${multispeakBean.selectedMspVendor.vendorID}"/>'>
<input type="hidden" name="mspCompanyName" value='<c:out value="${multispeakBean.selectedMspVendor.companyName}"/>'>

<table class="keyValueTable">
  <tr> 
    <td colspan="2" onMouseOver="dispStatusMsg('Select vendor');return document.statVal" 
        onMouseOut="dispStatusMsg('');return document.statVal">Company Name
    </td>
    <td colspan="2">
      <select name="mspVendor" onChange="vendorChanged(this[this.selectedIndex].value);">
      <c:forEach var="mspVendorEntry" items="${multispeakBean.mspVendorList}">
        <option <c:if test="${mspVendorEntry.vendorID == multispeakBean.selectedVendorID}">selected</c:if> value='<c:out value="${mspVendorEntry.vendorID}"/>'> <c:out value="${mspVendorEntry.companyName}"/> </option>
      </c:forEach>
      </SELECT>         
    </td>
  </tr>
  <tr>
    <td colspan="2" onMouseOver="dispStatusMsg('Enter the Application Name');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">App Name</td>
    <td>
      <input type="text" name="mspAppName" value='<c:out value="${multispeakBean.selectedMspVendor.appName}"/>'>
    </td>
    <td>&nbsp;</td>
    <td style="text-align:left"><u>Response Message Login</u></td>
  </tr>  
  <tr>
    <td colspan="2" onMouseOver="dispStatusMsg('Enter the Username');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">MSP UserName</td>
    <td>
      <input type="text" name="mspUserName" value='<c:out value="${multispeakBean.selectedMspVendor.userName}"/>'>
    </td>
    <td onMouseOver="dispStatusMsg('Enter the Username for Outgoing Yukon messages');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">UserName</td>
    <td>
      <input type="text" name="outUserName" value='<c:out value="${multispeakBean.selectedMspVendor.outUserName}"/>'>
    </td>
  </tr>
  <tr>
    <td colspan="2" onMouseOver="dispStatusMsg('Enter the Password');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">MSP Password</td>
    <td>
      <input type="text" name="mspPassword" value='<c:out value="${multispeakBean.selectedMspVendor.password}"/>'>
    </td>
    <td onMouseOver="dispStatusMsg('Enter the Password for Outgoing Yukon messags');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">Password</td>
    <td>
      <input type="text" name="outPassword" value='<c:out value="${multispeakBean.selectedMspVendor.outPassword}"/>'>
    </td>
  </tr>
  <tr>
    <td colspan="2" onMouseOver="dispStatusMsg('Enter the unique key');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">MSP Unique Key</td>
    <td>
      <select name="mspUniqueKey">
        <option value="meterNumber" <c:if test="${multispeakBean.selectedMspVendor.uniqueKey == 'meterNumber'}">selected</c:if>>meterNumber</option>
        <option value="deviceName" <c:if test="${multispeakBean.selectedMspVendor.uniqueKey == 'deviceName'}">selected</c:if>>deviceName</option>
      </SELECT>                     
    </td>
  </tr>
  <tr height="40" valign="bottom">
    <td colspan="2" style="text-align:right"><u>Interfaces</u>
    </td>
  </tr>
  <tr>
    <td colspan="2" style="text-align:right" onMouseOver="dispStatusMsg('Enter the Multispeak URL   EX: http://127.0.0.1:80/soap/ ');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">MSP URL</td>
    <td>
      <input type="text" name="mspURL" size="30" value='<c:out value="${multispeakBean.selectedMspVendor.url}"/>'>
    </td>
  </tr>

  <c:set var="interfacesMap" value="${multispeakBean.selectedInterfacesMap}" scope="page" />  
  <c:forEach var="mspPossibleInterface" items="${multispeakBean.allInterfaces}" varStatus="status">
    <c:set var="interfaceValue" value="${interfacesMap[mspPossibleInterface]}" scope="page" />    
    <c:set var="disabled" value="${interfaceValue == null}" scope="page" />  
      <tr>
        <td style="text-align:right">
          <input id="mspInterface" type="checkbox" <c:if test="${!disabled}">checked</c:if> name='mspInterface' value='<c:out value="${mspPossibleInterface}"/>' onclick='enableEndpointValue(this.checked, this.value)'>
        </td>
        <td style="text-align:center"><c:out value="${mspPossibleInterface}"/>
        </td>
        <td colspan="2">
          <input id="mspEndpoint<c:out value="${mspPossibleInterface}"/>" type="text" name="mspEndpoint" size="30" 
                value='<c:out value="${interfaceValue.mspEndpoint}" default="${mspPossibleInterface}Soap"/>'
                <c:if test="${disabled}">disabled</c:if>>                
          <c:choose>
            <c:when test="${disabled}">
              <a id="mspPing<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="pingURL">Ping</a>
              <a id="mspMethods<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="getMethods">Methods</a>
            </c:when>
            <c:otherwise>
              <a id="mspPing<c:out value="${mspPossibleInterface}"/>" style='cursor:pointer' href='JavaScript:serviceSubmit("<c:out value='${interfaceValue.mspInterface}'/>", "pingURL");' class="Link4" name="pingURL">Ping</a>
              <a id="mspMethods<c:out value="${mspPossibleInterface}"/>" style='cursor:pointer' href='JavaScript:serviceSubmit("<c:out value='${interfaceValue.mspInterface}'/>", "getMethods");' class="Link4" name="getMethods">Methods</a>
            </c:otherwise>
          </c:choose>         
        </td>
        <c:if test="${status.first}">
        <td rowspan='<%=MultispeakDefines.MSP_INTERFACE_ARRAY.length%>'>
          <textarea name="Results" readonly wrap="VIRTUAL" style='color:<c:out value="${sessionScope.resultColor}"/>'><c:out value="${sessionScope.MSP_RESULT_MSG}"/></textarea>
        </td>
        </c:if>          
      </tr>
  </c:forEach>
  <tr> 
    <td align="right" colspan="3">
      <input type="submit" name="Save" value="Save" onclick="document.form1.ACTION.value='Save';">
      <input type="submit" name="Delete" value="Delete" onclick="document.form1.ACTION.value='Delete';">
      <input type="button" name="New" value="New" onclick="javascript:window.location='<%=request.getContextPath()%>/msp_setup_new.jsp?init'">
    </td>
  </tr>
</table>
</form>
</div>
</cti:titledContainer>
</cti:standardPage>