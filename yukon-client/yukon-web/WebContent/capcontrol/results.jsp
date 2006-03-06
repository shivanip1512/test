<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:standardPage title="Search Results" module="capcontrol">
<%@ page import="com.cannontech.web.editor.*" %>
<%@ page import="com.cannontech.database.data.lite.LiteTypes" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache" type="com.cannontech.cbc.web.CapControlCache"
	scope="application"></jsp:useBean>
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
	// String type = ParamUtil.getString(request, "type", "");
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

<cti:standardMenu/>

<cti:breadCrumbs>
  <cti:crumbLink url="subareas.jsp" title="SubBus Areas"/>
  <cti:crumbLink url="results.jsp" title="Results"/>
</cti:breadCrumbs>

<cti:titledContainer title='<%="Search Resuls For: " + label + "   (" + items.length + " found)"%>'>

<form id="parentForm" action="feeders.jsp" method="post">
	<input type="hidden" name="<%=CBCSessionInfo.STR_CBC_AREA%>" />
	<input type="hidden" name="<%=CBCSessionInfo.STR_SUBID%>" />

   <table id="headerTable" width="100%" border="0" cellspacing="0" cellpadding="0">
 	 <tr class="columnHeader lAlign">
 	 <%
      String[] names = SearchResultsTableRow.getColumnNames();
     for (int j=0; j < names.length; j++) {
     %>
     <td><%=names[j]%></td>
     <%}%>
     </tr>
 </table>
<div class="scrollLarge">          
<table id="resTable" width="98%" border="0" cellspacing="0" cellpadding="0">

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
		<% } 
        String[] columns = {item.toString(), item.getItemType(), item.getDescription()};
        SearchResultsTableRow row = new SearchResultsTableRow (columns); 
        row.format();        
        %>
				<%=row.getCell(0)%></td>
				<td><%=row.getCell(1)%></td>

				<td><%= row.getCell(2)%></td>
				<td>
	<% 
		
		int parID = item.getParentID();
		
		if( parID > CtiUtilities.NONE_ZERO_ID ) { %>				<%if (capControlCache.isCapBank(parID)) { //check to see if point has a parent%>
				<%=capControlCache.getParentNames(parID)%>
				<% }else { %>
				<%= item.getParent()%>
				<%}%>
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
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('resTable','headerTable');    }, false);
</script>

</cti:titledContainer>

</cti:standardPage>

