
<%@ include file="include/oper_header.jsp" %>                                   
<!-- Find all the versacom serial groups associated with this operator -->
<!-- a serial address of 0 indicates that we should display the serial number text field -->

<cti:checkProperty propertyid="<%=DirectLoadcontrolRole.INDIVIDUAL_SWITCH%>">

<%                                       
    boolean showSerialNumberTextField = false;
    int serialNumberTextFieldIndex = -1;
    int serialNumberDropDownIndex = 0;

    int textFieldRouteID = -1;
    
    String sql = "select GENERICMACRO.CHILDID from OPERATORSERIALGROUP,GENERICMACRO WHERE GENERICMACRO.OWNERID=OPERATORSERIALGROUP.LMGROUPID AND OPERATORSERIALGROUP.LOGINID=" +user.getUserID()  + " ORDER BY GENERICMACRO.CHILDORDER";

    Object[][] serialGroupIDs = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sql, new Class[] { Integer.class } );
	Object[][] versacomNameSerial = null;
	Object[][] expresscomNameSerial = null;
    Object[][] nameSerial = null;

	// get versacom serial groups
    if( serialGroupIDs != null ) {
    
    sql = "SELECT YUKONPAOBJECT.PAONAME,LMGROUPVERSACOM.SERIALADDRESS,LMGROUPVERSACOM.DEVICEID,LMGROUPVERSACOM.ROUTEID FROM YUKONPAOBJECT,LMGROUPVERSACOM WHERE YUKONPAOBJECT.PAOBJECTID=LMGROUPVERSACOM.DEVICEID ";

    for( int i = 0; i < serialGroupIDs.length; i++ ) {

        if( i == 0 ) {
            sql += " AND (LMGROUPVERSACOM.DEVICEID=" + serialGroupIDs[i][0] + " ";
        }
        else {
            sql += " OR LMGROUPVERSACOM.DEVICEID=" + serialGroupIDs[i][0] + " ";
        }
	}
    if( serialGroupIDs.length > 0)
        sql += " )";

	versacomNameSerial = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sql, new Class[] { String.class, Integer.class, Integer.class, Integer.class } );

  	// get expresscom serial groups 
      
	sql = "SELECT YUKONPAOBJECT.PAONAME,LMGROUPEXPRESSCOM.SERIALNUMBER,LMGROUPEXPRESSCOM.LMGROUPID,LMGROUPEXPRESSCOM.ROUTEID FROM YUKONPAOBJECT,LMGROUPEXPRESSCOM WHERE YUKONPAOBJECT.PAOBJECTID=LMGROUPEXPRESSCOM.LMGROUPID ";

    for( int i = 0; i < serialGroupIDs.length; i++ ) {

        if( i == 0 ) {
            sql += " AND (LMGROUPEXPRESSCOM.LMGROUPID=" + serialGroupIDs[i][0] + " ";
        }
        else {
            sql += " OR LMGROUPEXPRESSCOM.LMGROUPID=" + serialGroupIDs[i][0] + " ";
        }
    }
    if( serialGroupIDs.length > 0)
        sql += " )";
    
    expresscomNameSerial = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sql, new Class[] { String.class, Integer.class, Integer.class, Integer.class } );
	
	int numSerial = 0;
	if(versacomNameSerial != null) numSerial += versacomNameSerial.length;
	if(expresscomNameSerial != null) numSerial += expresscomNameSerial.length;
	
	nameSerial = new Object[numSerial][3];
	{
		int i;
		for(i = 0; versacomNameSerial != null && i < versacomNameSerial.length; i++) {
			nameSerial[i] = versacomNameSerial[i];
		}
	
		for(int j = 0; expresscomNameSerial != null && j < expresscomNameSerial.length; j++) {
			nameSerial[i+j] = expresscomNameSerial[j];
		}
	}
	
    if( nameSerial != null ) {
    
    for( int i = 0; i < nameSerial.length; i++ ) {
        if( ((Integer) nameSerial[i][1]).intValue() == 0 ) {
            showSerialNumberTextField = true;
            serialNumberTextFieldIndex = 0;
            serialNumberDropDownIndex = 1;            
            textFieldRouteID = ((Integer) nameSerial[i][3]).intValue();
        }
    }
    }
    }

%>

<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
  
