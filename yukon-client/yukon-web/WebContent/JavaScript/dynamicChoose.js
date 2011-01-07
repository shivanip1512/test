function updateDynamicChoose(spanId) {
  //assumes data is of type Hash
    return function(data) {
        var showId = spanId + data.get('state');
        
        var children = $(spanId).immediateDescendants().each(function(childElement){
        	childElement.hide();
        });
        $(showId).show();
    };
}