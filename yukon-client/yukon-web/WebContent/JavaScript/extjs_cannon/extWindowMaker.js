
var ExtWindowMaker = Class.create();

ExtWindowMaker.prototype = { 
    
    // INIT
    //----------------------------------------------------------------------------------------------
    initialize: function() {
    
        // default window attributes
        this.windowAttributes = $H({
            'title': '',
            'width': 400,
            'height': 600,
            'layout': 'border',
            'modal': true,
            'items': [],
            'buttons': []
        });
    },
    
    /* Create a window and return it. Asks for most basic attributes of a window and sets them
     * so using setAttributes() is not required if defaults are ok.
     * 
     * @param	{String}	title Window Title
     * @param	{Number}	width Window width in pixels
     * @param	{Number}	height Window height in pixels
     * 
     * @returns	{Ext.Window} New instance of an Ext.Window
     */
    getWindow: function(title, width, height) {
    
        this.windowAttributes.set('title', title);
        this.windowAttributes.set('width', width);
        this.windowAttributes.set('height', height);
        
        return new Ext.Window(this.windowAttributes.toObject());
    },
    
    // Convienence function. automatically shows window on creation.
    showWindow: function(title, width, height) {
    
        var window = this.getWindow(title, width, height);
        window.show();
        
        return window;
    },

    /* Provide a hash of window panel attribute configs. Will override defaults. Should be used before getWindow()
     * @param	{Hash} 
     */
    setAttributes: function(attr) {
        this.windowAttributes.update($H(attr));
    },
    
    setItems: function(itemsList) {

        this.windowAttributes.set('items', $A(itemsList));
    },
    
    setButtons: function(buttonsList) {

        this.windowAttributes.set('buttons', $A(buttonsList));
    }
    
        
};

