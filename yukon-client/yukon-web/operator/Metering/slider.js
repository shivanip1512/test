// Put each of your customised sliders here.
// window.document.sliders = [ { ... }, { ... }, ... ] = an array of objects.
var i;
var label = [];
   for (i =0 ; i < 7; i++)
   {
		label[i] = i;
   }

var sliders = [

	    // First slider (and the only one in this template file)
	    {
	    interactive : true,		// User modifiable on 'true'
	    continuous : false,		// Any position allowed if 'true'
	    span_id : "slider1",

	    left : 300,			// all in 'px' pixels
	    top : 310,
	    pane_image: "pane.gif",

	    scale_width : 350,
	    scale_height : 6,		
	    scale_image : "scale.gif",

	    stylus_width : 26,
	    stylus_height : 40,
	    stylus_up   : "stylus.gif",
	    stylus_down : "stylus2.gif",

	    tick_height : 10,
	    tick_width : 2,
	    tick_image : "tick.gif",

	    ticks : label.length,
	    start_tick : 0,
	    tick_tabs : null,		// auto-calc'ed if set to null

	    label_size : 10,		// in 'px' not in 'pt'
	    label_font : "\"Courier\"",
        labels : label,
	    values : label,

	    form_field_id : "slider1",
	    form_id : "form1"			// in  the HTML page.
	}

	// next slider goes here. copy { ... } from the first slider
	// and add a , before the new slider.
    ];

