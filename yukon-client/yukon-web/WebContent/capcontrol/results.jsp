<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ page import="com.cannontech.database.data.lite.LiteTypes" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.web.navigation.CtiNavObject" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>

<cti:standardPage title="Search Results" module="capcontrol">
<%@include file="cbc_inc.jspf"%>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
    CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
    ParentStringPrinterFactory printerFactory = YukonSpringHook.getBean("parentStringPrinterFactory", ParentStringPrinterFactory.class);
	String srchCriteria = ParamUtil.getString(request, CCSessionInfo.STR_LAST_SEARCH, null);
	ParentStringPrinter psp = printerFactory.createParentStringPrinter(request);
	CtiNavObject nav = (CtiNavObject) request.getSession(false).getAttribute(ServletUtil.NAVIGATE);
	
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

<cti:standardMenu menuSelection="orphans"/>

<cti:breadCrumbs>
  <cti:crumbLink url="subareas.jsp" title="Home" />
  <cti:crumbLink title="Results"/>
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
				<input type="checkbox" name="cti_chkbxSubBuses" value="<%=item.getItemID()%>"/>
				<cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                    <a href="/editor/cbcBase.jsf?type=2&itemid=<%=item.getItemID()%>&ignoreBookmark=true" class="editImg">
                        <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
                    </a>
                    <a href="/editor/deleteBasePAO.jsf?value=<%=item.getItemID()%>" class="editImg">
                        <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
                    </a>
                </cti:checkProperty>
		<% } else { %>
				<cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                    <a href="/editor/pointBase.jsf?parentId=<%=item.getParentID()%>&itemid=<%=item.getItemID()%>" class="editImg">
                        <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
                    </a>
                    <a href="/editor/deleteBasePoint.jsf?value=<%=item.getItemID()%>" class="editImg">
                        <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
                    </a>
                </cti:checkProperty>
		<% }  %>
		<% if( CBCUtils.isController( item ) ) { %>
            <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                <a href="/editor/copyBase.jsf?itemid=<%=item.getItemID() %>&type=1>"><img src="/editor/images/page_copy.gif" border="0" height="15" width="15"/></a>
			</cti:checkProperty>
		<% }%>
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

</form>
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('resTable','headerTable');    }, false);
</script>

</cti:titledContainer>

</cti:standardPage>

