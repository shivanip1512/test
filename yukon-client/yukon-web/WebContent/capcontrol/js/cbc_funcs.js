
var manMsgID = 0;

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
//alert('f.'+arguments[i]+' = ' + arguments[i+1]);

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
function showRowElems( elemName, visible )
{
	var elem = document.getElementById(elemName);
	var rows = elem.getElementsByTagName('tr');
	//var currentMenus = elem.childNodes;

	for( i = 0; i < rows.length; i++ ) {
		rows[i].style.display = 
			(visible ? 'inline' : 'none');
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
function callBack( response )
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
function updateHTML( result )
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
			response, FULLHTML, STICKY, MOUSEOFF, FIXX, 25, FIXY, 95);

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

		if( elemName == 'cti_chkbxSubs' || elemName == 'cti_chkbxFdrs'
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

	var elemSubs = document.getElementsByName('cti_chkbxSubs');
	var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
	var elemBanks = document.getElementsByName('cti_chkbxBanks');
	var elemPoints = document.getElementsByName('cti_chkbxPoints');

	var validElems = new Array();
	getValidChecks( elemSubs, validElems );
	getValidChecks( elemFdrs, validElems );
	getValidChecks( elemBanks, validElems );
	getValidChecks( elemPoints, validElems );

	//only allow the editing of the zeroth element for now
	if ( validElems.length <= 0 )
		alert('You must check the item you want to edit first');
	else
		window.location =
			getUrlType(validElems) + '&itemid=' + validElems[0].getAttribute('value');
}

// -------------------------------------------
//Posts to the correct URL with the checked item
// for editing
// -------------------------------------------
function deletePost()
{
	var elemSubs = document.getElementsByName('cti_chkbxSubs');
	var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
	var elemBanks = document.getElementsByName('cti_chkbxBanks');
	var elemPoints = document.getElementsByName('cti_chkbxPoints');

	var validElems = new Array();
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
	for( var i = 0; i < elems.length; i++ )
	{
		//validate the elements
		//if( elems[i].getAttribute('value') != '0' && elems[i].checked )
		if( elems[i].checked )
		{
			validElems[cnt++] = elems[i];
		}

	}

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

var CtiNonScrollTable = Class.create();
CtiNonScrollTable.prototype = {
  initialize: function(mainTable, headerTable) {
    this.mainTable = mainTable;
    this.headerTable = headerTable;
    this._alignHeaders();
    Event.observe(window, 'resize', this._alignHeaders.bind(this)  ,false);
},

_alignHeaders: function() {

var mytable = $(this.mainTable);
var headerTable = $(this.headerTable);


mytable = document.getElementById(this.mainTable);
myrow=mytable.getElementsByTagName('tr').item(0);

hdrTable =  document.getElementById(this.headerTable);
hdrRow=hdrTable.getElementsByTagName('tr').item(0);


var colNum = myrow.cells.length;

for(i=0;i < colNum - 1; i++) {

maxWidth = Math.max(hdrRow.getElementsByTagName('td').item(i).offsetWidth, myrow.cells[i].offsetWidth);
hdrRow.getElementsByTagName('td').item(i).width = maxWidth;
myrow.cells[i].width = maxWidth;
                                                        
}

}

}