<%--
Required variables:
	hwConfigType: int
	configuration: StarsLMConfiguration
--%>
<%
	String[] coldLoadPickup = new String[8];
	Arrays.fill(coldLoadPickup, "");
	String[] tamperDetect = new String[8];
	Arrays.fill(tamperDetect, "");
	String[] program = new String[8];
	Arrays.fill(program, "");
	String[] splinter = new String[8];
	Arrays.fill(splinter, "");
	
	if (configuration != null) {
		if (configuration.getColdLoadPickup() != null) {
			String[] clp = configuration.getColdLoadPickup().split(",");
			System.arraycopy(clp, 0, coldLoadPickup, 0, clp.length);
		}
		if (configuration.getTamperDetect() != null) {
			String[] td = configuration.getTamperDetect().split(",");
			System.arraycopy(td, 0, tamperDetect, 0, td.length);
		}
		if (configuration.getExpressCom() != null) {
			String[] prog = configuration.getExpressCom().getProgram().split(",");
			String[] splt = configuration.getExpressCom().getSplinter().split(",");
			System.arraycopy(prog, 0, program, 0, prog.length);
			System.arraycopy(splt, 0, splinter, 0, splt.length);
		}
	}
	
	boolean hasTamperDetect = hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205
			|| hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305
			|| hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA_SIMPLE;
	boolean hasProgramSplinter = hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM;
%>
<table border="1" cellspacing="0" cellpadding="0" bgcolor="#CCCCCC">
  <tr> 
    <td> 
      <table border="0" cellspacing="3" cellpadding="0">
        <tr class="TitleHeader" valign="top" align="center"> 
          <td width="30" class="HeaderCell">Relay</td>
          <td width="<%= hasProgramSplinter?65:130 %>" class="HeaderCell">Cold Load 
            Pickup (seconds)</td>
<% if (hasTamperDetect) { %>
          <td width="65" class="HeaderCell">Tamper 
            Detect</td>
<% } %>
<% if (hasProgramSplinter) { %>
          <td width="65" class="HeaderCell">Program</td>
          <td width="65" class="HeaderCell">Splinter</td>
<% } %>
        </tr>
<%
	int numRelay = (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM)? 8 : 4;
	for (int i = 0; i < numRelay; i++) {
%>
        <tr align="center"> 
          <td width="30" class="HeaderCell"><%= i+1 %></td>
          <td width="<%= hasProgramSplinter?65:130 %>"> 
            <input type="text" name="ColdLoadPickup" value="<%= coldLoadPickup[i] %>" size="4" maxlength="10" onchange="setContentChanged(true)">
          </td>
<% if (hasTamperDetect && i < 2) { %>
          <td width="65"> 
            <input type="text" name="TamperDetect" value="<%= tamperDetect[i] %>" size="4" maxlength="10" onchange="setContentChanged(true)">
          </td>
<% } %>
<% if (hasProgramSplinter) { %>
          <td width="65"> 
            <input type="text" name="XCOM_Program" value="<%= program[i] %>" size="4" maxlength="10" onchange="setContentChanged(true)">
          </td>
          <td width="65"> 
            <input type="text" name="XCOM_Splinter" value="<%= splinter[i] %>" size="4" maxlength="10" onchange="setContentChanged(true)">
          </td>
<% } %>
        </tr>
<%
	}
%>
      </table>
    </td>
  </tr>
</table>
