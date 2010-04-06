var manMsgID = 0;
var menuPopUpWidth = 165;

function checkPageExpire() {
    var inputElements = $$('input');
    var paoIds = inputElements.findAll(function(it) {
        return it.id.startsWith('paoId_')
    }).pluck('value').toJSON();

    var url = '/spring/capcontrol/pageExpire';
    new Ajax.Request(url, {
        'method': 'POST',
        'parameters' : {'paoIds': paoIds},
        onSuccess: function(transport) {
            var html = transport.responseText;
            var expired = eval(html);
            if (expired) {
                if (confirm('This page has been updated.  Would you like to load the changes?')) {
                    window.location.reload();
                }    
                return;    
            }
            setTimeout(checkPageExpire, 15000);
        }
    });    
}

function getAreaMenu(id, event) {
    var url = '/spring/capcontrol/tier/popupmenu?menu=areaMenu&id=' + id;
    getMenuFromURL(url, event);
}

function getSpecialAreaMenu(id, event){
    var url = '/spring/capcontrol/tier/popupmenu?menu=specialAreaMenu&id=' + id;
    getMenuFromURL(url, event); 
}

function getSubstationMenu(id, event) {
    var url = '/spring/capcontrol/tier/popupmenu?menu=subStationMenu&id=' + id;
    getMenuFromURL(url, event);
}

function getSubBusMenu(id, event) {
   var url = '/spring/capcontrol/tier/popupmenu?menu=subBusMenu&id=' + id;
   getMenuFromURL(url, event);
}

function getFeederMenu(id, event) {
    var url = '/spring/capcontrol/tier/popupmenu?menu=feederMenu&id=' + id;
    getMenuFromURL(url, event);    
}

function getCapBankMenu(id, event) {
    var url = '/spring/capcontrol/tier/popupmenu?menu=capBankMenu&id=' + id;
    getMenuFromURLAbove(url, event); 
}

function getLtcMenu(id, event) {
    var url = '/spring/capcontrol/tier/popupmenu?menu=ltcMenu&id=' + id;
    getMenuFromUrlLeft(url, event); 
}

function getLtcPointsList(id) {
	var url = '/spring/capcontrol/tier/popupmenu?menu=ltcPointList&ltcId=' +id;
	new Ajax.Request(url, {
        method: 'POST',
        onSuccess: function(transport) {
            var html = transport.responseText;
            var body = $('ltcPopupBody');
        	body.innerHTML = html;
        	var popupDiv = $('ltcPointPopup');
        	var ltcName = $('ltcName');
        	var titleDiv = popupDiv.getElementsByClassName('boxContainer_title')[0];
        	titleDiv.innerHTML = ltcName.value;
        	popupDiv.show();
        }
    });
}

function getLocalControlMenu(id, capBankType, objectType, event) {
	if(!capBankType) {
		capBankType = false;
	}
	
    var url = '/spring/capcontrol/tier/popupmenu?menu=localControlMenu&id=' + id + '&capBankType=' + capBankType + '&controlType=' + objectType;
    getMenuFromURLAbove(url, event); 
}

function getCapBankChangeOpStateMenu(id, event) {
    var url = '/spring/capcontrol/tier/popupmenu?menu=capBankChangeOpStateMenu&id=' + id;
    getMenuFromURLAbove(url, event); 
}

function getCapBankSystemMenu(id, event) {
    var url = '/spring/capcontrol/tier/popupmenu?menu=capBankSystemMenu&id=' + id;
    getMenuFromURLAbove(url, event); 
}

function getCapBankTempMoveBack(id, event){
    var url = '/spring/capcontrol/tier/popupmenu?menu=capBankTempMoveBack&id=' + id;
    var redirect = window.location.href;
    url += '&redirectURL=' + escape(redirect);
    getMenuFromURL(url, event);
}

function willMenuFitAbove(yClick, menuHeight) {
	if ( yClick < menuHeight ) {
		return false;
	}
	return true;
}

