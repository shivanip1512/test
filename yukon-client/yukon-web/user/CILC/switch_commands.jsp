<html>
<head>
<%@ include file="../user_header.jsp" %>
<!-- Find all the versacom serial groups associated with this operator -->
<!-- a serial address of 0 indicates that we should NOT display the serial number text field -->

<%
    boolean showSerialNumberTextField = false;
    int textFieldGroupID = -1;

    String sql = "select GENERICMACRO.CHILDID from CUSTOMERLOGINSERIALGROUP,GENERICMACRO WHERE GENERICMACRO.OWNERID=CUSTOMERLOGINSERIALGROUP.LMGROUPID AND CUSTOMERLOGINSERIALGROUP.LOGINID=" + liteYukonUserID  + " ORDER BY GENERICMACRO.CHILDORDER";
   
    Object[][] serialGroupIDs = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sql, new Class[] { Integer.class } );
    Object[][] nameSerial = null;

    if( serialGroupIDs != null ) {
    
    sql = "SELECT YUKONPAOBJECT.PAONAME,LMGROUPVERSACOM.SERIALADDRESS,LMGROUPVERSACOM.DEVICEID FROM YUKONPAOBJECT,LMGROUPVERSACOM WHERE YUKONPAOBJECT.PAOBJECTID=LMGROUPVERSACOM.DEVICEID AND ";

    for( int i = 0; i < serialGroupIDs.length; i++ ) {

        if( i == 0 ) {
            sql += " (LMGROUPVERSACOM.DEVICEID=" + serialGroupIDs[i][0] + " ";
        }
        else {
            sql += " OR LMGROUPVERSACOM.DEVICEID=" + serialGroupIDs[i][0] + " ";
        }
        
    }
    
    sql += " )";

   
    nameSerial = com.cannontech.util.ServletUtil.executeSQL( dbAlias, sql, new Class[] { String.class, Integer.class, Integer.class } );
  
    if( nameSerial != null ) {
    
    for( int i = 0; i < nameSerial.length; i++ ) {
        if( ((Integer) nameSerial[i][1]).intValue() == 0 ) {
            showSerialNumberTextField = true;
            textFieldGroupID = ((Integer) nameSerial[i][2]).intValue();
        }
    }
    }
    }

    String prevSerialNumber = request.getParameter("sn");
%>

<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
  
  function validForm(form)
  {
      form.serialNumber.value = 0;
      form.groupid.value = 0;
      
      // did the user select a serial num or type it in?
      for( i = 0; i < availablesn.length; i++ ) {
          if( form.serialNumberField.value == availablesn[i] ) {
              form.groupid.value=availablegp[i];
              break;
          }
      }

      if( form.groupid.value == 0 ) {
          form.serialNumber.value = form.serialNumberField.value;
      }
      
     return true;
  }

  //End hiding script -->
  </SCRIPT>

<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height="28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Switch 
                  Commands </td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
            <table width="101" border="0" cellspacing="0" cellpadding="0" height="200">
              <tr> 
                <td valign="top"> 
                  <% String pageName = "switch_commands.jsp"; %>
          		  <%@ include file="nav.jsp" %>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
		  <FORM name="switchform" METHOD="POST" ACTION="/servlet/SwitchCommand" onSubmit="return validForm(this)">
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <p><br>
            <center>
              <p align="center" class="Main"><b>INDIVIDUAL SWITCH COMMANDS</b></p>
            </center>
            <table width="345" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
    
	         <td width="100%">
	         <table border="0" width="100%" cellspacing="0" cellpadding="0">
               <tr>
                        <td width="100%" align="center"> 
                          <p class="Main" align="center">Enter or select a switch 
                            serial number:<br>
                            <input name="serialNumberField" size="9" maxlength="9" type="text" <% if( prevSerialNumber != null ) { %><%= "value=\"" + prevSerialNumber + "\""%><% } %> >                            
                            &nbsp;&nbsp; 
                            <select name="selectbox" onChange="changecontent(this)">
                                <option> </option>
<%
if( nameSerial != null )
{     
   for( int i = 0; i < nameSerial.length; i++ ) {
      if( ((Integer) nameSerial[i][1]).intValue() != 0 ) {
%>
                                <option><%= nameSerial[i][1] %> </option>   
<%
      }
   }
}
%>
                            </select>
                            <br>
                          </p>
                          </td>
        </tr>
        <tr>
        </tr>
      </table>
    
    </td>
	
  </tr>
</table>
<script language="JavaScript">

/*
Drop down messages script
By JavaScript Kit (http://javascriptkit.com)
Over 400+ free scripts here!
*/

//change contents of message box, where the first one corresponds with the first drop down box, second with second box etc
var availablesn=new Array()
var availablegp=new Array()

availablesn[0]=''

<%
if( nameSerial != null )
{            
   for( int i = 0; i < nameSerial.length; i++ ) {
      if( ((Integer) nameSerial[i][1]).intValue() != 0 ) {
          out.println("availablesn[" + (i+1) + "]='" + nameSerial[i][1] + "'");
          out.println("availablegp[" + (i+1) + "]='" + nameSerial[i][2] + "'");
      }
   }
}
%>

//don't edit pass this line

function changecontent(which){
document.switchform.serialNumberField.value=availablesn[which.selectedIndex]
}

document.switchform.serialNumberField.value=availablesn[document.switchform.selectbox.selectedIndex]
</script>
<p>
            <div align="center"> 
              <span class="Main">Select one of the following functions:</span> 
              <div align="center"> 
                <table width="345" border="1" cellspacing="0"
        cellpadding="0">
                  <tr>
				  
                    <td width="100%"> 
                      <table width="100%" border="1" cellspacing="0" cellpadding="3">
                        <tr> 
						  <td width="12%" valign="TOP" class="TableCell">&nbsp; 
                            <input type="radio" value="SHED Time" name="function" >
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
                </div>
				<br>            
              <div align="center">
                <input type="submit" name="sendButton" value="Send">
              </div>            
            <p align="center">&nbsp;</p>
          </td>
		  </form>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
     <input name="serialNumber" type="hidden" value="0">
        <INPUT name="groupid" type="hidden" value="0">
        <input name="DATABASEALIAS" type="hidden" value="demo">
        <input name="nextURL" type="hidden" value="/switch_commands.jsp">
      <%
  } // end if name serial != null
  else
  {  
%>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
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
</body>
</html>

