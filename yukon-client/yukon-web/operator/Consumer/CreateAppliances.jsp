<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
var categoryIDs = [
<%
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++)
		out.print( categories.getStarsApplianceCategory(i).getCategoryID() + ", " );
%>
	0];

var prevCatDiv = null;

function showAdditionalAppInfo(index)
{
	var newCatDiv = document.getElementById('Category' + categoryIDs[index]);
	if (newCatDiv != prevCatDiv) {
		document.MForm.CatID.value = categoryIDs[index];
		if (prevCatDiv != null)
			prevCatDiv.style.display = 'none';
		newCatDiv.style.display = '';
		prevCatDiv = newCatDiv;
	}
}
</script>
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
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
          <td  valign="top" width="101"><% String pageName = "CreateAppliances.jsp"; %><%@ include file="Nav.jsp" %></td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "Main" align="center"><% String header = "CREATE NEW APPLIANCE"; %><%@ include file="InfoSearchBar.jsp" %>
			<% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
           
              <form name="MForm" method="post" action="/servlet/SOAPClient">
			    <input type="hidden" name="action" value="CreateAppliance">
				<input type="hidden" name="AppNo" value="<%= appliances.getStarsApplianceCount() %>">
				<input type="hidden" name="CatID" value="0">
                <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                  <tr> 
                    <td colspan = "2"  class="TableCell"> <span class="MainHeader"><b>APPLIANCE 
                      INFORMATION</b></span> 
                      <hr>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Description: </div>
                    </td>
                    <td width="200"> 
                      <select name="AppCatID" onchange="showAdditionalAppInfo(this.selectedIndex)">
                        <%
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
%>
                        <option value="<%= category.getApplianceCategoryID() %>"><%= category.getDescription() %></option>
                        <%
	}
%>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Model #:</div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="ModelNo" maxlength="40" size="24" value="">
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
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
                      <input type="text" name="ManuYear" maxlength="14" size="14">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">KW Capacity:</div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="KWCapacity" maxlength="14" size="14">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Efficiency Rating:</div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="EffRating" maxlength="14" size="14">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Notes:</div>
                    </td>
                    <td width="200"> 
                      <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                    </td>
                  </tr>
                </table>
<%
	int categoryID_AC = ServletUtils.getStarsCustListEntry(
			selectionListTable,
			YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
			YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER
			).getEntryID();
	// Check if this category is used by this energy company
	boolean isCategoryACUsed = false;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++)
		if (categories.getStarsApplianceCategory(i).getCategoryID() == categoryID_AC) {
			isCategoryACUsed = true;
			break;
		}
	if (isCategoryACUsed) {
%>
				<div id="Category<%= categoryID_AC %>" style="display:none">
                <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Tonnage:</div>
                    </td>
                    <td width="200"> 
                      <select name="AC_Tonnage">
<%
		StarsCustSelectionList tonnageList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_TONNAGE );
		for (int i = 0; i < tonnageList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = tonnageList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		for (int i = 0; i < typeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = typeList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
		}
%>
                      </select>
                    </td>
                  </tr>
                </table>
				</div>
<%
	}
	
	int categoryID_WH = ServletUtils.getStarsCustListEntry(
			selectionListTable,
			YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
			YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER
			).getEntryID();
	// Check if this category is used by this energy company
	boolean isCategoryWHUsed = false;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++)
		if (categories.getStarsApplianceCategory(i).getCategoryID() == categoryID_WH) {
			isCategoryWHUsed = true;
			break;
		}
	if (isCategoryWHUsed) {
%>
				<div id="Category<%= categoryID_WH %>" style="display:none">
                <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right"># Gallons:</div>
                    </td>
                    <td width="200"> 
                      <select name="WH_GallonNum">
<%
		StarsCustSelectionList gallonNumList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_GALLONS );
		for (int i = 0; i < gallonNumList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = gallonNumList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList energySrcList1 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE );
		for (int i = 0; i < energySrcList1.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = energySrcList1.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
                      <input type="text" name="WH_ElementNum" maxlength="14" size="14" value="">
                    </td>
                  </tr>
                </table>
				</div>
<%
	}
	
	int categoryID_DF = ServletUtils.getStarsCustListEntry(
			selectionListTable,
			YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
			YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL
			).getEntryID();
	// Check if this category is used by this energy company
	boolean isCategoryDFUsed = false;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++)
		if (categories.getStarsApplianceCategory(i).getCategoryID() == categoryID_DF) {
			isCategoryDFUsed = true;
			break;
		}
	if (isCategoryDFUsed) {
%>
				<div id="Category<%= categoryID_DF %>" style="display:none">
                <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Switch-over Type:</div>
                    </td>
                    <td width="200"> 
                      <select name="DF_SwitchOverType">
<%
		StarsCustSelectionList switchOverTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SWITCH_OVER_TYPE );
		for (int i = 0; i < switchOverTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = switchOverTypeList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
                      <input type="text" name="DF_KWCapacity2" maxlength="14" size="14" value="">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell" height="2"> 
                      <div align="right">Secondary Energy Source:</div>
                    </td>
                    <td width="200" height="2"> 
                      <select name="DF_EnergySrc2">
<%
		StarsCustSelectionList energySrcList2 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE );
		for (int i = 0; i < energySrcList2.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = energySrcList2.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
		}
