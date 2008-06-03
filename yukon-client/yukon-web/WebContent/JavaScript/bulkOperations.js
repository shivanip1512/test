

function showSelectedDevices(divId, innerDivId, url) {

    new Ajax.Request(url, {
            
        'parameters': {},
        
        'onSuccess': function(transport) {
        
            $(innerDivId).update(transport.responseText);
            $(divId).show();
        
        }
        
      }
    );
        
}

function closeSelectedDevices(divId) {

  $(divId).hide();
        
}