
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
//post events with a form rather than using the URL line
// -------------------------------------------
function postIt( paramName, paramVal, frmName )
{
	if( !validateData(frmName) )
		return false;

	if( !validateData(paramName) )
		return false;

	var f = document.getElementById(frmName);
	var ev = eval('f.'+paramName);
	ev.value = paramVal;

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
//hides a given DIV tag on a page
// -------------------------------------------
function menuDisappear(e, divid)
{
	var currentMenu = document.getElementById(divid);
	

	var relatedTarget = null;
	if( e )
	{
		relatedTarget = e.relatedTarget;
	    // work around Gecko Linux only bug where related target is null
	    // when clicking on menu links or when right clicking and moving
	    // into a context menu.
	    
	    if (navigator.product == 'Gecko' && navigator.platform.indexOf('Linux') != -1 && !relatedTarget)
	    {
	      relatedTarget = e.originalTarget;
	    }

		if (window.event && !relatedTarget)
		{
		    relatedTarget = window.event.toElement;
		}
	}

	if (elementContains(currentMenu, relatedTarget))
	{
		return false;
	}

	currentMenu.style.visibility = 'hidden';
	return false;
}

// -------------------------------------------
//shows a given DIV tag on a page
// -------------------------------------------
function menuAppear(event, divId, stayVisibleOnClick)
{
	var currentMenu = document.getElementById(divId);
	
	currentMenu.onmouseout = function (e)
	{
	  var relatedTarget = null;
	  if( e )
	  {
	    relatedTarget = e.relatedTarget;
	    // work around Gecko Linux only bug where related target is null
	    // when clicking on menu links or when right clicking and moving
	    // into a context menu.
	    
	    if (navigator.product == 'Gecko' && navigator.platform.indexOf('Linux') != -1 && !relatedTarget)
	    {
	      relatedTarget = e.originalTarget;
	    }
	  }
	  else if (window.event)
	  {
	    relatedTarget = window.event.toElement;
	  }

	  if (elementContains(this, relatedTarget))
	  {
	    return false;
	  }

	  this.style.visibility = 'hidden';
	  return false;
	};

	//if the popup needs to stay visible after a click, then do not
	// add the below function
	if( stayVisibleOnClick != null )
	{
		currentMenu.onclick = function (e)
		{
			if (window.event)
			{
				window.event.cancelBubble = true;
		    	window.event.returnValue = false;
			}
		
			this.style.visibility = 'hidden';
			return false;
		};
	}

	var source;
	if (window.event)
	{
		source = window.event.srcElement;
	}
	else
	{
		source = event.target;
	}

	var element = document.getElementById(divId);
	coordx = parseInt(source.offsetLeft, 10) - 5 + parseInt(source.offsetWidth, 10);
	coordy = parseInt(source.offsetTop, 10);// + parseInt(source.offsetHeight, 10) + 0;

	while (source.offsetParent)
	{
		source = source.offsetParent;
		coordx = coordx + parseInt(source.offsetLeft, 10);
		coordy = coordy + parseInt(source.offsetTop, 10);
	}
	
	
	element.style.left = coordx + 'px';
	element.style.top = coordy + 'px';
	element.style.visibility = 'visible';
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
function showRowElems( elemName, chkBox )
{
	var elem = document.getElementById(elemName);
	var rows = elem.getElementsByTagName('tr');
	//var currentMenus = elem.childNodes;

	for( i = 0; i < rows.length; i++ )
	{
		rows[i].style.display = 
			(chkBox.checked ? 'inline' : 'none');
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
		var url = createURLreq( elems, 'cbcproxy.jsp?method=callBack', 'id' );
		loadXMLDoc(url);
	}
}

// -------------------------------------------
//Creates a URL string that from teh elements given.
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
							elems[j].innerHTML = 
								'<font color="' + xmlColor + '" >' +
								getElementTextNS(result[i], 0, elemType) +
								'</font>';
							
							break;
						
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

		// ...processing statements go here...
 		response = req.responseText;

 		
		overlib(
			response, FULLHTML, STICKY, MOUSEOFF);
			//,WIDTH, 200, HEIGHT, 200, WRAP);

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
//Shows a sticky popup for the checked boxe elements
// that are valid values AND that are checked
// -------------------------------------------
function showPopUpForChkBoxes( typeStr )
{
	if( typeStr == null )
		return;

	var elemSubs = document.getElementsByName('cti_chkbxSubs');
	var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
	//var elemBanks = document.getElementsByName('cti_chkbxBanks');

	var validElems = new Array();
	doValidChecks( elemSubs, validElems );
	doValidChecks( elemFdrs, validElems );
	//doValidChecks( elemBanks, validElems );


	var url = createURLreq( validElems, 'charts.jsp?type='+typeStr, 'value' );
	manMsgID = loadXMLDoc(url, 'processMenuReq');
}

function doValidChecks( elems, validElems )
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