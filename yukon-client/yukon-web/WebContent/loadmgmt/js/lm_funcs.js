
<SCRIPT>

/* Page global ID that is set for control */
itemid = -1;

function showConfirmWin( theDiv )
{
   var url = "lmcmd.jsp" +
    		  	"?cmd=" + theDiv.name +
    		  	"&itemid=" + itemid;

   var width = 510;
	var height = 540;
	var winl = (screen.width - width) / 2; 
	var wint = (screen.height - height) / 2; 
			  	
	var w = window.parent.open(url, "LoadManagement",
				"width="+width+",height="+height+",top="+wint+",left="+winl+",resizable=yes,status,scrollbars");

} //end showControlWin

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


function changeOptionStyle(t)
{
	t.className = "optmenu2";
	t.onmouseout = function (e)
	{
	  t.className = "optmenu1";
	  return false;
	};	      

}

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
	coordx = parseInt(source.offsetLeft, 10);
	coordy = parseInt(source.offsetTop, 10) + parseInt(source.offsetHeight, 10) + 0;
	
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

function checkAll( chkAll, fields)
{
	for (i = 0; i < fields.length; i++)
		fields[i].checked = chkAll.checked;
}

/**
*
* startDate/stopDate format: 'MM/DD/YYYY HH:mm:ss'
*
*/
function validateDate( startDate, stopDate )
{
	var date1 = new Date();
   var date2 = new Date();

   date1.setTime( Date.parse(startDate) );
   date2.setTime( Date.parse(stopDate) );
   
   if( date1.getTime() >= date2.getTime() )
   {
      return false;
   }
   else
   {
      return true;
   }

}

</SCRIPT> <!--end javascript for trend/view menus-->