function willMenuFitBelow(yClick, menuHeight) {
	if ( (yClick + menuHeight) > document.documentElement.clientHeight ) {
		return false;
	}
	return true;
}

function getMenuFromURL(url, event) {
	/*
	 *  In IE the event does not pass through the ajax request
	 *  so the attributes of the event need to be set and passed
	 *  as variables.
	 */
	var x = mouseX(event);
    var y = mouseY(event);
    new Ajax.Request(url, {
        method: 'POST',
        onSuccess: function(transport) {
            var html = transport.responseText;
            showMenuPopup(html, false, false, x, y);
        }
    });
}

function getMenuFromUrlLeft(url, event) {
	/*
	 *  In IE the event does not pass through the ajax request
	 *  so the attributes of the event need to be set and passed
	 *  as variables.
	 */
	var x = mouseX(event);
    var y = mouseY(event);
    new Ajax.Request(url, {
        method: 'POST',
        onSuccess: function(transport) {
            var html = transport.responseText;
            showMenuPopup(html, false, true, x, y);
        }
    });
}
function getMenuFromURLAbove(url, event) {
	/*
	 *  In IE the event does not pass through the ajax request
	 *  so the attributes of the event need to be set and passed
	 *  as variables.
	 */
	var x = mouseX(event);
	var y = mouseY(event);
    new Ajax.Request(url, {
	    method: 'POST',
	    onSuccess: function(transport) {
	        var html = transport.responseText;
	        showMenuPopup(html, true, false, x, y);
	    }
	});
}

function getReasonMenuFromURL(url, event) {
	/*
	 *  In IE the event does not pass through the ajax request
	 *  so the attributes of the event need to be set and passed
	 *  as variables.
	 */
    var x = mouseX(event);
    var y = mouseY(event);
    new Ajax.Request(url, {
	    method: 'POST',
	    onSuccess: function(transport) {
	        var html = transport.responseText;
	        showReasonPopup(html, true, x, y);
	    }
	});
}

function showReasonPopup(html, up, x, y) {
	var body = $('popupBody');
	body.innerHTML = html;
	var popupDiv = $('tierPopup');
	var paoName = $('commentPaoName');
	var titleDiv = popupDiv.getElementsByClassName('boxContainer_title')[0];
	titleDiv.innerHTML = 'Comments: ' + paoName.value;
	popupDiv.show();
	if(up == true){
		if( willMenuFitAbove(y, popupDiv.clientHeight) ) {
			y = y - popupDiv.clientHeight;
		}
	}
	popupDiv.setStyle({
		top: y + "px",
		left: x + "px",
		width: 350 + "px"
	});
}

function showMenuPopup(html, up, left, x, y) {
	var body = $('popupBody');
	body.innerHTML = html;
	var popupDiv = $('tierPopup');
	var paoName = $('menuPaoName');
	var titleDiv = popupDiv.getElementsByClassName('boxContainer_title')[0];
	titleDiv.innerHTML = paoName.value;
	popupDiv.show();
	if(up == true){
		if( willMenuFitAbove(y, popupDiv.clientHeight) ) {
			y = y - popupDiv.clientHeight;
		}
	} else {
		if( !willMenuFitBelow(y, popupDiv.clientHeight) ) {
			y = y - popupDiv.clientHeight;
		}
	}
	if(left == true){
		x = x - menuPopUpWidth;
	}
	popupDiv.setStyle({
		top: y + "px",
		left: x + "px",
		width: menuPopUpWidth + "px"
	});
}

function closeTierPopup() {
	var tierPopup = $('tierPopup');
	tierPopup.hide();
}

function closeLtcPointPopup() {
	var ltcPointPopup = $('ltcPointPopup');
	ltcPointPopup.hide();
}

function updateStateColorGenerator(id) {
    return function(data) {
        var anchorTag = $(id);
        var state = data.value;
        var color;
        if (state.indexOf('Pending') != -1) {
            color = '#F09100';
        } else if (state.startsWith('ENABLED')) {
            color = '#3C8242';
        } else if (state.startsWith('DISABLED')) {
            color = '#FF0000';
        }
        anchorTag.style.color = color;
    };
}

