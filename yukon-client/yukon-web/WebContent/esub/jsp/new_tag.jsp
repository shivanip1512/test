<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>
<%@ page import="com.cannontech.database.cache.functions.DeviceFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.PointFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.database.data.lite.LitePoint" %>
<%@ page import="com.cannontech.database.data.lite.LiteTag" %>
<%@ page import="com.cannontech.tags.TagManager" %>
<%@ page import="com.cannontech.tags.Tag" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<jsp:useBean id="YUKON_USER" scope="session" class="com.cannontech.database.data.lite.LiteYukonUser"/>
<%
	int pointID = Integer.parseInt(request.getParameter("pointid"));
	
	//The next parameters are only used when adding a tag
	String tagIDStr = request.getParameter("tagid");
	String descriptionStr = request.getParameter("description");
	String actionStr = request.getParameter("action");
	
	LitePoint litePoint = PointFuncs.getLitePoint(pointID);
	LiteYukonPAObject liteDevice = DeviceFuncs.getLiteDevice(litePoint.getPaobjectID());
	
	if(actionStr != null && actionStr.equalsIgnoreCase("SUBMITTAG")) {
		TagManager.getInstance().createTag(pointID, Integer.parseInt(tagIDStr), YUKON_USER.getUsername(), descriptionStr, "-", "-");
		response.sendRedirect("control.jsp?pointid=" + pointID + "&action=DISPLAY");
		//Give dispatch a chance, see if this is long enough
		try { Thread.sleep(500); } catch(Exception e) { }
		return;
	}	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <meta http-equiv="content-type"
 content="text/html; charset=ISO-8859-1">
  <link type="text/css" href="CannonStyle.css" rel="stylesheet">
  <title>New Tag</title>

<script type="text/javascript">

function cancelTag() {
location="control.jsp?pointid=<%= pointID %>&action=DISPLAY";
}

function submitTag(tagid, description) {
location="new_tag.jsp?pointid=<%= pointID %>&tagid=" + tagid + "&description=" + description + "&action=SUBMITTAG";
}

</script>
</head>
<body class="esubTableCell">
<div align="center">
<div style="text-align: center;"> </div>
<table border="1" cellpadding="2" cellspacing="0"
 style="background-color: rgb(255, 255, 255); text-align: left; margin-left: auto; margin-right: auto;">
  <tbody>
    <tr>
      <td class="esubHeaderCell" style="text-align: center;">
      <div class="TitleHeader">New
Tag</div>
      </td>
    </tr>
    <tr>
      <td style="vertical-align: top;">
      <table style="width: 100%;">
        <tbody>
          <tr>
            <td>
            <table style="text-align: left; width: 100%;" border="0"
 cellspacing="2" cellpadding="2">
              <tbody>
                <tr>
                  <td class="TableCell" style="vertical-align: top;">Device:<br>
                  </td>
                  <td class="TableCell" style="vertical-align: top;"><%= liteDevice.getPaoName() %><br>
                  </td>
                </tr>
                <tr>
                  <td class="TableCell" style="vertical-align: top;">Point:<br>
                  </td>
                  <td class="TableCell" style="vertical-align: top;"><%= litePoint.getPointName() %><br>
                  </td>
                </tr>
                <tr>
                  <td class="TableCell" style="vertical-align: top;">Tag
Type:<br>
                  </td>
                  <td class="TableCell" style="vertical-align: top;">
                  <select name="tagtypeselect">
<%
	Iterator tagIter = DefaultDatabaseCache.getInstance().getAllTags().iterator();
	while(tagIter.hasNext()) {
		LiteTag lt = (LiteTag) tagIter.next();
%>                  
                  <option value="<%= lt.getTagID() %>"><%= lt.getTagName() %></option>
<%
	}
%>                  
                  </select>
                  </td>
                </tr>
                <tr>
                  <td class="TableCell" style="vertical-align: top;">Description:<br>
                  </td>
                  <td class="TableCell" style="vertical-align: top;"><textarea
 cols="50" rows="3" name="descriptiontextarea"></textarea> </td>
                </tr>
              </tbody>
            </table>
            </td>
          </tr>
          <tr>
            <td style="vertical-align: top; text-align: center;"><input
 type="button" name="submitbutton" value="Submit" onclick="submitTag(tagtypeselect.value, descriptiontextarea.value)">&nbsp;<input
 type="button" name="cancelbutton" value="Cancel" onclick="cancelTag()"> </td>
          </tr>
        </tbody>
      </table>
      </td>
    </tr>
  </tbody>
</table>
</div>
</body>
</html>
