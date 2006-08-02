var pointPicker_destPointIdFieldId;
var pointPicker_currentSearch = '';
var pointPicker_inSearch = false;
var pointPicker_lastWasAll = false;
function pointPicker_showPicker(destPointIdFieldId) {
    // create div to store result
    var bodyElem = document.documentElement.getElementsByTagName("body")[0];
    var newDivElem = document.createElement("div");
    newDivElem.setAttribute("id", "pointPickerContainer");
    bodyElem.appendChild(newDivElem);
    
    var url = '/pointPicker/initial?';
    url += 'destPointIdFieldId=' + destPointIdFieldId;
    url += '&currentPointId=' + $(destPointIdFieldId).value;
    new Ajax.Updater('pointPickerContainer', url, {'method': 'get'});
    pointPicker_destPointIdFieldId = destPointIdFieldId;
}

var onComplete = function() {
    var ss = escape($('query').value);
    if (!pointPicker_lastWasAll && pointPicker_currentSearch != ss) {
        //do another search
        pointPicker_doPartialSearch(0);
    } else {
        pointPicker_inSearch = false;
        $('pointPicker_indicator').style.visibility = 'hidden';
    }
}

function pointPicker_doKeyUp() {
    var timerFunction = function() {
        if (!pointPicker_inSearch) {
            pointPicker_doPartialSearch(0);
        }
    };
    var quietDelay = 300;
    setTimeout(timerFunction, quietDelay);
    $('pointPicker_indicator').style.visibility = 'visible';
}

function pointPicker_doPartialSearch(start) {
    pointPicker_inSearch = true;
    pointPicker_lastWasAll = false;
    var ss = escape($('query').value);
    pointPicker_currentSearch = ss;
    var url = '/pointPicker/search?';
    url += 'ss=' + ss;
    url += '&currentPointId=' + $(pointPicker_destPointIdFieldId).value;
    url += '&start=' + start;
    new Ajax.Updater('pointPicker_results', url, {'method': 'get', 'onComplete': onComplete});
}

function pointPicker_selectThisPoint(pointId) {
    $(pointPicker_destPointIdFieldId).value = pointId;
    $('pointPickerContainer').parentNode.removeChild($('pointPickerContainer'))
}

function pointPicker_cancel() {
    $('pointPickerContainer').parentNode.removeChild($('pointPickerContainer'));
}

function pointPicker_previous(index) {
    if (pointPicker_lastWasAll) {
        pointPicker_showAll(index);
    } else {
        pointPicker_doPartialSearch(index);
    }
}

function pointPicker_next(index) {
    if (pointPicker_lastWasAll) {
        pointPicker_showAll(index);
    } else {
        pointPicker_doPartialSearch(index);
    }
}

function pointPicker_showAll(start) {
    pointPicker_lastWasAll = true;
    var url = '/pointPicker/showAll?';
    url += '&currentPointId=' + $(pointPicker_destPointIdFieldId).value;
    url += '&start=' + start;
    new Ajax.Updater('pointPicker_results', url, {'method': 'get', 'onComplete': onComplete});
    Element.show('pointPicker_indicator');
}
