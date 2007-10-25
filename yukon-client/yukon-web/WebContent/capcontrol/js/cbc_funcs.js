var manMsgID = 0;
var GB_IMG_DIR = "../../editor/css/greybox/";

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
//CallBack method for the XMLhttp request/refresh.
// Uses a proxy JSP to get around security concerns
// in XMLhttp
// -------------------------------------------
function callBack( response)
{
    if( response != null )
    { 
        // Response mode
        updateHTML(response);
    }
    else
    {   
        // Input mode
        var elems = document.getElementsByName('cti_dyn');
        var url = createURLreq( elems, '/servlet/CBCServlet?method=callBack', 'id' );
        var lastUpdate = document.getElementById ('lastUpdate');
        if (lastUpdate != null)
        {
            url += '&lastUpdate=' + lastUpdate.value;
        }
        else if (elems.length > 0)
        {
            alert('Web page is not timestamped. Values will not be updated dynamically!');
            return;
        }
        loadXMLDoc(url);
    }
}

// -------------------------------------------
//Creates a URL string from the elements given.
// The attrib name/value pari is appended to the
// URL. Ensures that each element is only requested onces
// in the URL line. 
// -------------------------------------------
function createURLreq( elems, initialURL, attrib )
{
    var lastID = '';
    var sepChar = '?';
    if( initialURL.indexOf('?') >= 0 ) sepChar = '&';

    for (var i = 0; i < elems.length; i++, sepChar='&')
    {
        lastID = sepChar + attrib + '=' + elems[i].getAttribute(attrib);

        //only add the IDs that we do not have yet
        if( lastID != initialURL.substring(initialURL.lastIndexOf('&'), initialURL.length) )
            initialURL += lastID;
    }

    return initialURL;
}

