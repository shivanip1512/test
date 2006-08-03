var pointPicker_destPointIdFieldId;
var pointPicker_currentSearch = '';
var pointPicker_inSearch = false;
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
    if (pointPicker_currentSearch != ss) {
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
    pointPicker_doPartialSearch(index);
}

function pointPicker_next(index) {
    pointPicker_doPartialSearch(index);
}

function pointPicker_showAll(start) {
    $('query').value = '';
    pointPicker_doKeyUp();
}

function zoomClick(e, pointid) {
    var posx = 0;
    var posy = 0;
    if (!e) var e = window.event;
    if (e.pageX || e.pageY)
    {
        posx = e.pageX;
        posy = e.pageY;
    }
    else if (e.clientX || e.clientY)
    {
        posx = e.clientX + document.body.scrollLeft;
        posy = e.clientY + document.body.scrollTop;
    }
    // posx and posy contain the mouse position relative to the document
    // Do something with this information
    alert("Click: " + posx + "," + posy + "; Point: "  + pointid);
}
