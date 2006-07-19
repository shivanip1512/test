<%@ page import="com.cannontech.database.data.point.CBCPointTimestampParams" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Cap Bank Controller Points and TS" module="capcontrol_internal">
<%@include file="cbc_inc.jspf"%>


<%
	int cbcID = ParamUtil.getInteger(request, "cbcID");
	List pointList = DaoFactory.getDeviceDao().getCBCPointTimeStamps(new Integer (cbcID));
%>
<div >
      <div class="scrollHuge">
        <table id="innerTable" width="95%" border="0" cellspacing="0" cellpadding="0">
			<tr class="columnHeader lAlign">
				<td>Name</td>
				<td>Value</td>
				<td>Timestamp</td>
			</tr>

<%
String css = "tableCell";
for( int i = 0; i < pointList.size(); i++ )
{
	CBCPointTimestampParams pointTS = (CBCPointTimestampParams)pointList.get(i);		
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
%>

			<tr class="<%=css%>">
				<td>
				 <%=pointTS.getPointName() %>
				</td>
				<td>
				<%=pointTS.getValue()%>
				</td>
				<td>
				<%=pointTS.getTimestamp().toString()%>
				</td>
			</tr>

<%	} %>

        </table>
    </div>  
</div>

</cti:standardPage>
