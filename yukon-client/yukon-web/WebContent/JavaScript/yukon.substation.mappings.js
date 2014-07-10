var currentOrderByColum = 'STRATEGY';
var currentAscendingOrder = true;

var validateAndShow = function () {
    if ($.trim($('#strategyName').val()) === '' || $.trim($('#substationName').val()) === '') {
        alert('Strategy and Substation names are required.');
        return;
    }
    paoPicker.show();
};

var setMappedNameId = function () {
    var success = true;

    toggleLmMappingsWaitIndicators(true, $('#addButton')[0]);
    if ($.trim($('#mappedNameId').val()) === '') {
        alert('Select a Program/Scenario name.');
        return;
    }
    getMappedName(function (mappedName) {
        var overwrite = true,
            url,
            params;

        if (mappedName != null && mappedName !== $('#mappedName').html()) {
            overwrite = confirm('Strategy/Substation ' + $('#strategyName').val() + '/' + $('#substationName').val() + ' is already mapped to "' + mappedName + '", are you want to overwrite it with "' + $('#mappedName').html() + '"?');
            success = overwrite;
        }

        if (overwrite) {
            url = yukon.url('/multispeak/setup/lmMappings/addOrUpdateMapping');
            params = {
                'strategyName': $('#strategyName').val(),
                'substationName': $('#substationName').val(),
                'mappedNameId': $('#mappedNameId').val()
            };

            $.ajax({
                url: url,
                data: params
            }).done(function (data, textStatus, jqXHR) {
                $('#mappedNameDisplay').html($('#mappedName').html());
                toggleLmMappingsWaitIndicators(false, $('#addButton')[0]);
                flashYellow($('#mappedNameDisplay')[0]);
                reloadAllMappingsTable(null, false);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                toggleLmMappingsWaitIndicators(false, $('#addButton')[0]);
                alert('Error adding mapping: ' + errorThrown);
            });
        } else {
            toggleLmMappingsWaitIndicators(false, $('#addButton')[0]);
        }
    });
    return success;
};

function getMappedName (callback) {

    var url = yukon.url('/multispeak/setup/lmMappings/findMapping');
    var params = {
        'strategyName': $('#strategyName').val(),
        'substationName': $('#substationName').val()
    };

    $.ajax({
        url: url,
        data: params
    }).done(function (data, textStatus, jqXHR) {
        var mappedName = data.mappedName;
        callback(mappedName);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        alert('Error searching for Strategy/Substation: ' + errorThrown);
    });
}
  

function doLmMappingNameSearch () {
    toggleLmMappingsWaitIndicators(true, $('#searchButton')[0]);
    getMappedName(function (mappedName) {
        if (mappedName != null) {
            $('#mappedNameDisplay').html(mappedName);
        } else {
            $('#mappedNameDisplay').html('Not Found');
        }
        toggleLmMappingsWaitIndicators(false, $('#searchButton')[0]);
        flashYellow($('#mappedNameDisplay')[0]);
    });
}

function reloadAllMappingsTable (col, isReorder) {
    var url = yukon.url('/multispeak/setup/lmMappings/reloadAllMappingsTable'),
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
    $.ajax({
        url: url,
        data: params
    }).done(function (data, textStatus, jqXHR) {
        $('#allMappingsTableDiv').html(data);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        alert('reloadAllMappingsTable: error: ' + errorThrown);
    });
}


function removeLmMapping (mspLMInterfaceMappingId) {
    var url = yukon.url('/multispeak/setup/lmMappings/removeMapping'),
        params;
    if (!confirm('Are you sure you want to remove this mappping?')) {
        return;
    }

    params = {
        'mspLMInterfaceMappingId': mspLMInterfaceMappingId
    };
    $.ajax({
        url: url,
        data: params
    }).done(function (data, textStatus, jqXHR) {
        reloadAllMappingsTable(null, false);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        alert('Error removing mapping: ' + errorThrown);
    });
}

function toggleLmMappingsWaitIndicators (isWaiting, buttonEl) {
    if (isWaiting) {
        $('#waitImg').show();
        $(buttonEl).prop('disabled', true);
    } else {
        $('#waitImg').hide();
        $(buttonEl).prop('disabled', false);
    }
}