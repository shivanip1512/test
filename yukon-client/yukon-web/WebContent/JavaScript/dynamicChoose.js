function updateDynamicChoose(spanId) {
  //assumes data is of type Javascript object
    return function(data) {
        var showId = document.getElementById(spanId + data.state);
        jQuery(document.getElementById(spanId)).children().hide();
        jQuery(showId).show();
    };
}