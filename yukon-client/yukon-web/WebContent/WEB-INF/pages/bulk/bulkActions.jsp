<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msg var="title" key="yukon.common.device.bulk.bulkAction.title" />
<cti:standardPage title="${title}" module="amr">
<cti:standardMenu/>

    <script type="text/javascript">
        
        var previousSelection = null;
        
        function showInputs(select) {
        
            if(previousSelection != null) {
                previousSelection.toggle();
            }
            
            var value = $F(select);
            
            var theDiv = $(value + 'Div');
            if(theDiv != null) {
                theDiv.toggle();
            }
            
            previousSelection = theDiv;
            
        
        }
    </script>

    <form method="post" action="/spring/bulk/action">
        <select name="action" onchange="showInputs(this)">
            <c:forEach var="action" items="${actions}">
                <option value="${action.action}"><cti:msg key="${action.displayKey}" /></option>
            </c:forEach>
        </select>
        <br><br>

	    <cti:deviceCollection deviceCollection="${deviceCollection}" />
	    
	    <div style="display: none;" id="changeTypeDiv">
            <cti:msg key="yukon.common.device.bulk.bulkAction.selectType" /><br>
            <select name="type">
	            <c:forEach var="type" items="${types}">
                    <option value="${type.id}">${type.name}</option>
                </c:forEach>
            </select>
	    </div>
	    
	    <div style="display: none;" id="updateRouteDiv">
            <cti:msg key="yukon.common.device.bulk.bulkAction.selectRoute" /><br>
            <select name="route">
	            <c:forEach var="route" items="${routes}">
                    <option value="${route.liteID}">${route.paoName}</option>
                </c:forEach>
            </select>
	    </div>
	    
	    <div style="display: none;" id="enableDisableDevicesDiv">
            <cti:msg key="yukon.common.device.bulk.bulkAction.enableDisable" /><br>
            <input type="radio" name="enable" value="true" checked >
                <cti:msg key="yukon.common.device.bulk.bulkAction.enable" />
            </input> 
            <input type="radio" name="enable" value="false">
                <cti:msg key="yukon.common.device.bulk.bulkAction.disable" />
            </input> 
	    </div>

        <br><br>
        <input type="submit" value="<cti:msg key="yukon.common.device.bulk.bulkAction.doAction" />" />
    </form>
    

</cti:standardPage>