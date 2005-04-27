
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
	
				var ev = eval("f."+arguments[i]);
				ev.value = arguments[i+1];
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
	var ev = eval("f."+paramName);
	ev.value = paramVal;

	f.submit();
}

// -------------------------------------------
//validates input data for post events
// -------------------------------------------
function validateData( elemName )
{
	if( !document.getElementById(elemName) )
	{
		alert('Unable to find element for given operation, try again');
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
	t.className = "optSelect";
	t.onmouseout = function (e)
	{
	  t.className = "optDeselect";
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
function menuAppear(event, divId)
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
//given a menu with multiple DIVs, this function will set only 1 DIV
// to be visible at a time
// -------------------------------------------
function menuShow(menuDiv, visIndx)
{
	var currentMenus = document.getElementsByName(menuDiv);

	for (i = 0; i < currentMenus.length; i++)
		currentMenus[i].style.display = 'none';

	currentMenus[visIndx].style.display = 'inline';
}

// -------------------------------------------
//Allows a check box to control the visiblily of
// a set of elements
// -------------------------------------------
function showSubElems( elemId, chkBox )
{
	var currentMenus = document.getElementsByName(elemId);

	for (i = 0; i < currentMenus.length; i++)
		currentMenus[i].style.display = 
			(chkBox.checked ? 'inline' : 'none');
}
