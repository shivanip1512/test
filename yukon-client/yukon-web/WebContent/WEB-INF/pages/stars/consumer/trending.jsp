<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.graph.GraphBean" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="consumer" page="trending">
<cti:standardMenu/>

<script language="JavaScript">
    function MM_reloadPage(init) {  //reloads the window if Nav4 resized
        if (init==true) with (navigator) {
            if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
                document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; 
            }
        } else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) {
            location.reload();
        }
    }
    MM_reloadPage(true);
</script>

<%
    SimpleDateFormat datePart = new SimpleDateFormat("MM/dd/yy");
    GraphBean graphBean = (GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
%>

    <%@ include file="/include/trending_functions.jspf" %>

    <h3><cti:msg key="yukon.dr.consumer.trending.header" /></h3>
    
    <table width="760" border="0" cellspacing="0" cellpadding="0">
  
        <tr>
            <td>
                <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
                    <tr> 
                        <td width="657" valign="top" bgcolor="#FFFFFF" >
          
                            <table width="575" border="0" align="center" cellpadding="4" cellspacing="0">
                              <tr>
                                <td width="303" valign="top">
                                  <div>
                                        <form id=MForm method="POST" action="<%= request.getContextPath() %>/servlet/GraphGenerator"  name="MForm">
                                          <INPUT TYPE="hidden" NAME="gdefid" VALUE="<%=graphBean.getGdefid()%>">
                                          <INPUT TYPE="hidden" NAME="view" VALUE="<%=graphBean.getViewType()%>">
                                          <INPUT TYPE="hidden" NAME="option" VALUE = "<%=graphBean.getOption()%>" >
                                          <INPUT TYPE="hidden" NAME="action" VALUE = "updateOptions" >
                                          <input type="hidden" name="REDIRECT" value="/spring/stars/consumer/trending/view?gdefid=<%=graphBean.getGdefid()%>">
                                          <input type="hidden" name="REFERRER" value="/spring/stars/consumer/trending/view?gdefid=<%= graphBean.getGdefid()%>">
                                          
<%
    boolean isEvent = com.cannontech.util.ServletUtil.EVENT.equals(graphBean.getPeriod());

    String spanStyle = "none";
    java.util.Date startDate = graphBean.getStartDate();
    if(isEvent){
        spanStyle = "";
        startDate = graphBean.getStopDate();
    }

%>
                                    <table width="450" border="0" cellspacing="2" cellpadding="0">
                                      <tr>
                                          <td width="225" valign="top" class="SubText">
                                          	<div class="fl" style="margin: 5px 5px 0 0">
                                          		Start Date:
                                          	</div>
                                            <dt:date id="cal" name="start" value="<%= startDate %>" />
                                          </td>
                                          <td width="225" valign="top" class="SubText">
                                          	<div class="fl" style="margin-right: 5px;">
                                          	Time Period:
                                            <select id="period" name="period" onchange="changeEvent()">
<% /* Fill in the period drop down and attempt to match the current period with one of the options */                           
    for( String period : com.cannontech.util.ServletUtil.historicalPeriods) {
        String selected = "";
        if( period.equals(graphBean.getPeriod()) ){
            selected = "selected";
        }
        out.println("<option value=\"" + period + "\"" + selected + ">" + period + "</option>");
    }
%>
                                    </select><br />
                                    <span id="eventSpan" style="display:<%=spanStyle%>">Number of Events:
                                      <select name="events">
<%
    int events = graphBean.getNumberOfEvents();
    for(int i = 1; i <= 50; i++) {
        String selected = (i == events) ? " selected" : "";                                    
        out.println("<option value=\"" + i + "\"" + selected + ">" + i + "</option>");
    }
%>
                                              </select>
                                              </span>
                                              </div>
                                              <input type="image" src="/WebConfig/yukon/Buttons/GoButton.gif" name="image" border="0" class="fl">
                                          </td>
                                      </tr>
                                    </table>
                                        </form>
                                  </div>
                                </td>
                                <td width="200" valign = "top" align= "center"> 
                                  <table width="200" border="0" class = "MainText" cellspacing = "4" height="16">
                                    <tr>
                                      <td width = "40%"> 
                                        <div name="trend" align="center" style="border:solid 1px #666999; cursor:default;" onMouseOver="menuAppear(event, 'trendMenu')" >Trend</div>
                                      </td>
                                      <td width = "40%"> 
                                        <div align = "center" style = "border:solid 1px #666999; cursor:default;" onMouseOver = "menuAppear(event, 'viewMenu')">View</div>
                                      </td>
                                      <td width="12%">
                                        <div align="right" class="Subtext">
                                          <a href="JavaScript:" class="Link4" name="optionPopup" onClick="window.open('/include/options_popup.jsp','optionPopup','width=200,height=160,top=250,left=520');">Options</a>
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
                                  <div id="viewMenu" class = "bgmenu" style = "width:130px" align = "left"> 
                                    <div id = "DEFAULTID" name = "view"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.DEFAULT%>);">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.DEFAULT_STRING%></div>
                                    <div id = "LINEID" name = "view"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.LINE%>);">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.LINE_STRING%></div>
                                    <div id = "LINESHAPESID" name = "view"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.LINE_SHAPES%>);">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.LINE_SHAPES_STRING%></div>
                                    <div id = "LINEAREAID" name = "view"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.LINE_AREA%>);">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.LINE_AREA_STRING%></div>
                                    <div id = "LINEAREASHAPESID" name = "view"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.LINE_AREA_SHAPES%>);">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.LINE_AREA_SHAPES_STRING%></div>
                                    <div id = "STEPID" name = "view" style = "width:130px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.STEP%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.STEP_STRING%></div>
                                    <div id = "STEPSHAPESID" name = "view" style = "width:130px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.STEP_SHAPES%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.STEP_SHAPES_STRING%></div>
                                    <div id = "STEPAREAID" name = "view" style = "width:130px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.STEP_AREA%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.STEP_AREA_STRING%></div>
                                    <div id = "STEPAREASHAPESID" name = "view" style = "width:130px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.STEP_AREA_SHAPES%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.STEP_AREA_SHAPES_STRING%></div>
                                    <div id = "BARID" name = "view"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.BAR%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.BAR_STRING%></div>
                                    <div id = "3DBARID" name = "view"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.BAR_3D%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.BAR_3D_STRING%></div>
                                    <div id = "TABULARID" name = "view"  style = "width:130px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.TABULAR%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.TABULAR_STRING%></div>
                                    <div id = "SUMMARYID" name = "view" style = "width:130px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(<%=GraphRenderers.SUMMARY%>)">&nbsp;&nbsp;&nbsp;<%=GraphRenderers.SUMMARY_STRING%></div>
                                      <hr>
                                    <div id = "LDID" onmouseover = "changeOptionStyle(this)" style = "width:130px" class = "optmenu1" onclick = "changeLD()">&nbsp;&nbsp;&nbsp;Load Duration</div>
                                  </div>
                                  <form name="exportForm">
                                    <div id="trendMenu" class = "bgmenu" style = "width:75px" align = "left"> 
