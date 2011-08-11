<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="adminSetup" page="encryption">

    <script type="text/javascript">
    document.observe("dom:loaded", function(){
        
        <c:forEach var="route" items="${encryptedRoutes}">
        
            if(${route.encrypted}) {
                showEnabled('${route.paobjectId}')
            } else {
                showDisabled('${route.paobjectId}');
            }
            
        </c:forEach>  
    });
    
        function cancel(from, formId) {
            if (from == "save") {
                showDisabled(formId);
            } else if (from == "delete") {
                showEnabled(formId);
            }
        }
        
        function showDisabled(formId) {
            var formChildren = $(formId).childElements();
            formChildren[2].hide();
            formChildren[0].show();
        }
        
        function showEnabled(formId) {
            var formChildren = $(formId).childElements();
            formChildren[3].hide();
            formChildren[1].show();
        }
        
        function showAddKey(formId) {
            var formChildren = $(formId).childElements();
            formChildren[0].hide();
            formChildren[2].show();
        }
        
        function addKey(formId) {
            Yukon.ui.blockPage();
            $(formId).action = "saveKey"
            $(formId).submit();
        }
        
        function showDeleteConfirm(formId) {
            var formChildren = $(formId).childElements();
            formChildren[1].hide();
            formChildren[3].show();
        }
        
        function confirmDelete(formId) {
            $(formId).reset();
            Yukon.ui.blockPage();
            $(formId).action = "deleteKey"
            $(formId).submit();
        }
    </script>
    
    
    <table class="resultsTable">
        <tr>
            <th><i:inline key=".paoNameLbl"/></th>
            <th><i:inline key=".paoTypeLbl"/></th>
            <th><i:inline key=".CPSkeyLbl"/></th>
        </tr>
        
        <c:forEach var="route" items="${encryptedRoutes}">
            <tr>
                <td>${route.paoName}</td>
                <td>${route.type}</td>
                <td width="50%">
                    <form id="${route.paobjectId}"  method="POST" autocomplete="off">
                        <span style="display:none">
                            <cti:button key="enableEncryptionBtn" onclick="javascript:showAddKey('${route.paobjectId}')"/>
                        </span>
                        <span style="display:none">
                            <cti:button  key="deleteBtn" onclick="javascript:showDeleteConfirm('${route.paobjectId}')"/>
                            <i:inline key=".keyFoundLbl"/>
                        </span>
                        <span style="display:none">
                            <i:inline key=".keyLbl"/>
                            <input name="value" type="text" size ="50"/>
                            <cti:button key="saveBtn"  onclick="javascript:addKey('${route.paobjectId}')"/>
                            <cti:button key="cancelBtn" onclick="javascript:cancel('save','${route.paobjectId}')"/>
                        </span>
                        <span style="display:none">
                            <i:inline key=".confirmDeleteMsg"/>
                            <cti:button key="confirmDeleteBtn" onclick="javascript:confirmDelete('${route.paobjectId}')"/>
                            <cti:button key="cancelBtn" onclick="javascript:cancel('delete','${route.paobjectId}')"/>
                        </span>
                        <input name="paobjectId" type="hidden" value="${route.paobjectId}"/>
                        <input name="type" type="hidden" value="${route.type}"/>
                        <input name="paoName" type="hidden" value="${route.paoName}"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty encryptedRoutes}">
            <tr>
                <td colspan="4">
                    <i:inline key=".noRoutesMsg"/>
                </td>
            </tr>
        </c:if>
        </table>
</cti:standardPage>
