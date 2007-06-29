<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:standardPage title="MultiSpeak Setup" module="multispeak">
<cti:standardMenu menuSelection="multispeak|interfaces"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/msp_setup.jsp" title="Multispeak"  />
    &gt; Create Interface
</cti:breadCrumbs>
<%@ page import="com.cannontech.multispeak.client.*" %>

<jsp:useBean id="multispeakBean" class="com.cannontech.multispeak.client.MultispeakBean" scope="session"/>
<c:if test="${param.init != null}">
<c:set var="MSP_RESULT_MSG" value="" scope="session" />
<c:set var="ERROR_MESSAGE" value="" scope="session" />
</c:if>
<script language="JavaScript">
function enableEndpointValue(selected, interface) { //v1.0
 
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
    javascript:window.location='<%=request.getContextPath()%>/msp_setup.jsp?vendor='+vendor+'&MSP_RESULT_MSG';
}
</script>
    
<br>
  <h2 class="setup">Welcome to</h2>
  <h3 class="setup">Yukon MultiSpeak Interface Setup</h3>
  <h4 class='ErrorMsg'><c:out value="${sessionScope.ERROR_MESSAGE}" default=""/></h4>

<cti:titledContainer title="MultiSpeak Interface Setup">
<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/MultispeakServlet">
<input type="hidden" name="ACTION" value="updateMSP">
<input type="hidden" name="REDIRECT" value='<c:out value="${pageContext.request.requestURI}"/>' >
<input type="hidden" name="actionEndpoint">
<input type="hidden" name="actionService">

<table class="keyValueTable">
  <tr> 
    <td colspan="2" onMouseOver="dispStatusMsg('Select vendor');return document.statVal" 
        onMouseOut="dispStatusMsg('');return document.statVal">Company Name
    </td>
    <td colspan="2">
      <input type="text" name="mspCompanyName">
    </td>
  </tr>
  <tr>
    <td colspan="2" onMouseOver="dispStatusMsg('Enter the Application Name');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">App Name</td>
    <td>
      <input type="text" name="mspAppName">
    </td>
    <td>&nbsp;</td>
    <td style="text-align:left"><u>Response Message Login</u></td>
  </tr>  
  <tr>
    <td colspan="2" onMouseOver="dispStatusMsg('Enter the Username');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">MSP UserName</td>
    <td>
      <input type="text" name="mspUserName">
    </td>
    <td onMouseOver="dispStatusMsg('Enter the Username for Outgoing Yukon messages');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">UserName</td>
    <td>
      <input type="text" name="outUserName">
    </td>
  </tr>
  <tr>
    <td colspan="2" onMouseOver="dispStatusMsg('Enter the Password');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">MSP Password</td>
    <td>
      <input type="text" name="mspPassword">
    </td>
    <td onMouseOver="dispStatusMsg('Enter the Password for Outgoing Yukon messags');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">Password</td>
    <td>
      <input type="text" name="outPassword">
    </td>
  </tr>
  <tr>
    <td colspan="2" onMouseOver="dispStatusMsg('Enter the unique key');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">MSP Unique Key</td>
    <td>
      <select name="mspUniqueKey">
        <option value="meterNumber" selected>meterNumber</option>
        <option value="deviceName" >deviceName</option>
      </SELECT>                     
    </td>
  </tr>
  <tr height="40" valign="bottom">
    <td colspan="2" style="text-align:right"><u>Interfaces</u>
    </td>
  </tr>
  <tr>
    <td colspan="2" style="text-align:right" onMouseOver="dispStatusMsg('Enter the MultiSpeak URL   EX: http://127.0.0.1:80/soap/ ');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">MSP URL</td>
    <td>
      <input type="text" name="mspURL" size="30" value="http://&lt;server&gt;:&lt;port&gt;/soap/">
    </td>
  </tr>

  <c:forEach var="mspPossibleInterface" items="${multispeakBean.allInterfaces}" varStatus="status">
    <tr>
      <td style="text-align:right">
        <input id="mspInterface" type="checkbox" name='mspInterface' value='<c:out value="${mspPossibleInterface}"/>' onclick='enableEndpointValue(this.checked, this.value)'>
      </td>
      <td style="text-align:center"><c:out value="${mspPossibleInterface}"/>
      </td>
      <td colspan="2">
        <input id="mspEndpoint<c:out value="${mspPossibleInterface}"/>" type="text" name="mspEndpoint" size="30" disabled
            value='<c:out value="${mspPossibleInterface}"/>Soap'>
          <a id="mspPing<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="pingURL">Ping</a>
          <a id="mspMethods<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="getMethods">Methods</a>
      </td>
      <c:if test="${status.first}">
      <td rowspan='<%=MultispeakDefines.MSP_INTERFACE_ARRAY.length%>'>
        <textarea cols="50" rows='<%=MultispeakDefines.MSP_INTERFACE_ARRAY.length * 2%> name="Results" readonly wrap="VIRTUAL" style='color:<c:out value="${sessionScope.resultColor}"/>'><c:out value="${sessionScope.MSP_RESULT_MSG}"/></textarea>
      </td>
      </c:if>          
    </tr>
  </c:forEach>
  <tr> 
    <td align="right" colspan="3">
      <input type="submit" name="Submit" value="Save" onclick="document.form1.ACTION.value='Create';">
      <input type="button" name="Cancel" value="Cancel" onclick="location.href='msp_setup.jsp?init'">
    </td>
  </tr>
</table>
</form>
</cti:titledContainer>
</cti:standardPage>