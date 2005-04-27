<%@include file="cbc_inc.jspf"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<HTML>
<HEAD>
<%@ page 
language="java"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>
<SCRIPT type="text/javascript">
<!--
	// -------------------------------------------
	// Page scoped javascript variables
	// -------------------------------------------
	var intSubID = -1;
//-->
</SCRIPT>
<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<META name="GENERATOR" content="IBM WebSphere Studio">
<TITLE>Substation Bus Areas</TITLE>
</HEAD>


<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
    <%@include file="cbc_header.jspf"%>
    </td>
  </tr>

    <td> 
      <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center" height="30">
        <tr>
          <td valign="bottom" colspan="2">
            <div class="rAlign">
				<cti:breadCrumb>
					<cti:crLink url="subareas.jsp" title="SubBus Areas" cssClass="crumbs" />
				</cti:breadCrumb>

				<form name="findForm" action="results.jsp" method="post">
					<p class="main">Find: <input type="text" name="searchCriteria">
					<INPUT type="image" name="Go" src="images\GoButton.gif" alt="Find"></p>
				</form>
            </div>
          </td>

        </tr>
      </table>

      <table width="95%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td class="cellImgFill"><img src="images\Header_left.gif" class="cellImgFill"></td>
          <td class="trimBGColor cellImgShort">Substation Bus Areas</td>
          <td class="cellImgFill"><img src="images\Header_right.gif" class="cellImgFill"></td>
        </tr>
        <tr>
          <td class="cellImgFill lAlign" background="images\Side_left.gif"></td>

          <td>          
          <div class="scrollLarge">
            <table id="areaTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnheader lAlign">				
				<td>Area Name</td>
                <td>Setup</td>
                <td>kVARS</td>
                <td>Avail. kVARS</td>
                <td>PFactor/Est.</td>
              </tr>

			<form name="areaForm" action="subs.jsp" method="post">
			<input type="hidden" name="<%=CBCSessionInfo.STR_CBC_AREA%>" />
<%
		for( int i = 0; i < capControlCache.getAreaNames().size(); i++ )
		{
			String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
			String areaStr = (String)capControlCache.getAreaNames().get(i);
			SubBus[] areaBuses = capControlCache.getSubsByArea(areaStr);
			//Feeder[] areaFeeders = capControlCache.getFeedersByArea(areaStr);
			CapBankDevice[] areaCapBanks = capControlCache.getCapBanksByArea(areaStr);
			
			String totalVars =
				CBCUtils.format( CBCUtils.calcTotalVARS(areaCapBanks) );
			String availVars =
				CBCUtils.format( CBCUtils.calcAvailableVARS(areaCapBanks) );

			String currPF = CBCDisplay.getPowerFactorText(
								CBCUtils.calcAvgPF(areaBuses), true);
			String estPF = CBCDisplay.getPowerFactorText(
								CBCUtils.calcAvgEstPF(areaBuses), true);
%>
	        <tr class="<%=css%>">
				<td>				
				<input type="checkbox" name="chkBxAreas" onclick="showSubElems( '<%=areaStr%>', this );"/>
				<a href="#" onclick="postMany('areaForm', '<%=CBCSessionInfo.STR_CBC_AREA%>', '<%=areaStr%>')">
				<%=areaStr%></a>
				</td>
				<td><%=areaBuses.length%> Substations</td>
				<td><%=totalVars%></td>
				<td><%=availVars%></td>
				<td><%=currPF%> / <%=estPF%></td>
			</tr>

<%
		for( int j = 0; j < areaBuses.length; j++ )
		{
			SubBus subBus = areaBuses[j];
			Feeder[] subFeeders = capControlCache.getFeedersBySub(subBus.getCcId());
			CapBankDevice[] subCapBanks = capControlCache.getCapBanksBySub(subBus.getCcId());	
%>
	        <tr id="<%=areaStr%>" class="<%=css%>" style="display: none;">
				<td><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN)%></td>
				<td><%=subFeeders.length%> Feeders, <%=subCapBanks.length%> Banks</td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
<% 		} %>





<% } %>
			</form>

            </table>
        	</div>
         </td>         
         
          <td class="cellImgFill rAlign" background="images\Side_right.gif"></td>
        </tr>
        <tr>
          <td class="cellImgShort"><img src="images\Bottom_left.gif"></td>
          <td class="cellImgShort" background="images\Bottom.gif"></td>
          <td class="cellImgShort"><img src="images\Bottom_right.gif"></td>
        </tr>
      </table>
    </td>
    
  </table>

</body>
</HTML>
