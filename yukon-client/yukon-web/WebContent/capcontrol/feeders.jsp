<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Feeders" module="capcontrol">
<%@include file="cbc_inc.jspf"%>
<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.yukon.cbc.CBCUtils" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>

<jsp:useBean id="capControlCache"
    class="com.cannontech.cbc.web.CapControlCache"
    type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>
<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
	
	String nd = "\"return nd(5000);\"";
	int subid = cbcSession.getLastSubID();
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);			
	String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
	if (popupEvent == null) popupEvent = "onmouseover";
	SubBus subBus = capControlCache.getSubBus( new Integer(subid) );
	Feeder[] feeders = capControlCache.getFeedersBySub( new Integer(subid) );
	CapBankDevice[] capBanks = capControlCache.getCapBanksBySub( new Integer(subid) );

	boolean hasControl = CBCWebUtils.hasControlRights(session);
	
%>

<cti:standardMenu/>

<cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="SubBus Areas"  />
    <cti:crumbLink url="subs.jsp" title="Substations" />
    <cti:crumbLink url="feeders.jsp" title="Feeders" />
</cti:breadCrumbs>


<script type="text/javascript">
   	Event.observe(window, 'load', function () {								
 								callBack();
 								});
   
   var GB_IMG_DIR = "../editor/css/greybox/";
   GreyBox.preloadGreyBoxImages();
   
   function onGreyBoxClose () {
   	window.parent.location.replace('feeders.jsp');
   }
 </script>
<%
String css = "tableCell";
%>
	<cti:titledContainer title="Substation">

			<table id="subTable" width="98%" border="0" cellspacing="0"
				cellpadding="0">
				<tr class="columnHeader lAlign">
					<th>Sub Name</th>
					<th>State</th>
					<th>Target</th>
					<th>VAR Load / Est.</th>
					<th>Date/Time</th>
					<th>PFactor / Est.</th>
					<th>Watts / Volts</th>
					<th>Daily / Max Ops</th>
				</tr>

<%
if( subBus != null ) {
%>
				<tr class="altTableCell">
					<td><input type="checkbox" name="cti_chkbxSubs" value="<%=subBus.getCcId()%>" />
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN)%>
					<% if( subBus.getVerificationFlag().booleanValue() ) { %>
						<span class="popupImg"
							onmouseover="statusMsg(this, 'This SubBus is currently being<br>used in a Verification schedule');" >
						(v)</span>
					<% } %>
					</td>
					
					<td>
					<% if( hasControl ) { %>
						<!--Create  popup menu html-->
						<input id="cmd_sub_<%=subBus.getCcId()%>" type="hidden" name = "cmd_dyn" value= "" />				
						<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>"
							style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;"
							href="javascript:void(0);"
						    <%= popupEvent %> ="return overlib(
								$F('cmd_sub_<%=subBus.getCcId()%>'),
								STICKY, WIDTH,210, HEIGHT,170, OFFSETX,-15,OFFSETY,-15,MOUSEOFF, FULLHTML);"
						    onmouseout = <%=nd %> > 
						    
					<% } else { %>
						<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;" >
					<% } %>
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
					</a>
					</td>
					
					
					<td><a type="param1" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_COLUMN) %></a>
					</td>
					<td><a type="param2" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_VAR_LOAD_COLUMN)%></a>
					</td>
					<td><a type="param3" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TIME_STAMP_COLUMN)%></a>
					</td>
					<td><a type="param4" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_POWER_FACTOR_COLUMN)%></a>
					</td>
					<td><a type="param5" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_WATTS_COLUMN)%></a>
					</td>
					<td><a type="param6" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN)%></a>
					</td>
				</tr>
<% } %>

			</table>
		</cti:titledContainer>

	<br>

	<cti:titledContainer title="Feeders">

    <form id="fdrForm" action="feeders.jsp" method="post">
       <table id=fdrHeaderTable width="100%" border="0" cellspacing="0" cellpadding="0">
         <tr class="columnHeader lAlign">
         <td><input type="checkbox" name="chkAllFdrsBx"
              onclick="checkAll(this, 'cti_chkbxFdrs');" /> Feeder Name</td>
         <td>State</td>
         <td>Target</td>
         <td>VAR Load / Est.</td>
         <td>Date/Time</td>
         <td>PFactor / Est.</td>
         <td>Watts / Volts</td>
         <td>Daily / Max Ops</td>
         </tr>
       </table>

			<div>
			<table id="fdrTable" width="98%" border="0" cellspacing="0" cellpadding="0">
