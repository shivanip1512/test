<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.InventoryBean" %>

<jsp:useBean id="configBean" class="com.cannontech.stars.web.bean.ConfigBean" scope="page"/>
<jsp:useBean id="resultSetBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>
<jsp:setProperty name="resultSetBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>

<% if (request.getParameter("page") == null) { %>
    <%-- intialize bean properties --%>
    <jsp:setProperty name="resultSetBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>"/>
    <jsp:setProperty name="resultSetBean" property="htmlStyle" value="<%= InventoryBean.HTML_STYLE_INVENTORY_SET %>"/>
    <jsp:setProperty name="resultSetBean" property="page" value="1"/>
    <jsp:setProperty name="resultSetBean" property="referer" value="<%= session.getAttribute(ServletUtils.ATT_REFERRER) %>"/>
<% } %>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<%pageContext.setAttribute("currentUser", lYukonUser);%>
	<c:set target="${configBean}" property="currentUser" value="${currentUser}" />
	
	<cti:includeCss link="/include/PurpleStyles.css"/>
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jspf" %>
	</div>
 	<br clear="all"> 
 	                                      
	<div class="standardpurplesidebox"> 
	</div>
                            
	<div class="standardcentralwhitebody">
		<div align="center"> <br>
           
    	</div>
    	   `
            
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager">
            <cti:csrfToken/>
	    	<input type="hidden" name="action" value="StaticLoadGroupMap">
	    	<input type="hidden" name="actionTask" value="StaticLoadGroupMapMasterReset">
	    	
            <div align="center">
                <c:choose>
                    <c:when test="${configBean.writeToFileAllowed && configBean.hasResetPermission}"> 
                        <td> 
                            <span class="ConfirmMsg">This will set any address group mappings in the database that currently</span>
                            <br>
                            <span class="ConfirmMsg">have no addressing group assigned to the defaults specified in the static load group mapping table.</span>
                        </td> 
                    </c:when>
                    <c:otherwise>   
                        <span class="ErrorMsg"> You have reached this page as a result of incorrect navigation.  Please click Back on your browser.</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <table width="80%" border="2" cellspacing="0" cellpadding="0" align="center" class="TableCell">
                <tr>
                    <c:choose>
                        <c:when test="${configBean.writeToFileAllowed && configBean.hasResetPermission}"> 
                            <td> 
                                <div align="center"> 
                                    <input type="button" name="DefaultAndConfig" value="Set Unassigned to Default Groups" onclick="setDefaults(this.form)">
                                </div>
                                <div align="center"> 
                                    <input type="button" name="DefaultJustSave" value="Set Unassigned and Save Only (No Configs Sent)" onclick="setDefaultsNoConfig(this.form)">
                                </div>
                            </td> 
                        </c:when>
                        <c:otherwise>   
                            
                        </c:otherwise>
                    </c:choose>
                </tr>
            </table>
            <br>
            <br>
            <c:choose>
                <c:when test="${configBean.writeToFileAllowed && configBean.hasResetPermission}"> 
                    <div align="center">
                        <span class="ErrorMsg">**The actions on this page have the potential to seize maximum resources of this system until completion.</span>
                        <span class="ErrorMsg">  It may cause visible slowness or even lack of functionality for any users logged in.</span>
                        <span class="ErrorMsg">  It is recommended that these actions only be performed during non-business hours.</span>
                    </div>
                </c:when>
                <c:otherwise>   
                    
                </c:otherwise>
            </c:choose>
            <br>
            <br>
	    	<div align="center">
				<c:choose>
					<c:when test="${configBean.writeToFileAllowed && configBean.hasResetPermission}"> 
					 	<td> 
		                	<span class="ConfirmMsg">This will reset all address group mappings in this database to the defaults</span>
		                	<br>
		                	<span class="ConfirmMsg">specified in the static load group mapping table.</span>
	                    </td> 
                    </c:when>
                    <c:otherwise> 	
	                	<span class="ErrorMsg">	You have reached this page as a result of incorrect navigation.  Please click Back on your browser.</span>
		            </c:otherwise>
			    </c:choose>
			</div>
	    	<table width="80%" border="2" cellspacing="0" cellpadding="0" align="center" class="TableCell">
			  	<tr>
					<c:choose>
						<c:when test="${configBean.writeToFileAllowed && configBean.hasResetPermission}"> 
						 	<td> 
			                  <div align="center"> 
		                        	<input type="submit" name="Reset" value="Reset All Now">
		                      </div>
                              <div align="center"> 
                                    <input type="button" name="ResetJustSave" value="Reset and Save Only (No Config Sent)" onclick="resetAllNoConfig(this.form)">
                              </div>
		                    </td> 
	                    </c:when>
	                    <c:otherwise> 	
		                    
			            </c:otherwise>
			    	</c:choose>
				</tr>
 			</table>
        	<br>
            <br>
            <br>
            <table width="80%" border="2" cellspacing="0" cellpadding="0" align="center" class="TableCell">
                <tr>
                    <c:choose>
                        <c:when test="${configBean.writeToFileAllowed && configBean.hasResetPermission}"> 
                            <td> 
                                <span class="SubtitleHeader">STATUS MESSAGES</span> 
                                <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
                                <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
                                <%
                                ArrayList inventorySet = (ArrayList) session.getAttribute(InventoryManagerUtil.INVENTORY_SET);
                                if (inventorySet != null) {
                                    resultSetBean.setInventorySet(inventorySet);
                                %>
                                <% } %>
                            </td> 
                        </c:when>
                        <c:otherwise>   
                            
                        </c:otherwise>
                    </c:choose>
                </tr>
            </table>
        </form>
    </div>
    
    <script language="JavaScript">
		function init()
		{
		}
        
        function setDefaults(form)
        {
            form.actionTask.value="StaticLoadGroupMapSetDefaults";
            form.submit();
        }
        
        function setDefaultsNoConfig(form)
        {
            form.actionTask.value="StaticLoadGroupMapSetDefaultsNoConfig";
            form.submit();
        }
        
        function resetAllNoConfig(form)
        {
            form.actionTask.value="StaticLoadGroupMapMasterResetNoConfig";
            form.submit();
        }
		
	</script>
</cti:standardPage>          