var textFieldRouteID = <%=textFieldRouteID%>;

var dropDownSerialIDArray = new Array();
var dropDownRouteIDArray = new Array();

<% 
if( nameSerial != null )
{     
   int ii = 0;
   for( int i = 0; i < nameSerial.length; i++ ) {
      if( ((Integer) nameSerial[i][1]).intValue() != 0 ) {
%>
dropDownSerialIDArray[<%=ii%>] = <%=nameSerial[i][1]%>;
dropDownRouteIDArray[<%=ii%>] = <%=nameSerial[i][3]%>;
<%
      ii++;
      }
   }
}
%>
 

  function validForm(form)
  {
      form.serialNumber.value=0;         
<% if( showSerialNumberTextField ) { %>
        if( form.radioButton[<%= serialNumberTextFieldIndex %>].checked ) {
            form.serialNumber.value = form.serialNumberField.value;
            form.routeid.value = <%= textFieldRouteID %>; 
        }
<%      } %>

<% if( nameSerial != null && (nameSerial.length > 1 || (nameSerial.length > 0 && !showSerialNumberTextField)) ) { %>        
        if( form.radioButton<% if(showSerialNumberTextField) { %>[<%= serialNumberDropDownIndex %>]<% } %>.checked ) {               
            form.serialNumber.value = dropDownSerialIDArray[ form.serialSelect.selectedIndex ];
            form.routeid.value = dropDownRouteIDArray[ form.serialSelect.selectedIndex ];            
        }        
<% } %>
        
     return true;
  }

  //End hiding script -->
  </SCRIPT>

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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="253" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Load 
                  Response</td>
                <td width="235" valign="middle">&nbsp;</td>
                
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
<%
  if( nameSerial != null ) 
  {
%>  
      <FORM name="switchform" METHOD="POST" ACTION="<%=request.getContextPath()%>/servlet/SwitchCommand" onSubmit="return validForm(this)">
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
            <table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
              <tr> 
                <td height="20" valign="top"> 
                  <div align="left">
                    <p align="center" class="TableCell1"><b><a href="oper_direct.jsp" class="Link2"><br>
                     Program Summary</a></b></p>
                    
                    
                  </div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <p><br>
            <center>
                <p align="center" class="TitleHeader">INDIVIDUAL SWITCH COMMANDS</p>
                <span align="center" class="MainText">Enter or select a switch serial number:</span>
                <table width="345" border="1" cellspacing = "0" cellpadding = "3" height="66" >
<%  if( showSerialNumberTextField ) {       %>
                  <tr> 
                    <td width="9%" height="15"> 
                      <input type="radio" name="radioButton" value="field" checked>
                    </td>
                    <td width="91%" height="15"> 
                      <input name="serialNumberField" size="9" maxlength="9" type="text"  >
                    </td>
                  </tr>
<%  } %>
<%  if( nameSerial != null  ) { %>
                  <tr> 
                    <td width="9%"> 
                      <input type="radio" name="radioButton" value="dropdown" <% if(!showSerialNumberTextField) { %>checked<% } %>>
                    </td>
                    <td width="91%"> 
                      <select name="serialSelect">
<%    for( int i = 0; i < nameSerial.length; i++ ) {
      if( ((Integer) nameSerial[i][1]).intValue() != 0 ) {
%>
                        <option><%= nameSerial[i][1].toString() + " - " + nameSerial[i][0].toString() %> </option>
                        <%
      }
   }
%>
                      </select>
                    </td>
                  </tr>
<% } %>
                </table>
                </center>
            <table width="345" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
    
  </tr>
</table><br>

              <div align="center"> <span class="MainText">Then, select one of the following functions:</span> 
                <div align="center"> 
                <table width="345" border="0" cellspacing="0" cellpadding="0">
                  <tr>
				  
                    <td width="100%"> 
                      <table width="100%" border="1" cellspacing="0" cellpadding="3">
                        <tr> 
						  <td width="12%" valign="TOP" class="TableCell">&nbsp; 
                            <input type="radio" value="SHED Time" name="function" checked>
                            </td>
                          <td width="31%" valign="TOP" class="TableCell">&nbsp;SHED 
                            Time:</td>
                          <td width="57%" valign="TOP" class="TableCell"> 
                            <table width="182" border="0" cellspacing="0" cellpadding="2">
                              <tr> 
                                <td width="87" class="TableCell"> 
                                    <p align=RIGHT>&nbsp;Time: 
                                  </td>
								<td width="87" class="TableCell"> 
                                <select name="time">
                                    <option SELECTED>1 min 
                                    <option>1 sec 
                                    <option>5 min 
                                    <option>10 min 
                                    <option>20 min 
                                    <option>30 min 
                                    <option>1 hr 
                                    <option>4 hr 
                                    <option>8 hr 
                                  </select>
                                  </td>
                              </tr>
                              <tr> 
                                <td width="87" class="TableCell"> 
                                    <p align=RIGHT>&nbsp;Relay #: 
                                  </td>
								<td width="87" class="TableCell"> 
                                  <select name="shedRelayNumber">
                                    <option SELECTED>1 
                                    <option>2 
                                    <option>3 
                                  </select>
                                  </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
						  <td width="12%" valign="TOP" class="TableCell">&nbsp; 
                            <input type="radio" value="Restore" name="function">
                            </td>
                            <td width="31%" valign="TOP" class="TableCell"> &nbsp;Restore:</td>
                          <td width="57%" valign="TOP" class="TableCell"> 
                            <table width="182" border="0" cellspacing="0" cellpadding="2">
                              <tr> 
                                <td width="87" class="TableCell"> 
                                    <p align=RIGHT>&nbsp;Relay #: 
                                  </td>
								<td width="87" class="TableCell">
                                  <select name="restoreRelayNumber">
                                    <option SELECTED>1 
                                    <option>2 
                                    <option>3 
                                  </select>
                                  </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
						  <td width="12%" valign="TOP" class="TableCell">&nbsp; 
                            <input type="radio" value="Cycle Rate" name="function">
                            </td>
                            <td width="31%" valign="TOP" class="TableCell"> &nbsp;Cycle 
                              Rate:</td>
                          <td width="57%" valign="TOP" class="TableCell"> 
                            <table width="182" border="0" cellspacing="0" cellpadding="2">
                              <tr> 
                                <td width="87" class="TableCell"> 
                                    <p align=RIGHT>&nbsp;Percent: 
                                  </td>
								<td width="87" class="TableCell"> 
                                  <select name="percent">
                                    <option>5 
                                    <option>10 
                                    <option>15 
                                    <option>20 
                                    <option>25 
                                    <option>30 
                                    <option>33 
                                    <option>35 
                                    <option>40 
                                    <option>45 
                                    <option selected>50 
                                    <option>55 
                                    <option>60 
                                    <option>65 
                                    <option>66 
                                    <option>70 
                                    <option>75 
                                    <option>80 
                                    <option>85 
                                    <option>90 
                                    <option>95 
                                    <option>100 
                                  </select>
                                  </td>
                              </tr>
                              <tr> 
                                <td width="87" class="TableCell"> 
                                    <p align=RIGHT>&nbsp;Period (min): 
                                  </td>
								<td width="87" class="TableCell"> 
                                  <select name="period">
                                    <option>5 
                                    <option>10 
                                    <option>15 
                                    <option>20 
                                    <option>25 
                                    <option selected>30 
                                    <option>35 
                                    <option>40 
                                    <option>45 
                                    <option>50 
                                    <option>55 
                                    <option>60 
                                  </select>
                                  </td>
                              </tr>
                              <tr> 
                                <td width="87" class="TableCell"> 
                                    <p align=RIGHT>&nbsp;# of Periods: 
                                  </td>
								<td width="87" class="TableCell"> 
                                  <select name="periodCount">
                                    <option>1 
                                    <option>2 
                                    <option>3 
                                    <option>4 
                                    <option>5 
                                    <option>6 
                                    <option>7 
                                    <option selected>8 
                                    <option>9 
                                    <option>10 
                                    <option>11 
                                    <option>12 
                                    <option>13 
                                    <option>14 
                                    <option>15 
                                    <option>16 
                                    <option>17 
                                    <option>18 
                                    <option>19 
                                    <option>20 
                                    <option>21 
                                    <option>22 
                                    <option>23 
                                    <option>24 
                                    <option>25 
                                    <option>26 
                                    <option>27 
                                    <option>28 
                                    <option>29 
                                    <option>30 
                                    <option>31 
                                    <option>32 
                                    <option>33 
                                    <option>34 
                                    <option>35 
                                    <option>36 
                                    <option>37 
                                    <option>38 
                                    <option>39 
                                    <option>40 
                                    <option>41 
                                    <option>42 
                                    <option>43 
                                    <option>44 
                                    <option>45 
                                    <option>46 
                                    <option>47 
                                    <option>48 
                                    <option>49 
                                    <option>50 
                                    <option>51 
                                    <option>52 
                                    <option>53 
                                    <option>54 
                                    <option>55 
                                    <option>56 
                                    <option>57 
                                    <option>58 
                                    <option>59 
                                    <option>60 
                                    <option>61 
                                    <option>62 
                                    <option>63 
                                  </select>
                                  </td>
                              </tr>
                              <tr> 
                                <td width="87" class="TableCell"> 
                                    <p align=RIGHT>&nbsp;Relay #: 
                                  </td>
								<td width="87" class="TableCell"> 
                                  <select name="startRelayNumber">
                                    <option SELECTED>1 
                                    <option>2 
                                    <option>3 
                                  </select>
                                  </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr> 
						  <td width="12%" valign="TOP" class="TableCell">&nbsp; 
                            <input type="radio" value="Stop Cycle" name="function">
                            </td>
                            <td width="31%" valign="TOP" class="TableCell"> &nbsp;Stop 
                              Cycle:</td>
                          <td width="57%" valign="TOP" class="TableCell"> 
                            <table width="182" border="0" cellspacing="0" cellpadding="2">
                              <tr> 
                                <td width="87" class="TableCell"> 
                                    <p align=RIGHT>&nbsp;Relay #: 
                                  </td>
								<td width="87" class="TableCell"> 
                                  <select name="stopRelayNumber">
                                    <option SELECTED>1 
                                    <option>2 
                                    <option>3 
                                  </select>
                                  </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <table width="100%" border="1" cellspacing="0" cellpadding="3">
                        <tr> 
						  <td width="12%" class="TableCell">&nbsp; 
                            <input type="radio" value="Service Enable" name="function">
                            </td>
                            <td width="88%" valign="TOP" class="TableCell"> &nbsp;Service 
                              Enable</td>
                        </tr>
                        <tr> 
						  <td width="12%" class="TableCell">&nbsp; 
                            <input type="radio" value="Service Disable" name="function">
                            </td>
                            <td width="88%" valign="TOP" class="TableCell"> &nbsp;Service 
                              Disable</td>
                        </tr>
                        <tr> 
						  <td width="12%" class="TableCell">&nbsp; 
                            <input type="radio" value="Cap Control Open" name="function">
                            </td>	
                            <td width="88%" valign="TOP" class="TableCell"> &nbsp;Cap 
                              Control Open</td>
                        </tr>
                        <tr> 
						  <td width="12%" class="TableCell">&nbsp; 
                            <input type="radio" value="Cap Control Closed"
                name="function" >
                           </td>
                            <td width="88%" valign="TOP" class="TableCell"> &nbsp;Cap 
                              Control Closed</td>
                        </tr>
                      </table>
                    </td>					
                  </tr>
                </table>
              </div>
                <br></div>            
              <div align="center">
                <table width="150" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td valign="top"> 
                      <p align=center> 
                        <input type="submit" name="sendButton" value="Send" border="0" align="middle">
                    </td>
                    <td> 
                      <div align="center"> 
                      <!--  <input type = "button" value="Cancel" name = "cancel" onClick = "goBack()"> -->
                      </div>
                    </td>
                  </tr>
                </table>
              </div>            
            <p align="center">&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
        <input name="serialNumber" type="hidden" value="0">
        <INPUT NAME="routeid" TYPE="hidden" VALUE="-1">
        <INPUT name="groupid" type="hidden" value="0">
        <input name="DATABASEALIAS" type="hidden" value="demo">
        <input name="nextURL" type="hidden" value="<%=request.getContextPath()%>/operator/LoadControl/switch_commands.jsp">
    </FORM>
<%
  } // end if name serial != null
  else
  {  
%>
    <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#666699" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>        
    </table>
<%
  }
%>
    </td>
	</tr>
</table>
<br>
<div align="center"></div>
</body>
</html>

</cti:checkProperty>
