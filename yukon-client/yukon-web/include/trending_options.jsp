<!--TRENDING OPTIONS-->
<table width="575" border="0" align="center" cellpadding="4" cellspacing="0">
  <tr>
    <td width="303" valign="top">
      <div>
        <table width="375" border="0" cellspacing="2" cellpadding="0">
          <tr>
            <form id=MForm method="POST" action="<%= request.getContextPath() %>/servlet/GraphGenerator"  name="MForm">
              <INPUT TYPE="hidden" NAME="gdefid" VALUE="<%=graphBean.getGdefid()%>">
              <INPUT TYPE="hidden" NAME="view" VALUE="<%=graphBean.getViewType()%>">
              <INPUT TYPE="hidden" NAME="option" VALUE = "<%=graphBean.getOption()%>" >
			  <INPUT TYPE="hidden" NAME="action" VALUE = "UpdateOptions" >
              <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?gdefid=<%=graphBean.getGdefid()%>">
			  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?gdefid=<%= graphBean.getGdefid()%>">
              
              <td width="163" valign="top"><font face="Arial, Helvetica, sans-serif" size="1">Start Date:</font>
			    <input id="cal" type="text" name="start" value="<%= datePart.format(graphBean.getStartDate()) %>" size="8">
                  <A HREF="javascript:openCalendar(document.getElementById('MForm').cal)"
                    onMouseOver="window.status='Start Date Calendar';return true;"
                    onMouseOut="window.status='';return true;">
                    <IMG SRC="<%=request.getContextPath()%>/Images/Icons/StartCalendar.gif" WIDTH="20" HEIGHT="15" ALIGN="ABSMIDDLE" BORDER="0">
                  </A>
              </td>
              <td width="154" valign="top"><font face="Arial, Helvetica, sans-serif" size="1">Time Period:</font>
                <select name="period">
                  <% /* Fill in the period drop down and attempt to match the current period with one of the options */                           
                    for( int j = 0; j < com.cannontech.util.ServletUtil.historicalPeriods.length; j++ )
                    {
                      if( com.cannontech.util.ServletUtil.historicalPeriods[j].equals(graphBean.getPeriod()) )
                        out.println("<OPTION SELECTED>" + graphBean.getPeriod());
                      else
                        out.println("<OPTION>" + com.cannontech.util.ServletUtil.historicalPeriods[j]);
                    }%>
                </select>
              </td>
              <td width="75">
                <div align="left">
                  <input type="image" src="<%=request.getContextPath()%>/Images/Buttons/GoButton.gif" name="image" border="0">
                </div>
              </td>
            </form>
          </tr>
        </table>
      </div>
    </td>
    <td width="200" valign = "top" align= "center"> 
      <table width="200" border="0" class = "MainText" cellspacing = "4" height="16">
        <tr>
          <td width = "40%"> 
            <div name = "trend" align = "center" style = "border:solid 1px #666999; cursor:default;" onMouseOver = "menuAppear(event, 'trendMenu')" >Trend</div>
          </td>
          <td width = "40%"> 
            <div align = "center" style = "border:solid 1px #666999; cursor:default;" onMouseOver = "menuAppear(event, 'viewMenu')">View</div>
          </td>
          <td width="12%">
            <div align="right" class="Subtext">
			  <a href="JavaScript:" class="Link4" name="optionPopup" onClick="window.open('<%=request.getContextPath()%>/include/options_popup.jsp','optionPopup','width=200,height=160,top=250,left=520');">Options</a>            	                            
            </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<table width="575" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td>
      <div id="viewMenu" class = "bgmenu" style = "width:120px" align = "left"> 
        <div id = "LINEID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.LINE%>);">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.LINE_STRING%></div>
        <div id = "BARID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.BAR%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.BAR_STRING%></div>
        <div id = "3DBARID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.BAR_3D%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.BAR_3D_STRING%></div>
        <div id = "SHAPEID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.SHAPES_LINE%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.SHAPES_LINE_STRING%></div>
        <div id = "STEPID" name = "view" style = "width:120px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.STEP%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.STEP_STRING%></div>
        <div id = "TABULARID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.TABULAR%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.TABULAR_STRING%></div>
        <div id = "SUMMARYID" name = "view" style = "width:120px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.SUMMARY%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.SUMMARY_STRING%></div>
          <hr>
        <div id = "LDID" onmouseover = "changeOptionStyle(this)" style = "width:120px" class = "optmenu1" onclick = "changeLD()">&nbsp;&nbsp;&nbsp;Load Duration</div>
      </div>
      <form name="exportForm">
	    <div id="trendMenu" class = "bgmenu" style = "width:75px" align = "left"> 
          <%if (graphBean.getViewType() == GraphRenderers.TABULAR || graphBean.getViewType() == GraphRenderers.SUMMARY )
          {%>
            <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('csv')">&nbsp;&nbsp;&nbsp;Export .csv</div>
            <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('html')">&nbsp;&nbsp;&nbsp;Export .html</div>
          <%}
          else if (graphBean.getViewType() == GraphRenderers.SUMMARY )
          {%>
            <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('html')">&nbsp;&nbsp;&nbsp;Export .html</div>
          <%}
          else
          {%>
            <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('csv')">&nbsp;&nbsp;&nbsp;Export .csv</div>
            <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('png')">&nbsp;&nbsp;&nbsp;Export .png</div>
            <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('pdf')">&nbsp;&nbsp;&nbsp;Export .pdf</div>
            <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('jpeg')">&nbsp;&nbsp;&nbsp;Export .jpeg</div>
          <%}%>
            <div id = "PRINTID" name = "print" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "location='<%=request.getContextPath()%>/include/trending_print.jsp?';">&nbsp;&nbsp;&nbsp;Print</div>
        </div>
      </form>
    </td>
  </tr>
</table>
<!--END TRENDING OPTIONS-->