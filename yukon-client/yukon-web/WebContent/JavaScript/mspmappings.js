
function SubstationToRouteMappings_SelectListener(url) {
    var substationIndex = jQuery("#subSelectList")[0].selectedIndex,
        optionsList = jQuery("#subSelectList")[0].options,
        substationId,
        routeElement = jQuery('#route_element');
    if (substationIndex < 0 || 0 >= optionsList.length) {
        return;
    }
    substationId = optionsList[substationIndex].value;
    jQuery.ajax({
        type: "POST",
        url: url,
        data: {"substationid": substationId, "view": ""}
    }).done( function (data, textStatus, jqXHR) {
        routeElement.html(data);
    });
}
function SubstationToRouteMappings_addRoute() {
    var substationIndex = jQuery("#subSelectList")[0].selectedIndex,
        routeIndex,
        routeId,
        routeName,
        length;
    if (substationIndex === -1) {
        return false;
    }
    routeIndex = jQuery("#avRoutesSelectList")[0].selectedIndex;
    routeId = jQuery("#avRoutesSelectList")[0].options[routeIndex].value;
    routeName = jQuery("#avRoutesSelectList")[0].options[routeIndex].text;
    jQuery(jQuery("#avRoutesSelectList")[0].options[routeIndex]).remove();
    length = jQuery("#routeIdSelectList")[0].options.length;
    jQuery("#routeIdSelectList")[0].options[length] = new Option(routeName, routeId);
}
function SubstationToRouteMappings_removeRoute() {
    var substationIndex = jQuery("#subSelectList")[0].selectedIndex,
        routeIndex,
        route,
        length;
    if (substationIndex === -1) {
        return false;
    }
    routeIndex = jQuery("#routeIdSelectList")[0].selectedIndex;
    if (0 > routeIndex) {
        return false;
    }
    route = jQuery("#routeIdSelectList")[0].options[routeIndex];
    if ('undefined' === typeof route) {
        return false;
    }
    jQuery("#routeIdSelectList")[0].options[routeIndex] = null;

    length = jQuery("#avRoutesSelectList")[0].options.length;
    jQuery("#avRoutesSelectList")[0].options[length] = new Option(route.text, route.value);
}
function SubstationToRouteMappings_updateRoutes(url) {
    var substationIndex = jQuery("#subSelectList")[0].selectedIndex,
        substationId,
        array,
        array2,
        jsonObject;
    if (-1 === substationIndex) {
        return;
    }
    substationId = jQuery("#subSelectList")[0].options[substationIndex].value;
    array = jQuery("#routeIdSelectList")[0].options;
    array = Array.prototype.slice.call(array, 0); // convert HTMLOptionsCollection to array
    array2 = array.map(function (el) {
        return el.value;
    });
    jsonObject = JSON.stringify(array2);
    jQuery.ajax({
        type: "POST",
        url: url,
        data: {"array": jsonObject, "substationid": substationId, "update": ""}
    }).done( function (data, textStatus, jqXHR) {
    });
}

function mspSubstations_check(doCheck) {
    jQuery('input[type=checkbox].mspSubstationCheckbox').each(function(index, c) {
        jQuery(c).prop("checked", doCheck);
    });
}

function SubstationToRouteMappings_disableInputs(doDisable) {
    jQuery('input').each(function(index, c) {
        jQuery(c).prop('disabled', doDisable);
    });
}
