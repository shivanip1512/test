<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="../Consumer/include/StarsHeader.jsp" %>

<jsp:useBean id="woManipulationBean" class="com.cannontech.stars.web.bean.ManipulationBean" scope="session"/>
<jsp:useBean id="workOrderBean" class="com.cannontech.web.bean.WorkOrderBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">

    <!-- ACTION TYPES (will be accessible with JSTL tags after they are declared) -->
    <%pageContext.setAttribute("changeServiceCompany", new Integer(ServletUtils.ACTION_TOSERVICECOMPANY).toString());%>
    <%pageContext.setAttribute("changeServiceType", new Integer(ServletUtils.ACTION_CHANGE_WO_SERVICE_TYPE).toString());%>
    <%pageContext.setAttribute("changeServiceStatus", new Integer(ServletUtils.ACTION_CHANGE_WO_SERVICE_STATUS).toString());%>

    <cti:includeCss link="/include/PurpleStyles.css"/>
    <div class="headerbar">
        <%@ include file="include/HeaderBar.jspf" %>
    </div>
    <br clear="all"> 
     
    <%pageContext.setAttribute("liteEC",liteEC);%>
    <c:set target="${woManipulationBean}" property="energyCompany" value="${liteEC}" />
    <%pageContext.setAttribute("currentUser", lYukonUser);%>
    <c:set target="${woManipulationBean}" property="currentUser" value="${currentUser}" />
    <%pageContext.setAttribute("actionFilterListName", YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY);%>
    <c:set target="${woManipulationBean}" property="actionFilterListName" value="${actionFilterListName}" />

    <div class="standardpurplesidebox"> 
        <% String pageName = "ChangeWorkOrders.jsp"; %>
        <%@ include file="include/Nav.jspf" %>
    </div>

    <div class="standardcentralwhitebody">
        <div align="center"> <br>
            <% String header = "APPLY ACTIONS TO WORK ORDERS"; %>
            <%@ include file="include/SearchBar.jspf" %>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            <br clear="all">
        </div>
        
        <form name="MForm" method="post" action="<cti:url value="/servlet/WorkOrderManager"/>" onsubmit="prepareSubmit(this)">
            <cti:csrfToken/>
            <input type="hidden" name="action" value="ApplyActions">
            <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                      <td width="55" class="HeaderCell">Actions</td>
                       <td width="85"> 
                    </td>
                    <td width="200"> 
                        <select name="actionType" size="1" style="width: 200px" onChange="changeActionType(this.value)" >
                            <option value="<c:out value="${changeServiceStatus}"/>"> Change Service Status </option>
                            <option value="<c:out value="${changeServiceType}"/>"> Change Service Type</option>
                            <option value="<c:out value="${changeServiceCompany}"/>"> Move to Service Company </option>
                        </select>
                    </td>
                    <td width="240"> 
                        <div id='<c:out value="${changeServiceCompany}"/>' style="display:none" > 
                            <select id='<c:out value="${changeServiceCompany}"/>1' name='<c:out value="${changeServiceCompany}"/>1' size="1" style="width: 200px" onChange="selectAction(this.value)">
                                <c:forEach var="serviceCompany" items="${woManipulationBean.availableServiceCompanies}">
                                    <option value='<c:out value="${serviceCompany.liteID}"/>'> <c:out value="${fn:escapeXml(serviceCompany.companyName)}"/> </option>
                                </c:forEach>
                              </select>
                        </div>
                        <div id='<c:out value="${changeServiceType}"/>' style="display:none"> 
                            <select id='<c:out value="${changeServiceType}"/>1' name='<c:out value="${changeServiceType}"/>1' size="1" style="width: 200px" onChange="selectAction(this.value)">
                                <c:forEach var="serviceType" items="${woManipulationBean.availableServiceTypes.yukonListEntries}">
                                    <option value='<c:out value="${serviceType.entryID}"/>'> <c:out value="${fn:escapeXml(serviceType.entryText)}"/> </option>
                                </c:forEach>
                              </select>
                        </div>
                        <div id='<c:out value="${changeServiceStatus}"/>' style="display:true"> 
                            <select id='<c:out value="${changeServiceStatus}"/>1' name='<c:out value="${changeServiceStatus}"/>1' size="1" style="width: 200px" onChange="selectAction(this.value)">
                                <c:forEach var="serviceStatus" items="${woManipulationBean.availableServiceStatuses.yukonListEntries}">
                                    <option value='<c:out value="${serviceStatus.entryID}"/>'> <c:out value="${fn:escapeXml(serviceStatus.entryText)}"/> </option>
                                </c:forEach>
                            </select>
                        </div>
                    </td>
                   </tr>
            </table>
            <br>
            <div align="center"> 
                <input type="button" name="Save" value="Add Action to List" onclick="saveEntry(this.form)">
            </div>
            <br>
            <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                      <td width="55" class="HeaderCell">Current Actions</td>
                    <td width="285"> 
                        <select name="AssignedActions" size="7" style="width: 293px">
                        </select>
                    </td>
                    <td width="240"> 
                        <input type="button" name="Remove" value="Remove" style="width:80" onclick="deleteEntry(this.form)">
                        <br>
                        <input type="button" name="RemoveAll" value="Remove All" style="width:80" onclick="deleteAllEntries(this.form)">
                    </td>
                   </tr>
            </table>
            <br>
            <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                    <td width="320" class="headeremphasis"> 
                        <c:out value="${workOrderBean.numberOfRecords}"/>&nbsp;Work Orders Selected
                      </td>
                      <td width="205"> 
                        <input type="reset" name="Reset" value="Reset" onclick="location.reload()">
                      </td>
                      <td width="75" align="right"> 
                        <input type="button" name="Back" value="Back" onclick="location.href='WorkOrder.jsp'">
                      </td>
                  </tr>
                  <tr>
                    <td width="100%" class="headeremphasis">  
                        <input type="submit" name="Submit" value="Apply to Selected Work Orders">
                    </td>
                </tr>
            </table>
            <br>
        </form>
    </div>
    <script language="JavaScript">
        var actionTexts = new Array();
        var selectionIDs = new Array();
        var actionTypeIDs = new Array();
        var curIdx = 0;
        var selectedActionType = "0";
        var selectedAction = ""
        var selectedActionID = "0";
        
        function init()
        {
            selectedActionType = '<c:out value="${changeServiceStatus}"/>';
            selectedAction = "Change Service Status: ";
            selectedAction += '<c:out value="${woManipulationBean.defaultActionSelection.entryText}"/>';
            selectedActionID = '<c:out value="${woManipulationBean.defaultActionSelection.entryID}"/>';
        }
        
        function changeActionType(action) 
        {
            selectedActionType = action;
            var type = document.MForm.actionType;
            document.getElementById('<c:out value="${changeServiceCompany}"/>').style.display = "none";
            document.getElementById('<c:out value="${changeServiceType}"/>').style.display = "none";
            document.getElementById('<c:out value="${changeServiceStatus}"/>').style.display = "none";
             document.getElementById(action).style.display = "";
            action += 1;
            selectedAction = type.options[type.selectedIndex].text;  
            selectedAction += ": ";
            selectedAction += document.getElementById(action).options[0].text;
            selectedActionID = document.getElementById(action).options[0].value;                        
        }
        
        function selectAction(actionID)
        {
            selectedActionID = actionID;
            var type = document.MForm.actionType;
            var actionBy = selectedActionType;
            actionBy += 1;
            var action = document.getElementById(actionBy);
            selectedAction = type.options[type.selectedIndex].text;  
            selectedAction += ": ";
            selectedAction += action.options[action.selectedIndex].text;
        }
        
        function saveEntry(form) 
        {
            var actions = form.AssignedActions;
            var oOption = document.createElement("OPTION");
            oOption.text = selectedAction;
            oOption.value = selectedAction;
            actions.options.add(oOption, curIdx);
            actions.selectedIndex = curIdx;
            actionTexts[curIdx] = selectedAction;
            selectionIDs[curIdx] = selectedActionID;
            actionTypeIDs[curIdx] = selectedActionType;
            curIdx = actionTexts.length;

            
        }
        
        function deleteEntry(form) 
        {
            var actions = form.AssignedActions;
            var idx = actions.selectedIndex;
            if (idx >= 0 && idx < actions.options.length) 
            {
                actions.options.remove(idx);
                actionTexts.splice(idx, 1);
                selectionIDs.splice(idx, 1);
                actionTypeIDs.splice(idx, 1);
                actions.selectedIndex = actions.options.length;
                
            }
            
            curIdx = actionTexts.length;
        }
        
        function deleteAllEntries(form) 
        {
            var actions = form.AssignedActions;
            if (actions.options.length > 1) 
            {
                if (!confirm("Are you sure you want to remove all actions?")) return;
                for (idx = actions.options.length; idx >= 0; idx--)
                    actions.options.remove(idx);
                actionTexts.splice(0, actionTexts.length);
                selectionIDs.splice(idx, selectionIDs.length);
                actionTypeIDs.splice(idx, actionTypeIDs.length);
                actions.selectedIndex = 0;
                
            }
            
            curIdx = actionTexts.length;
        }
        
        function prepareSubmit(form) 
        {
            if(actionTexts.length < 1)
            {
                if(!confirm("You have not defined any Changes!")) return;
            }
            else
            {
                for (idx = 0; idx < actionTexts.length; idx++) 
                {
                    var html = '<input type="hidden" name="SelectionIDs" value="' + selectionIDs[idx] + '">';
                    form.insertAdjacentHTML("beforeEnd", html);
                    html = '<input type="hidden" name="ActionTexts" value="' + actionTexts[idx] + '">';
                    form.insertAdjacentHTML("beforeEnd", html);
                    html = '<input type="hidden" name="ActionTypeIDs" value="' + actionTypeIDs[idx] + '">';
                    form.insertAdjacentHTML("beforeEnd", html);
                }
            }
        }
    </script>
</cti:standardPage>