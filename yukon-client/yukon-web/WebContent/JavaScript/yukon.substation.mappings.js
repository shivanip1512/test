var currentOrderByColum = 'STRATEGY';
var currentAscendingOrder = true;

var validateAndShow = function () {
    if (jQuery.trim(jQuery('#strategyName').val()) === '' || jQuery.trim(jQuery('#substationName').val()) === '') {
        alert('Strategy and Substation names are required.');
        return;
    }
    paoPicker.show();
};

var setMappedNameId = function () {
    var success = true;

    toggleLmMappingsWaitIndicators(true, jQuery('#addButton')[0]);
    if (jQuery.trim(jQuery('#mappedNameId').val()) === '') {
        alert('Select a Program/Scenario name.');
        return;
    }
    getMappedName(function (mappedName) {
        var overwrite = true,
            url,
            params;

        if (mappedName != null && mappedName !== jQuery('#mappedName').html()) {
            overwrite = confirm('Strategy/Substation ' + jQuery('#strategyName').val() + '/' + jQuery('#substationName').val() + ' is already mapped to "' + mappedName + '", are you want to overwrite it with "' + jQuery('#mappedName').html() + '"?');
            success = overwrite;
        }

        if (overwrite) {
            url = '/multispeak/setup/lmMappings/addOrUpdateMapping';
            params = {
                'strategyName': jQuery('#strategyName').val(),
                'substationName': jQuery('#substationName').val(),
                'mappedNameId': jQuery('#mappedNameId').val()
            };

            jQuery.ajax({
                url: url,
                data: params
            }).done(function (data, textStatus, jqXHR) {
                jQuery('#mappedNameDisplay').html(jQuery('#mappedName').html());
                toggleLmMappingsWaitIndicators(false, jQuery('#addButton')[0]);
                flashYellow(jQuery('#mappedNameDisplay')[0]);
                reloadAllMappingsTable(null, false);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                toggleLmMappingsWaitIndicators(false, jQuery('#addButton')[0]);
                alert('Error adding mapping: ' + jqXHR.responseText + ', error: ' + errorThrown);
            });
        } else {
            toggleLmMappingsWaitIndicators(false, jQuery('#addButton')[0]);
        }
    });
    return success;
};

function getMappedName (callback) {

    var url = '/multispeak/setup/lmMappings/findMapping';
    var params = {
        'strategyName': jQuery('#strategyName').val(),
        'substationName': jQuery('#substationName').val()
    };

    // setting the async flag is now deprecated in jQuery. See the jQuery ajax docs.
    // the question is, was it necessary?
    jQuery.ajax({
        url: url,
        data: params
    }).done(function (data, textStatus, jqXHR) {
        var jsonData = yukon.ui.util.getHeaderJSON(jqXHR),
            mappedName = jsonData['mappedName'];
        callback(mappedName);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        alert('Error searching for Strategy/Substation: ' + jqXHR.responseText + ', error: ' + errorThrown);
    });
}
  

function doLmMappingNameSearch () {
    toggleLmMappingsWaitIndicators(true, jQuery('#searchButton')[0]);
    getMappedName(function (mappedName) {
        if (mappedName != null) {
            jQuery('#mappedNameDisplay').html(mappedName);
        } else {
            jQuery('#mappedNameDisplay').html('Not Found');
        }
        toggleLmMappingsWaitIndicators(false, jQuery('#searchButton')[0]);
        flashYellow(jQuery('#mappedNameDisplay')[0]);
    });
}

function reloadAllMappingsTable (col, isReorder) {
    var url = '/multispeak/setup/lmMappings/reloadAllMappingsTable',
        params;
    if (isReorder) {
        if (col !== currentOrderByColum) {
            currentAscendingOrder = true;
        } else {
            currentAscendingOrder = !currentAscendingOrder;
        }
        currentOrderByColum = col;
    }
    // call reload
    params = {
        'col': currentOrderByColum,
        'ascending': currentAscendingOrder
    };
    jQuery.ajax({
        url: url,
        data: params
    }).done(function (data, textStatus, jqXHR) {
        jQuery('#allMappingsTableDiv').html(data);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        alert('reloadAllMappingsTable: error: ' + textStatus);
    });
}


function removeLmMapping (mspLMInterfaceMappingId) {
    var url = '/multispeak/setup/lmMappings/removeMapping',
        params;
    if (!confirm('Are you sure you want to remove this mappping?')) {
        return;
    }

    params = {
        'mspLMInterfaceMappingId': mspLMInterfaceMappingId
    };
    jQuery.ajax({
        url: url,
        data: params
    }).done(function (data, textStatus, jqXHR) {
        reloadAllMappingsTable(null, false);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        alert('Error removing mapping: ' + jqXHR.responseText + ', error: ' + errorThrown);
    });
}

function toggleLmMappingsWaitIndicators (isWaiting, buttonEl) {
    if (isWaiting) {
        jQuery('#waitImg').show();
        jQuery(buttonEl).prop('disabled', true);
    } else {
        jQuery('#waitImg').hide();
        jQuery(buttonEl).prop('disabled', false);
    }
}