function updateWarningImage(id) {
    return function(data) {
        var alertSpan = $(id + '_alert');
        var okSpan = $(id + '_ok');
        
        var isWarning = eval(data.value);
        if (isWarning) {
            okSpan.hide();
            alertSpan.show();
        } else {
            alertSpan.hide();
            okSpan.show();
        }
    }
}

function updateDualBusImage(id) {
    return function(data) {
        var primarySpan = $(id + '_primary');
        var alternateSpan = $(id + '_alternate');
        
        var icon = data.value;
        
        if (icon == 'Primary') {
        	primarySpan.show();
        	alternateSpan.hide();
        } else if (icon == 'Alternate'){
        	primarySpan.hide();
        	alternateSpan.show();
        } else {
        	primarySpan.hide();
        	alternateSpan.hide();        	
        }
    }
}

function updateCapBankWarningImage(id) {
    return function(data) {
        var yellowSpan = $(id + '_yellow');
        var greenSpan = $(id + '_green');
        var yellowLocalSpan = $(id + '_yellow_local');
        var greenLocalSpan = $(id + '_green_local');
        
        var icon = data.value;
        
        if (icon == 'GreenRemote') {
            yellowSpan.hide();
            yellowLocalSpan.hide();
            greenLocalSpan.hide();
            greenSpan.show();
        } else if (icon == 'GreenLocal'){
            yellowSpan.hide();
            yellowLocalSpan.hide();
            greenLocalSpan.show();
            greenSpan.hide();
        } else if (icon == 'YellowRemote'){
            yellowSpan.show();
            yellowLocalSpan.hide();
            greenLocalSpan.hide();
            greenSpan.hide();
        } else if (icon == 'YellowLocal'){
            yellowSpan.hide();
            yellowLocalSpan.show();
            greenLocalSpan.hide();
            greenSpan.hide();
        }
    }
}

function updateLtcModeIndicator(id){
	return function(data) {
        var yellowLocalSpan = $(id + '_local_warning');
        var greenLocalSpan = $(id + '_local_normal');
        
        var icon = data.value;
        
        if (icon == 'none') {
            yellowLocalSpan.hide();
            greenLocalSpan.hide();
        } else if (icon == 'NormalLocal'){
            yellowLocalSpan.hide();
            greenLocalSpan.show();
        } else if (icon == 'WarningLocal'){
            yellowLocalSpan.show();
            greenLocalSpan.hide();
        }
	}
}

function updateLtcTapIndicator(id){
	return function(data) {
        var lowerTapSpan = $(id + '_lower');
        var raiseTapSpan = $(id + '_raise');
        
        var icon = data.value;
        
        if (icon == 'none') {
        	lowerTapSpan.hide();
        	raiseTapSpan.hide();
        } else if (icon == 'RaiseTap'){
        	lowerTapSpan.hide();
        	raiseTapSpan.show();
        } else if (icon == 'LowerTap'){
        	lowerTapSpan.show();
        	raiseTapSpan.hide();
        }
	}
}

function updateVerificationImage(spanId) {
    return function(data) {
        var isVerification = eval(data.value);
        if (isVerification) {
            $(spanId).show();
        } else {
            $(spanId).hide();
        }
    }
}

// -------------------------------------------
//post events with a form rather than using the URL line.
// Accepts a form name and any number of [ParamName,ParamValue] pairs.
// Example: postMany('frmA', 'paoID', intSubID, 'cmdID', 9)"
// -------------------------------------------
function postMany( frmName )
{
    if( !validateData(frmName) )
        return false;

    var f = document.getElementById(frmName);

    if( arguments.length > 1 )
    {
        //each ParamName must have a corresponding ParamValue
        if( ((arguments.length-1) % 2) != 0 )
            alert('Incorrect number of parameters for postMany() function');
        else
        {
            for( i = 1; i < arguments.length; i+=2 )
            {           
                if( !validateData(arguments[i]) )
                    return false;
                var ev = eval('f.'+arguments[i]);
                ev.value = arguments[i+1];
            }
        }
    }
    f.submit();
}

