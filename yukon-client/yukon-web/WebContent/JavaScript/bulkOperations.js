

function showSelectedDevices(imgEl, divId, innerDivId, url, mag, magOverDisabled) {

    var savedOnClickValue = imgEl.attributes['onclick'].value;
    
    imgEl.attributes['onclick'].value = '';
    imgEl.src = magOverDisabled;
    
    new Ajax.Request(url, {
            
        'parameters': {},
        
        'onSuccess': function(transport) {
        
            $(innerDivId).update(transport.responseText);
            $(divId).show();
        
            imgEl.attributes['onclick'].value = savedOnClickValue;
            imgEl.src = mag;
        }
        
      }
    );
        
}

function closeSelectedDevices(divId) {

  $(divId).hide();
        
}