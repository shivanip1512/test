<%
	// Table of [link, label, page name(optional)]
	String linkTable[][] = {{"Inventory.jsp", "Show All"},
						  {"CreateHardware.jsp", "Create Hardware"},
						  {"CreateMCT.jsp", "Create MCT"},
						  {"AddSN.jsp", "Add Range"},
						  {"UpdateSN.jsp", "Update Range"},
						  {"DeleteSN.jsp", "Delete Range"},
						  {"ConfigSN.jsp", "Configure Range"},
						 };

	String bulletImg = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED, "Bullet.gif") + "' width='9' height='9'>";
	
	// List of String[] (link image, link html)
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkTable.length; i++) {
		String linkImg = null;
		String linkHtml = null;
		String linkPageName = (linkTable[i].length == 3)? linkTable[i][2] : linkTable[i][0];
		if (linkPageName.equalsIgnoreCase(pageName)) {
			linkImg = bulletImg;
			linkHtml = "<span class='Nav'>" + linkTable[i][1] + "</span>";
		}
		else {
			linkImg = "";
			linkHtml = "<a href='" + linkTable[i][0] + "' class='Link2' onclick='return warnUnsavedChanges();'><span class='NavText'>" + linkTable[i][1] + "</span></a>";
		}
		links.put(linkPageName, new String[] {linkImg, linkHtml});
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
<cti:checkMultiProperty propertyid="<%= Integer.toString(InventoryRole.INVENTORY_SHOW_ALL) + ',' + Integer.toString(InventoryRole.INVENTORY_CREATE_HARDWARE) + ',' + Integer.toString(InventoryRole.INVENTORY_CREATE_MCT) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Inventory</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%= InventoryRole.INVENTORY_SHOW_ALL %>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("Inventory.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Inventory.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= InventoryRole.INVENTORY_CREATE_HARDWARE %>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("CreateHardware.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("CreateHardware.jsp"))[1] %></td>
          </tr>
          <% if (liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT, false) != null) { %>
          <tr> 
            <td width="10"><%= ((String[]) links.get("CreateMCT.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("CreateMCT.jsp"))[1] %></td>
          </tr>
          <% } %>
		  </cti:checkProperty>
        </table>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= Integer.toString(InventoryRole.SN_ADD_RANGE) + ',' + Integer.toString(InventoryRole.SN_UPDATE_RANGE) + ',' + Integer.toString(InventoryRole.SN_CONFIG_RANGE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Serial Range</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%= InventoryRole.SN_ADD_RANGE %>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("AddSN.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("AddSN.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty> 
		  <cti:checkProperty propertyid="<%= InventoryRole.SN_UPDATE_RANGE %>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("UpdateSN.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("UpdateSN.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= InventoryRole.SN_DELETE_RANGE %>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("DeleteSN.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("DeleteSN.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= InventoryRole.SN_CONFIG_RANGE %>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("ConfigSN.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("ConfigSN.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
        </table>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
</table>