// -------------------------------------------
//validates input data for post events
// -------------------------------------------
function validateData( elemName )
{
    if( !document.getElementsByName(elemName) )
    {
        alert('Unable to find element for given operation ('+elemName+'), try again');
        return false;
    }
    return true;
}

// -------------------------------------------
// Any javascript that is needed to init a page
// -------------------------------------------
function init()
{   
}

// -------------------------------------------
// A fun method that may come in handy. Uses the DOM to dynamically
//  include javascript files. Better than writting out HTML!
// -------------------------------------------
function includeJSinJS( jsFileName )
{
    myscript = document.createElement('script');
    myscript.type = 'text/javasript';
    myscript.src = jsFileName; //'myscript.js'
    document.getElementsByTagName('head')[0].appendChild(myscript);
}

// -------------------------------------------
//checks all checkboxes that have the given itemName
// -------------------------------------------
function checkAll(chkAll, itemName)
{
    if(!document.getElementsByName(itemName) )
        return;
        
    var fields = document.getElementsByName(itemName);
    for( i = 0; i < fields.length; i++ )
        fields[i].checked = chkAll.checked;
}

// -------------------------------------------
//toggles the CSS class for a selected item to selected/deselected
// -------------------------------------------
function changeOptionStyle(t)
{
    t.className = 'optSelect';

    t.onmouseout = function (e)
    {
      t.className = 'optDeselect';
      return false;
    };  

}

// -------------------------------------------
//Returns true if the given elmInner element is inside
// of the given elmOuter element.
// -------------------------------------------
function elementContains(elmOuter, elmInner)
{
  while (elmInner && elmInner != elmOuter)
  {
    elmInner = elmInner.parentNode;
  }
  if (elmInner == elmOuter)
  {
    return true;
  }
  return false;
}

// -------------------------------------------
//Given a menu with multiple DIVs, this function will set only 1 DIV
// to be visible at a time
// -------------------------------------------
function menuShow(menuDiv, visIndx)
{
    var subDiv = document.getElementById(menuDiv);
    var currentMenus = subDiv.getElementsByTagName('div');

    for( i = 0; i < currentMenus.length; i++ )
    {
        currentMenus[i].style.display = 'none';
    }

    currentMenus[visIndx].style.display = 'inline';
}

// -------------------------------------------
//Allows a check box to control the visiblily of
// a set of row <tr> elements
// -------------------------------------------
function showRowElems( elemName, toggleName)

{
    visible = toggleImg(toggleName) ;
    var elem = document.getElementById(elemName);
    var rows = elem.getElementsByTagName('tr');
    for( i = 0; i < rows.length; i++ ) {
    	var row = rows[i];
        row.style.display = (visible ? '' : 'none');
    }
}

// -------------------------------------------
//Allows a boolean to control the visiblily of
// a set of elements. If no boolean is given,
// the state is toggled 
// -------------------------------------------
function showSubMenu( elemName, visible )
{
    var currentMenus = document.getElementsByName(elemName);
    var toggle = visible != null;

    for (i = 0; i < currentMenus.length; i++)
    {
        if( visible == null )
        {
            if( currentMenus[i].style.display == 'none' )
                currentMenus[i].style.display = 'inline';
            else
                currentMenus[i].style.display = 'none';
        }
        else
            currentMenus[i].style.display = 
                (visible ? 'inline' : 'none');
    }
}

// -------------------------------------------
//Creates a URL string from the elements given.
// The attrib name/value pari is appended to the
// URL. Ensures that each element is only requested onces
// in the URL line. 
// -------------------------------------------
function createURLreq( elems, initialURL, attrib ) {
    var lastID = '';
    var sepChar = '?';
    if( initialURL.indexOf('?') >= 0 ) sepChar = '&';
    for (var i = 0; i < elems.length; i++, sepChar='&') {
        lastID = sepChar + attrib + '=' + elems[i].getAttribute(attrib);
        //only add the IDs that we do not have yet
        if( lastID != initialURL.substring(initialURL.lastIndexOf('&'), initialURL.length) ){
            initialURL += lastID;
        }
    }
    return initialURL;
}

