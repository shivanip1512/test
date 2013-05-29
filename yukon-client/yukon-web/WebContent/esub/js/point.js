/**
 * point.js
 *
 * The purpose of this script is do point related scripting tasks.
 * Note the funky use of window.parent to store the pointDetailsPopup.
 * This is to enable communications between html script and svg script.
 */
 
/**
 * Popup a point details jsp, pass in the click event it can
 * figure out what point was clicked and where to pop up
 */
function showPointDetails(evt, allowControl) {
	// Figure out the point id that was clicked on
	var node = evt.getTarget();
    var id = node.getAttribute('id');
    var controlPointId = node.getAttribute('controlPointId');

	// Store the location and the dimensions of the point
	// so we can position the popup
	popupx = parseInt(node.getAttribute('x'));
	popupy = parseInt(node.getAttribute('y'));
	
	var url = "/esub/jsp/point_detail.jsp?pointid=" + id + "&controlPointId=" + controlPointId + "&allowControl=" + allowControl;
		
	getCtiURL(url, handleShowPointReq);
	
}

/**
 * Call this to hide the point details popup
 */
function hidePointDetails() {
	if(window.parent.pointDetailsPopup != null) {
		window.parent.pointDetailsPopup.hidePopup();
	}
}

/**
 * Callback from getURL call in showPointDetails.
 * handles the response from the server, fills in the
 * popup and pops it up
 */
function handleShowPointReq(obj) {
	pointDetailsPopup = new PopupWindow("controlrequest");
	pointDetailsPopup.offsetX= popupx + 35;
	pointDetailsPopup.offsetY= popupy - 35;

	pointDetailsPopup.autoHide();
	pointDetailsPopup.populate(obj.content);

	pointDetailsPopup.showPopup("popupanchor");	
	window.parent.pointDetailsPopup = pointDetailsPopup;
}

/**
 * Displays a control window, a real window, not just a div popup.
 * This can only be done from a html script or you'll get an error.
 */
function showControlWindow(pointID) {
	var url = "/esub/jsp/control.jsp?pointid=" + pointID + "&action=display";

	var popUp = new PopupWindow();
	popUp.setSize(800,480);
	popUp.autoHide();
	popUp.setUrl(url);
	popUp.showPopup("popupanchor");
}

/**
 * Submit a control request to the server via xmlHTTP
 * TODO better error handling
 */
function submitControl(pointid, rawstate) {
	var url = '/servlet/ControlServlet?id=' + pointid + '&rawstate=' + rawstate;
	getCtiURL(url, null);
}

function submitControlXMLHttp(pointid, rawstate, close) {
	var url = '/servlet/ControlServlet?id=' + pointid + '&rawstate=' + rawstate;
	//  Add a unique suffix to prevent caching
	url +=  '&_=' + (new Date).getTime();
	
	loadXMLDoc(url, null);
	if(close == true) {
		self.close();
	}
}
