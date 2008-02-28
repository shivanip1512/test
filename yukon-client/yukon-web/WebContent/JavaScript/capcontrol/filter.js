
function dateFilter(selectID, values, type) {
	url = "tabledata.jsp?type=" +type;
	for (var i=0; i < values.length; i++)
	{
		url += "&value=" + values[i];
	}
	dayCnt = $(selectID).options[$(selectID).selectedIndex].value;
	url+= "&dayCnt=" + dayCnt;
	window.location.replace(url);
}

