<%@include file="cbc_inc.jspf"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	String type = ParamUtil.getString(request, "type", "");
	String srchCriteria = ParamUtil.getString(request, "searchCriteria", "");
	LiteWrapper[] items = new LiteWrapper[0];

	if( CBCWebUtils.TYPE_ORPH_FEEDERS.equals(type) )
	{
		items = capControlCache.getOrphanedFeeders();
		srchCriteria = "Orphaned Feeders";
	}
	else if( CBCWebUtils.TYPE_ORPH_BANKS.equals(type) )
	{
		items = capControlCache.getOrphanedCapBanks();
		srchCriteria = "Orphaned CapBanks";
	}
	else if( CBCWebUtils.TYPE_ORPH_CBCS.equals(type) )
	{
		items = capControlCache.getOrphanedCBCs();
		srchCriteria = "Orphaned CBCs";
	}
	else
	{	
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
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

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
				<form id="findForm" action="results.jsp" method="post">
					<p class="main">Find: <input type="text" name="searchCriteria">
					<INPUT type="image" name="Go" src="images\GoButton.gif" alt="Find"></p>
				</form>
			</div>
          </td>

          <td valign="top">
	          <div class="rAlign">
				<cti:breadCrumb>
					<cti:crLink url="subareas.jsp" title="SubBus Areas" cssClass="crumbs" />
					<cti:crLink url="results.jsp" title="Results" cssClass="crumbs" />
				</cti:breadCrumb>
	          </div>
          </td>
		</tr>

      </table>

      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td class="cellImgFill"><img src="images\Header_left.gif" class="cellImgFill"></td>
          <td class="trimBGColor cellImgShort">Search Resuls For: '<%=srchCriteria%>'   (<%=items.length%> found)</td>
          <td class="cellImgFill"><img src="images\Header_right.gif" class="cellImgFill"></td>
        </tr>
        <tr>
          <td class="cellImgFill lAlign" background="images\Side_left.gif"></td>
          <td>
          
          <div class="scrollLarge">
            <table id="resTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnheader lAlign">				
				<td>Name</td>
                <td>Item Type</td>
                <td>Description</td>
                <td>Parent</td>
              </tr>

			<form id="resForm" action="feeders.jsp" method="post">
			<input type="hidden" name="itemid" />
<%
for( int i = 0; i < items.length; i++ )
{
	String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
	LiteWrapper item = items[i];
%>
	        <tr class="<%=css%>">
				<td>
				<a href="#" onclick="postMany('resForm', 'itemid', <%=item.getItemID()%>)">
				<%=item.toString()%>
				</a></td>
				<td><%=item.getItemType()%></td>
				<td><%=item.getDescription()%></td>
				<td><%=item.getParent()%></td>
			</tr>
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

<%@include file="cbc_footer.jspf"%>

</HTML>