%>
                      </select>
                    </td>
                  </tr>
                </table>
				</div>
<%
	}
	
	int categoryID_GEN = ServletUtils.getStarsCustListEntry(
			selectionListTable,
			YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
			YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR
			).getEntryID();
	// Check if this category is used by this energy company
	boolean isCategoryGENUsed = false;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++)
		if (categories.getStarsApplianceCategory(i).getCategoryID() == categoryID_GEN) {
			isCategoryGENUsed = true;
			break;
		}
	if (isCategoryGENUsed) {
%>
				<div id="Category<%= categoryID_GEN %>" style="display:none">
                <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Transfer switch Type:</div>
                    </td>
                    <td width="200"> 
                      <select name="GEN_TranSwitchType">
<%
		StarsCustSelectionList tranSwitchTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_TRANSFER_SWITCH_TYPE );
		for (int i = 0; i < tranSwitchTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = tranSwitchTypeList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList tranSwitchMfgList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_TRANSFER_SWITCH_MFG );
		for (int i = 0; i < tranSwitchMfgList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = tranSwitchMfgList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
                      <input type="text" name="GEN_KWCapacity" maxlength="14" size="14" value="">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Fuel Capacity (gal):</div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="GEN_FuelCapGal" maxlength="14" size="14" value="">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell" height="2"> 
                      <div align="right">Start Delay (sec):</div>
                    </td>
                    <td width="200" height="2"> 
                      <input type="text" name="GEN_StartDelaySec" maxlength="14" size="14" value="">
                    </td>
                  </tr>
                </table>
				</div>
<%
	}
	
	int categoryID_GD = ServletUtils.getStarsCustListEntry(
			selectionListTable,
			YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
			YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER
			).getEntryID();
	// Check if this category is used by this energy company
	boolean isCategoryGDUsed = false;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++)
		if (categories.getStarsApplianceCategory(i).getCategoryID() == categoryID_GD) {
			isCategoryGDUsed = true;
			break;
		}
	if (isCategoryGDUsed) {
%>
                <div id="Category<%= categoryID_GD %>" style="display:none">
                <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                  <tr> 
                    <td width="100" class="TableCell" height="17"> 
                      <div align="right">Dryer Type:</div>
                    </td>
                    <td width="200" height="17"> 
                      <select name="GD_DryerType">
<%
		StarsCustSelectionList dryerTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DRYER_TYPE );
		for (int i = 0; i < dryerTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = dryerTypeList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList binSizeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_BIN_SIZE );
		for (int i = 0; i < binSizeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = binSizeList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList energySrcList3 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE );
		for (int i = 0; i < energySrcList3.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = energySrcList3.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList horsePowerList1 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_HORSE_POWER );
		for (int i = 0; i < horsePowerList1.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = horsePowerList1.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList heatSrcList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_HEAT_SOURCE );
		for (int i = 0; i < heatSrcList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = heatSrcList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
		}
