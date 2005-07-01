var browser = new Object();
browser.isNetscape = false;
browser.isMicrosoft = false;
if (navigator.appName.indexOf("Netscape") != -1)
	browser.isNetscape = true;
else if (navigator.appName.indexOf("Microsoft") != -1)
	browser.isMicrosoft = true;

var currentMenu;
var selectedNavItem;
var invNo;

// Variables that must be initialized before calling functions below!
var pageLinks;
var pageName;

function initHardwareMenu(menu, num) {
	invNo = num;
	var menuItems, menuItemsSelected;
	
	if (browser.isMicrosoft) {
		menuItems = menu.all(menu.id + "Item");
		menuItemsSelected = menu.all(menu.id + "ItemSelected");
	}
	else {
		menuItems = document.getElementsByName(menu.id + "Item");
		menuItemsSelected = document.getElementsByName(menu.id + "ItemSelected");
	}
	
	for (i = 0; i < pageLinks[invNo].length; i++) {
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
	coordx = parseInt(source.offsetLeft + source.offsetWidth/5);
	coordy = parseInt(source.offsetTop + source.offsetHeight + 2);
	basicMenuAppear(event, source, divId, coordx, coordy);
}
function basicMenuAppear(event, source, divId, xpos, ypos)
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
	if( xpos == null)
		xpos = parseInt(source.offsetLeft + source.offsetWidth);
	if( ypos == null)
		ypos = parseInt(source.offsetTop);
	coordx = parseInt(xpos);
	coordy = parseInt(ypos);
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
	
	coordx = parseInt(source.offsetLeft + source.offsetWidth);
	coordy = parseInt(source.offsetTop + source.offsetHeight - 12);
	basicMenuAppear(event, source, divId, coordx, coordy);
}

function showPage(idx) {
	location.href = pageLinks[invNo][idx];
}
