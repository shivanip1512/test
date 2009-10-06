function updateDynamicChoose(spanId) {

    return function(data) {

        var showId = spanId + data.state;
        
        var children = $(spanId).immediateDescendants().each(function(childElement){
        	childElement.hide();
        });
        $(showId).show();
    };
}