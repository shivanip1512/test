<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

</head>

<body class="Background" leftmargin="0" topmargin="0">
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
          <td  valign="top" width="101">
		  <% String pageName = "Residence.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "ACCOUNT - CUSTOMER RESIDENCE"; %><%@ include file="include/InfoSearchBar.jsp" %>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %></div>
			
			<form method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			<input type="hidden" name="action" value="UpdateResidenceInfo">
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Residence Type:</div>
                        </td>
                        <td width="184" valign="top"> 
                          <select name="ResidenceType">
<%
	StarsCustSelectionList resTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE );
	for (int i = 0; i < resTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = resTypeList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getResidenceType().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Construction Material: </div>
                        </td>
                        <td width="184"> 
                          <select name="ConstMaterial">
<%
	StarsCustSelectionList constMtrlList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL );
	for (int i = 0; i < constMtrlList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = constMtrlList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getConstructionMaterial().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Decade Built:</div>
                        </td>
                        <td width="184"> 
                          <select name="DecadeBuilt">
<%
	StarsCustSelectionList decadeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT );
	for (int i = 0; i < decadeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = decadeList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getDecadeBuilt().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Square Feet:</div>
                        </td>
                        <td width="184"> 
                          <select name="SquareFeet">
<%
	StarsCustSelectionList sqrFeetList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET );
	for (int i = 0; i < sqrFeetList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = sqrFeetList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getSquareFeet().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Insulation Depth:</div>
                        </td>
                        <td width="184"> 
                          <select name="InsulationDepth">
<%
	StarsCustSelectionList inslDepthList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH );
	for (int i = 0; i < inslDepthList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = inslDepthList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getInsulationDepth().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">General Condition:</div>
                        </td>
                        <td width="184"> 
                          <select name="GeneralCondition">
<%
	StarsCustSelectionList conditionList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION );
	for (int i = 0; i < conditionList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = conditionList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getGeneralCondition().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                    </table>
                    </td>
                  <td width="300" valign="top" bgcolor="#FFFFFF">
<hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Main Cooling System:</div>
                        </td>
                        <td width="184"> 
                          <select name="CoolingSystem">
<%
	StarsCustSelectionList coolSysList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM );
	for (int i = 0; i < coolSysList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = coolSysList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getMainCoolingSystem().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Main Heating System:</div>
                        </td>
                        <td width="184"> 
                          <select name="HeatingSystem">
<%
	StarsCustSelectionList heatSysList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM );
	for (int i = 0; i < heatSysList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = heatSysList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getMainHeatingSystem().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right"># of Occupants:</div>
                        </td>
                        <td width="184"> 
                          <select name="OccupantNum">
<%
	StarsCustSelectionList occpNumList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS );
	for (int i = 0; i < occpNumList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = occpNumList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getNumberOfOccupants().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Ownership Type:</div>
                        </td>
                        <td width="184"> 
                          <select name="OwnershipType">
<%
	StarsCustSelectionList ownTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE );
	for (int i = 0; i < ownTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = ownTypeList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getOwnershipType().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Main Fuel Type:</div>
                        </td>
                        <td width="184"> 
                          <select name="FuelType">
<%
	StarsCustSelectionList fuelTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE );
	for (int i = 0; i < fuelTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = fuelTypeList.getStarsSelectionListEntry(i);
		String selectStr = (residence != null && residence.getMainFuelType().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
							<option value="<%= entry.getEntryID() %>" <%= selectStr %>><%= entry.getContent() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell" valign="top"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="184"> 
                          <textarea name="Notes" rows="6" wrap="soft" cols="26" class = "TableCell"><%
						  	if (residence != null) out.write(residence.getNotes().replaceAll("<br>", System.getProperty("line.separator")));
						  %></textarea>
                        </td>
                      </tr>
                    </table>
                    </td>
              </tr>
            </table>
            <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
              <tr> 
                  <td width="186"> 
                    <div align="right"> 
                      <input type="submit" name="Save2" value="Save">
                    </div>
                  </td>
                  <td width="194"> 
                    <div align="left"> 
                      <input type="reset" name="Reset" value="Reset">
                    </div>
                  </td>
              </tr>
            </table>
			</form>
            <p align="center">&nbsp;</p>
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
