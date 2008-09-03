<%@ page import="com.cannontech.util.ParamUtil" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.cannontech.loadcontrol.LCUtils" %>
<%@ include file="lm_header.jsp" %>

<script type="text/javascript">
Event.observe (window, 'load', function  () { setAdjustments();});
</script>
<%

String gName = ParamUtil.getString(request,"gearName", "");
String gearIdx = ParamUtil.getString (request, "idx", "");
Integer gearPeriod = ParamUtil.getInteger(request, "prd");

Calendar c = GregorianCalendar.getInstance();
c.setTime(new Date());
String start = ParamUtil.getString(request, "start", c.toString());
String stop = ParamUtil.getString(request, "stop", c.toString());
c.setTimeInMillis(( Date.parse(start)));

Date sp = new Date(Date.parse (stop));
Date st = new Date (Date.parse(start));
int timeSlots = LCUtils.getTimeSlotsForTargetCycle(sp, st, gearPeriod);

%>
<html>
    <title> Gear Configuration</title>
    <body>
        <h4 align="center" style="color: blue"> <%=gName%></h4>
        <input type="hidden" id="prg_idx" value="<%=gearIdx%>"/>
        
        <div style="overflow: scroll">
        <table id="tgctable" width="200" border="1" cellspacing="0" cellpadding="6" align="center" 
            valign="top" bgcolor="#FFFFFF">
        <tr> 
            <td style="font-weight: bold;font-size: 11px;background-color : gray">Time Frame</td>
            <td style="font-weight: bold;font-size: 11px;background-color : gray">Adjustment %</td>
        </tr>
        <%
        for(int i=0; i < timeSlots; i++) 
        {
        int hour = c.get(Calendar.HOUR_OF_DAY);
       %>
            <tr> 
                <td style="font-size: 11px"> <%=hour%>:00 - <%=hour + 1%>:00</td>
                <td> 
                    <input type="text" size="10" value="" name="tcg_input" onchange="b_submit.disabled = false"/>
                </td>
            </tr>
        <%
                   c.set(Calendar.HOUR_OF_DAY, hour + 1);
        }
        %>
        </table>
        </div>
        <br/><br/>
            <input id="b_submit" type="submit" name="submit" value="Submit" class="defButton" 
                    onclick = "check_and_submit();">
            <input type="submit" name="cancel" value="Cancel" class="defButton" 
                    onclick = "window.parent.closeConfirmWin();">
    </body>
</html>