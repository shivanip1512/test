
function indexManager_buildIndex (indexName) {
    var indexManager_getProgressWrapper = function () {
        indexManager_getProgress(indexName);
    };
    jQuery.ajax({
        url: "/index/buildIndex?index=" + indexName,
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
    jQuery.ajax({
        url: "/index/percentDone?index=" + indexName,
        type: 'get'
    }).done(function (data, textStatus, jqXHR) {
        var json = yukon.ui.aux.getHeaderJSON(jqXHR);
        indexManager_updateProgressWrapper(jqXHR, json);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        indexManager_ajaxError(jqXHR);
    });
};
var indexManager_updateProgress = function (transport, json, indexName) {
    var percentDone = json.percentDone,
        isBuilding = json.isBuilding,
        newDate = json.newDate,
        dateCreated = document.getElementById(indexName + "dateCreated"),
        percentComplete = document.getElementById(indexName + "percentComplete"),
        buildIndex = document.getElementById(indexName + "buildIndex");

    if (isBuilding) {
        setTimeout("indexManager_getProgress('" + indexName + "')", 1000);
    }
    if (isBuilding && percentDone < 100) {
        jQuery(dateCreated).html("Building started at: " + newDate);
        buildIndex.style.display = "none";
        percentComplete.style.display = "";
        indexManager_updateIndexProgressBar(indexName, percentDone);
    } else {
        jQuery(dateCreated).html(newDate);
        percentComplete.style.display = "none";
        buildIndex.style.display = "";
        jQuery('#' + indexName + "progressText").html("");
    }
};
var indexManager_ajaxError = function (transport, json) {
    errorHolder = document.createElement("div");
    jQuery(errorHolder).html("There is a problem with the index: " + transport.responseText);
    document.getElementsByTagName("body")[0].appendChild(errorHolder);
};

function indexManager_updateIndexProgressBar (indexName, percent) {
    progressText = document.getElementById(indexName + "progressText");
    progressInner = document.getElementById(indexName + "progressInner");
    if (percent > 20) {
        jQuery(progressText).html(parseInt(percent, 10) + "%");
    }
    progressInner.style.width = percent + "%";
}