function createResultMap(result) {
    var map = new Hash();
    for (var x = 0; x < result.length; x++) {
        var value = result[x];
        var key = getElementTextNS(result[x], 0, 'id');
        map[key] = value;
    }
    return map;
}

// -------------------------------------------
//	Callback function for the xml HTTP request to
// return the entire result object.
//	This popup will not close by itself, closing must be 
// handled by the html of the body.
// -------------------------------------------
function processMenuReqClosable() 
{
    var xmlHTTPreq = getReq(manMsgID);

    // only if req shows "complete" and HTTP code is "OK"
    if( xmlHTTPreq != null
        && getReq(manMsgID).req != null
        && getReq(manMsgID).req.readyState == 4 
        && getReq(manMsgID).req.status == 200 )
    {    
        var req = getReq(manMsgID).req;

        //store the response of the request
        response = req.responseText;

        overlib( response, FULLHTML, STICKY, FIXX, 225, FIXY, 125);

        //always do this
        freeReq( manMsgID );        
    }

}

// -------------------------------------------
//Callback function for the xml HTTP request to
// return the entire result object
//
// -------------------------------------------
function processMenuReq() 
{
    var xmlHTTPreq = getReq(manMsgID);

    // only if req shows "complete" and HTTP code is "OK"
    if( xmlHTTPreq != null
        && getReq(manMsgID).req != null
        && getReq(manMsgID).req.readyState == 4 
        && getReq(manMsgID).req.status == 200 )
    {    
        var req = getReq(manMsgID).req;

        //store the response of the request
        response = req.responseText;

        overlib(
            response, FULLHTML, STICKY, MOUSEOFF, FIXX, 225, FIXY, 225);

        //always do this
        freeReq( manMsgID );        
    }

}

// -------------------------------------------
// Shows a popup of the given message string.
// Uses the overlib library for display purposes.
// -------------------------------------------
function statusMsg(elem, msgStr) {
    elem.onmouseout = function (e) {
        nd();
    };
    overlib( msgStr, WIDTH, 160, CSSCLASS, TEXTFONTCLASS, 'flyover' );
}

// -------------------------------------------
// Shows a popup of the given message string.
// Uses the overlib library for display purposes.
// -------------------------------------------
function statusMsgAbove(elem, msgStr) {
    elem.onmouseout = function (e) {
        nd();
    };
    overlib( msgStr, WIDTH, 260, CSSCLASS, TEXTFONTCLASS, 'flyover', ABOVE );
}

// -------------------------------------------
// Shows a popup of the given message string.
// Uses the overlib library for display purposes.
// Leaves the closing of the popup to the caller.
// -------------------------------------------
function showDynamicPopup(elem) {
	showDynamicPopup(elem, 200);
}

// -------------------------------------------
// Shows a popup of the given message string with
// a custom width.
// Uses the overlib library for display purposes.
// Leaves the closing of the popup to the caller.
// -------------------------------------------
function showDynamicPopup(elem, spanWidth) {
    var spans = elem.getElementsByTagName('span');
    var msg = spans[0].innerHTML;
    overlib( msg, WIDTH, spanWidth, CSSCLASS, TEXTFONTCLASS, 'flyover' );
}

// -------------------------------------------
// Shows a popup of the given message string.
// Uses the overlib library for display purposes.
// Leaves the closing of the popup to the caller.
// -------------------------------------------
function showDynamicPopupAbove(elem) {
    showDynamicPopupAbove(elem, 200);
}

