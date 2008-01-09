<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ page import="com.cannontech.database.data.lite.LiteTypes" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.web.navigation.CtiNavObject" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>

<cti:standardPage title="Search Results" module="capcontrol">
<%@include file="cbc_inc.jspf"%>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
    CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
	// String type = ParamUtil.getString(request, "type", "");
	String srchCriteria = ParamUtil.getString(request, CCSessionInfo.STR_LAST_SEARCH, null);
	ParentStringPrinter psp = new ParentStringPrinter (capControlCache);
	psp.setLinkedToEditors(true);
	CtiNavObject nav = (CtiNavObject) request.getSession(false).getAttribute(ServletUtil.NAVIGATE);
	String returnURL = nav.getPreviousPage();
	
	if( srchCriteria == null )
		srchCriteria = ccSession.getLastSearchCriteria();

	String label = srchCriteria;

	LiteWrapper[] items = new LiteWrapper[0];

	if( CBCWebUtils.TYPE_ORPH_SUBSTATIONS.equals(srchCriteria) ) {
		items = capControlCache.getOrphanedSubstations();
		label = "Orphaned Substations";
	}
	else if( CBCWebUtils.TYPE_ORPH_SUBS.equals(srchCriteria) ) {
		items = capControlCache.getOrphanedSubBuses();
		label = "Orphaned Substation Buses";
	}
	else if( CBCWebUtils.TYPE_ORPH_FEEDERS.equals(srchCriteria) ) {
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
  <cti:crumbLink url="subareas.jsp" title="Home" />
  <cti:crumbLink url="subareas.jsp" title="Substation Areas"/>
  <cti:crumbLink url="results.jsp" title="Results"/>
</cti:breadCrumbs>

<cti:titledContainer title='<%="Search Results For: " + label + "   (" + items.length + " found)"%>'>

<form id="parentForm" action="feeders.jsp" method="post">
	<input type="hidden" name="<%=CCSessionInfo.STR_CC_AREA%>" />
	<input type="hidden" name="<%=CCSessionInfo.STR_SUBID%>" />

   <table id="headerTable" width="100%" border="0" cellspacing="0" cellpadding="0">
 	 <tr class="columnHeader lAlign">
 	 <td>Name</td>
     <td>Item Type</td>
     <td>Description</td>
     <td>Parent</td>    
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
		<% }  %>
				<%=item.toString()%></td>
				<td><%=item.getItemType()%></td>

				<td><%= item.getDescription()%></td>
				<td>
<% 
	boolean isPoint = item.getParentID() != CtiUtilities.NONE_ZERO_ID;
	String parentString = (!isPoint) ? psp.printPAO(item.getItemID()) :  psp.printPoint(item.getItemID());
%>
	<%=parentString %>
				</td>
			</tr>
<% } %>

            </table>
        </div>
<br/>        

<%	if( returnURL.contains("results.jsp") || returnURL.contains(".jsf")) { %>
<input id="BackButton" type="button" value="Back" disabled>
<%} else {%>
<input id="BackButton" type="button" value="Back" onclick="javascript:location.href='<%=returnURL %>'">
<% } %>

</form>
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('resTable','headerTable');    }, false);
</script>

</cti:titledContainer>

</cti:standardPage>

