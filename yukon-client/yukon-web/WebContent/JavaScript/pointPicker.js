// the structure of the point picker dialog looks something like this

//div#pointPickerContainer (generated by js, pointPicker_showPicker)
//    div#pointPicker_popup (inner.jsp)
//        random stuff, including query field, links, and indicator (inner.jsp)
//        div#pointPicker_results (inner.jsp)
//            div#pointPicker_resultArea (generated by js, renderHtmlResult)
//                div#pointPicker_resultAreaFixed (generated by js, renderHtmlResult)
//                    table.pointPickerResultTable (generated by js, createHtmlTableFromJson)
//                table#pointPicker_navTable (generated by js, renderHtmlResult)

var pointPicker_destPointIdFieldId;
var pointPicker_currentSearch = '';
var pointPicker_inSearch = false;
var pointPicker_criteria = '';
var pointPicker_extraInfo = [];
var pointPicker_lastMethod = function() {};
var pointPicker_context = '';

function pointPicker_showPicker(destPointIdFieldId, criteria, extraMapping, context) {
    // store server root context
    if (context != null)
    	pointPicker_context = context;
    // store constraint away
    pointPicker_criteria = criteria;
    // parse extraMapping
    // format: "property1:field1id;property2:field2id"
    pointPicker_extraInfo = [];
    if (extraMapping) {
	    var pairs = extraMapping.split(/;/);
	    for (i = 0; i < pairs.length; i += 1) {
	        var pair = pairs[i].split(/:/);
	        if (pair.length == 2) {
	            var info = {'property': pair[0], 'fieldid': pair[1]};
	            pointPicker_extraInfo.push(info);
	        }
	    }
    }
    // create div to store result
    var bodyElem = document.documentElement.getElementsByTagName("body")[0];
    var newDivElem = document.createElement("div");
    newDivElem.setAttribute("id", "pointPickerContainer");
    bodyElem.appendChild(newDivElem);
    
    var url = pointPicker_context + '/pointPicker/initial';
    new Ajax.Updater('pointPickerContainer', url, {'method': 'get', 'onComplete': onPickerShown, 'onFailure': pointPicker_ajaxError});
    pointPicker_destPointIdFieldId = destPointIdFieldId;
}

var onPickerShown = function(transport, json) {
    $('pointPicker_query').focus();
    pointPicker_sameDevice();
};

var onComplete = function(transport, json) {
    var newResultArea = renderHtmlResult(json);
    var oldResultArea = $("pointPicker_resultArea");
    var resultHolder = $("pointPicker_results");
    if (oldResultArea) {
      resultHolder.removeChild(oldResultArea);
    }
    var oldError = $("pointPicker_errorHolder");
    if (oldError) {
      resultHolder.removeChild(oldError);
    }
    resultHolder.appendChild(newResultArea);
    
    var ss = escape($('pointPicker_query').value);
    if (pointPicker_currentSearch != ss) {
        //do another search
        pointPicker_doPartialSearch(0);
    } else {
        pointPicker_inSearch = false;
        $('pointPicker_indicator').style.visibility = 'hidden';
    }
};

function pointPicker_ajaxError(transport, json) {
    pointPicker_inSearch = false;
    $('pointPicker_indicator').style.visibility = 'hidden';
    $("pointPicker_results").innerHTML = "";
    errorHolder = document.createElement("div");
    errorHolder.id = "pointPicker_errorHolder";
    errorHolder.innerHTML = "There was a problem searching the index: " + transport.responseText;
    $("pointPicker_results").appendChild(errorHolder);
}

function pointPicker_doKeyUp() {
    $('showAllLink').removeClassName('pointPicker_selectedLink');
    $('sameDeviceLink').removeClassName('pointPicker_selectedLink');
    var timerFunction = function() {
        var ss = escape($('pointPicker_query').value);
        if (!pointPicker_inSearch && pointPicker_currentSearch != ss) {
            pointPicker_doPartialSearch(0);
        } else {
            $('pointPicker_indicator').style.visibility = 'hidden';
        }
    };
    var quietDelay = 300;
    setTimeout(timerFunction, quietDelay);
    $('pointPicker_indicator').style.visibility = 'visible';
}

function pointPicker_doPartialSearch(start) {
    pointPicker_lastMethod = pointPicker_doPartialSearch;
    pointPicker_inSearch = true;
    var ss = escape($('pointPicker_query').value);
    pointPicker_currentSearch = ss;
    var url = pointPicker_context + '/pointPicker/search?';
    url += 'ss=' + ss;
    url += '&currentPointId=' + $(pointPicker_destPointIdFieldId).value;
    url += '&criteria=' + pointPicker_criteria;
    url += '&start=' + start;
    new Ajax.Request(url, {'method': 'get', 'onComplete': onComplete, 'onFailure': pointPicker_ajaxError});
}

