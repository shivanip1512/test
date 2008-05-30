<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="org.springframework.web.bind.ServletRequestUtils"%>

<%@ page import="com.cannontech.cbc.dao.*"%>
<%@ page import="com.cannontech.cbc.model.*"%>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>

<%@page import="com.cannontech.core.dao.YukonUserDao"%>
<%@page import="com.cannontech.core.dao.AuthDao"%>
<%@page import="com.cannontech.core.dao.RoleDao"%>

<%@page import="com.cannontech.database.data.lite.LiteYukonUser"%>
<%@page import="com.cannontech.util.ServletUtil"%>
<%@page import="com.cannontech.yukon.cbc.StreamableCapObject"%>
<%@page import="com.cannontech.database.data.lite.LiteYukonRoleProperty"%>
<%@page import="com.cannontech.roles.capcontrol.CBCSettingsRole"%>

<!-- Layout CSS files -->
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/CannonStyle.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/StandardStyles.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/YukonGeneralStyles.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/InnerStyles.css" >
<link rel="stylesheet" type="text/css" href="/capcontrol/css/base.css" >

<!-- Consolidated Script Files -->
<script type="text/javascript" src="/JavaScript/prototype.js" ></script>

<c:url var="commentsURL" value="/spring/capcontrol/comments?action="/>

<%
    FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
    CapControlCommentDao commentDao = YukonSpringHook.getBean("capCommentDao", CapControlCommentDao.class);
    YukonUserDao yukonUserDao = YukonSpringHook.getBean("yukonUserDao", YukonUserDao.class);
    AuthDao authDao = YukonSpringHook.getBean("authDao", AuthDao.class);
    RoleDao roleDao = YukonSpringHook.getBean("roleDao", RoleDao.class);
    
    LiteYukonUser user = ServletUtil.getYukonUser(request);
    CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);

    //Coming into this page, we get the paoId of the capbank.
    int paoId = ServletRequestUtils.getIntParameter(request, "paoID", -1);
    
    StreamableCapObject capObject = filterCapControlCache.getCapControlPAO(paoId);
    if (capObject == null) {
        String location = ServletUtil.createSafeUrl(request, "/capcontrol/invalidAccessErrorPage.jsp");
        response.sendRedirect(location);
        return;
    }
    
    String name = capObject.getCcName();
    List<CapControlComment> comments = commentDao.getAllCommentsByPao(paoId);
    
    SimpleDateFormat formatter = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss");
    
    //role property   'database editing' determines this.
    LiteYukonRoleProperty modifyProp = roleDao.getRoleProperty(CBCSettingsRole.MODIFY_COMMENTS);
    LiteYukonRoleProperty addProp = roleDao.getRoleProperty(CBCSettingsRole.ADD_COMMENTS);

    String modValue = authDao.getRolePropertyValue(user, modifyProp.getRolePropertyID() );
    boolean modifyPermission = Boolean.parseBoolean(modValue);
    
    String addValue2 = authDao.getRolePropertyValue(user, addProp.getRolePropertyID() );
    boolean addPermission = Boolean.parseBoolean(addValue2);
%>

<c:set var="paoId" value="<%=paoId%>"/>
<c:set var="modifyPermission" value="<%=modifyPermission%>"/>
<c:set var="addPermission" value="<%=addPermission%>"/>

<script type="text/javascript"> 

function addComment(commentText) {
    var url = '${commentsURL}' + 'add';
    var parameters = { 'paoId': ${paoId}, 'comment': commentText };
    executeUrl(url, parameters);  
}

function updateComment(commentId, commentText) {
    var url = '${commentsURL}' + 'update';
    var parameters = { 'commentId': commentId, 'comment': commentText };
    executeUrl(url, parameters); 
}

function removeComment(commentId) {
    var url = '${commentsURL}' + 'remove';
    var parameters = { 'commentId': commentId };
    executeUrl(url, parameters);
}

function executeUrl(url, parameters) {
    new Ajax.Request(url, {
        'method': 'POST',
        'parameters': parameters,
        'onSuccess': function() {
            window.location.reload();    
        }
    });
}

function selectComment(commentId, inputElement) {
    $('updateCommentId').value = commentId;
    $('updateCommentInput').value = inputElement.value;
    $('updateCommentDiv').show();
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

<div>
    <cti:titledContainer title="<%=name%>" >
        <table id="innerTable" width="98%" border="0" cellspacing="0" cellpadding="0">
            <tr class="columnHeader lAlign">
                <td width="7%">Edit</td>
                <td>Comment</td>
                <td>By User</td>
                <td>Time</td>
                <td>Altered</td>
            </tr>
            <tr id="addCommentRow" >
                <td>
                    <c:choose>
                        <c:when test="${addPermission}">
                            <img src="/editor/images/edit_item.gif" border="0" height="15" width="15" onclick="$('addCommentDiv').show();unHighlightAllRows();"/>
                        </c:when>
                        <c:otherwise>
                            <img src="/editor/images/edit_item_gray.gif" border="0" height="15" width="15" onclick=""/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>Click the edit button to add or edit a comment.</td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <!-- Loops for each comment here. -->
            <%
                for (CapControlComment comment : comments ){
            %>
                    <c:set var="comment" value="<%=comment%>"/>
                    <c:set var="commentId" value="${comment.id}"/>
                    
                    <tr id="commentRow_${commentId}" class="<tags:alternateRow odd="altRow" even=""/>">
                        <td>
                            <c:choose>
                                <c:when test="${modifyPermission}">
                                    <input type="hidden" id="commentInput_${commentId}" value="${comment.comment}"></input>
                                    <img src="/editor/images/edit_item.gif" border="0" height="15" width="15"  onclick="selectComment(${commentId}, $('commentInput_${commentId}'));highlightRow('commentRow_${commentId}');"/>
                                    <img src="/editor/images/delete_item.gif" border="0" height="15" width="15" onclick="removeComment(${commentId});" />
                                </c:when>
                                <c:otherwise>
                                    <img src="/editor/images/edit_item_gray.gif" border="0" height="15" width="15"  onclick=""/>
                                    <img src="/editor/images/delete_item_gray.gif" border="0" height="15" width="15" onclick="" />                          
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${comment.comment}</td>   
                        <td><%=yukonUserDao.getLiteYukonUser(comment.getUserId()).getUsername() %></td>
                        <td><%= formatter.format(comment.getTime()) %></td>
                        <td>
                            <c:choose>
                                <c:when test="${comment.altered}">Yes</c:when>
                                <c:otherwise>No</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <% } %>    
        </table>
    </cti:titledContainer>
            
    <br>
            
    <div id="addCommentDiv" style="width: auto; display: none;">
        <cti:titledContainer title="Add Comment">
            <input id="addCommentInput" type="text" value=""></input>
            <input type="button" value="Add Comment" onclick="addComment($('addCommentInput').value);"></input>
        </cti:titledContainer>    
    </div>
            
    <div id="updateCommentDiv" style="width: auto; display: none;">
        <cti:titledContainer title="Update Comment">
            <input id="updateCommentInput" type="text" value=""></input>
            <input id="updateCommentId" type="hidden" value=""></input>
            <input type="button" value="Update Comment" onclick="updateComment($('updateCommentId').value, $('updateCommentInput').value);"></input>
        </cti:titledContainer>         
    </div>
</div>