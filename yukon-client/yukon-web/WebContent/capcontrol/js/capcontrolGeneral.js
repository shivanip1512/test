
function checkPageExpire() {
    var paoIds = Object.toJSON($$("input[id^='paoId_']").pluck('value'));
    var url = '/spring/capcontrol/pageExpire';
    
    new Ajax.Request(url, {
        'method': 'POST',
        'parameters' : {'paoIds': paoIds},
        onSuccess: function(transport) {
            var html = transport.responseText;
            var expired = eval(html);
            if (expired) {
                $('updatedWarning').show();    
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

function getRegulatorMenu(id, event) {
    var url = '/spring/capcontrol/tier/popupmenu?menu=regulatorMenu&id=' + id;
    getMenuFromUrlLeft(url, event); 
}

function getRegulatorPointsList(id) {
	var url = '/spring/capcontrol/tier/popupmenu?menu=regulatorPointList&regulatorId=' + id;
	var regulatorNameAnchor = $('regulatorName_' + id);
	var title = regulatorNameAnchor.innerHTML.strip();
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
		x = x - 165;
	}
	popupDiv.setStyle({
		top: y + "px",
		left: x + "px",
		width: 165 + "px"
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
  //assumes data is of type Hash
    return function(data) {
        var anchorTag = $(id);
        var state = data.get('value');
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
  //assumes data is of type Hash
    return function(data) {
        var alertSpan = $(id + '_alert');
        var okSpan = $(id + '_ok');
        
        var isWarning = eval(data.get('value'));
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
  //assumes data is of type Hash
    return function(data) {
        var primarySpan = $(id + '_primary');
        var alternateSpan = $(id + '_alternate');
        
        var icon = data.get('value');
        
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
  //assumes data is of type Hash
    return function(data) {
        var yellowSpan = $(id + '_yellow');
        var greenSpan = $(id + '_green');
        var yellowLocalSpan = $(id + '_yellow_local');
        var greenLocalSpan = $(id + '_green_local');
        
        var icon = data.get('value');
        
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

function updateRegulatorModeIndicator(id){
  //assumes data is of type Hash
	return function(data) {
        var yellowLocalSpan = $(id + '_local_warning');
        var greenLocalSpan = $(id + '_local_normal');
        var greenNormalSpan = $(id + '_normal');
        
        var icon = data.get('value');
        
        if (icon == 'none') {
            yellowLocalSpan.hide();
            greenLocalSpan.hide();
            greenNormalSpan.show();
        } else if (icon == 'NormalLocal'){
            yellowLocalSpan.hide();
            greenLocalSpan.show();
            greenNormalSpan.hide();
        } else if (icon == 'WarningLocal'){
            yellowLocalSpan.show();
            greenLocalSpan.hide();
            greenNormalSpan.hide();
        }
	}
}

function updateRegulatorThreePhaseTapIndicator(zoneId, zoneType, phase) {

    var tapContainer = $('tapContainer_' + zoneId);
    var divPhaseA = tapContainer.down('.phaseA');
    var divPhaseB = tapContainer.down('.phaseB');
    var divPhaseC = tapContainer.down('.phaseC');
    
    if (zoneType == 'THREE_PHASE') {
        // assumes data is of type Hash
        return function(data) {
            
            hideAll();

            var modeA = data.get('modeA');
            var modeB = data.get('modeB');
            var modeC = data.get('modeC');
            var tapIconA = data.get('tapA');
            var tapIconB = data.get('tapB');
            var tapIconC = data.get('tapC');
            var tapTooltipA = data.get('tapTooltipA');
            var tapTooltipB = data.get('tapTooltipB');
            var tapTooltipC = data.get('tapTooltipC');
            
            setMode(modeA, divPhaseA);
            setMode(modeB, divPhaseB);
            setMode(modeC, divPhaseC);
            
            setTapIcon(tapIconA, modeA, divPhaseA, tapTooltipA);
            setTapIcon(tapIconB, modeB, divPhaseB, tapTooltipB);
            setTapIcon(tapIconC, modeC, divPhaseC, tapTooltipC);
        }
    } else if (zoneType == 'GANG_OPERATED') {
        // assumes data is of type Hash
        return function(data) {
            
            hideAll();
            
            var mode = data.get('mode');
            var icon = data.get('value');
            var tapTooltip = data.get('tapTooltip');

            setMode(mode, divPhaseA);
            setMode(mode, divPhaseB);
            setMode(mode, divPhaseC);
            
            setTapIcon(icon, mode, divPhaseA, tapTooltip);
            setTapIcon(icon, mode, divPhaseB, tapTooltip);
            setTapIcon(icon, mode, divPhaseC, tapTooltip);
        }
    } else if (zoneType == 'SINGLE_PHASE') {
        // assumes data is of type Hash
        return function(data) {
            
            hideAll();

            var mode = data.get('mode');
            var icon = data.get('value');
            var tapTooltip = data.get('tapTooltip');
            var divPhase;

            if (phase == 'A') {
                divPhase = divPhaseA;
            } else if (phase == 'B') {
                divPhase = divPhaseB;
            } else {
                divPhase = divPhaseC;
            }

            setMode(mode, divPhase);
            setTapIcon(icon, mode, divPhase, tapTooltip);
        }
    }

    function setMode(mode, div) {
        if (mode == 'NormalLocal'){
            setModeNormalLocal(div);
        } else if (mode == 'WarningLocal'){
            setModeWarningLocal(div);
        } else {
            setModeNormalRemote(div);
        }
    }
    
    function setTapIcon(tapIcon, mode, div, tapTooltip) {
        if (tapIcon == 'RAISE_TAP') {
            if (mode == 'WarningLocal') {
                showTapRaiseWarning(div, tapTooltip);
            } else {
                showTapRaise(div, tapTooltip);
            }
        } else if (tapIcon == 'LOWER_TAP') {
            if (mode == 'WarningLocal') {
                showTapLowerWarning(div, tapTooltip);
            } else {
                showTapLower(div, tapTooltip);
            }
        } else if (tapIcon == 'LOWER_TAP_RECENT') {
            if (mode == 'WarningLocal') {
                showTapLowerRecentWarning(div, tapTooltip);
            } else {
                showTapLowerRecent(div, tapTooltip);
            }
        } else if (tapIcon == 'RAISE_TAP_RECENT') {
            if (mode == 'WarningLocal') {
                showTapRaiseRecentWarning(div, tapTooltip);
            } else {
                showTapRaiseRecent(div, tapTooltip);
            }
        } else {
            showTapDefault(div, tapTooltip);
        }
    }
    
    function showTapDefault(phase, tapTooltip) {
        var tapDiv = phase.down('.tapDefault');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapRaise(phase, tapTooltip) {
        var tapDiv = phase.down('.tapRaise');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapRaiseWarning(phase, tapTooltip) {
        var tapDiv = phase.down('.tapRaiseWarning');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapLower(phase, tapTooltip) {
        var tapDiv = phase.down('.tapLower');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapLowerWarning(phase, tapTooltip) {
        var tapDiv = phase.down('.tapLowerWarning');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapLowerRecent(phase, tapTooltip) {
        var tapDiv = phase.down('.tapLowerRecent');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapLowerRecentWarning(phase, tapTooltip) {
        var tapDiv = phase.down('.tapLowerRecentWarning');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapRaiseRecent(phase, tapTooltip) {
        var tapDiv = phase.down('.tapRaiseRecent');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapRaiseRecentWarning(phase, tapTooltip) {
        var tapDiv = phase.down('.tapRaiseRecentWarning');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }

    function setModeNormalRemote(divPhase) {
        divPhase.down('.regulatorModeRemote').show();
    }
    function setModeNormalLocal(divPhase) {
        divPhase.down('.regulatorModeLocal').show();
    }
    function setModeWarningLocal(divPhase) {
        divPhase.down('.regulatorModeLocalWarning').show();
    }

    function hideAll() {
        var phaseContainers = tapContainer.childElements();
        phaseContainers.each(function(phaseContainer) {
            var phaseElements = phaseContainer.childElements();
            phaseElements.each(function(phaseElement) {
                phaseElement.hide();
            });
        });
    }
}

function updateVerificationImage(spanId) {
  //assumes data is of type Hash
    return function(data) {
        var isVerification = eval(data.get('value'));
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
function showDynamicPopup(elem, popupWidth) {
    var msg = elem.innerHTML;
    overlib( msg, WIDTH, popupWidth, CSSCLASS, TEXTFONTCLASS, 'flyover' );
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
function showDynamicPopupAbove(elem, popupWidth) {
    var msg = elem.innerHTML;
    overlib( msg, WIDTH, popupWidth, CSSCLASS, TEXTFONTCLASS, 'flyover', ABOVE );
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

function applySubBusFilter(select) {
	
    var rows = $$("tr[id^='tr_sub_']"); /* tr's whose id begins with 'tr_sub_' */
    var subBusIds = new Array();
    
    if(select.options[select.selectedIndex].text == 'All SubBuses'){
        
        rows.each(function (row) {
            row.show();
            subBusIds.push(row.id.split('_')[2]);
        });
        
        $('feederFilter').selectedIndex = 0;
   		applyFeederFilter(subBusIds);
   		
    } else {
        var selectedSubBusId = select.options[select.selectedIndex].value;
        rows.each(function (row) {
            var subBusId =  row.id.split('_')[2];
            
            if (subBusId == selectedSubBusId){
                row.show();
                subBusIds.push(subBusId);
            } else {
            	row.hide();
            }
            
        });
        
        applyFeederFilter(subBusIds);
    }
}

function applyFeederSelectFilter(select) {
	var rows = $$("tr[id^='tr_feeder_']"); /* tr's whose id begins with 'tr_feeder_' */
    var feederIds = new Array();
    
    if(select.options[select.selectedIndex].text == 'All Feeders'){
        rows.each(function (row) {
        	row.show();
            feederIds.push(row.id.split('_')[2]);
        });
        
        applyCapBankFilter(feederIds);
   		
    } else {
        var selectedFeederId = select.options[select.selectedIndex].value;
        rows.each(function (row) {
            var feederId =  row.id.split('_')[2];
            
            if (feederId == selectedFeederId){
                row.show();
                feederIds.push(feederId);
            } else {
            	row.hide();
            }
        });
        applyCapBankFilter(feederIds);
    }
}

function applyFeederFilter(subBusIds) {
    var rows = $$("tr[id^='tr_feeder_']"); /* tr's whose id begins with 'tr_feeder_' */
    var feederIds = new Array();
    
    rows.each(function(row) {
        var parentId = row.id.split('_')[4];
        
		if (subBusIds.indexOf(parentId) != -1) {
			row.show();
			feederIds.push(row.id.split('_')[2]);
		} else {
		    row.hide();
		}
		
    });
    applyCapBankFilter(feederIds);
}

function applyCapBankFilter(feederIds) {
    
    var rows = $$("tr[id^='tr_cap_']"); /* tr's whose id begins with 'tr_cap_' */
    
    rows.each(function(row) {
        var parentId = row.id.split('_')[4];
        
        if (feederIds.indexOf(parentId) != -1) {
            row.show();
        } else {
            row.hide();
        }
        
    });
    
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
        if ((ret >=0) && (ret < 100000)) {
            return true;
        }
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