// -------------------------------------------
// Shows a popup of the given message string with
// a custom width.
// Uses the overlib library for display purposes.
// Leaves the closing of the popup to the caller.
// -------------------------------------------
function showDynamicPopupAbove(elem, spanWidth) {
    var spans = elem.getElementsByTagName('span');
    var msg = spans[0].innerHTML;
    overlib( msg, WIDTH, spanWidth, CSSCLASS, TEXTFONTCLASS, 'flyover', ABOVE );
}

// -------------------------------------------
//Shows a sticky popup for the checked box elements
// that are valid values AND that are checked. The returned
// html is placed in the popup box and then the box is
// made visible
// -------------------------------------------
function showPopUpChkBoxes( baseUrl ) {
    if( baseUrl == null ){
        return;
	}
    var elemSubs = document.getElementsByName('cti_chkbxSubBuses');
    var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
    var validElems = new Array();
    getValidChecks( elemSubs, validElems );
    getValidChecks( elemFdrs, validElems );
    var url = createURLreq( validElems, baseUrl, 'value' );
    manMsgID = loadXMLDoc(url, 'processMenuReqClosable');
}

// -------------------------------------------
//Shows a sticky popup for the checked box elements
// that are valid values AND that are checked. The returned
// html is placed in the popup box and then the box is
// made visible
// -------------------------------------------
function showRecentCmds( baseUrl )
{
    if( baseUrl == null )
        return;

    var elemSubs = document.getElementsByName('cti_chkbxSubBuses');
    var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
    var elemBanks = document.getElementsByName('cti_chkbxBanks');

    var validElems = new Array();
    getValidChecks( elemSubs, validElems );
    getValidChecks( elemFdrs, validElems );
    getValidChecks( elemBanks, validElems );
    var url = createURLreq( validElems, baseUrl, 'value' );
    window.location.href = '/spring/capcontrol/search/' + url;
}

function showRecentCmdsForSingle( baseUrl, id ){
	window.location.href = baseUrl + '?value=' + id;
}

// -------------------------------------------
//Simple post for a Wizard->Create action
// -------------------------------------------
function post( href )
{
    window.location = href;
}

// -------------------------------------------
//Adds the elems that are checked into a second
// arry called validElems
// -------------------------------------------
function getValidChecks( elems, validElems )
{
    var cnt = validElems.length;
    var returncount = 0;
    for( var i = 0; i < elems.length; i++ )
    {
        //validate the elements
        //if( elems[i].getAttribute('value') != '0' && elems[i].checked )
        if( elems[i].checked )
        {
            validElems[cnt++] = elems[i];
            returncount = 1;
        }
    }
    return returncount;
}

// -------------------------------------------
//Toggles the img from an expanded state to a collapsed state
// -------------------------------------------
function toggleImg( imgID ) {   
    var imgElem = document.getElementById(imgID);

    if( imgElem.src.indexOf('/capcontrol/images/nav-minus.gif') > 0 ) {
        imgElem.src='/capcontrol/images/nav-plus.gif';
        return false;
    }
    else {
        imgElem.src = '/capcontrol/images/nav-minus.gif';
        return true;
    }
}

function addLockButtonForButtonGroup (groupId, secs) {
Event.observe(window, 'load', function() {
var button_group = document.getElementById(groupId);
var buttons = button_group.getElementsByTagName("input");

for (var i=0; i<buttons.length; i++) {
    var button_el =  buttons.item(i);
    lock_buttons(button_el.id);
}
});

if (secs != null)
	{
		pause (secs * 1000);
	}
}

function lock_buttons(el_id){
var button_el = document.getElementById(el_id);
var parent_el = document.getElementById(button_el.parentNode.id);
var button_els = parent_el.getElementsByTagName("input");
Event.observe(button_el, 'click', function () {
for (var i=0; i < button_els.length; i++)
{
 var current_button = document.getElementById(button_els.item(i).id);
 if (current_button.id != el_id) {
    current_button.disabled = true;
    }
 else {    
    setTimeout("document.getElementById('" + el_id + "').disabled=true;", 1);
 }

}

});

}