// -------------------------------------------
//Update the HTML on the screen with the results
// returned from the XMLhttp call
// -------------------------------------------
function updateHTML( result)
{
    if( result != null )
    {
        
        var elems = document.getElementsByName('cti_dyn');
        for (var i = 0; i < result.length; i++)
        {
            var xmlID = getElementTextNS(result[i], 0, 'id');

            for( var j = 0; j < elems.length; j++ )
            {
                if( elems[j].getAttribute('id') == xmlID )
                {
                    var elemType = elems[j].getAttribute('type');
                    switch( elemType )
                    {
                        //special case since 2 elements are involved with 1 TAG
                        case 'state':
                            var xmlColor = getElementTextNS(result[i], 0, 'param0');                            
                            elems[j].style.color = xmlColor;

                        
                        //most of this time this will suffice
                        default:
                            elems[j].innerHTML = getElementTextNS(result[i], 0, elemType);
                            break;
                    }
                }
            }
        }
        updateCommandMenu(result);
        
        var lastUpdate = document.getElementById('lastUpdate');
        if (lastUpdate)
            lastUpdate.value = new Date().getTime();
        setTimeout('callBack()', clientRefresh );
      
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
//Shows a popup of the given message string.
// Uses the overlib library for display purposes.
// -------------------------------------------
function statusMsg(elem, msgStr)
{
    elem.onmouseout = function (e)
    {
        nd();
    };  
    overlib( msgStr, WIDTH, 160, CSSCLASS, TEXTFONTCLASS, 'flyover' );
}

// -------------------------------------------
//Shows a sticky popup for the checked box elements
// that are valid values AND that are checked. The returned
// html is placed in the popup box and then the box is
// made visible
// -------------------------------------------
function showPopUpChkBoxes( baseUrl )
{
    if( baseUrl == null )
        return;

    var elemSubs = document.getElementsByName('cti_chkbxSubs');
    var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
    //var elemBanks = document.getElementsByName('cti_chkbxBanks');

    var validElems = new Array();
    getValidChecks( elemSubs, validElems );
    getValidChecks( elemFdrs, validElems );
    //getValidChecks( elemBanks, validElems );

    var url = createURLreq( validElems, baseUrl, 'value' );
    manMsgID = loadXMLDoc(url, 'processMenuReq');
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

    var elemSubs = document.getElementsByName('cti_chkbxSubs');
    var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
    var elemBanks = document.getElementsByName('cti_chkbxBanks');

    var validElems = new Array();
    getValidChecks( elemSubs, validElems );
    getValidChecks( elemFdrs, validElems );
    getValidChecks( elemBanks, validElems );

    var url = createURLreq( validElems, baseUrl, 'value' );
    window.location = url;
}

// -------------------------------------------
//Shows a sticky popup for the given urlStr The returned
// html is placed in the popup box and then the box is
// made visible
// -------------------------------------------
function showPopUp( urlStr )
{
    if( urlStr == null )
        return;

    //var url = createURLreq( validElems, 'charts.jsp?type='+typeStr, 'value' );
    manMsgID = loadXMLDoc(urlStr, 'processMenuReq');
}

// -------------------------------------------
//Returns the URL based on the name of the
// selected elements
// -------------------------------------------
function getUrlType( selElems, type ) {

    if( type == null )
        type = 'editor';

    if ( selElems.length > 0 ) {

        elemName = selElems[0].getAttribute('name');

        if( elemName == 'cti_chkbxAreas' ||elemName == 'cti_chkbxSubs' || elemName == 'cti_chkbxFdrs'
                || elemName == 'cti_chkbxBanks' ) {
            return '/servlet/CBCServlet?type=nav&pageType='+type+'&modType=capcontrol';
        }
        else if( elemName == 'cti_chkbxPoints' ) {
        
            return '/servlet/CBCServlet?type=nav&pageType='+type+'&modType=point';
        }
    }

    return '';
}

// -------------------------------------------
//Posts to the correct URL with the checked item
// for editing
// -------------------------------------------
function editorPost()
{
    var elemAreas = document.getElementsByName('cti_chkbxAreas');	
    var elemSubs = document.getElementsByName('cti_chkbxSubs');
    var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
    var elemBanks = document.getElementsByName('cti_chkbxBanks');
    var elemPoints = document.getElementsByName('cti_chkbxPoints');

    var validElems = new Array();
    getValidChecks( elemAreas, validElems );
    getValidChecks( elemSubs, validElems );
    getValidChecks( elemFdrs, validElems );
    getValidChecks( elemBanks, validElems );
    getValidChecks( elemPoints, validElems );

    //only allow the editing of the zeroth element for now
    if ( validElems.length <= 0 ){
        alert('You must check the item you want to edit first');
    }else if (validElems.length > 1){
        alert ("You can only edit 1 item at a time");
	}else{
        window.location =
            getUrlType(validElems) + '&itemid=' + validElems[0].getAttribute('value');
    }
}

// -------------------------------------------
//Posts to the correct URL with the checked item
// for editing
// -------------------------------------------
function copyPost()
{
	var elemAreas = document.getElementsByName('cti_chkbxAreas');
    var elemSubs = document.getElementsByName('cti_chkbxSubs');
    var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
    var elemBanks = document.getElementsByName('cti_chkbxBanks');
    var elemPoints = document.getElementsByName('cti_chkbxPoints');

    if ( elemAreas.length > 0 ){
		//Cannot Copy area's forwarding to the creation wizard.
    	window.location = '/editor/cbcWizBase.jsf?type=4002';
    	return;
    }
    
    var validElems = new Array();
    var isPoint = 0;
    var isCap = 0;
    getValidChecks( elemSubs, validElems );
    getValidChecks( elemFdrs, validElems );
    isCap   += getValidChecks( elemBanks, validElems );
	isPoint += getValidChecks( elemPoints, validElems );
    //only allow the editing of the zeroth element for now
    if ( validElems.length <= 0 )
        alert('You must check the item you want to edit first');
    else {      
        if (validElems.length > 1)
            alert ("You can only copy 1 item at a time"); 
        var type = 0;   
        if (isPoint)  type = 2;
        if(isCap)    type =1;
        window.location = getUrlType(validElems, 'copy') + '&itemid=' + validElems[0].getAttribute('value') + '&type=' + type;        
    }
}
// -------------------------------------------
//Posts to the correct URL with the checked item
// for editing
// -------------------------------------------
function deletePost()
{
    var elemAreas = document.getElementsByName('cti_chkbxAreas');
    var elemSubs = document.getElementsByName('cti_chkbxSubs');
    var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
    var elemBanks = document.getElementsByName('cti_chkbxBanks');
    var elemPoints = document.getElementsByName('cti_chkbxPoints');

    var validElems = new Array();
    getValidChecks( elemAreas, validElems );
    getValidChecks( elemSubs, validElems );
    getValidChecks( elemFdrs, validElems );
    getValidChecks( elemBanks, validElems );
    
    //only add the points if we do not have any PAOs selected
    if( validElems.length <= 0 )
        getValidChecks( elemPoints, validElems );

    //force the selection of something
    if ( validElems.length <= 0 )
        alert('You must check one or more items that you want to delete');
    else {
        window.location =   
            createURLreq( validElems, getUrlType(validElems, 'delete'), 'value' );
    }
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
//Creates an IFrame to be used primarily for
// popups
// -------------------------------------------
function createIFrame(src, width, height, name, frameborder) {

    return '<iframe src="'+src+'" width="'+width+'" height="'+height+'"'
        + (name!=null?' name="'+name+'" id="'+name+'"':'')
        + (frameborder!=null?' frameborder="'+frameborder+'"':'')
        + ' scrolling="auto" marginwidth="1" marginheight="1" >'
        + '<div>[iframe not supported]</div></iframe>';
}

// -------------------------------------------
//Toggles the img from an expanded state to a collapsed state
// -------------------------------------------
function toggleImg( imgID ) {   
    var imgElem = document.getElementById(imgID);

    if( imgElem.src.indexOf('images/nav-minus.gif') > 0 ) {
        imgElem.src='images/nav-plus.gif';
        return false;
    }
    else {
        imgElem.src = 'images/nav-minus.gif';
        return true;
    }
}



function alignHeaders(mainTable, headerTable) {
mytable = document.getElementById(mainTable);

hdrTable =  document.getElementById(headerTable);
	if (hdrTable)
	{
	hdrRow=hdrTable.getElementsByTagName('tr').item(0);
	
		for (j=0; j < mytable.getElementsByTagName('tr').length; j ++ ) {
		    var myrow = mytable.getElementsByTagName('tr').item(j);  
		     if ((myrow != null) && myrow.style.display != 'none' && hdrRow.style.display != 'none') {
		        var colNum = myrow.cells.length;
		        
		        for(i=0;i < colNum - 1; i++) {
		            var hdrCell = hdrRow.getElementsByTagName('td').item(i);
		            var myrowCell = myrow.cells[i];
		            if ((hdrCell.style.display != 'none') && (myrowCell.style.display != 'none')) {
		                maxWidth = Math.max(hdrCell.offsetWidth, myrowCell.offsetWidth);
		                hdrCell.width = maxWidth;
		                myrowCell.width = maxWidth;
		                }
		                                                                       
		            }
		    
		        }
		    }
	}
}


function enableDisplayAll (mainTable) {
    var mytable = document.getElementById(mainTable);
    for (j=0; j < mytable.getElementsByTagName('tr').length; j ++ ) {
        var myrow = mytable.getElementsByTagName('tr').item(j);
     
        if ((myrow != null) && myrow.style.display != 'none') {
            var colNum = myrow.cells.length;
            for(i=0;i < colNum - 1; i++) {
                myrow.cells[i].style.display = '';
            }
        }
    }
}


function alignHeadersIgnoreDisplayNone (mainTable, headerTable, invis_idx) {
mytable = document.getElementById(mainTable);
hdrTable =  document.getElementById(headerTable);
if (hdrTable)
	{
	hdrRow=hdrTable.getElementsByTagName('tr').item(0);
		if (hdrRow)
	    {
			for (j=0; j < mytable.getElementsByTagName('tr').length; j ++ ) 
            {
			    var myrow = mytable.getElementsByTagName('tr').item(j);
			    
			     if (myrow != null) 
                 {
			        var colNum = myrow.cells.length;
			    
			        for(i=0;i < colNum - 1; i++) 
                    {
			            maxWidth = Math.max(hdrRow.getElementsByTagName('td').item(i).offsetWidth, myrow.cells[i].offsetWidth);
			            hdrRow.getElementsByTagName('td').item(i).width = maxWidth;
			            myrow.cells[i].width = maxWidth;
			            if (i == invis_idx)
			                myrow.cells[i].style.display = 'none';
			                                                                       
			         }
			    
			      }
			 }
		}
    }
}

var CtiNonScrollTable = Class.create();
CtiNonScrollTable.prototype = {
  initialize: function(mainTable, headerTable) {
    this.mainTable = mainTable;
    this.headerTable = headerTable;
    alignHeaders(mainTable, headerTable);
    Event.observe(window, 'resizeend', function () { 
                                                    alignHeaders (mainTable, headerTable)
        });
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
    


    function applyFilter(select_filter, parent_table, column_filter_index) {
        var rows = parent_table.getElementsByTagName('tr'); 
        //make all rows visible
        for (var i=0; i < rows.length; i++) {
            var row = rows[i];
            row.style.display = 'block';        
        }
        if (select_filter.options[select_filter.selectedIndex].text == 'All Feeders')
            return;
        for (var i=0; i < rows.length; i++) {
            var row = rows[i];
            var cells = row.getElementsByTagName('td');
            var parent_fdr = cells[column_filter_index];
            displayed_name = new String (parent_fdr.innerText);
            selected_name = new String (select_filter.options[select_filter.selectedIndex].text);
            //displayed name always contains a white space at the end
            if (trim(displayed_name) != trim (selected_name))
                row.style.display = 'none';
        }   
    }
    
    function applySubBusFilter(select){
		var rows = $$('#subTable tr.altTableCell', '#subTable tr.tableCell');
        var subBusNames = new Array();
        if(select.options[select.selectedIndex].text == 'All SubBuses'){
        	selectedSubBus = 'All SubBuses';
        	for (var i=0; i < rows.length; i++) {
	            var row = rows[i];
            	row.setStyle({'display' : ''});
            	var cells = row.getElementsByTagName('td');
	            var sub = cells[1];
	            var spans = sub.getElementsByTagName('span');
	            var subName = new String (spans[0].innerHTML);
	            subBusNames.push(trim(subName));
       		}
       		$('feederFilter').selectedIndex = 0;
       		applyFeederFilter(subBusNames);
        }else{
	        for (var i=0; i < rows.length; i++) {
	            var row = rows[i];
	            var cells = row.getElementsByTagName('td');
	            var sub = cells[1];
	            var spans = sub.getElementsByTagName('span');
	            var subName = new String (spans[0].innerHTML);
	            var selectedSubBus = new String (select.options[select.selectedIndex].text);
	            //displayed name always contains a white space at the end
	            if (trim(subName) == trim (selectedSubBus)){
	                row.setStyle({'display' : ''});
	                subBusNames.push(trim(subName));
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
	            var fdrName = new String (spans[0].innerHTML);
	            feederNames.push(trim(fdrName));
       		}
       		applyCapBankFilter(feederNames);
        }else{
	        for (var i=0; i < rows.length; i++) {
	            var row = rows[i];
	            var cells = row.getElementsByTagName('td');
	            var fdr = cells[0];
	            var spans = fdr.getElementsByTagName('span');
	            var fdrName = new String (spans[0].innerHTML);
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
            var sub = cells[8];
            var subBusName = new String (sub.innerHTML);
			var index = subBusNames.indexOf(subBusName);
			if(index > -1){
				row.setStyle({'display' : ''});
				var fdr = cells[0];
				var spans = fdr.getElementsByTagName('span');
				var fdrName = spans[0].innerHTML;
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
            var fdr = cells[7];
            var spans = fdr.getElementsByTagName('span');
	        var fdrName = new String (spans[0].innerHTML);
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

   var elemSubs = document.getElementsByName('cti_chkbxSubs');
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
   var elemSubs = document.getElementsByName('cti_chkbxSubs');
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

function results (param) {
	redirect = 'results.jsp?'+param;
	post(redirect);
}