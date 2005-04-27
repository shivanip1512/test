<%@include file="cbc_inc.jspf"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%
	String srchCriteria = ParamUtil.getString(request, "searchCriteria", "");
	LiteBaseResults lbr = new LiteBaseResults();
	lbr.searchLiteObjects( srchCriteria );

	List items = lbr.getFoundItems();
	int foundItems = lbr.getFoundItems().size();
	
//	searchResults = lbr;
//	getRequestScope().put( "criteriaValue", lbr.getCriteria() );
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

<META name="GENERATOR" content="IBM WebSphere Studio">
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
      <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center" height="30">
        <tr>
          <td valign="bottom" colspan="2">
            <div class="rAlign">
				<cti:breadCrumb>
					<cti:crLink url="subs.jsp" title="Substations" cssClass="crumbs" />
					<cti:crLink url="results.jsp" title="Results" cssClass="crumbs" />
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
          <td class="trimBGColor cellImgShort">Search Resuls For: '<%=srchCriteria%>'   (<%=foundItems%> found)</td>
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
                <td>Parent</td>
              </tr>

			<form name="resForm" action="feeders.jsp" method="post">
			<input type="hidden" name="<%=CBCSessionInfo.STR_SUBID%>" />
<%
for( int i = 0; i < items.size(); i++ )
{
	String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
	LiteWrapper item = (LiteWrapper)items.get(i);
%>
	        <tr class="<%=css%>">
				<td>
				<a href="#" onclick="postIt('resultItemID', <%=item.getItemID()%>, 'resForm')">
				<%=item.toString()%>
				</a></td>
				<td><%=item.getItemType()%></td>
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


</HTML>
