function scrollPerNode(comp, div) {
	//get the number of elements
	var len = 0;
	if (comp.tagName == "SPAN") {
	   len = div.getElementsByTagName("span").length;
    }
	if (comp.tagName == "UL") {
	   len = comp.getElementsByTagName("a").length;	
	}
	return div.scrollHeight / len;
}

function addSmartScrolling(hidden_id, div_id, indx_id, comp_id){
    jQuery(function() {
	//get the hidden and div
	var hidden_el = document.getElementById(hidden_id);
	var div_el =  document.getElementById(div_id);
	//to remember the selected
	var indx_el = document.getElementById(indx_id);
	var comp_el = document.getElementById(comp_id);
	
	if(hidden_el.value != 0) {
		div_el.scrollTop = hidden_el.value;	
	}
	else {
		if (indx_el!=null && comp_el!=null)	{
            div_el.scrollTop = indx_el.value * scrollPerNode(comp_el, div_el);
	       }	    
    }
	if (div_el != null)
		div_el.onscroll = function () {hidden_el.value = div_el.scrollTop + 1;};
	});
}
