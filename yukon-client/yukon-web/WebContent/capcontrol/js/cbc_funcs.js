
<SCRIPT> <!--trend/view menu items-->
//view types
var currentMenu;
var selectedItem;

function showGraphWin( theURL )
{
	//alert( theURL );

	var width = 640;
	var height = 580;
	var winl = (screen.width - width) / 2; 
	var wint = (screen.height - height) / 2; 
			  	
	var w = window.parent.open(theURL, "CapControl_Graphs",
				"width="+width+",height="+height+",top="+wint+",left="+winl+",resizable=yes,status,scrollbars");

} //end showControlWin

function init()
{
	
}


function changeView(viewType)
{
	//document.MForm.view.value = viewType;
	submitMForm();
}

function submitMForm()
{
	document.MForm.submit();
}

function changeOptionStyle(t)
{
	t.className = "optmenu2";

	if (selectedItem && t != selectedItem)
	{
		selectedItem.className = "optmenu1";
	}
	selectedItem = t;
}

function menuAppear(event, divId)
{
	if (currentMenu)
	{
		currentMenu.style.visibility = 'hidden';
		if (selectedItem)
		{
			selectedItem.className = "optmenu1";
		}
	}
	currentMenu = document.getElementById(divId);
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
	coordx = parseInt(source.offsetLeft);
	coordy = parseInt(source.offsetTop) + parseInt(source.offsetHeight) + 2;
	
	while (source.offsetParent)
	{
		source = source.offsetParent;
		coordx = coordx + parseInt(source.offsetLeft);
		coordy = coordy + parseInt(source.offsetTop);
	}
	
	element.style.left = coordx + 'px';
	element.style.top = coordy + 'px';
	element.style.visibility = 'visible';
	if (window.event)
	{
 		document.attachEvent("onclick", hideMenu);
		window.event.cancelBubble = true;
    	window.event.returnValue = false;
	}
	else
	{
		document.addEventListener("click", hideMenu, true);
		event.preventDefault();
    }
	
	function hideMenu(event)
	{
		var element = document.getElementById(divId);
		element.style.visibility = 'hidden';
		if (window.event)
		{
			document.detachEvent("onclick", hideMenu);
		}
		else
		{
			document.removeEventListener("click",hideMenu, true);
			event.preventDefault();
		}
	}
}

</SCRIPT> <!--end javascript for trend/view menus-->