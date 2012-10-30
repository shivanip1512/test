function updateDynamicChoose(spanId) {
  //assumes data is of type Hash
	//@todo: Prototype Hash rewrite
    return function(data) {
        var showId = document.getElementById(spanId + data.get('state'));
        jQuery(document.getElementById(spanId)).children().hide();
        jQuery(showId).show();
    };
}