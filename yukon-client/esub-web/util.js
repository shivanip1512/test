var doc;
var currentPoint;

function start(svgDoc) {
    doc = svgDoc;
    doUpdate();
}

function doUpdate() {

  var start = new Date();
  for( var i = 747; i < 752; i++ ) {
        currentPoint = i;
	 	getURL('/esubstation/servlet/PointData?id=lastchange', valueTextCallBack);  
  }
  
  var end = new Date();
  
  alert('Took ' + (end.getTime()-start.getTime()) + ' millis');
  
  setTimeout('doUpdate()',15000);
}

function valueTextCallBack(obj) {
    alert(currentPoint);
    var elem = doc.getElementById("pt" + currentPoint);
    var text = doc.createTextNode(obj.content);
    elem.replaceChild(text, elem.getFirstChild());
}