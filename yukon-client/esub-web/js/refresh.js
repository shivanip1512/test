function refresh(evt)

{
	SVGDoc = evt.getTarget().getOwnerDocument();
	group = SVGDoc.getElementsByTagName('text');
	setInterval('doRefresh()', 10000);
}

function doRefresh() {
	for (var i=0; i<group.getLength(); i++) 
	{
		updateNode(group.item(i));
	}
}

function updateNode(node) {
	if (node.getAttribute('dattrib')) {
		url = '/servlet/DynamicTextServlet' + '?' + 'id=' + node.getAttribute('id') + '&' + 'dattrib=' + node.getAttribute('dattrib');
		getURL(url, fn);
	}

	function fn(obj) {
		node.getFirstChild().setData(obj.content);	
	}
}




