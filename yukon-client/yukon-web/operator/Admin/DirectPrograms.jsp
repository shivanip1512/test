<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirect" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%
    LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
    LoadcontrolCache cache = cs.getCache();

    // list of all available programs, contains LMProgramDirect objects
    ArrayList availPrograms = new ArrayList();
    // list of all published programs, contains LMProgramDirect objects
    ArrayList pubPrograms = new ArrayList();
    // list of all unpublished programs, contains LMProgramDirect objects
    ArrayList unpubPrograms = new ArrayList();

	// List of published program IDs
	ArrayList pubProgIDs = new ArrayList();
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory appCat = categories.getStarsApplianceCategory(i);
		for (int j = 0; j < appCat.getStarsEnrLMProgramCount(); j++)
			pubProgIDs.add( new Integer(appCat.getStarsEnrLMProgram(j).getProgramID()) );
	}

    long[] programIDs = com.cannontech.database.db.web.LMDirectOperatorList.getProgramIDs( user.getUserID() );       
    java.util.Arrays.sort(programIDs);
    LMProgramDirect[] allPrograms = cache.getDirectPrograms(); 

    // Match our program ids with the actual programs in the cache so we know what to display
    for( int i = 0; i < allPrograms.length; i++ )
    {
        long id = allPrograms[i].getYukonID().longValue();
        if( java.util.Arrays.binarySearch(programIDs, id ) >= 0 )
        {
            // found one
			if (pubProgIDs.contains(allPrograms[i].getYukonID()))
            	pubPrograms.add(allPrograms[i]);
			else
				unpubPrograms.add(allPrograms[i]);
        }
		else
		{
			availPrograms.add(allPrograms[i]);
		}
    }
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function isPublished(progID) {
<%
	if (pubProgIDs.size() > 0) {
%>
	return (progID == <%= pubProgIDs.get(0) %>)
<%
		for (int i = 1; i < pubProgIDs.size(); i++) {
%>
		|| (progID == <%= pubProgIDs.get(i) %>)
<%
		}
%>
		;
<%
	}
	else {
%>
	return false;
<%
	}
%>
}

function addProgram(form) {
	var assgnProgList = document.getElementById("ProgramsAssigned");
	var availProgList = document.getElementById("ProgramsAvailable");
	if (availProgList.value > 0) {
		var oOption = availProgList.options[availProgList.selectedIndex];
		availProgList.remove(availProgList.selectedIndex);
		if (assgnProgList.options[0].value == 0) assgnProgList.remove(0);
		assgnProgList.add(oOption);
	}
	if (availProgList.options.length == 0) {
		var emptyOption = document.createElement("OPTION");
		availProgList.add(emptyOption);
		emptyOption.innerText = "<No Program Available>";
		emptyOption.value = "0";
	}
}

function removeProgram(form) {
	var assgnProgList = document.getElementById("ProgramsAssigned");
	var availProgList = document.getElementById("ProgramsAvailable");
	if (assgnProgList.value > 0) {
		if (isPublished(assgnProgList.value)) {
			alert("Cannot remove a published program! To remove this program, you must edit the 'Appliance Category and Published Programs' first.");
			return;
		}
		
		var oOption = assgnProgList.options[assgnProgList.selectedIndex];
		assgnProgList.remove(assgnProgList.selectedIndex);
		if (availProgList.options[0].value == 0) availProgList.remove(0);
		availProgList.add(oOption);
	}
	if (assgnProgList.options.length == 0) {
		var emptyOption = document.createElement("OPTION");
		assgnProgList.add(emptyOption);
		emptyOption.innerText = "<No Program Assigned>";
		emptyOption.value = "0";
	}
}

function init() {
	addProgram(document.form1);
	removeProgram(document.form1);
}

function prepareSubmit(form) {
	var assgnProgList = document.getElementById("ProgramsAssigned");
	if (assgnProgList.options[0].value > 0) {
		for (i = 0; i < assgnProgList.options.length; i++) {
			var programID = assgnProgList.options[i].value;
			var html = '<input type="hidden" name="ProgIDs" value="' + programID + '">';
			form.insertAdjacentHTML("beforeEnd", html);
		}
	}
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - DIRECT PROGRAMS</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="prepareSubmit(this)">
              <input type="hidden" name="action" value="UpdateDirectPrograms">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Direct Programs</td>
                </tr>
                <tr> 
                  <td> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <tr> 
                        <td width="85%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                  <tr> 
                                    <td width="45%" valign="top"> Available Programs<br>
                                      <select id="ProgramsAvailable" name="ProgramsAvailable" size="5" style="width:235">
<%
	for (int i = 0; i < availPrograms.size(); i++) {
		LMProgramDirect program = (LMProgramDirect) availPrograms.get(i);
%>
                                        <option value="<%= program.getYukonID() %>"><%= program.getYukonName() %></option>
<%
	}
%>
                                      </select>
                                    </td>
                                    <td width="10%"> 
                                      <input type="button" id="AddButton" name="Remove" value=" >> " onClick="addProgram(this.form)">
                                      <br>
                                      <input type="button" id="RemoveButton" name="Add" value=" << " onclick="removeProgram(this.form)">
                                    </td>
                                    <td width="45%" valign="top"> Assigned Programs:<br>
                                      <select id="ProgramsAssigned" name="ProgramsAssigned" size="5" style="width:235">
<%
	for (int i = 0; i < pubPrograms.size(); i++) {
		LMProgramDirect program = (LMProgramDirect) pubPrograms.get(i);
%>
                                        <option value="<%= program.getYukonID() %>" style="color:#999999"><%= program.getYukonName() %></option>
<%
	}
	for (int i = 0; i < unpubPrograms.size(); i++) {
		LMProgramDirect program = (LMProgramDirect) unpubPrograms.get(i);
%>
                                        <option value="<%= program.getYukonID() %>"><%= program.getYukonName() %></option>
<%
	}
%>
                                      </select>
                                      <br>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="205"> 
                    <input type="button" name="Reset" value="Reset" onclick="location.reload()">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Done" value="Done" onclick="location.href='AdminTest.jsp'">
                  </td>
                </tr>
              </table>
            </form>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
