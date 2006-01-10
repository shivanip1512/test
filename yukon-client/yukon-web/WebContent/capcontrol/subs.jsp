<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Substations" module="capcontrol">


<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
    class="com.cannontech.cbc.web.CapControlCache"
    type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
    SubBus[] areaSubs =
        capControlCache.getSubsByArea( cbcSession.getLastArea() );

    boolean hasControl = CBCWebUtils.hasControlRights(session);
%>

  <cti:includeCss link="base.css"/>
  <cti:standardMenu/>
  <cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="SubBus Areas" />
    <cti:crumbLink url="subs.jsp" title="Substations" />
  </cti:breadCrumbs>
  
  <script type="text/javascript">
    Event.observe(window, 'load', callBack);
  </script>

      <cti:titledContainer title="<%="Substation Buses In Area:  " + cbcSession.getLastArea()%>">
          
          <div class="scrollLarge">
            <form id="subForm" action="feeders.jsp" method="post">
            <input type="hidden" name="<%=CBCSessionInfo.STR_SUBID%>" />
          
            <table id="subTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnHeader lAlign">              
                <th>
                <input type="checkbox" id="chkAllBx" onclick="checkAll(this, 'cti_chkbxSubs');"/>
                Sub Name</th>
                <th>State</th>
                <th>Target</th>
                <th>VAR Load / Est.</th>
                <th>Date/Time</th>
                <th>PFactor / Est.</th>
                <th>Watts / Volts</th>
                <th>Daily / Max Ops</th>
              </tr>

<%
String css = "tableCell";
for( int i = 0; i < areaSubs.length; i++ )
{
    css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
    SubBus subBus = areaSubs[i];
%>
	        <tr class="<%=css%>">
				<td>
				<input type="checkbox" name="cti_chkbxSubs" value="<%=subBus.getCcId()%>" />
				<a href="#" class="<%=css%>" onclick="postMany('subForm', '<%=CBCSessionInfo.STR_SUBID%>', <%=subBus.getCcId()%>)">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN) %>
				</a>
				
				<% if( subBus.getVerificationFlag().booleanValue() ) { %>
					<span class="popupImg"
						onmouseover="statusMsg(this, 'This SubBus is currently being<br>used in a Verification schedule');" >
					(v)</span>
				<% } %>
				</td>
				
				<td>
			<% if( hasControl && !CtiUtilities.STRING_NONE.equals(subBus.getControlUnits()) ) { %>
				<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>"
					style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;"
					href="javascript:void(0);"
				    onmouseover="overlib(
						createIFrame('subCmd.jsp?subId=<%=subBus.getCcId()%>', 135, 90, 'tempIFrame', 0),
						STICKY, WIDTH,135, HEIGHT,90, OFFSETX,-15,OFFSETY,-15,
						MOUSEOFF, FULLHTML);"
				    onmouseout="nd();">

			<% } else { %>
				<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;" >
			<% } %>
			<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
			</a>
				</td>

                <td><a type="param1" name="cti_dyn" id="<%=subBus.getCcId()%>">
                <%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_COLUMN)%></a>
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
            </form>
        </div>

      </cti:titledContainer>
      
</cti:standardPage>