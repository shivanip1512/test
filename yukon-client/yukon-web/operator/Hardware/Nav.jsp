<%
	// Map of page name / link text
	String linkPairs[][] = {{"Inventory.jsp", "Show All"},
						  {"AddSN.jsp", "Add Range"},
						  {"UpdateSN.jsp", "Update Range"},
						  {"ConfigSN.jsp", "Configure"},
						 };

	String bulletImg = "../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED);
	if (bulletImg == null) bulletImg = "../../WebConfig/Bullet.gif";
	String bulletImg2 = "../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET);
	if (bulletImg2 == null) bulletImg2 = "../../WebConfig/Bullet2.gif";
	
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkPairs.length; i++) {
		if (linkPairs[i][0].equalsIgnoreCase(pageName))
			links.put(linkPairs[i][0], "<img src='" + bulletImg + "' width='12' height='12'><span class='Nav'>" + linkPairs[i][1] + "</span>");
		else
			links.put(linkPairs[i][0], "<img src='" + bulletImg2 + "' width='12' height='12'><a href='" + linkPairs[i][0] + "' class='Link2'><span class='NavText'>" + linkPairs[i][1] + "</span></a>");
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
<cti:checkMultiProperty propertyid="<%= Integer.toString(InventoryRole.INVENTORY_SHOW_ALL) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Inventory</span><br>
<cti:checkProperty propertyid="<%= InventoryRole.INVENTORY_SHOW_ALL %>">
        <%= links.get("Inventory.jsp") %><br>
</cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= Integer.toString(InventoryRole.SN_ADD_RANGE) + ',' + Integer.toString(InventoryRole.SN_UPDATE_RANGE) + ',' + Integer.toString(InventoryRole.SN_CONFIG_RANGE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Serial Range</span><br>
<cti:checkProperty propertyid="<%= InventoryRole.SN_ADD_RANGE %>">
        <%= links.get("AddSN.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= InventoryRole.SN_UPDATE_RANGE %>">
        <%= links.get("UpdateSN.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= InventoryRole.SN_CONFIG_RANGE %>">
        <%= links.get("ConfigSN.jsp") %><br>
</cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
</table>
