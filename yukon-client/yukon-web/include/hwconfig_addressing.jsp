<%--
Required variables:
	hwConfigType: int
	configuration: StarsLMConfiguration
--%>
<%
	if (hwConfigType == ECUtils.HW_CONFIG_TYPE_SA205) {
		SA205 sa205 = null;
		if (configuration != null) sa205 = configuration.getSA205();
		if (sa205 == null) {
			sa205 = new SA205();
			sa205.setSlot1( ECUtils.SA205_UNUSED_ADDR );
			sa205.setSlot2( ECUtils.SA205_UNUSED_ADDR );
			sa205.setSlot3( ECUtils.SA205_UNUSED_ADDR );
			sa205.setSlot4( ECUtils.SA205_UNUSED_ADDR );
			sa205.setSlot5( ECUtils.SA205_UNUSED_ADDR );
			sa205.setSlot6( ECUtils.SA205_UNUSED_ADDR );
		}
%>
<table width="180" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC">
  <tr> 
    <td class="TitleHeader" align="center"> Slot Addresses<br>
      <table width="80%" border="0" cellspacing="3" cellpadding="0">
        <tr> 
          <td width="60%" align="center"> 
            <input type="text" name="SA205_Slot1" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot1(), 0) %>" size="15" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td width="60%" align="center"> 
            <input type="text" name="SA205_Slot2" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot2(), 0) %>" size="15" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td width="60%" align="center"> 
            <input type="text" name="SA205_Slot3" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot3(), 0) %>" size="15" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td width="60%" align="center"> 
            <input type="text" name="SA205_Slot4" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot4(), 0) %>" size="15" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td width="60%" align="center"> 
            <input type="text" name="SA205_Slot5" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot5(), 0) %>" size="15" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td width="60%" align="center"> 
            <input type="text" name="SA205_Slot6" value="<%= ServletUtils.hideUnsetNumber(sa205.getSlot6(), 0) %>" size="15" maxlength="15">
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<%
	}
	else if (hwConfigType == ECUtils.HW_CONFIG_TYPE_SA305) {
		SA305 sa305 = null;
		if (configuration != null) sa305 = configuration.getSA305();
		if (sa305 == null) sa305 = new SA305();
%>
<table width="180" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC">
  <tr> 
    <td align="center" class="TitleHeader">SA305 Addresses<br>
      <table width="100%" border="0" cellspacing="3" cellpadding="0">
        <tr> 
          <td align="right" class="MainText" width="55%">Utility:</td>
          <td width="45%"> 
            <input type="text" name="SA305_Utility" value="<%= sa305.getUtility() %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="55%">Group:</td>
          <td width="45%"> 
            <input type="text" name="SA305_Group" value="<%= ServletUtils.hideUnsetNumber(sa305.getGroup(), 0) %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="55%">Division:</td>
          <td width="45%"> 
            <input type="text" name="SA305_Division" value="<%= ServletUtils.hideUnsetNumber(sa305.getDivision(), 0) %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="55%">Substation:</td>
          <td width="45%"> 
            <input type="text" name="SA305_Substation" value="<%= ServletUtils.hideUnsetNumber(sa305.getSubstation(), 0) %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="TitleHeader" width="55%">Rate 
            Address </td>
          <td width="45%">&nbsp;</td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="55%">Family:</td>
          <td width="45%"> 
            <input type="text" name="SA305_RateFamily" value="<%= sa305.getRateFamily() %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="55%">Member:</td>
          <td width="45%"> 
            <input type="text" name="SA305_RateMember" value="<%= sa305.getRateMember() %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="55%"> 
            Hierarchy:</td>
          <td width="45%"> 
            <input type="text" name="SA305_RateHierarchy" value="<%= sa305.getRateHierarchy() %>" size="6" maxlength="15">
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<%
	}
	else if (hwConfigType == ECUtils.HW_CONFIG_TYPE_VERSACOM) {
		VersaCom vcom = null;
		if (configuration != null) vcom = configuration.getVersaCom();
		if (vcom == null) vcom = new VersaCom();
		
		String[] classChecked = new String[16];
		String[] divisionChecked = new String[16];
		Arrays.fill(classChecked, "");
		Arrays.fill(divisionChecked, "");
		
		for (int i = 0, bm = 1; i < 16; i++) {
			if ((vcom.getClassAddress() & bm) != 0) classChecked[i] = "checked";
			if ((vcom.getDivision() & bm) != 0) divisionChecked[i] = "checked";
			bm *= 2;
		}
%>
<table width="240" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC">
  <tr> 
    <td align="center" class="TitleHeader" height="69">VERSACOM 
      Addresses<br>
      <table width="100%" border="0" cellspacing="3" cellpadding="0">
        <tr> 
          <td align="right" class="MainText" width="20%">Utility:</td>
          <td width="80%"> 
            <input type="text" name="VCOM_Utility" value="<%= vcom.getUtility() %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="20%">Section:</td>
          <td width="80%"> 
            <input type="text" name="VCOM_Section" value="<%= ServletUtils.hideUnsetNumber(vcom.getSection(), 0) %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td class="MainText" colspan="2"> 
            <hr>
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="20%">Class:</td>
          <td width="80%"> 
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell2">
              <tr align="center"> 
                <td>1</td>
                <td>2</td>
                <td>3</td>
                <td>4</td>
                <td>5</td>
                <td>6</td>
                <td>7</td>
                <td>8</td>
              </tr>
              <tr align="center"> 
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="1" <%= classChecked[0] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="2" <%= classChecked[1] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="4" <%= classChecked[2] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="8" <%= classChecked[3] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="16" <%= classChecked[4] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="32" <%= classChecked[5] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="64" <%= classChecked[6] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="128" <%= classChecked[7] %>>
                </td>
              </tr>
              <tr align="center"> 
                <td>9</td>
                <td>10</td>
                <td>11</td>
                <td>12</td>
                <td>13</td>
                <td>14</td>
                <td>15</td>
                <td>16</td>
              </tr>
              <tr align="center"> 
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="256" <%= classChecked[8] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="512" <%= classChecked[9] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="1024" <%= classChecked[10] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="2048" <%= classChecked[11] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="4096" <%= classChecked[12] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="8192" <%= classChecked[13] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="16384" <%= classChecked[14] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Class" value="32768" <%= classChecked[15] %>>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <td class="MainText" colspan="2"> 
            <hr>
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="20%">Division:</td>
          <td width="80%"> 
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell2">
              <tr align="center"> 
                <td>1</td>
                <td>2</td>
                <td>3</td>
                <td>4</td>
                <td>5</td>
                <td>6</td>
                <td>7</td>
                <td>8</td>
              </tr>
              <tr align="center"> 
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="1" <%= divisionChecked[0] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="2" <%= divisionChecked[1] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="4" <%= divisionChecked[2] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="8" <%= divisionChecked[3] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="16" <%= divisionChecked[4] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="32" <%= divisionChecked[5] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="64" <%= divisionChecked[6] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="128" <%= divisionChecked[7] %>>
                </td>
              </tr>
              <tr align="center"> 
                <td>9</td>
                <td>10</td>
                <td>11</td>
                <td>12</td>
                <td>13</td>
                <td>14</td>
                <td>15</td>
                <td>16</td>
              </tr>
              <tr align="center"> 
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="256" <%= divisionChecked[8] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="512" <%= divisionChecked[9] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="1024" <%= divisionChecked[10] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="2048" <%= divisionChecked[11] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="4096" <%= divisionChecked[12] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="8192" <%= divisionChecked[13] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="16384" <%= divisionChecked[14] %>>
                </td>
                <td> 
                  <input type="checkbox" name="VCOM_Division" value="32768" <%= divisionChecked[15] %>>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<%
	}
	else if (hwConfigType == ECUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
		ExpressCom xcom = null;
		if (configuration != null) xcom = configuration.getExpressCom();
		if (xcom == null) xcom = new ExpressCom();
		
		String[] checked = new String[16];
		Arrays.fill(checked, "");
		for (int i = 0, bm = 1; i < 16; i++) {
			if ((xcom.getFeeder() & bm) != 0) checked[i] = "checked";
			bm *= 2;
		}
%>
<table width="240" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC">
  <tr> 
    <td align="center" class="TitleHeader" height="69">EXPRESSCOM 
      Addresses<br>
      <table width="100%" border="0" cellspacing="3" cellpadding="0">
        <tr> 
          <td align="right" class="MainText" width="20%">SPID:</td>
          <td width="80%"> 
            <input type="text" name="XCOM_SPID" value="<%= xcom.getServiceProvider() %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="20%">GEO:</td>
          <td width="80%"> 
            <input type="text" name="XCOM_GEO" value="<%= ServletUtils.hideUnsetNumber(xcom.getGEO(), 0) %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="20%">SUB:</td>
          <td width="80%"> 
            <input type="text" name="XCOM_SUB" value="<%= ServletUtils.hideUnsetNumber(xcom.getSubstation(), 0) %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td class="MainText" colspan="2"> 
            <hr>
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="20%">FEED:</td>
          <td width="80%"> 
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell2">
              <tr align="center"> 
                <td>1</td>
                <td>2</td>
                <td>3</td>
                <td>4</td>
                <td>5</td>
                <td>6</td>
                <td>7</td>
                <td>8</td>
              </tr>
              <tr align="center"> 
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="1" <%= checked[0] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="2" <%= checked[1] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="4" <%= checked[2] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="8" <%= checked[3] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="16" <%= checked[4] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="32" <%= checked[5] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="64" <%= checked[6] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="128" <%= checked[7] %>>
                </td>
              </tr>
              <tr align="center"> 
                <td>9</td>
                <td>10</td>
                <td>11</td>
                <td>12</td>
                <td>13</td>
                <td>14</td>
                <td>15</td>
                <td>16</td>
              </tr>
              <tr align="center"> 
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="256" <%= checked[8] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="512" <%= checked[9] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="1024" <%= checked[10] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="2048" <%= checked[11] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="4096" <%= checked[12] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="8192" <%= checked[13] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="16384" <%= checked[14] %>>
                </td>
                <td> 
                  <input type="checkbox" name="XCOM_FEED" value="32768" <%= checked[15] %>>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <td class="MainText" colspan="2"> 
            <hr>
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="20%">ZIP:</td>
          <td width="80%"> 
            <input type="text" name="XCOM_ZIP" value="<%= ServletUtils.hideUnsetNumber(xcom.getZip(), 0) %>" size="6" maxlength="15">
          </td>
        </tr>
        <tr> 
          <td align="right" class="MainText" width="20%">USER:</td>
          <td width="80%"> 
            <input type="text" name="XCOM_USER" value="<%= ServletUtils.hideUnsetNumber(xcom.getUserAddress(), 0) %>" size="6" maxlength="15">
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<%
	}
%>