function lockButtonsPerSubmit (groupId) {
var button_group = document.getElementById(groupId);
var buttons = button_group.getElementsByTagName("input");

for (var i=0; i<buttons.length; i++) {
    var button_el =  buttons.item(i);
    button_el.disabled = true;
    }
}

function pause(numberMillis) {
        var now = new Date();
        var exitTime = now.getTime() + numberMillis;
        while (true) {
            now = new Date();
            if (now.getTime() > exitTime)
                return;
        }
    }

    function applySubBusFilter(select){
		var rows = $$('#subBusTable tr.altTableCell', '#subBusTable tr.tableCell');
        var subBusNames = new Array();
        if(select.options[select.selectedIndex].text == 'All SubBuses'){
        	selectedSubBus = 'All SubBuses';
        	for (var i=0; i < rows.length; i++) {
	            var row = rows[i];
            	row.setStyle({'display' : ''});
            	var cells = row.getElementsByTagName('td');
                var firstCell = cells[0];
                var firstSpan = firstCell.getElementsByTagName('span')[0];
                var subBusName = new String (firstSpan.innerHTML);
                
	            subBusNames.push(trim(subBusName));
       		}
       		$('feederFilter').selectedIndex = 0;
       		applyFeederFilter(subBusNames);
        }else{
	        for (var i=0; i < rows.length; i++) {
	            var row = rows[i];
	            var cells = row.getElementsByTagName('td');
	            var firstCell = cells[0];
                var firstSpan = firstCell.getElementsByTagName('span')[0];
                var subBusName = new String (firstSpan.innerHTML);
	            
	            var selectedSubBus = new String (select.options[select.selectedIndex].text);
	            //displayed name always contains a white space at the end
	            if (trim(subBusName) == trim (selectedSubBus)){
	                row.setStyle({'display' : ''});
	                subBusNames.push(trim(subBusName));
	            }else{
	            	row.style.display = 'none';
	            }
	        }
	        applyFeederFilter(subBusNames);
        }
    }
    
    function applyFeederSelectFilter(select){
		var rows = $$('#fdrTable tr.altTableCell', '#fdrTable tr.tableCell');
        var feederNames = new Array();
        var selectedFeeder = new String (select.options[select.selectedIndex].text);
        
        if(selectedFeeder == 'All Feeders'){
        	for (var i=0; i < rows.length; i++) {
	            var row = rows[i];
            	row.setStyle({'display' : ''});
            	var cells = row.getElementsByTagName('td');
	            var fdr = cells[0];
	            var spans = fdr.getElementsByTagName('span');
	            var fdrName = new String (spans[1].innerHTML);
	            feederNames.push(trim(fdrName));
       		}
       		applyCapBankFilter(feederNames);
        }else{
	        for (var i=0; i < rows.length; i++) {
	            var row = rows[i];
	            var cells = row.getElementsByTagName('td');
	            var fdr = cells[0];
	            var spans = fdr.getElementsByTagName('span');
	            var fdrName = new String (spans[1].innerHTML);
	            if (trim(fdrName) == trim (selectedFeeder)){
	                row.setStyle({'display' : ''});
	                feederNames.push(trim(fdrName));
	            }else{
	            	row.style.display = 'none';
	            }
	        }
	        applyCapBankFilter(feederNames);
        }
    }
    
    function applyFeederFilter(subBusNames){
    	var rows = $$('#fdrTable tr.altTableCell', '#fdrTable tr.tableCell');
        var feederNames = new Array();
        for (var i=0; i < rows.length; i++) {
            var row = rows[i];
            var cells = row.getElementsByTagName('td');
            var firstCell = cells[0];
            var firstSpan = firstCell.getElementsByTagName('span')[0];
            
            var subBusName = new String (firstSpan.innerHTML);
            var index = subBusNames.indexOf(trim(subBusName));

			if(index > -1){
				row.setStyle({'display' : ''});
				var fdr = cells[0];
				var spans = fdr.getElementsByTagName('span');
				var fdrName = spans[1].innerHTML;
				feederNames.push(trim(fdrName));
			}else{
				row.setStyle({'display' : 'none'});
			}
        }
        applyCapBankFilter(feederNames);
    }
    
    function applyCapBankFilter(feederNames){
	    var rows = $$('#capBankTable tr.altTableCell', '#capBankTable tr.tableCell');
    	for (var i=0; i < rows.length; i++) {
            var row = rows[i];
            var cells = row.getElementsByTagName('td');
            var firstCell = cells[0];
            var feederSpan = firstCell.getElementsByTagName('span')[0].firstChild;
	        var fdrName = new String (feederSpan.innerHTML);
    		var index = feederNames.indexOf(trim(fdrName));
    		if(index > -1){
				row.setStyle({'display' : ''});
			}else{
				row.setStyle({'display' : 'none'});
			}
		}
    }

