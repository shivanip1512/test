
function indexManager_buildIndex (indexName) {
    var indexManager_getProgressWrapper = function () {
        indexManager_getProgress(indexName);
    };
    $.ajax({
        url: yukon.url('/index/buildIndex?index=' + indexName),
        type: 'get'
    }).done(function (data, textStatus, jqXHR) {
        indexManager_getProgressWrapper();
    }).fail(function (jqXHR, textStatus, errorThrown) {
        indexManager_ajaxError(jqXHR);
    });
}
var indexManager_getProgress = function (indexName) {
    var indexManager_updateProgressWrapper = function (transport, json) {
        indexManager_updateProgress(transport, json, indexName);
    };
    $.ajax({
        url: yukon.url('/index/percentDone?index=' + indexName),
        type: 'get'
    }).done(function (data, textStatus, jqXHR) {
        var json = yukon.ui.util.getHeaderJSON(jqXHR);
        indexManager_updateProgressWrapper(jqXHR, json);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        indexManager_ajaxError(jqXHR);
    });
};
var indexManager_updateProgress = function (transport, json, indexName) {
    var percentDone = json.percentDone,
        isBuilding = json.isBuilding,
        newDate = json.newDate,
        dateCreated = document.getElementById(indexName + 'dateCreated'),
        percentComplete = document.getElementById(indexName + 'percentComplete'),
        buildIndex = document.getElementById(indexName + 'buildIndex');

    if (isBuilding) {
        setTimeout('indexManager_getProgress("' + indexName + '")', 1000);
    }
    if (isBuilding && percentDone < 100) {
        $(dateCreated).html('Building started at: ' + newDate);
        buildIndex.style.display = 'none';
        percentComplete.style.display = '';
        indexManager_updateIndexProgressBar(indexName, percentDone);
    } else {
        $(dateCreated).html(newDate);
        percentComplete.style.display = 'none';
        buildIndex.style.display = '';
        $('#' + indexName + 'progressText').html('');
    }
};
var indexManager_ajaxError = function (transport, json) {
    errorHolder = document.createElement('div');
    $(errorHolder).html('There is a problem with the index: ' + transport.responseText);
    document.getElementsByTagName('body')[0].appendChild(errorHolder);
};

function indexManager_updateIndexProgressBar (indexName, percent) {
    progressText = document.getElementById(indexName + 'progressText');
    progressInner = document.getElementById(indexName + 'progressInner');
    if (percent > 20) {
        $(progressText).html(parseInt(percent, 10) + '%');
    }
    progressInner.style.width = percent + '%';
}

