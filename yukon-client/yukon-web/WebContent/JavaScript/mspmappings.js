
function SubstationToRouteMappings_SelectListener(url) {
    var x = $("subSelectList").selectedIndex;
    var substationId = $("subSelectList").options[x].value;
    new Ajax.Updater($("route_element"), url, {method:"post", parameters:{substationid:substationId, view:""}});
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
    $("routeIdSelectList").options[routeIndex] = null;
}
function SubstationToRouteMappings_updateRoutes(url) {
    var substationIndex = $("subSelectList").selectedIndex;
    var substationId = $("subSelectList").options[substationIndex].value;
    var array = $("routeIdSelectList").options;
    var array2 = $A(array).pluck("value");
    var jsonObject = array2.toJSON();
    new Ajax.Request(url, {method:"post", parameters:{array:jsonObject, substationid:substationId, update:""}});
}
function SubstationToRouteMappings_move(direction) {
    var routeIndex = $("routeIdSelectList").selectedIndex;
    if (routeIndex == -1) {
        return false;
    }
    var routeIndex2 = routeIndex + direction;
    if (routeIndex2 == -1 || routeIndex2 == $("routeIdSelectList").options.length) {
        return false;
    }
    var selectList = $("routeIdSelectList");
    var selectedIndex = selectList.selectedIndex;
    var one = selectList.options[routeIndex];
    var two = selectList.options[routeIndex2];
    selectList.options[routeIndex] = new Option(two.text, two.value);
    selectList.options[routeIndex2] = new Option(one.text, one.value);
    selectList.selectedIndex = selectedIndex + direction;
}