<%
css = "tableCell";
for( int i = 0; i < feeders.length; i++ )
{
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	Feeder feeder = feeders[i];
%>
				<tr class="<%=css%>">
					<td><input type="checkbox" name="cti_chkbxFdrs" value="<%=feeder.getCcId()%>"/><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %></td>

					<td>
<% if( hasControl ) { %>
	<input id="cmd_fdr_<%=feeder.getCcId()%>" type="hidden" name = "cmd_dyn" value= "" />	
	<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>"
		style="color: <%=CBCDisplay.getHTMLFgColor(feeder)%>;"
		href="javascript:void(0);"
	    <%= popupEvent %> ="return overlib(
			$F('cmd_fdr_<%=feeder.getCcId()%>'),
			STICKY, WIDTH,<%=feeder.getCcName().length() * 8 +  75%>, HEIGHT,75, OFFSETX,-15, OFFSETY,-15,
			MOUSEOFF, FULLHTML);"
	    onmouseout = <%=nd %> >	
<% } else { %>
	<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(feeder)%>;" >
<% } %>
	<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_CURRENT_STATE_COLUMN)%>
</a>
					</td>

					<td><a type="param1" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TARGET_COLUMN)%></a>
					</td>
					<td><a type="param2" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_VAR_LOAD_COLUMN)%></a>
					</td>
					<td><a type="param3" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TIME_STAMP_COLUMN)%></a>
					</td>
					<td><a type="param4" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_POWER_FACTOR_COLUMN)%></a>
					</td>
					<td><a type="param5" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_WATTS_COLUMN)%></a>
					</td>
					<td><a type="param6" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_DAILY_OPERATIONS_COLUMN)%></a>
					</td>
				</tr>
<% } %>

				
			</table>
			</div>
            </form>
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('fdrTable','fdrHeaderTable');    }, false);
</script>

			</cti:titledContainer>

	<br>

	<cti:titledContainer title="Capacitor Banks" id="last_titled_container">
         <form id="capBankForm" action="feeders.jsp" method="post">
             <table id="capBankHeaderTable" width="100%" border="0" cellspacing="0" cellpadding="0">
             
             <tr class="columnHeader lAlign">
              	<td/>
              	<td> Parent Feeder </td>
              	<td/>
              	<td/>
              	<td/>
              	<td/>
              	<td/>
              	<td/>
              <tr/> 
             <tr class="columnHeader lAlign">
                <td><input type="checkbox" name="chkAllBanksBx"
                    onclick="checkAll(this, 'cti_chkbxBanks');" /> </td>
                    
             <td id="parent_fdr_td"/>  
 
                    <td> CB Name (Order)
                    <img class="rAlign popupImg" src="images\question.gif"
                        onmouseover="statusMsg(this, 'Order is the order the CapBank will control in.<br>Commands that can be sent to a field device are initiated from this column');" />
                </td>
         
                <td>State <img class="rAlign popupImg" src="images\question.gif"
                        onmouseover="statusMsg(this, 'System Commands, those commands that do NOT send out a message to a field device, can be initiated from this column');"/>
                </td>
                <td>Bank Address</td>
                <td>Date/Time</td>
                <td>Bank Size</td>
  
                <td>Op Count</td>
              </tr>              
              

             </table>

		<div id="capBankDiv">
			<table id="capBankTable" width="98%" border="0" cellspacing="0" cellpadding="0" >



