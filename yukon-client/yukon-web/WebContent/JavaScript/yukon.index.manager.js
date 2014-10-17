/** Build indexes for specified indexName.
 * @param {String } indexName - Index Name
 */ 
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

/**
 * Check Build Progress for given indexName. 
 * @param {String} indexName - Index Name.
 */
var indexManager_getProgress = function (indexName) {
    var indexManager_updateProgressWrapper = function (transport, json) {
        indexManager_updateProgress(transport, json, indexName);
    };
    $.ajax({
        url: yukon.url('/index/percentDone?index=' + indexName),
        type: 'get'
    }).done(function (data, textStatus, jqXHR) {
        var json = JSON.parse(data);
        indexManager_updateProgressWrapper(jqXHR, json);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        indexManager_ajaxError(jqXHR);
    });
};

/**
 * Update progress for build index for given indexName.
 * @param {Object} transport - response object containing response text. 
 * @param {Object} json - json object having information  about index like percentage done etc.
 * @param {String} indexName - Index Name
 */
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

/**
 * Error display in build index for specified IndexName.
 * @param {Object} json - json object having information about dateCreated , isbuilding and newdate.  
 * @param {Object} transport -response object containing response text.
 */
var indexManager_ajaxError = function (transport, json) {
    errorHolder = document.createElement('div');
    $(errorHolder).html('There is a problem with the index: ' + transport.responseText);
    document.getElementsByTagName('body')[0].appendChild(errorHolder);
};

/**
 * Update the ProgressBar of the index.
 * @param {String} indexName - Index Name.
 * @param {Number}  percent - Percentage value of index build.
 */
function indexManager_updateIndexProgressBar (indexName, percent) {
    $('#' + indexName + 'percentComplete').find('.progress-bar').width(percent + '%');
}

