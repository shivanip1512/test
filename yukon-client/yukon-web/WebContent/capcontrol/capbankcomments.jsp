<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.cbc.dao.*"%>
<%@ page import="com.cannontech.cbc.model.*"%>
<%@ page import="com.cannontech.web.navigation.CtiNavObject" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>

<cti:standardPage title="Cap Bank Comments" module="capcontrol">
<cti:standardMenu/>

<c:url var="commentUpdatePage" value="/capcontrol/commentUpdate.jsp"/>

<%@include file="cbc_inc.jspf"%>

<%
    LiteYukonUser user = (LiteYukonUser) request.getSession(false).getAttribute(LoginController.YUKON_USER);
    FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
	CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);

	//Coming into this page, we get the paoId of the capbank.
	int paoId = ParamUtil.getInteger(request,"capbankID",-1);
	CapBankDevice bank = filterCapControlCache.getCapBankDevice(paoId);
	String name = bank.getCcName();
	
	CapControlCommentDao dao = YukonSpringHook.getBean("capCommentDao", CapControlCommentDao.class);
	YukonUserDao yukonUserDao = (YukonUserDao) YukonSpringHook.getBean("yukonUserDao");
	
	List<CapControlComment> comments = dao.getAllCommentsByPao(paoId);
	
	CtiNavObject nav = (CtiNavObject) request.getSession(false).getAttribute(ServletUtil.NAVIGATE);
	
	//Setup return URL (Need to set the returnURL when loading this page)
	String returnURL = ParamUtil.getString(request,"returnURL");
	
	//if param not set lets default to something plausible
	if( returnURL == null ){
		returnURL = nav.getPreviousPage();
		request.getParameterMap().put("returnURL",returnURL);
	}
	
	String currentURL = nav.getCurrentPage();
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat ("dd/MM/yyyy HH:mm:ss");
	
	boolean modifyPermission = false;
	boolean addPermission = false;
	//role property   'database editing' determines this.
	AuthDao authDao = YukonSpringHook.getBean("authDao", AuthDao.class);
	LiteYukonRoleProperty modifyProp = authDao.getRoleProperty(CBCSettingsRole.MODIFY_COMMENTS);
	LiteYukonRoleProperty addProp = authDao.getRoleProperty(CBCSettingsRole.ADD_COMMENTS);
	String modValue = authDao.getRolePropertyValue(user,modifyProp.getRolePropertyID() );
	String addValue2 = authDao.getRolePropertyValue(user,addProp.getRolePropertyID() );
	
	if( modValue.compareToIgnoreCase("true") == 0 ){		
		modifyPermission = true;
	}
	if( addValue2.compareToIgnoreCase("true") == 0 ){		
		addPermission = true;
	}
%>

<script type="text/javascript"> 

function showDiv( v ){
	$(v).toggle();
}

function updateUpdateField()
{
	var params = {'commentID': getSelectedComment(),'paoID': getPaoId()};
    new Ajax.Updater('ChangeDiv', '${commentUpdatePage}', {method: 'post', parameters: params});
}

function selectComment( id ){
	setCommentValue(id);
	updateUpdateField();
}

function getSelectedComment()
{
	var elem = $("selectedComment");
	return elem.value;
}

function getPaoId()
{
	var elem = $("paoID");
	return elem.value;
}

function setComment( c ){
	$("comment").value = $("commentTextBox").value;
}

function setDelete( i ){
	$("delete").value = i;
}

function setCommentValue( i )
{
	$("selectedComment").value = i;
}

function highlightRow( id ){
	unHighlightAllRows();
	$(id).style.backgroundColor = 'yellow';
}

function unHighlightAllRows(){
	var rows = $$('#innerTable tr');
	for( var i = 2; i < rows.length; i++){
		rows[i].style.backgroundColor = 'white';
	}
}

</script>

<cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="SubBus Areas" />
    <cti:crumbLink url="<%=returnURL%>" title="Return" />
</cti:breadCrumbs>

<form id="" action="/servlet/CBCServlet" method="post">
	<input type="hidden" name="redirectURL" value="<%=currentURL%>" id="redirectURL"/>
	<input type="hidden" name="selectedComment" value="-1" id="selectedComment"/>
	<input type="hidden" name="comment" id="comment" value=""/>
	<input type="hidden" name="paoID" value="<%=paoId%>" id="paoID"/>
	<input type="hidden" name="delete" value="-1" id="delete"/>
	
	<div style="margin-left: 10%; margin-right: 10%;" >
		
		<div id="DisplayDiv"></div>
			<cti:titledContainer title="<%=name%>" >
		
				<table id="innerTable" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr class="columnHeader lAlign">
						<% if ( modifyPermission || addPermission ) { %>
						<td>Edit</td>
						<% } %>
						<td>Comment</td>
						<td>By User</td>
						<td>Time</td>
						<td>Altered</td>
					</tr>
					<% if ( addPermission || modifyPermission) { %>
					<tr class="altTableCell" id="addCommentRow" >
						<td>
						
						<img src="/editor/images/edit_item.gif" border="0" height="15" width="15" onclick="selectComment(-1);unHighlightAllRows();"/>

						</td>
						<td>Click the edit button to add or edit a comment.</td>
						<td/>
						<td/>
						<td/>
					</tr>
						<% } %>
					<!-- Loops for each comment here. -->
					<%
					    for( CapControlComment c : comments ){
					    	boolean b = c.getAction().equals(CommentAction.USER_COMMENT.toString());
					    	String style = new String();
				    		if( b ) 
				    			style = "";
				    		else 
				    			style = "onelineTableCell";
					%>
					<tr id="commentRow_<%= c.getId() %>" class=<%=style %>>
						<% if ( modifyPermission || addPermission ) { %>
						<td>
							<% if ( b && modifyPermission) { %>
							<img src="/editor/images/edit_item.gif" border="0" height="15" width="15"  onclick="selectComment(<%=c.getId() %>);highlightRow('commentRow_<%= c.getId() %>');"/>
							<img src="/editor/images/delete_item.gif" border="0" height="15" width="15" onclick="setCommentValue(<%=c.getId() %>);setDelete(1);submit();" />
							<% } %>
						</td>
						<% } %>
						<td><%= c.getComment() %></td>
						<td><%= yukonUserDao.getLiteYukonUser(c.getUserId()).getUsername() %></td>
						<td><%= formatter.format(c.getTime()) %></td>
						<td><%= c.isAltered() %></td>
					</tr>
					<% } %>
				</table>
		    </cti:titledContainer>
	    <br>
	    
	    <div id="ChangeDiv" style="margin-left: 0%; margin-right: 55%;" ></div>
	
	</div>
</form>
</cti:standardPage>