<%
css = "tableCell";
for( int i = 0; i < capBanks.length; i++ )
{
	CapBankDevice capBank = capBanks[i];
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
%>
				<tr class="<%=css%>">
										

					<td><input type="checkbox" name="cti_chkbxBanks" value="<%=capBank.getCcId()%>" /></td>
					<td>
		          	<input id="cmd_cap_move_back_<%=capBank.getCcId()%>" type="hidden" value= "" />
		          	<a href="javascript:void(0);"
		          		<% if( capBank.isBankMoved() ) { %>
		       
		          			class="warning" <%= popupEvent %>=
		          			"
							return overlib(
									$F('cmd_cap_move_back_<%=capBank.getCcId()%>'),
									STICKY, WIDTH,155, HEIGHT,50, OFFSETX,-15,OFFSETY,-15, FULLHTML);"
									onmouseout = <%=nd %> 
		          		<% } else { %>
							onmouseover="statusMsg(this, 'Click here to temporarily move this CapBank from it\'s current parent feeder');"
		          			onclick="return GB_show('CapBank Temp Move (Pick feeder by clicking on name)','tempmove.jsp?bankid='+<%=capBank.getCcId()%>, 500, 700, onGreyBoxClose);"
		          		<% } %>
						><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_PARENT_COLUMN)%>
					</a>					
					</td>
					<td>
					<% if( hasControl && !CtiUtilities.STRING_NONE.equals(subBus.getControlUnits()) ) { %>
						<input id="cmd_cap_<%=capBank.getCcId()%>_field" type="hidden" name = "cmd_dyn" value= "" />
						<a href="javascript:void(0);"
						    <%= popupEvent %>="return overlib(
								$F('cmd_cap_<%=capBank.getCcId()%>_field'),
								STICKY, WIDTH,155, HEIGHT,110, OFFSETX,-15,OFFSETY,-15,
								MOUSEOFF, FULLHTML, ABOVE);"
						    onmouseout = <%=nd %> >	
							
							<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN) %>
						</a>
					<% } else { %>
						<span>
							<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN) %>
						</span>
					<% } 
					%>

					<!-- -------------------------------------->
					<%
					LiteYukonPAObject obj = DaoFactory.getPaoDao().getLiteYukonPAO(capBank.getControlDeviceID().intValue());					
					if (CBCUtils.isTwoWay(obj)) {
					%>					
						<a href="#" 
							onclick="return GB_show('Device <%=obj.getPaoName()%>', 'cbcPointTimestamps.jsp?cbcID=<%=obj.getLiteID()%>', 500, 600)" >
					        <img class="rAlign popupImg" src="images\magnifier.gif"
                        		onmouseover="statusMsg(this, 'Click here to see the timestmap information.<br>for the cap bank controller device');" />
       
					</a>
					<%}%>
					</td>

					<td>
					<% if( hasControl ) { %>
						<input id="cmd_cap_<%=capBank.getCcId()%>_system" type="hidden" name = "cmd_dyn" value= "" />
						<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>"
							style="color: <%=CBCDisplay.getHTMLFgColor(capBank)%>;"
							href="javascript:void(0);"
						    <%= popupEvent %>	= "return overlib(
								$F('cmd_cap_<%=capBank.getCcId()%>_system'),
								STICKY, WIDTH,155, HEIGHT,200, OFFSETX,-15,OFFSETY,-15,
								MOUSEOFF, FULLHTML, ABOVE);"
						    
						    onmouseout = <%=nd %> >	
							
					<% } else { %>
						<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>" href="javascript:void(0);" style="color: <%=CBCDisplay.getHTMLFgColor(capBank)%>;" >
					<% } %>
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_COLUMN) %>
					</a>
					</td>

					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_BANK_ADDRESS_COLUMN)%></td>
					<td><a type="param1" name="cti_dyn" id="<%=capBank.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_TIME_STAMP_COLUMN)%></a>
					</td>
					
					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_BANK_SIZE_COLUMN)%></td>


					<td><a type="param2" name="cti_dyn" id="<%=capBank.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_OP_COUNT_COLUMN)%></a>
					</td>
				</tr>
				<% } %>
				
			</table>
		</div>
		<input type="hidden" id="lastUpdate" value="">
    </form>
<script type="text/javascript">
Event.observe(window, 'load', function() { 
								initFilter($('parent_fdr_td'), $('capBankTable'), 1);			
								new CtiNonScrollTable('capBankTable','capBankHeaderTable');
});
</script>
            
            </cti:titledContainer>

	<div id="cmd_msg_div" style="display: none;" />

</cti:standardPage>