
function SubstationToRouteMappings_SelectListener(url) {
    var x = $("subSelectList").selectedIndex;
    var substationId = $("subSelectList").options[x].value;
    new Ajax.Updater($("route_element"), url, {"method": "post", "parameters": {"substationid": substationId, "view": ""}});
}
function SubstationToRouteMappings_addRoute() {
    var substationIndex = $("subSelectList").selectedIndex;
    if (substationIndex == -1) {
        return false;
    }
    var routeIndex = $("avRoutesSelectList").selectedIndex;
    var routeId = $("avRoutesSelectList").options[routeIndex].value;
    var routeName = $("avRoutesSelectList").options[routeIndex].text;
    $($("avRoutesSelectList").options[routeIndex]).remove();
    var length = $("routeIdSelectList").options.length;
    $("routeIdSelectList").options[length] = new Option(routeName, routeId);
}
function SubstationToRouteMappings_removeRoute() {
    var substationIndex = $("subSelectList").selectedIndex;
    if (substationIndex == -1) {
        return false;
    }
    var routeIndex = $("routeIdSelectList").selectedIndex;
    var route = $("routeIdSelectList").options[routeIndex];
    $("routeIdSelectList").options[routeIndex] = null;
    
    var length = $("avRoutesSelectList").options.length;
    $("avRoutesSelectList").options[length] = new Option(route.text, route.value);
}
function SubstationToRouteMappings_updateRoutes(url) {
    var substationIndex = $("subSelectList").selectedIndex;
    var substationId = $("subSelectList").options[substationIndex].value;
    var array = $("routeIdSelectList").options;
    var array2 = $A(array).pluck("value");
    var jsonObject = Object.toJSON(array2);
    new Ajax.Request(url, {"method": "post", "parameters": {"array": jsonObject, "substationid": substationId, "update": ""}});
}

function mspSubstations_check(doCheck) {
	$$('input[type=checkbox].mspSubstationCheckbox').each(function(c) {
		c.checked = doCheck;
	});
}

function SubstationToRouteMappings_disableInputs(doDisable) {
	$$('input').each(function(c) {
		if (doDisable) {
			c.disable();
		} else {
			c.enable();
		}
	});
}
