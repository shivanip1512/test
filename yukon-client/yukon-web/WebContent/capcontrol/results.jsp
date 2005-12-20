<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.database.data.lite.LiteTypes" %>
<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache" type="com.cannontech.cbc.web.CapControlCache"
	scope="application"></jsp:useBean>

<%
	//String type = ParamUtil.getString(request, "type", "");
	String srchCriteria = ParamUtil.getString(request, CBCSessionInfo.STR_LAST_SEARCH, null);
	if( srchCriteria == null )
		srchCriteria = cbcSession.getLastSearchCriteria();

	String label = srchCriteria;

	LiteWrapper[] items = new LiteWrapper[0];

	if( CBCWebUtils.TYPE_ORPH_FEEDERS.equals(srchCriteria) ) {
		items = capControlCache.getOrphanedFeeders();
		label = "Orphaned Feeders";
	}
	else if( CBCWebUtils.TYPE_ORPH_BANKS.equals(srchCriteria) ) {
		items = capControlCache.getOrphanedCapBanks();
		label = "Orphaned CapBanks";
	}
	else if( CBCWebUtils.TYPE_ORPH_CBCS.equals(srchCriteria) ) {
		items = capControlCache.getOrphanedCBCs();
		label = "Orphaned CBCs";
	}
	else {		
		LiteBaseResults lbr = new LiteBaseResults();
		lbr.searchLiteObjects( srchCriteria );
		items = lbr.getFoundItems();
	}
	

%>

<HTML>
<HEAD>
<%@ page 
language="java"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>
<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<TITLE>Search Results</TITLE>
</HEAD>

<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
    <%@include file="cbc_header.jspf"%>
    </td>
  </tr>

    <td> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" height="30">
		<tr>
          <td valign="top">
	          <div class="lAlign">
				<cti:breadCrumbs>
					<cti:crumbLink url="subareas.jsp" title="SubBus Areas" />
					<cti:crumbLink url="results.jsp" title="Results" />
				</cti:breadCrumbs>
	          </div>
          </td>
		
          <td valign="top">
			<div class="rAlign">
				<form id="findForm" action="results.jsp" method="post">
					<p class="main">Find: <input type="text" name="<%=CBCSessionInfo.STR_LAST_SEARCH%>">
					<INPUT type="image" name="Go" src="images\GoButton.gif" alt="Find"></p>
				</form>
			</div>
          </td>
		</tr>

      </table>

      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td class="cellImgFill"><img src="images\Header_left.gif" class="cellImgFill"></td>
          <td class="trimBGColor cellImgShort">Search Resuls For: '<%=label%>'   (<%=items.length%> found)</td>
          <td class="cellImgFill"><img src="images\Header_right.gif" class="cellImgFill"></td>
        </tr>
        <tr>
          <td class="cellImgFill lAlign" background="images\Side_left.gif"></td>
          <td>



<form id="parentForm" action="feeders.jsp" method="post">
	<input type="hidden" name="<%=CBCSessionInfo.STR_CBC_AREA%>" />
	<input type="hidden" name="<%=CBCSessionInfo.STR_SUBID%>" />

          <div class="scrollLarge">          
            <table id="resTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnheader lAlign">				
				<td>Name</td>
                <td>Item Type</td>
                <td>Description</td>
                <td>Parent</td>
              </tr>

<%
for( int i = 0; i < items.length; i++ )
{
	String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
	LiteWrapper item = items[i];
%>
	        <tr class="<%=css%>">
				<td>
		<% if( item.getLiteType() == LiteTypes.YUKON_PAOBJECT ) { %>
				<input type="checkbox" name="cti_chkbxSubs" value="<%=item.getItemID()%>"/>
		<% } else { %>
				<input type="checkbox" name="cti_chkbxPoints" value="<%=item.getItemID()%>"/>
		<% } %>
				<%=item.toString()%></td>
				<td><%=item.getItemType()%></td>
				<td><%=item.getDescription()%></td>
				<td>
	<% 
		int parID = item.getParentID();
		if( parID > CtiUtilities.NONE_ZERO_ID ) { %>
				<%=item.getParent()%>
	<% } else {
			parID = capControlCache.getParentSubBusID(item.getItemID());
				
			if( parID <= CtiUtilities.NONE_ZERO_ID ) { %>
				<%=item.getParent()%>
		<% } else { 
				SubBus pBus = capControlCache.getSubBus( new Integer(parID) ); %>

				<% if( pBus != null) { %>
					<a href="#" class="<%=css%>"
						onclick="postMany('parentForm', '<%=CBCSessionInfo.STR_SUBID%>', <%=pBus.getCcId()%>, '<%=CBCSessionInfo.STR_CBC_AREA%>', '<%=pBus.getCcArea()%>')">
					<%=pBus.getCcName()%></a>
				<% } else { %>
					<span class="<%=css%>" >
					<%=item.getParent()%></span>
				<% } %>
		<% } %>
	<% } %>
				</td>
			</tr>
<% } %>

            </table>
        </div>
</form>


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

<%@include file="cbc_footer.jspf"%>

</HTML>