function pointPicker_doSameDeviceSearch(start) {
    pointPicker_lastMethod = pointPicker_doSameDeviceSearch;
    pointPicker_inSearch = true;
    var deviceId = escape($('pointPicker_query').value);
    pointPicker_currentSearch = '';
    var url = pointPicker_context + '/pointPicker/sameDevice?';
    url += 'currentPointId=' + $(pointPicker_destPointIdFieldId).value;
    url += '&criteria=' + pointPicker_criteria;
    url += '&start=' + start;
    new Ajax.Request(url, {'method': 'get', 'onComplete': onComplete, 'onFailure': pointPicker_ajaxError});
}

function pointPicker_selectThisPoint(hit) {
    $(pointPicker_destPointIdFieldId).value = hit.pointId;
    $('pointPickerContainer').parentNode.removeChild($('pointPickerContainer'));
    for (i=0; i < pointPicker_extraInfo.length; i+=1) {
        info = pointPicker_extraInfo[i];
        $(info.fieldid).innerHTML = hit[info.property];
    }
}

function pointPicker_cancel() {
    $('pointPickerContainer').parentNode.removeChild($('pointPickerContainer'));
}

function pointPicker_previous(index) {
    $('pointPicker_indicator').style.visibility = 'visible';
    pointPicker_lastMethod(index);
}

function pointPicker_next(index) {
    $('pointPicker_indicator').style.visibility = 'visible';
    pointPicker_lastMethod(index);
}

function pointPicker_showAll() {
    $('pointPicker_indicator').style.visibility = 'visible';
    $('showAllLink').addClassName('pointPicker_selectedLink');
    $('sameDeviceLink').removeClassName('pointPicker_selectedLink');
    $('pointPicker_query').value = '';
    pointPicker_doPartialSearch();
}

function pointPicker_sameDevice() {
    $('pointPicker_indicator').style.visibility = 'visible';
    $('sameDeviceLink').addClassName('pointPicker_selectedLink');
    $('showAllLink').removeClassName('pointPicker_selectedLink');
    $('pointPicker_query').value = '';
    pointPicker_doSameDeviceSearch(0);
}

function renderHtmlResult(json) {
    var createPointLink = function(hit) {
        return function() {
            pointPicker_selectThisPoint(hit);
        };
    };
    var currentId = $(pointPicker_destPointIdFieldId).value;
    var selectCurrent = function(rowElement, data) {
        if (data.pointId == currentId) {
            rowElement.className = "pointPicker_currentPointRow";
        }
    };
    // The following array refers to properties that are available in
    // the UltraLightPoint interface. If additional properties are added
    // to that class, they will automatically be added to the JSON 
    // data structure that is returned. The link attribute, when set,
    // must refer to a function that will return an appropriate onClick
    // function for the row. Again, the "createPointLink" function is NOT
    // the onClick function, instead it generates the onClick function.
    var outputCols = [
        {"title": "Device", "field": "deviceName", "link": null},
        {"title": "Point Id", "field": "pointId", "link": null},
        {"title": "Point", "field": "pointName", "link": createPointLink}
      ];
    var hitList = json.hitList;
    var resultArea = document.createElement("div");
    resultArea.id = "pointPicker_resultArea";
    var resultAreaFixed = document.createElement("div");
    resultAreaFixed.id = "pointPicker_resultAreaFixed";
    resultArea.appendChild(resultAreaFixed);
    if (hitList && hitList.length && hitList.length > 0) {
	    var resultTable = createHtmlTableFromJson(hitList, outputCols, selectCurrent);
        resultTable.className = "pointPickerResultTable";
        resultAreaFixed.appendChild(resultTable);
    } else {
        var emptyResultDiv = document.createElement("div");
        emptyResultDiv.id = "pointPicker_noResult";
        var noResultText = "No results found";
        emptyResultDiv.appendChild(document.createTextNode(noResultText));
        resultAreaFixed.appendChild(emptyResultDiv);
    }

    // create lower section
    var navTable = document.createElement("table");
    navTable.id = "pointPicker_navTable";
    resultArea.appendChild(navTable);
    var navTableBody = document.createElement("tbody");
    navTable.appendChild(navTableBody);
    var row1 = document.createElement("tr");
    navTableBody.appendChild(row1);
    var leftCell = document.createElement("td");
    leftCell.className = "left";
    row1.appendChild(leftCell);
    var centerCell = document.createElement("td");
    //centerCell.setAttribute("class", "center");
    centerCell.className = "center";
    row1.appendChild(centerCell);
    var rightCell = document.createElement("td");
    rightCell.className = "right";
    row1.appendChild(rightCell);
    
    if (json.startIndex > 0) {
        // need previous button
        leftCell.innerHTML = "<a href=\"javascript:pointPicker_previous(" + json.previousIndex + ")\">Previous</a>";
    }
    
    if (json.endIndex < json.hitCount) {
        // need next button
        rightCell.innerHTML = "<a href=\"javascript:pointPicker_next(" + json.nextIndex + ")\">Next</a>";
    }
    
    centerCell.innerHTML = (json.startIndex + 1) + " - " + json.endIndex + " of " + json.hitCount;
    
    return resultArea;
}


