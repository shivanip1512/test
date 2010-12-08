function showInventoryCollection(popup, popupContent, url, img) {
    
    img.src = '/WebConfig/yukon/Icons/indicator_arrows.gif';
    new Ajax.Updater(popupContent, url, {method: 'get', evalScripts: true, parameters: {},
        onComplete: function() {
            $(popup).show();
            $('inventoryCollectionImg').src = '/WebConfig/yukon/Icons/magnifier.gif'
        }
        
    });
}

function showSelectedDevices(imgEl, divId, innerDivId, url, mag, magOverDisabled) {

	var savedOnMouseOverValue = imgEl.attributes['onmouseover'].value;
    var savedOnMouseOutValue = imgEl.attributes['onmouseout'].value;
    
    imgEl.attributes['onmouseover'].value = '';
    imgEl.attributes['onmouseout'].value = '';
    imgEl.src = magOverDisabled;
    
    new Ajax.Request(url, {
            
        'parameters': {},
        
        'onComplete': function(transport) {
        
            $(innerDivId).update(transport.responseText);
            $(divId).show();
        
            imgEl.attributes['onmouseover'].value = savedOnMouseOverValue;
            imgEl.attributes['onmouseout'].value = savedOnMouseOutValue;
            imgEl.src = mag;
        }
        
      }
    );
}