function isOnTheList (list, string) {
    for(var i=0; i < list.length; i++) {
        if (list[i] == string)
            return true;
    }
return false;
}   
/**
 * Remove white space from a string.
 */
function trim (s) {
    return s.replace(/^\s+|\s+$/g, '');
}

function isValidOpcount (number){
var ret = parseInt(number)
if (Number.NaN != ret) {
    if ((ret >=0) && (ret < 100000))
        return true;
        }
return false;
}
//old method that goes to retrieve the static SVG file
function showStaticOneLine () {

   var elemSubs = document.getElementsByName('cti_chkbxSubBuses');
   var validElems = new Array();
   getValidChecks( elemSubs, validElems );
   if ( validElems.length <= 0 ) {
        alert('Select Substation To View');
        return;
        }
    else {      
        if (validElems.length > 1)
            alert ("You can only copy 1 item at a time"); 
         var anc_id = 'anc_' + validElems[0].getAttribute('value');        
         //there is an id for the sub name
         if (document.getElementById (anc_id)) {
            var subName = document.getElementById (anc_id).innerText;
            subName = subName.removeLeadTrailSpace();
            var url = "/capcontrol/oneline/" + subName + ".html";
            window.location.href = url;
            }
         else {
            alert ("Couldn't open window - URL invalid");
            return;         
         }
   }
}
function showOneLine () {
   var currLoc = window.location.href;
   var elemSubs = document.getElementsByName('cti_chkbxSubBuses');
   var validElems = new Array();
   getValidChecks( elemSubs, validElems );
   if ( validElems.length <= 0 ) {
        alert('Please select a Substation Bus to view');
        return;
        }
    else {      
        if (validElems.length > 1){
            alert ("You can only view 1 item at a time");
            return;
        }
        var anc_id = 'anc_' + validElems[0].getAttribute('value');        
        //there is an id for the sub name
        if (document.getElementById (anc_id)) {
           id = anc_id.split('_')[1];
           url = "/capcontrol/oneline/OnelineCBCServlet?id=" + id + "&redirectURL=" + currLoc;
           post(url);
           }
        else {
           alert ("Couldn't open window - URL invalid");
           return;         
        }
   }
}

function  openConfirmWin (url) {
    GreyBox.preloadGreyBoxImages();
    return GB_show('One Line Report',url, 700, 700);
}

function removeSpaceFromEnd (string) {
var retStr = new String (string);
for (var i=string.length - 1; i >= 0; i --) {
if (string.charAt(i) == ' ') {
    retStr = string.substr (0, i); 
    }
 else
    break;            
}
return retStr;
}

function removeSpaceFromBeginning (string) {
var retStr = new String (string);
for (var i=0; i < string.length; i ++) {
if (string.charAt(i) == ' ') {
    retStr = string.substr (i+1, string.length ); 
    }
 else
    break;            
}
return retStr;
}

String.prototype.removeLeadTrailSpace = function () {
    return new String ( removeSpaceFromBeginning ( removeSpaceFromEnd (this) ) );
}

function mouseX(evt) {
	if (evt.clientX) {
		return evt.clientX;
	} else {
		return null;
	}
}

function mouseY(evt) {
	if (evt.clientY) {
		return evt.clientY;
	} else {
		return null;
	}
}