
var win;
var parentWin;
var controlRequestURL;

/* entry point */
function controlPoint(evt) {
	var node = evt.getTarget();
    parentWin = window.parent;
    
    var id = node.getAttribute('id');

	var url = "/esub/jsp/control.jsp?pointid=" + id + "&action=display";
	var w = 800;
	var h = 480;
	var winl = (screen.width - w) / 2; 
	var wint = (screen.height - h) / 2; 
	
	win = window.parent.open(url, 'Control', 'width='+w+',height='+h+',top='+wint+',left='+winl+',scrollbars=yes,resizable=yes');
	window.setTimeout("chckCtrlWindow()", 250);
	//window.setTimeout("closeCtrlWindow()", 300000);
} //end updateGraphChange

function chckCtrlWindow() {

	if(win.closed && parentWin.requestURL != null) {
		var url = parentWin.requestURL;
		parentWin.requestURL = null;

		getURL(url, fn);		
	}
	else {
		setTimeout("chckCtrlWindow()", 250);
	}
	
	function fn(obj) {
		if(obj.content == 'error') {
			alert('An error occured sending control request, make sure that you have permission to control');
		}
	}
}

/* close the control window after a period of time */
function closeCtrlWindow() {
	if(!win.closed) {
		win.close();
	}
}

/* actually submit the control */
function submitControl(pointid, rawstate) {
	var url = '/servlet/ControlServlet?id=' + pointid + '&rawstate=' + rawstate;
	opener.requestURL = url;
	self.close();
}