
function SubstationToRouteMappings_SelectListener(url) {
    var substationIndex = $("#subSelectList")[0].selectedIndex,
        optionsList = $("#subSelectList")[0].options,
        substationId,
        routeElement = $('#route_element');
    if (substationIndex < 0 || 0 >= optionsList.length) {
        return;
    }
    substationId = optionsList[substationIndex].value;
    $.ajax({
        type: "POST",
        url: url,
        data: {"substationid": substationId, "view": ""}
    }).done( function (data, textStatus, jqXHR) {
        routeElement.html(data);
    });
}
function SubstationToRouteMappings_addRoute() {
    var substationIndex = $("#subSelectList")[0].selectedIndex,
        routeIndex,
        routeId,
        routeName,
        length;
    if (substationIndex === -1) {
        return false;
    }
    routeIndex = $("#avRoutesSelectList")[0].selectedIndex;
    routeId = $("#avRoutesSelectList")[0].options[routeIndex].value;
    routeName = $("#avRoutesSelectList")[0].options[routeIndex].text;
    $($("#avRoutesSelectList")[0].options[routeIndex]).remove();
    length = $("#routeIdSelectList")[0].options.length;
    $("#routeIdSelectList")[0].options[length] = new Option(routeName, routeId);
}
function SubstationToRouteMappings_removeRoute() {
    var substationIndex = $("#subSelectList")[0].selectedIndex,
        routeIndex,
        route,
        length;
    if (substationIndex === -1) {
        return false;
    }
    routeIndex = $("#routeIdSelectList")[0].selectedIndex;
    if (0 > routeIndex) {
        return false;
    }
    route = $("#routeIdSelectList")[0].options[routeIndex];
    if ('undefined' === typeof route) {
        return false;
    }
    $("#routeIdSelectList")[0].options[routeIndex] = null;

    length = $("#avRoutesSelectList")[0].options.length;
    $("#avRoutesSelectList")[0].options[length] = new Option(route.text, route.value);
}
function SubstationToRouteMappings_updateRoutes(url) {
    var substationIndex = $("#subSelectList")[0].selectedIndex,
        substationId,
        array,
        array2,
        jsonObject;
    if (-1 === substationIndex) {
        return;
    }
    substationId = $("#subSelectList")[0].options[substationIndex].value;
    array = $("#routeIdSelectList")[0].options;
    array = Array.prototype.slice.call(array, 0); // convert HTMLOptionsCollection to array
    array2 = array.map(function (el) {
        return el.value;
    });
    jsonObject = JSON.stringify(array2);
    $.ajax({
        type: "POST",
        url: url,
        data: {"array": jsonObject, "substationid": substationId, "update": ""}
    }).done( function (data, textStatus, jqXHR) {
    });
}

function mspSubstations_check(doCheck) {
    $('input[type=checkbox].mspSubstationCheckbox').each(function(index, c) {
        $(c).prop("checked", doCheck);
    });
}

function SubstationToRouteMappings_disableInputs(doDisable) {
    $('input').each(function(index, c) {
        $(c).prop('disabled', doDisable);
    });
}
