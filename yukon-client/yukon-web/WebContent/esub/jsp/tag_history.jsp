<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="com.cannontech.database.cache.functions.TagFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.PointFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteTag" %>
<%@ page import="com.cannontech.database.data.lite.LitePoint" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.tags.TagManager" %>
<%@ page import="com.cannontech.database.db.point.TAGLog" %>

<%
	String pointIDStr = request.getParameter("pointid");
	
	if(pointIDStr == null) {
		throw new IllegalArgumentException("pointid is a required parameter");
	}
	
	int pointID = Integer.parseInt(pointIDStr);
	
	LitePoint lPoint = PointFuncs.getLitePoint(pointID);
	LiteYukonPAObject lDevice = PAOFuncs.getLiteYukonPAO(lPoint.getPaobjectID());	
	
	List tagLogList = TagManager.getInstance().getTagLog(pointID);
	java.util.Collections.reverse(tagLogList);
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
%>

<html>
<head>
<meta content="text/html; charset=ISO-8859-1"
 http-equiv="content-type">
<link rel="stylesheet" href="CannonStyle.css" type="text/css">
<title>Tag History</title>
<script langauge = "Javascript" src= "point.js"></script>
<script type="text/javascript">

function goBack(id) {
location="control.jsp?pointid=" + id + "&action=display";
}

</script>
</head>
<body class="esubTableCell">
<div align="center">

<table style="text-align: left; width: 100%;" border="1" cellspacing="0"
 cellpadding="0">
  <tbody>
    <tr align="center">
      <td style="vertical-align: top;">
      <table
 style="width: 100%; text-align: left; margin-left: auto; margin-right: auto;"
 border="0" cellspacing="0" cellpadding="5">
        <tbody>
          <tr>
            <td
 style="vertical-align: middle; width: 33%; background-color: rgb(255, 255, 255);"><br>
            </td>
            <td
 style="width: 33%; text-align: center; vertical-align: middle; background-color: rgb(255, 255, 255);">
            <div class="TitleHeader">TAG HISTORY<br>Device: <%= lDevice.getPaoName() %><br>Point: <%= lPoint.getPointName() %></div>
            </td>
            <td
 style="width: 33%; text-align: right; background-color: rgb(255, 255, 255); vertical-align: middle;"><input
 onclick="goBack(<%= pointID %>)" name="backbutton" type="button"
 value="Back"><br>
            </td>
          </tr>
        </tbody>
      </table>
      </td>
    </tr>
    <tr>
      <td style="vertical-align: top;">
      <div style="text-align: center;"> </div>
      <table cellpadding="2" cellspacing="0" border="1"
 style="text-align: left; background-color: rgb(255, 255, 255); width: 100%;">
        <tbody>
          <tr>
            <td style="vertical-align: top; text-align: center;"
 class="esubHeaderCell">Tag #<br>
            </td>
            <td style="vertical-align: top; text-align: center;"
 class="esubHeaderCell">Tag Type<br>
            </td>
            <td style="vertical-align: top; text-align: center;"
 class="esubHeaderCell">Timestamp<br>
 			</td>
			 <td style="vertical-align: top; text-align: center;"
 class="esubHeaderCell">Additional Info<br>
            </td>
            <td style="vertical-align: top; text-align: center;"
 class="esubHeaderCell">Tagged By<br>
            </td>
            <td style="vertical-align: top; text-align: center;"
 class="esubHeaderCell">Description<br>
            </td>      
          </tr>
          
<%  // Tag loop
	Iterator tagLogIter = tagLogList.iterator();
	while(tagLogIter.hasNext()) {
		TAGLog tag = (TAGLog) tagLogIter.next();
		LiteTag lt = TagFuncs.getLiteTag(tag.getTagID().intValue());
%>	      
      
          <tr>
            <td class="esubTableCell"
 style="vertical-align: top; text-align: center;"><%= tag.getInstanceID() %><br>
            </td>
            
            <td class="esubTableCell"
 style="vertical-align: top; text-align: center;"><%= lt.getTagName() %><br>
            </td>
            
            <td class="esubTableCell" style="vertical-align: top;">
            <p align="center" class="esubTableCell"><%= dateFormat.format(tag.getTagTime()) %></p>
            </td>
            
             <td class="esubTableCell" style="vertical-align: top;">
            <p align="center" class="esubTableCell"><%= tag.getAction() %></p>
            </td>
            
            <td class="esubTableCell"
 style="vertical-align: top; text-align: center;"><%= tag.getUserName() %><br>
            </td>
            <td class="esubTableCell" style="vertical-align: top;"><textarea
 rows="3" cols="50" name="desc<%= tag.getInstanceID() %>"><%= tag.getDescription() %></textarea><br>
            </td>            
          </tr>
          
<%
	} // end while	
%>	                  
        </tbody>
      </table>
      </td>
    </tr>
  </tbody>
</table>
</html>