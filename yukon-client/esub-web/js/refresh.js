function refresh(evt)

{
	SVGDoc = evt.getTarget().getOwnerDocument();
	dynText = SVGDoc.getElementsByTagName('text');
	dynImages = SVGDoc.getElementsByTagName('image');
	setInterval('doRefresh()', 10000);
}

function doRefresh() {
	
	for (var i=0; i<dynText.getLength(); i++) 
	{
		updateNode(dynText.item(i));
	}
	for(var j=0; j<dynImages.getLength(); j++)
	{
		updateImage(dynImages.item(j));
	}
}

function updateNode(node) {
	if (node.getAttribute('dattrib')) {
		url = '/servlet/DynamicTextServlet' + '?' + 'id=' + node.getAttribute('id') + '&' + 'dattrib=' + node.getAttribute('dattrib');
		getURL(url, fn);
	}

	function fn(obj) {
		if(obj.content) {
		node.getFirstChild().setData(obj.content);
		}	
	}
}


function updateImage(node) {
		
		url = '/servlet/StateImageServlet' + '?' + 'id=' + node.getAttribute('id');
		getURL(url, fn);
	


	function fn(obj) {
		
	    if (obj.content) {  
	    node.setAttribute('xlink:href', obj.content);
		}
		
		
	
	}


}