<%
    if (graphBean.getViewType() == GraphRenderers.TABULAR || graphBean.getViewType() == GraphRenderers.SUMMARY ) {
%>
                                        <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('csv')">&nbsp;&nbsp;&nbsp;Export .csv</div>
                                        <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('html')">&nbsp;&nbsp;&nbsp;Export .html</div>
<%
    } else if (graphBean.getViewType() == GraphRenderers.SUMMARY ) {
%>
                                        <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('html')">&nbsp;&nbsp;&nbsp;Export .html</div>
<%
    } else {
%>
                                        <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('csv')">&nbsp;&nbsp;&nbsp;Export .csv</div>
                                        <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('png')">&nbsp;&nbsp;&nbsp;Export .png</div>
                                        <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('pdf')">&nbsp;&nbsp;&nbsp;Export .pdf</div>
                                        <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "exportData('jpeg')">&nbsp;&nbsp;&nbsp;Export .jpeg</div>
<%  
    }
%>
                                        <div id = "PRINTID" name = "print" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "location='/include/trending_print.jsp?';">&nbsp;&nbsp;&nbsp;Print</div>
                                    </div>
                                  </form>
                                </td>
                              </tr>
                            </table>
          
                            <table width="575" border="0" cellspacing="0" cellpadding="0" align="center" height="46">
                                <tr> 
                                    <td>
                                        <hr>
                                        <center>
                                        
                                            <c:choose>
                                                <c:when test="${viewType == 'SUMMARY'}">
                                                    ${graphHtml}
                                                </c:when>
                                                <c:when test="${viewType == 'TABULAR'}">
                                                    <%@ include file="/include/trending_tabular.jspf" %>                    
                                                </c:when>
                                                <c:otherwise>
                                                    <img id="theGraph" src="/servlet/GraphGenerator?action=EncodeGraph" >
                                                </c:otherwise>
                                            </c:choose>

                                        </center>
                                    </td>
                                </tr>
                            </table>
                            <br>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>

</cti:standardPage>
