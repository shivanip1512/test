<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="com.cannontech.common.cache.PointChangeCache" %>
<%@ page import="com.cannontech.database.cache.functions.PointFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.StateFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.TagFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.database.data.lite.LitePoint" %>
<%@ page import="com.cannontech.database.data.lite.LiteState" %>
<%@ page import="com.cannontech.database.data.lite.LiteTag" %>

<%@ page import="com.cannontech.tags.TagManager" %>
<%@ page import="com.cannontech.tags.Tag" %>

<%@ page import="com.cannontech.esub.util.UpdateUtil" %>

<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.SimpleDateFormat" %>
<jsp:useBean id="YUKON_USER" scope="session" class="com.cannontech.database.data.lite.LiteYukonUser"/>
<%
	
	String pointIDStr = request.getParameter("pointid");
	String instanceIDStr = request.getParameter("instanceid");
	String stateStr = request.getParameter("state");
	String descriptionStr = request.getParameter("description");
	String msg = request.getParameter("msg");
	String actionStr = request.getParameter("action");

	if(pointIDStr == null) {
		throw new IllegalArgumentException("pointid is a required parameter");
	}
	
	int pointID = Integer.parseInt(pointIDStr);
	int instanceID = -1;
	int state = -1;
	boolean controlConfirm = false;
	
	if(instanceIDStr != null) {
		instanceID = Integer.parseInt(instanceIDStr);
	}
	
	if(stateStr != null) {
		state = Integer.parseInt(stateStr);
	}

	LitePoint lPoint = PointFuncs.getLitePoint(pointID);
	LiteYukonPAObject lDevice = PAOFuncs.getLiteYukonPAO(lPoint.getPaobjectID());	
	LiteState[] ls = StateFuncs.getLiteStates(lPoint.getStateGroupID());
	LiteState lState = PointChangeCache.getPointChangeCache().getCurrentState(pointID);	
	String controlDenyMsg = null;
	
	if(!UpdateUtil.isControllable(pointID)) {
		controlDenyMsg = "Control is inhibited or unavailable on this point";
	}
	
	Set tagSet = TagManager.getInstance().getTags(pointID);
	Iterator tagIter = tagSet.iterator();
	while(tagIter.hasNext()) {
		Tag t = (Tag) tagIter.next();
		LiteTag lt = TagFuncs.getLiteTag(t.getTagID());
		if(lt.isInhibit()) {
			controlDenyMsg = "Control is inhibited by due to tags";
		}
	}
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	if(actionStr.equalsIgnoreCase("REMOVETAG")) {
		//Remove a tag, make sure it exists, then 		
		try {
			Tag tagToRemove = TagManager.getInstance().getTag(pointID, instanceID);
			TagManager.getInstance().removeTag(tagToRemove, YUKON_USER.getUsername());
		} catch(Exception e) {
			String errMsg = java.net.URLEncoder.encode(e.getMessage());
			response.sendRedirect("control.jsp?pointid=" + pointID + "&msg=" + errMsg + "&action=DISPLAY");
			return;
		}

		response.sendRedirect("control.jsp?pointid=" + pointID + "&action=DISPLAY");
		//Give dispatch a chance, see if this is long enough
		try { Thread.sleep(500); } catch(Exception e) { }
		return;
	} 
	else
	if(actionStr.equalsIgnoreCase("UPDATETAG")) {
		//Update a tag, make sure it exists
		try {
			Tag tagToUpdate = TagManager.getInstance().getTag(pointID, instanceID);
			tagToUpdate.setDescriptionStr(descriptionStr);
			TagManager.getInstance().updateTag(tagToUpdate, YUKON_USER.getUsername());
		
		} catch(Exception e) {
			String errMsg = java.net.URLEncoder.encode(e.getMessage());
			response.sendRedirect("control.jsp?pointid=" + pointID + "&msg=" + errMsg + "&action=DISPLAY");
			return;
		}
		response.sendRedirect("control.jsp?pointid=" + pointID + "&action=DISPLAY");
		return;
	} 
	else
	if(actionStr.equalsIgnoreCase("CONTROLCONFIRM")) {
		controlConfirm = true;
		System.out.println("confirming control, pointid=" + pointID + " state=" + state);
	}
	else
	if(actionStr.equalsIgnoreCase("CONTROLEXECUTE")) {
		System.out.println("execute control, pointid=" + pointID + " state=" + state);
		response.sendRedirect("control.jsp?pointid=" + pointID + "&action=CLOSE");
		return;
	}
	else 
	if(actionStr.equalsIgnoreCase("CLOSE")) {
		out.println("<script type=\"text/javascript\">");
		out.println("Javascript:window.close();");
		out.println("</script>");
	}
	
	if(msg != null) {
		out.println("<script type=\"text/javascript\">");
		out.println("alert(\"" + msg + "\");");
		out.println("</script>");
	}
	
