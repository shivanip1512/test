
function indexManager_buildIndex(indexName) {
    var indexManager_getProgressWrapper = function () {
        indexManager_getProgress(indexName);
    };
    new Ajax.Request("/index/buildIndex?index=" + indexName, {"method":"get", "onComplete":indexManager_getProgressWrapper, "onFailure":indexManager_ajaxError});
}
var indexManager_getProgress = function (indexName) {
    var indexManager_updateProgressWrapper = function (transport, json) {
        indexManager_updateProgress(transport, json, indexName);
    };
    new Ajax.Request("/index/percentDone?index=" + indexName, {"method":"get", "onComplete":indexManager_updateProgressWrapper, "onFailure":indexManager_ajaxError});
};
var indexManager_updateProgress = function (transport, json, indexName) {
    var percentDone = json.percentDone;
    var isBuilding = json.isBuilding;
    var newDate = json.newDate;
    var newDatabase = json.newDatabase;
    
    var dateCreated = document.getElementById(indexName + "dateCreated");
    var percentComplete = document.getElementById(indexName + "percentComplete");
    var buildIndex = document.getElementById(indexName + "buildIndex");
    var database = document.getElementById(indexName + "database");
    if (isBuilding) {
        setTimeout("indexManager_getProgress('" + indexName + "')", 1000);
    }
    if (isBuilding && percentDone < 100) {
        dateCreated.innerHTML = "Building started at: " + newDate;
        database.innerHTML = "Building...";
        buildIndex.style.display = "none";
        percentComplete.style.display = "";
        indexManager_updateIndexProgressBar(indexName, percentDone);
    } else {
        dateCreated.innerHTML = newDate;
        database.innerHTML = newDatabase;
        percentComplete.style.display = "none";
        buildIndex.style.display = "";
        document.getElementById(indexName + "progressText").innerHTML = "";
    }
};
var indexManager_ajaxError = function (transport, json) {
    errorHolder = document.createElement("div");
    errorHolder.innerHTML = "There is a problem with the index: " + transport.responseText;
    document.getElementsByTagName("body")[0].appendChild(errorHolder);
};

function indexManager_updateIndexProgressBar(indexName, percent) {
    progressText = document.getElementById(indexName + "progressText");
    progressInner = document.getElementById(indexName + "progressInner");
    if (percent > 20) {
        progressText.innerHTML = parseInt(percent) + "%";
    }
    progressInner.style.width = percent + "%";
}