%>
                      </select>
                    </td>
                  </tr>
                </table>
				</div>
<%
	}
	
	int categoryID_SH = ServletUtils.getStarsCustListEntry(
			selectionListTable,
			YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
			YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT
			).getEntryID();
	// Check if this category is used by this energy company
	boolean isCategorySHUsed = false;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++)
		if (categories.getStarsApplianceCategory(i).getCategoryID() == categoryID_SH) {
			isCategorySHUsed = true;
			break;
		}
	if (isCategorySHUsed) {
%>
				<div id="Category<%= categoryID_SH %>" style="display:none">
                <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Storage Type:</div>
                    </td>
                    <td width="200"> 
                      <select name="SH_StorageType">
<%
		StarsCustSelectionList storageTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_TYPE );
		for (int i = 0; i < storageTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = storageTypeList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
                      <input type="text" name="SH_KWCapacity" maxlength="14" size="14" value="">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Recharge Time (hr):</div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="SH_RechargeHour" maxlength="14" size="14" value="">
                    </td>
                  </tr>
                </table>
				</div>
<%
	}
	
	int categoryID_HP = ServletUtils.getStarsCustListEntry(
			selectionListTable,
			YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
			YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP
			).getEntryID();
	// Check if this category is used by this energy company
	boolean isCategoryHPUsed = false;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++)
		if (categories.getStarsApplianceCategory(i).getCategoryID() == categoryID_HP) {
			isCategoryHPUsed = true;
			break;
		}
	if (isCategoryHPUsed) {
%>
				<div id="Category<%= categoryID_HP %>" style="display:none">
                <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Pump Type:</div>
                    </td>
                    <td width="200"> 
                      <select name="HP_PumpType">
<%
		StarsCustSelectionList pumpTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_PUMP_TYPE );
		for (int i = 0; i < pumpTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = pumpTypeList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList standbySrcList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_STANDBY_SOURCE );
		for (int i = 0; i < standbySrcList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = standbySrcList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
                      <input type="text" name="HP_RestartDelaySec" maxlength="14" size="14" value="">
                    </td>
                  </tr>
                </table>
				</div>
<%
	}
	
	int categoryID_IRR = ServletUtils.getStarsCustListEntry(
			selectionListTable,
			YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
			YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION
			).getEntryID();
	// Check if this category is used by this energy company
	boolean isCategoryIRRUsed = false;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++)
		if (categories.getStarsApplianceCategory(i).getCategoryID() == categoryID_IRR) {
			isCategoryIRRUsed = true;
			break;
		}
	if (isCategoryIRRUsed) {
%>
				<div id="Category<%= categoryID_IRR %>" style="display:none">
                <table width="300" border="0" cellpadding="1" align="center" cellspacing="0">
                  <tr> 
                    <td width="100" class="TableCell" height="17"> 
                      <div align="right">Irrigation Type:</div>
                    </td>
                    <td width="200" height="17"> 
                      <select name="IRR_IrrigationType">
<%
		StarsCustSelectionList irrTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE );
		for (int i = 0; i < irrTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = irrTypeList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList horsePowerList2 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_HORSE_POWER );
		for (int i = 0; i < horsePowerList2.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = horsePowerList2.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList energySrcList4 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE );
		for (int i = 0; i < energySrcList4.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = energySrcList4.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList soilTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SOIL_TYPE );
		for (int i = 0; i < soilTypeList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = soilTypeList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList locationList1 = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION );
		for (int i = 0; i < locationList1.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = locationList1.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
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
		StarsCustSelectionList voltageList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
		for (int i = 0; i < voltageList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = voltageList.getStarsSelectionListEntry(i);
%>
                        <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
		}
%>
                      </select>
                    </td>
                  </tr>
                </table>
				</div>
<%
	}
%>
<script language="JavaScript">
	showAdditionalAppInfo(0);
</script>
                <br>
                <table width="150" border="0">
                <tr>
                  <td align = "center" width = "50%"> 
                    <input type="submit" name="Submit" value="Save">
                  </td>
                  <td> 
                    <input type="reset" name="Cancel" value="Cancel">
                  </td>
                </tr>
              </table><br>
              </form>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
