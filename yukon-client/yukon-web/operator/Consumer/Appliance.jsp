<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	if (appliances.getStarsApplianceCount() == 0) {
		response.sendRedirect("CreateAppliances.jsp"); return;
	}
	
	int appNo = Integer.parseInt(request.getParameter("AppNo"));
	StarsAppliance appliance = appliances.getStarsAppliance(appNo);
	
	String kwCap = "";
	if (appliance.hasKWCapacity())
		kwCap = String.valueOf(appliance.getKWCapacity());
	
	String effRate = "";
	if (appliance.hasEfficiencyRating())
		effRate = String.valueOf(appliance.getEfficiencyRating());
	
	StarsInventory hardware = null;
	StarsLMProgram program = null;
	StarsApplianceCategory category = null;
	int progNo = 0;
	
	for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
		StarsInventory hw = inventories.getStarsInventory(i);
		if (hw.getInventoryID() == appliance.getInventoryID()) {
			hardware = hw;
			break;
		}
	}

	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
		StarsLMProgram starsProg = programs.getStarsLMProgram(i);
		if (starsProg.getProgramID() == appliance.getLmProgramID()) {
			program = starsProg;
			progNo = i;
			break;
		}
	}
	
	if (program != null) {
		for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
			if (categories.getStarsApplianceCategory(i).getApplianceCategoryID() == program.getApplianceCategoryID()) {
				category = categories.getStarsApplianceCategory(i);
				break;
			}
		}
	}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function deleteAppliance(form) {
<%
	if (program != null) {
%>
	if (!confirm('To delete the appliance, the program related with it will be removed as well, do you want to proceed?')) return;
<%
	}
	else {
%>
	if (!confirm('Are you sure you would like to delete this appliance?')) return;
<%
	}
%>
	form.action.value = 'DeleteAppliance';
	form.submit();
}
</script>
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
            <% String pageName = "Appliance.jsp?AppNo=" + appNo; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "APPLIANCES"; %> <%@ include file="include/InfoSearchBar.jsp" %>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			
              <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                <tr>
				<form name="form1" method="post" action="/servlet/SOAPClient"> 
				  <input type="hidden" name="action" value="UpdateAppliance">
				  <input type="hidden" name="AppID" value="<%= appliance.getApplianceID() %>">
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Description: </div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="Category" maxlength="40" size="24" value="<%= appliance.getDescription() %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell" height="5"> 
                            <div align="right">Model #:</div>
                          </td>
                          <td width="200" height="5"> 
                            <input type="text" name="ModelNo" maxlength="40" size="24" value="<%= appliance.getModelNumber() %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Location:</div>
                          </td>
                          <td width="200"> 
                            <select name="Location">
