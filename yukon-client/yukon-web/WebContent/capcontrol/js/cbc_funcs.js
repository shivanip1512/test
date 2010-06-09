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
	var url = '/spring/capcontrol/tier/popupmenu?menu=ltcPointList&ltcId=' + id;
	var ltcNameAnchor = $('ltcName_' + id);
	var title = ltcNameAnchor.innerHTML.strip();
	openSimpleDialog('tierContentPopup', url, title, null, null, 'get');
}

function showComments(paoName, url) {
	openSimpleDialog('tierContentPopup', url, 'Comments for ' + paoName, null, null, 'get');
}

function showCbcPointList(id, cbcName) {
	var url = '/spring/capcontrol/oneline/popupmenu?menu=pointTimestamp&cbcID=' + id;
	openSimpleDialog('tierContentPopup', url, 'CBC Points: ' + cbcName, null, null, 'get');
}

function showCapBankAddInfo(id, cbName) {
	var url = '/spring/capcontrol/capAddInfo/view?paoId=' + id;
	openSimpleDialog('tierContentPopup', url, 'Cap Bank Additional Information: ' + cbName, null, null, 'get');
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
	var popupDiv = $('tierMenuPopup');
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
	var popupDiv = $('tierMenuPopup');
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
	var tierPopup = $('tierMenuPopup');
	tierPopup.hide();
}

function closeTierContentPopup() {
	var tierContentPopup = $('tierContentPopup');
	tierContentPopup.hide();
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
			} else {    
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