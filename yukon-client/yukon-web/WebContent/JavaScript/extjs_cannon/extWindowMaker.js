
var ExtWindowMaker = Class.create();

ExtWindowMaker.prototype = { 
    
    // INIT
    //----------------------------------------------------------------------------------------------
    initialize: function() {
    
        // default window attributes
        this.windowAttributes = $H();
        this.windowAttributes['title'] = '';
        this.windowAttributes['width'] = 400;
        this.windowAttributes['height'] = 600;
        this.windowAttributes['layout'] = 'border';
        this.windowAttributes['modal'] = true;
        this.windowAttributes['items'] = [];
        this.windowAttributes['buttons'] = [];
    },
    
    // Create a window and return it. Asks for most basic attributes of a window and sets them
    // so using setAttributes() is not required if defaults are ok.
    getWindow: function(title, width, height) {
    
        this.windowAttributes['title'] = title;
        this.windowAttributes['width'] = width;
        this.windowAttributes['height'] = height;
        
        var window = new Ext.Window(this.windowAttributes);
        
        return window;
    },
    
    // Convienence function. automatically shows window on creation.
    showWindow: function(title, width, height) {
    
        var window = this.getWindow(title, width, height);
        window.show();
        
        return window;
    },

    // Provide a hash of window panel attribute configs. Will override defaults. Should be used before
    // getWindow()
    setAttributes: function(attr) {

        // override defaults - will "break" with prototype 1.6 (use update() instead!)
        this.windowAttributes.merge($H(attr));
    },
    
    setItems: function(itemsList) {

        this.windowAttributes['items'] = $A(itemsList);
    },
    
    setButtons: function(buttonsList) {

        this.windowAttributes['buttons'] = $A(buttonsList);
    }
    
        
} 

