var currentMenu;
var selectedNavItem;
var invNo;

// Variables that must be initialized before calling functions below!
var pageLinks;
var pageName;

function initHardwareMenu(menu, num) {
	invNo = num;
	for (i = 0; i < pageLinks[invNo].length; i++) {
		var menuItems = menu.all("MenuItem");
		var menuItemsSelected = menu.all("MenuItemSelected");
		menuItems[i].className = "navmenu1";
		menuItemsSelected[i].className = "navmenu2";
		if (pageLinks[invNo][i] == pageName) {
			menuItems[i].style.display = "none";
			menuItemsSelected[i].style.display = "";
			selectedNavItem = menuItemsSelected[i];
		}
		else {
			menuItems[i].style.display = "";
			menuItemsSelected[i].style.display = "none";
		}
	}
/*	
	if (pageLinks[invNo][0] == pageName) {
		document.getElementById("ScheduleMenuItem").style.display = "none";
		document.getElementById("ScheduleMenuItem").className = "navmenu1";
		document.getElementById("ScheduleMenuItemSelected").style.display = "";
		document.getElementById("ScheduleMenuItemSelected").className = "navmenu2";
		selectedNavItem = document.getElementById("ScheduleMenuItemSelected");
	}
	else {
		document.getElementById("ScheduleMenuItem").style.display = "";
		document.getElementById("ScheduleMenuItem").className = "navmenu1";
		document.getElementById("ScheduleMenuItemSelected").style.display = "none";
		document.getElementById("ScheduleMenuItemSelected").className = "navmenu1";
	}
	
	if (pageLinks[invNo][1] == pageName) {
		document.getElementById("ManualMenuItem").style.display = "none";
		document.getElementById("ManualMenuItem").className = "navmenu1";
		document.getElementById("ManualMenuItemSelected").style.display = "";
		document.getElementById("ManualMenuItemSelected").className = "navmenu2";
		selectedNavItem = document.getElementById("ManualMenuItemSelected");
	}
	else {
		document.getElementById("ManualMenuItem").style.display = "";
		document.getElementById("ManualMenuItem").className = "navmenu1";
		document.getElementById("ManualMenuItemSelected").style.display = "none";
		document.getElementById("ManualMenuItemSelected").className = "navmenu1";
	}*/
	return true;
}

function changeNavStyle(t)
{
	t.className = "navmenu2";

	if (selectedNavItem && t != selectedNavItem)
	{
		selectedNavItem.className = "navmenu1";
	}
	selectedNavItem = t;
}
	
function trendMenuAppear(event, source, divId)
{
	if (currentMenu)
	{
		currentMenu.style.visibility = 'hidden';
	}
	currentMenu = document.getElementById(divId);
	
/*	var source;
	if (window.event)
	{
		source = window.event.srcElement;
	}
	else
	{
		source = event.target;
	}*/

	var element = document.getElementById(divId);
	coordx = parseInt(source.offsetLeft + source.offsetWidth/5);
	coordy = parseInt(source.offsetTop + source.offsetHeight + 2);
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
function hardwareMenuAppear(event, source, divId, num)
{
	if (currentMenu)
	{
		currentMenu.style.visibility = 'hidden';
	}
	currentMenu = document.getElementById(divId);
	if (!initHardwareMenu(currentMenu, num)) return;
/*	
	var source;
	if (window.event)
	{
		source = window.event.srcElement;
	}
	else
	{
		source = event.target;
	}*/
	
	var element = document.getElementById(divId);
	coordx = parseInt(source.offsetLeft + source.offsetWidth);
	coordy = parseInt(source.offsetTop + source.offsetHeight - 12);
	
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

function showPage(idx) {
	location.href = pageLinks[invNo][idx];
}