%>
<html>
<head>
  <meta content="text/html; charset=ISO-8859-1"
 http-equiv="content-type">
  <link rel="stylesheet" href="CannonStyle.css" type="text/css">
  <title><% if(!controlConfirm) { %>Control<% } else { %>Confirm Control<% } %></title>
  <script langauge = "Javascript" src= "control.js"></script>
  <script type="text/javascript">


function newTag(id) {
location="new_tag.jsp?pointid=" + id;
}

function confirmControl(id, state) {
location="control.jsp?pointid=" + id + "&state=" + state + "&action=CONTROLCONFIRM";
}

function executeControl(id, state) {
submitControl(id,state); // in control.js
}

function cancelControl() {
location="control.jsp?pointid=<%= pointID %>&action=DISPLAY";
}

function tagHistory(id) {
location="tag_history.jsp?pointid=" + id;
}

function updateTag(instanceID, description) {
location="control.jsp?pointid=<%= pointID %>&instanceid=" + instanceID + "&description=" + description + "&action=UPDATETAG";
}

function removeTag(instanceID) {
location="control.jsp?pointid=<%= pointID %>&instanceid=" + instanceID + "&action=REMOVETAG";
}


  </script>
</head>
<body class="esubTableCell">
<div align="center">
<table
 style="background-color: rgb(255, 255, 255); width: 50%; text-align: left; margin-left: auto; margin-right: auto;"
 cellpadding="2" cellspacing="0" border="1">
  <tbody>
    <tr align="center">
      <td style="vertical-align: top;" class="esubHeaderCell">
      <div class="TitleHeader"><% if(!controlConfirm) { %>Control<% } else { %>Confirm Control<% } %></div>
      </td>
    </tr>
    <tr>
      <td
 style="text-align: center; vertical-align: middle; background-color: rgb(255, 255, 255);">
      <div style="text-align: center;" class="TableCell">Device:&nbsp;
<%= lDevice.getPaoName() %><br>
      </div>
      <div class="TableCell" style="text-align: center;">Point:&nbsp;
<%= lPoint.getPointName() %><br>
      </div>
      <div class="TableCell" style="text-align: center;">Current State:&nbsp;
<%= lState.getStateText() %><br>
      </div>
      <br>
<%
    if(controlDenyMsg == null) {
%>  
      <div align="center">
<%
	if(!controlConfirm) {
%>	     
     <input type="button" name="controlstate1" value="<%= ls[0].getStateText() %>" onclick="confirmControl(<%= pointID %>,<%= ls[0].getStateRawState() %>)">&nbsp;&nbsp;
     <input type="button" name="controlstate2" value="<%= ls[1].getStateText() %>" onclick="confirmControl(<%= pointID %>,<%= ls[1].getStateRawState() %>)">
<%
    } else {
%>  
	<input type="button" name="executecontrolbutton" value="Execute" onclick="executeControl(<%= pointID %>,<%= state %>)">&nbsp&nbsp
	<input type="button" name="cancelcontrolbutton" value="Cancel" onclick="cancelControl()">
<%  }
%>
      </div>
       <% } else { %>
      <div class="TableCell" style="text-align: center;"><%= controlDenyMsg %><br>
      </div>
<%
	} //end if
%>
     </td>
    </tr>
  </tbody>
</table>
</div>
<br>
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
            <div class="TitleHeader">CURRENT
TAGS</div>
            </td>
            <td
 style="width: 33%; text-align: right; background-color: rgb(255, 255, 255); vertical-align: middle;"><input
 onclick="newTag(<%= pointID %>)" name="newtagbutton" type="button" value="New Tag">&nbsp;<input
 onclick="tagHistory(<%= pointID %>)" name="taghistorybutton" type="button"
 value="Tag History"><br>
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
 class="esubHeaderCell">Tagged By<br>
            </td>
            <td style="vertical-align: top; text-align: center;"
 class="esubHeaderCell">Description<br>
            </td>
            <td style="vertical-align: top;" class="esubHeaderCell"><br>
            </td>
          </tr>
          
<%  // Tag loop
	tagIter = tagSet.iterator();
	while(tagIter.hasNext()) {
		Tag tag = (Tag) tagIter.next();
		LiteTag lt = TagFuncs.getLiteTag(tag.getTagID());
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
            <td class="esubTableCell"
 style="vertical-align: top; text-align: center;"><%= tag.getUsername() %><br>
            </td>
            <td class="esubTableCell" style="vertical-align: top;"><textarea
 rows="3" cols="50" name="desc<%= tag.getInstanceID() %>"><%= tag.getDescriptionStr() %></textarea><br>
            </td>
            <td class="esubTableCell"
 style="text-align: center; vertical-align: middle;"><input
 name="updatetagbutton" value="Update" type="button"
 onclick="updateTag(<%= tag.getInstanceID() %>, desc<%= tag.getInstanceID() %>.value)"><br>
            <br>
            <input name="removetagbutton" value="Remove" type="button"
 onclick="removeTag(<%= tag.getInstanceID() %>)"> </td> 
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
</body>
</html>
