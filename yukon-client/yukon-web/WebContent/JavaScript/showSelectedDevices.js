

function showSelectedDevices(imgEl, divId, innerDivId, url, mag, magOverDisabled) {

	var savedOnMouseOverValue = imgEl.attributes['onmouseover'].value;
    var savedOnMouseOutValue = imgEl.attributes['onmouseout'].value;
    
    imgEl.attributes['onmouseover'].value = '';
    imgEl.attributes['onmouseout'].value = '';
    imgEl.src = magOverDisabled;
    
    new Ajax.Request(url, {
            
        'parameters': {},
        
        'onSuccess': function(transport) {
        
            $(innerDivId).update(transport.responseText);
            $(divId).show();
        
            imgEl.attributes['onmouseover'].value = savedOnMouseOverValue;
            imgEl.attributes['onmouseout'].value = savedOnMouseOutValue;
            imgEl.src = mag;
        }
        
      }
    );
}