<%
	StarsCustSelectionList locationList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION );
	for (int i = 0; i < locationList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = locationList.getStarsSelectionListEntry(i);
		String selectedStr = (entry.getEntryID() == appliance.getLocation().getEntryID()) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Manufacturer:</div>
                          </td>
                          <td width="200"> 
                            <select name="Manufacturer">
                              <%
	StarsCustSelectionList manuList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER );
	for (int i = 0; i < manuList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = manuList.getStarsSelectionListEntry(i);
		String selectedStr = (entry.getEntryID() == appliance.getManufacturer().getEntryID()) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Year Manufactured:</div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="ManuYear" maxlength="14" size="14" value="<%= appliance.getYearManufactured() %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">KW Capacity:</div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="KWCapacity" maxlength="14" size="14" value="<%= kwCap %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Efficiency Rating:</div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="EffRating" maxlength="14" size="14" value="<%= effRate %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Notes:</div>
                          </td>
                          <td width="200"> 
                            <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= appliance.getNotes().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                          </td>
                        </tr>
                      </table>
<%
	if (appliance.getAirConditioner() != null) {
%>
                      <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Tonnage:</div>
                          </td>
                          <td width="200"> 
                            <select name="AC_Tonnage">
<%
		StarsCustSelectionList tonnageList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE );
		int tonnageID = (appliance.getAirConditioner().getTonnage() != null)? appliance.getAirConditioner().getTonnage().getEntryID() : 0;
		for (int i = 0; i < tonnageList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = tonnageList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == tonnageID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Type:</div>
                          </td>
                          <td width="200"> 
                            <select name="AC_Type">
<%
		StarsCustSelectionList typeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE );
		int typeID = (appliance.getAirConditioner().getACType() != null)? appliance.getAirConditioner().getACType().getEntryID() : 0;
		for (int i = 0; i < typeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = typeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == typeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (appliance.getWaterHeater() != null) {
		String numElmt = "";
		if (appliance.getWaterHeater().hasNumberOfElements())
			numElmt = String.valueOf(appliance.getWaterHeater().getNumberOfElements());
%>
					  <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right"># Gallons:</div>
                          </td>
                          <td width="200"> 
                            <select name="WH_GallonNum">
<%
		StarsCustSelectionList gallonNumList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS );
		int numGalID = (appliance.getWaterHeater().getNumberOfGallons() != null)? appliance.getWaterHeater().getNumberOfGallons().getEntryID() : 0;
		for (int i = 0; i < gallonNumList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = gallonNumList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == numGalID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Energy Source:</div>
                          </td>
                          <td width="200"> 
                            <select name="WH_EnergySrc">
<%
		StarsCustSelectionList energySrcList1 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE );
		int energySrcID = (appliance.getWaterHeater().getEnergySource() != null)? appliance.getWaterHeater().getEnergySource().getEntryID() : 0;
		for (int i = 0; i < energySrcList1.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = energySrcList1.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == energySrcID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right"># Heating Coils:</div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="WH_ElementNum" maxlength="14" size="14" value="<%= numElmt %>">
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (appliance.getDualFuel() != null) {
		String kwCap2 = "";
		if (appliance.getDualFuel().hasSecondaryKWCapacity())
			kwCap2 = String.valueOf(appliance.getDualFuel().getSecondaryKWCapacity());
%>
                      <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Switch-over Type:</div>
                          </td>
                          <td width="200"> 
                            <select name="DF_SwitchOverType">
<%
		StarsCustSelectionList switchOverTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE );
		int soTypeID = (appliance.getDualFuel().getSwitchOverType() != null)? appliance.getDualFuel().getSwitchOverType().getEntryID() : 0;
		for (int i = 0; i < switchOverTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = switchOverTypeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == soTypeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell" height="2"> 
                            <div align="right">Secondary KW Capacity:</div>
                          </td>
                          <td width="200" height="2"> 
                            <input type="text" name="DF_KWCapacity2" maxlength="14" size="14" value="<%= kwCap2 %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell" height="2"> 
                            <div align="right">Secondary Energy Source:</div>
                          </td>
                          <td width="200" height="2"> 
                            <select name="DF_SecondarySrc">
<%
		StarsCustSelectionList energySrcList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE );
		int energySrcID = (appliance.getDualFuel().getSecondaryEnergySource() != null)? appliance.getDualFuel().getSecondaryEnergySource().getEntryID() : 0;
		for (int i = 0; i < energySrcList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = energySrcList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == energySrcID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (appliance.getGenerator() != null) {
		String peakKW = "";
		if (appliance.getGenerator().hasPeakKWCapacity())
			peakKW = String.valueOf(appliance.getGenerator().getPeakKWCapacity());
		
		String fuelCap = "";
		if (appliance.getGenerator().hasFuelCapGallons())
			fuelCap = String.valueOf(appliance.getGenerator().getFuelCapGallons());
		
		String startDelay = "";
		if (appliance.getGenerator().hasStartDelaySeconds())
			startDelay = String.valueOf(appliance.getGenerator().getStartDelaySeconds());
%>
                      <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Transfer switch Type:</div>
                          </td>
                          <td width="200"> 
                            <select name="GEN_TranSwitchType">
<%
		StarsCustSelectionList tranSwitchTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE );
		int tsTypeID = (appliance.getGenerator().getTransferSwitchType() != null)? appliance.getGenerator().getTransferSwitchType().getEntryID() : 0;
		for (int i = 0; i < tranSwitchTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = tranSwitchTypeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == tsTypeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Transfer Switch Manufacturer:</div>
                          </td>
                          <td width="200"> 
                            <select name="GEN_TranSwitchMfg">
<%
		StarsCustSelectionList tranSwitchMfgList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG );
		int tsMfgID = (appliance.getGenerator().getTransferSwitchManufacturer() != null)? appliance.getGenerator().getTransferSwitchManufacturer().getEntryID() : 0;
		for (int i = 0; i < tranSwitchMfgList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = tranSwitchMfgList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == tsMfgID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Peak KW Capacity:</div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="GEN_KWCapacity" maxlength="14" size="14" value="<%= peakKW %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Fuel Capacity (gal):</div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="GEN_FuelCapGal" maxlength="14" size="14" value="<%= fuelCap %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell" height="2"> 
                            <div align="right">Start Delay (sec):</div>
                          </td>
                          <td width="200" height="2"> 
                            <input type="text" name="GEN_StartDelaySec" maxlength="14" size="14" value="<%= startDelay %>">
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (appliance.getGrainDryer() != null) {
%>
                      <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                        <tr> 
                          <td width="100" class="TableCell" height="17"> 
                            <div align="right">Dryer Type:</div>
                          </td>
                          <td width="200" height="17"> 
                            <select name="GD_DryerType">
<%
		StarsCustSelectionList dryerTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE );
		int typeID = (appliance.getGrainDryer().getDryerType() != null)? appliance.getGrainDryer().getDryerType().getEntryID() : 0;
		for (int i = 0; i < dryerTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = dryerTypeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == typeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Bin Size:</div>
                          </td>
                          <td width="200"> 
                            <select name="GD_BinSize">
<%
		StarsCustSelectionList binSizeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE );
		int binSizeID = (appliance.getGrainDryer().getBinSize() != null)? appliance.getGrainDryer().getBinSize().getEntryID() : 0;
		for (int i = 0; i < binSizeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = binSizeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == binSizeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Blower Energy Source:</div>
                          </td>
                          <td width="200"> 
                            <select name="GD_BlowerEenrgySrc">
<%
		StarsCustSelectionList energySrcList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE );
		int energySrcID = (appliance.getGrainDryer().getBlowerEnergySource() != null)? appliance.getGrainDryer().getBlowerEnergySource().getEntryID() : 0;
		for (int i = 0; i < energySrcList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = energySrcList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == energySrcID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Blower Horse Power:</div>
                          </td>
                          <td width="200">
                            <select name="GD_BlowerHorsePower">
<%
		StarsCustSelectionList horsePowerList1 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER );
		int horsePwrID = (appliance.getGrainDryer().getBlowerHorsePower() != null)? appliance.getGrainDryer().getBlowerHorsePower().getEntryID() : 0;
		for (int i = 0; i < horsePowerList1.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = horsePowerList1.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == horsePwrID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell" height="2"> 
                            <div align="right">Blower Heat Source:</div>
                          </td>
                          <td width="200" height="2">
                            <select name="GD_BlowerHeatSrc">
<%
		StarsCustSelectionList heatSrcList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE );
		int heatSrcID = (appliance.getGrainDryer().getBlowerHeatSource() != null)? appliance.getGrainDryer().getBlowerHeatSource().getEntryID() : 0;
		for (int i = 0; i < heatSrcList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = heatSrcList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == heatSrcID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (appliance.getStorageHeat() != null) {
		String peakKW = "";
		if (appliance.getStorageHeat().hasPeakKWCapacity())
			peakKW = String.valueOf(appliance.getStorageHeat().getPeakKWCapacity());
		
		String rechargeTime = "";
		if (appliance.getStorageHeat().hasHoursToRecharge())
			rechargeTime = String.valueOf(appliance.getStorageHeat().getHoursToRecharge());
%>
                      <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Storage Type:</div>
                          </td>
                          <td width="200"> 
                            <select name="SH_StorageType">
<%
		StarsCustSelectionList storageTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE );
		int typeID = (appliance.getStorageHeat().getStorageType() != null)? appliance.getStorageHeat().getStorageType().getEntryID() : 0;
		for (int i = 0; i < storageTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = storageTypeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == typeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Peak KW Capacity:</div>
                          </td>
                          <td width="200">
                            <input type="text" name="SH_KWCapacity" maxlength="14" size="14" value="<%= peakKW %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Recharge Time (hr):</div>
                          </td>
                          <td width="200">
                            <input type="text" name="SH_RechargeHour" maxlength="14" size="14" value="<%= rechargeTime %>">
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (appliance.getHeatPump() != null) {
		String restartDelay = "";
		if (appliance.getHeatPump().hasRestartDelaySeconds())
			restartDelay = String.valueOf(appliance.getHeatPump().getRestartDelaySeconds());
%>
                      <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Pump Type:</div>
                          </td>
                          <td width="200"> 
                            <select name="HP_PumpType">
                              <%
		StarsCustSelectionList pumpTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE );
		int typeID = (appliance.getHeatPump().getPumpType() != null)? appliance.getHeatPump().getPumpType().getEntryID() : 0;
		for (int i = 0; i < pumpTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = pumpTypeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == typeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
                              <%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Pump Size:</div>
                          </td>
                          <td width="200"> 
                            <select name="HP_PumpSize">
                              <%
		StarsCustSelectionList pumpSizeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE );
		int sizeID = (appliance.getHeatPump().getPumpSize() != null)? appliance.getHeatPump().getPumpSize().getEntryID() : 0;
		for (int i = 0; i < pumpSizeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = pumpSizeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == sizeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
                              <%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Stand-by Source:</div>
                          </td>
                          <td width="200"> 
                            <select name="HP_StandbySrc">
                              <%
		StarsCustSelectionList standbySrcList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE );
		int standbySrcID = (appliance.getHeatPump().getStandbySource() != null)? appliance.getHeatPump().getStandbySource().getEntryID() : 0;
		for (int i = 0; i < standbySrcList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = standbySrcList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == standbySrcID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
                              <%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Restart Delay (sec):</div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="HP_RestartDelaySec" maxlength="14" size="14" value="<%= restartDelay %>">
                          </td>
                        </tr>
                      </table>
<%
	}
	else if (appliance.getIrrigation() != null) {
%>
                      <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                        <tr> 
                          <td width="100" class="TableCell" height="17"> 
                            <div align="right">Irrigation Type:</div>
                          </td>
                          <td width="200" height="17"> 
                            <select name="IRR_IrrigationType">
<%
		StarsCustSelectionList irrTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE );
		int typeID = (appliance.getIrrigation().getIrrigationType() != null)? appliance.getIrrigation().getIrrigationType().getEntryID() : 0;
		for (int i = 0; i < irrTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = irrTypeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == typeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Horse Power:</div>
                          </td>
                          <td width="200"> 
                            <select name="IRR_HorsePower">
<%
		StarsCustSelectionList horsePowerList2 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER );
		int horsePwrID = (appliance.getIrrigation().getHorsePower() != null)? appliance.getIrrigation().getHorsePower().getEntryID() : 0;
		for (int i = 0; i < horsePowerList2.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = horsePowerList2.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == horsePwrID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Energy Source:</div>
                          </td>
                          <td width="200"> 
                            <select name="IRR_EnergySrc">
<%
		StarsCustSelectionList energySrcList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE );
		int energySrcID = (appliance.getIrrigation().getEnergySource() != null)? appliance.getIrrigation().getEnergySource().getEntryID() : 0;
		for (int i = 0; i < energySrcList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = energySrcList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == energySrcID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Soil Type:</div>
                          </td>
                          <td width="200"> 
                            <select name="IRR_SoilType">
<%
		StarsCustSelectionList soilTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE );
		int soilTypeID = (appliance.getIrrigation().getSoilType() != null)? appliance.getIrrigation().getSoilType().getEntryID() : 0;
		for (int i = 0; i < soilTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = soilTypeList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == soilTypeID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell" height="2"> 
                            <div align="right">Meter Location:</div>
                          </td>
                          <td width="200" height="2"> 
                            <select name="IRR_MeterLoc">
<%
		StarsCustSelectionList meterLocList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION );
		int meterLocID = (appliance.getIrrigation().getMeterLocation() != null)? appliance.getIrrigation().getMeterLocation().getEntryID() : 0;
		for (int i = 0; i < meterLocList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = meterLocList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == meterLocID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell" height="2"> 
                            <div align="right">Meter Voltage:</div>
                          </td>
                          <td width="200" height="2"> 
                            <select name="IRR_MeterVolt">
<%
		StarsCustSelectionList meterVoltList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE );
		int meterVoltID = (appliance.getIrrigation().getMeterVoltage() != null)? appliance.getIrrigation().getMeterVoltage().getEntryID() : 0;
		for (int i = 0; i < meterVoltList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = meterVoltList.getStarsSelectionListEntry(i);
			String selectedStr = (entry.getEntryID() == meterVoltID) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
%>
                            </select>
                          </td>
                        </tr>
                      </table>
<%
	}
%>
                    </td>
				  </form>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
<%
	if (program != null) {
%>
				    <form name="form7" method="POST" action="ContHist.jsp">
					  <input type="hidden" name="prog" value="<%= progNo %>">
                      <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td width="109" class="HeaderCell"> 
                            <div align="center">
                             Enrolled Programs
                            </div>
                          </td>
                          <td width="151" class="HeaderCell"> 
                            <div align="center"><cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_CONTROL %>" format="capital"/> 
                              History</div>
                          </td>
                        </tr>
                        <tr valign="top"> 
                          <td width="109" bgcolor="#FFFFFF"> 
                            <div align="center">
<% if (!category.getStarsWebConfig().getLogoLocation().equals("")) { %>
							  <img src="../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>"><br>
<% } %>
							  <span class="TableCell"><%= program.getProgramName() %></span>
							</div>
                          </td>
                          <td width="151" bgcolor="#FFFFFF"> 
                            <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
                              <tr> 
                                <td width="180" valign="top" align="center"> 
                                  <select name="Period">
                                    <option value="<%= StarsCtrlHistPeriod.PASTWEEK.toString() %>">Past Week</option>
                                    <option value="<%= StarsCtrlHistPeriod.PASTMONTH.toString() %>">Past Month </option>
                                    <option value="<%= StarsCtrlHistPeriod.ALL.toString() %>">All</option>
                                  </select>
                                  <br>
                                </td>
                              </tr>
                              <tr> 
                                <td width="180" valign="top" align="center"> 
                                  <input type="submit" name="View22" value="View">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td class="TableCell"> 
                            <div align="center">Controllable kW:&nbsp; 
                              <input type="text" name="textfield2233" maxlength="2" size="10">
                            </div>
                          </td>
                          </tr>
                      </table>
                     </form>
<%
	}
	else {
%>
					<p align="center" class="MainText">There is no program for this appliance</p>
<%
	}
%>
                    </td>
                </tr>
              </table>
              <table width="250" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr>
                  <td width="33%" align = "right"> 
                    <input type="button" name="Submit" value="Submit" onclick="document.form1.submit()">
                  </td>
                    <td width="33%" align = "center"> 
                      <div>
                        <input type="button" name="Cancel" value="Cancel" onclick="document.form1.reset()">
                      </div>
                    </td>
                    <td width="33%"> 
                      <div align="left"> 
                        <input type="button" name="Delete" value="Delete" onclick="deleteAppliance(document.form1)">
                      </div>
                    </td>
                </tr>
              </table>
              <hr>
              <div align="center" class="SubtitleHeader"><br>Hardware Summary</div>
<%
	if (hardware != null) {
%>
              <table width="360" border="1" cellspacing="0" cellpadding="3" align="center">
                <tr bgcolor="#CCCCCC"> 
                  <td width="75" class="HeaderCell">Category</td>
                  <td width="75" class="HeaderCell">Type</td>
                  <td width="75" class="HeaderCell">Serial #</td>
                </tr>
                <tr bgcolor="#FFFFFF"> 
                  <td width="75" class="TableCell"><%= hardware.getCategory() %></td>
                  <td width="75" class="TableCell"><%= hardware.getDeviceType().getContent() %></td>
                  <td width="75" class="TableCell"><%= hardware.getLMHardware().getManufacturerSerialNumber() %></td>
                </tr>
              </table>
<%
	}
	else {
%>
			  <p class="MainText">There is no hardware attached to this appliance</p>
<%
	}
%>
              <p>&nbsp;</p>
              </div>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
