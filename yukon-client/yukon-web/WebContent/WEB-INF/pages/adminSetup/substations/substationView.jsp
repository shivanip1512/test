<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<cti:msgScope paths="yukon.web.modules.adminSetup.substationToRouteMapping">

<cti:url var="routeUrl" value="/admin/substations/routeMapping/viewRoute" />
<cti:url var="subUrl" value="/admin/substations/routeMapping/edit" />
<cti:url var="removeAllUrl" value="/admin/substations/routeMapping/removeAll">
    <cti:param name="removeAll" value=""/>
</cti:url>


<script type="text/javascript">
    function removeAllCheck() {
        if (confirm('Are you sure you want to delete all substation mappings?')) {
            window.location = '${removeAllUrl}';
        }
    }

</script>

<b>Substation Name</b>
<form name="subform" action="${subUrl}" method="post">
    <cti:csrfToken/>
        
    <table>
    
    <tr>
        <td class="vat">
            <select id="subSelectList" name="selection_list" size="10" style="width:200px" onclick='javascript:SubstationToRouteMappings_SelectListener("${routeUrl}")'>
                <c:forEach var="substation" items="${list}">
                    <option value="${substation.id}">
                        ${fn:escapeXml(substation.name)}
                    </option>
                </c:forEach>
            </select>
        </td>
        <td class="vab">
            <cti:button type="submit" nameKey="delete" name="remove" value="Delete"/>
            <br><br>
            <cti:button label="Delete All" onclick="removeAllCheck();"/>
        </td>
    </tr>
        
    <tr><td><div style="height:5px;"></div></td></tr>
        
    <tr>
        <td>
            Substation Name<br>
            <input type="text" value="" name="name" size="20" style="width:188px;"/>
        </td>
        <td class="vab">
            <cti:button id="addButton" type="submit" value="Add" name="add" nameKey="add"/>
            
            <c:if test="${hasVendorId}"> 
                
                <cti:url var="mspAddUrl" value="/admin/substations/routemapping/multispeak/choose"/>
                <cti:button id="mspAddButton" type="button" label="MSP" onclick="SubstationToRouteMappings_disableInputs(true);openSimpleDialog('mspAddDialog', '${mspAddUrl}', 'Choose Substations');"/>
            </c:if>
    
        </td>
    </tr>
    
    </table>
        
</form>

<tags:simpleDialog id="mspAddDialog" onClose="SubstationToRouteMappings_disableInputs(false);"/>
</cti:msgScope>