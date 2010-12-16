
var extTreeHelper = new ExtTreeHelper();

ExtTreeMaker = Class.create();

ExtTreeMaker.prototype = { 

    // Constructor
	initialize: function(id, static) {
    
        this.id = id;
        this.static = static;
        this.extTreeHelper = new ExtTreeHelper();
        
        // set using specific data loader setup function
        this.root = null;
        this.treeLoader = null;
        
        // default tree attributes
        this.treeAttributes = $H({
            'id': this.id,
            'useArrows': false,
            'autoScroll': true,
            'animate': false,
            'enableDD': false,
            'border': false,
            'rootVisible': true,
            'pathSeperator': '>',
            'frame': false
        });
	},
    
    // setup loader and root for static data tree
    setupStaticDataLoader: function(dataJson) {
    
        this.treeLoader = this.extTreeHelper.getStaticTreeLoader();
        this.root = this.extTreeHelper.getStaticTreeRoot(dataJson);
        
        this.treeAttributes.set('loader', this.treeLoader);
    },
    
    
    // setup loader and root for async data tree
    setupAsyncDataLoader: function(dataUrl, baseParams, rootAttributes) {
    
        this.treeLoader = this.extTreeHelper.getAsyncExtTreeLoader(dataUrl, baseParams);
        this.root = this.extTreeHelper.getAsyncExtTreeRoot(rootAttributes);
        
        this.treeAttributes.set('loader', this.treeLoader);
    },
    
    
    // Returns a tree panel object with data and root setup, user may do with it what they want...
    // If the tree it to be rendered to an element, the 'el' attribute should be set before using
    // getTree() using either the setEl() or setAttributes() functions.
    getTree: function() {

        // be nice to new users, loader and root MUST be set by now...
        if (this.treeLoader == null || this.root == null) {alert('Internal Error.\n\nNo treeLoader/root set.\n\nUse setupStaticDataLoader() or setupAsyncDataLoader()');return false;}
    
        var tree = new Ext.tree.TreePanel(this.treeAttributes.toObject());
        tree.setRootNode(this.root);
        
        return tree;
    },
    
    // Convienence function.
    // Provided an element id to render to, will call getTree() internally, set its el,  
    // render it, and set its state
    showTree: function(el) {
    
        this.setEl(el);
        var tree = this.getTree();
        
        tree.render();
        this.setState(tree);
        
        return tree;
    },
    
    // Can be used to set the elemnt to render tree to. To be used if you don't want to specify 
    // using setAttributes(). Should be used before getTree() or showTree() if you plan to render to an element.
    setEl: function(el) {
        this.treeAttributes.set('el', el);
    },
    
    // Provide a hash of tree panel attribute configs. Will override defaults. Should be used before
    // getTree() or showTree().
    setAttributes: function(attr) {
        this.treeAttributes.update($H(attr));
    },
    
    // Convienence function, delegates to helper class
    setState: function(tree) {
        this.extTreeHelper.setTreeState(tree);
    }
}
