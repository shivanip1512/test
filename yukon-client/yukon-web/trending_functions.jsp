<%            
// Grab their graphdefinitions
%>
<jsp:setProperty name="graphBean" property="period" param="period"/>
<jsp:setProperty name="graphBean" property="gdefid" param="gdefid"/>
<jsp:setProperty name="graphBean" property="start" param="start"/>
<jsp:setProperty name="graphBean" property="viewType" param="view"/>
<jsp:setProperty name="graphBean" property="option" param="option"/>
<jsp:setProperty name="graphBean" property="format" param="format"/>
<jsp:setProperty name="graphBean" property="page" param="page"/>

<SCRIPT> <!--trend/view menu items-->
//view types
var LINE  = parseInt(<%=TrendModelType.LINE_VIEW%>);
var BAR   = parseInt(<%=TrendModelType.BAR_VIEW%>);
var DBAR  = parseInt(<%=TrendModelType.BAR_3D_VIEW%>);
var STEP  = parseInt(<%=TrendModelType.STEP_VIEW%>);
var SHAPE = parseInt(<%=TrendModelType.SHAPES_LINE_VIEW%>);
var TABULAR = parseInt(<%=TrendModelType.TABULAR_VIEW%>);
var SUMMARY = parseInt(<%=TrendModelType.SUMMARY_VIEW%>);

//options
var BASIC = parseInt(<%=TrendModelType.BASIC_MASK%>); 
var LD = parseInt(<%=TrendModelType.LOAD_DURATION_MASK%>); 

//Global variables
var viewType = parseInt(<%=graphBean.getViewType()%>);
var currentMenu;
var selectedItem;
var loadDur = false;


function init()
{
	initViewMenu();
}


function initViewMenu()
{
	switch(viewType)
	{
		case LINE:
			document.getElementById('LINEID').innerHTML = "&nbsp;&#149;&nbsp;<%=TrendModelType.LINE_VIEW_STRING%>";
			break;
		case BAR:
			document.getElementById('BARID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.BAR_VIEW_STRING%>";
			break;
		case DBAR:
			document.getElementById('3DBARID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.BAR_3D_VIEW_STRING%>";
			break;
		case STEP:
			document.getElementById('STEPID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.STEP_VIEW_STRING%>";
			break;
		case SHAPE:
			document.getElementById('SHAPEID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.SHAPES_LINE_VIEW_STRING%>";
			break;
		case TABULAR:
			document.getElementById('TABULARID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.TABULAR_VIEW_STRING%>";
			break;
		case SUMMARY:
			document.getElementById('SUMMARYID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.SUMMARY_VIEW_STRING%>";
		break;
	}
	
	//This is actually an option but it appears on the viewMenu
	var currentMask = parseInt(document.MForm.option.value);
	if ((currentMask & LD ) !=0)
	{
		document.getElementById('LDID').innerHTML = "&nbsp;&#149;&nbsp;Load Duration";
		loadDur = true;
	}
	
}

function changeView(viewType)
{
	document.MForm.view.value = viewType;
	submitMForm();
}

function changeLD ()
{
	if (loadDur == true)
	{
    	setMask(LD, false);
	    document.getElementById('LDID').innerHTML = "&nbsp;&nbsp;&nbsp;Load Duration";
		loadDur = false;
    }
	else
	{
		setMask(LD, true);
		document.getElementById('LDID').innerHTML = "&nbsp;&#149;&nbsp;Load Duration";
		loadDur = true;
	}
	document.MForm.submit();
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

function setMask (newMask,setMasked)
{
	var currentMask = parseInt(document.MForm.option.value);
	if(setMasked)
		currentMask |= newMask;
	else
	{
		//check to make sure it's there if we are going to remove it
		if( (currentMask & newMask) != 0)
		{
			currentMask ^= newMask;
		}
	}
	document.MForm.option.value = currentMask;	
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

function changeFormat(format)
{
	document.exportForm.format.value = format;
	document.exportForm.submit();
}

function exportData(extension)
{
	changeFormat(extension);
	document.exportForm.action="/servlet/Download?";
	document.exportForm.method="post";
	document.exportForm.submit();
}
</SCRIPT> <!--end javascript for trend/view menus-->