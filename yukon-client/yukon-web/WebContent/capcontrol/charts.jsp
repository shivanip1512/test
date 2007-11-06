
<jsp:directive.page import="com.cannontech.cbc.cache.CapControlCache"/>
<jsp:directive.page import="com.cannontech.spring.YukonSpringHook"/><%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@include file="cbc_inc.jspf"%>

<%
	int MAX_TRENDS = 3;
    CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
	String type = ParamUtil.getString(request, "type", CBCWebUtils.TYPE_VARWATTS );
	String period = ParamUtil.getString(request, "period", ServletUtil.PREVTHIRTYDAYS );
	String[] chartParam = ParamUtil.getStrings(request, "value");
	String[] titles = new String[ chartParam.length ];
	if( chartParam == null ) chartParam = new String[0];

	for( int i = 0; i < chartParam.length && i < MAX_TRENDS; i++ )
	{
		int id = Integer.parseInt( chartParam[i] );
		chartParam[i] = CBCWebUtils.genGraphURL( id, capControlCache, period, type );

		titles[i] = "Trend for: " + capControlCache.getCapControlPAO(new Integer(id));//.getCcName();
	}

%>
  <table id="chartTable" width="505px" border="0" cellspacing="0" cellpadding="0" class="pageBlank" >
    <tr> 
      <td class="cellImgFill"><img src="images/Header_left.gif" class="cellImgFill"></td>
      <td class="trimBGColor cellImgShort">Charts (Only the first <%=MAX_TRENDS%> selected are displayed)</td>
      <td class="trimBGColor cellImgShort" onclick="cClick()"><a href='javascript:void(0)'>X</a></td>
      <td class="cellImgFill"><img src="images/Header_right.gif" class="cellImgFill"></td>
    </tr>
    <tr>
      <td class="cellImgFill lAlign" background="images/Side_left.gif"></td>
      <td colSpan="2">
      
      <div class="scrollHuge">
        <table id="innerTable" width="100%" border="1" cellspacing="0" cellpadding="0">
<%
	if( chartParam.length == 0 ) {
%>
        <tr class="columnHeader">
			<td><br></td>
		</tr>
        <tr class="tableCell">
			<td class="alert cAlign">
			No items selected, try again</td>
		</tr>
<%
	}
	else for( int i = 0; i < chartParam.length && i < MAX_TRENDS; i++ ) {
		if( chartParam[i] != null ) {
%>
        <tr class="columnHeader">
			<td><br></td>
		</tr>
        <tr class="altTableCell">
			<td><%=titles[i]%></td>
		</tr>
        <tr class="tableCell">
            <td><img src="<%=chartParam[i]%>"/></td>
		</tr>
<%	  	} else {  %>
        <tr class="columnHeader">
			<td><br></td>
		</tr>
        <tr class="altTableCell">
			<td><%=titles[i]%></td>
		</tr>
        <tr class="tableCell">
			<td class="alert cAlign">
			No point data found</td>
		</tr>
<%
			}
	} %>
        </table>
    </div>

      </td>
      <td class="cellImgFill rAlign" background="images/Side_right.gif"></td>
    </tr>
    <tr>
      <td class="cellImgShort"><img src="images/Bottom_left.gif"></td>
      <td class="cellImgShort" background="images/Bottom.gif" colspan="2"></td>
      <td class="cellImgShort"><img src="images/Bottom_right.gif"></td>
    </tr>
  </table>