
var ExtTreeHelper = Class.create();

ExtTreeHelper.prototype = { 
    
    // INIT
    // No init required
    //----------------------------------------------------------------------------------------------
    initialize: function() {
    },
    
    // STATIC TREE LOADER/ROOT
    // Static trees get all the info they need (including root) from the Json they are constructed
    // with. The tree loader is simply a blank instance of an Ext.TreeLoader.
    //----------------------------------------------------------------------------------------------

    getStaticTreeLoader: function() {
    
        var treeLoader = new Ext.tree.TreeLoader();
        return treeLoader;
    },
    
    getStaticTreeRoot: function(dataJson) {

        var root = new Ext.tree.AsyncTreeNode(dataJson);
        return root;
    },
    
    // ASYNC TREE LOADER/ROOT
    // Async trees get info from a controller method that returns Json respresenting nodes UNDER the
    // root. The root is constructed by hand in js using a hash of attributes.
    //----------------------------------------------------------------------------------------------
    getAsyncExtTreeLoader: function(dataUrl, baseParams) {
    
        var treeLoader = new Ext.tree.TreeLoader({
                                 dataUrl:dataUrl,
                                 baseParams:baseParams
                              });
                        
        return treeLoader;
    },
    
    getAsyncExtTreeRoot: function(rootAttributes) {

        var defaultRootAttributes = $H();
        defaultRootAttributes['id'] = 'root';
        defaultRootAttributes['text'] = 'Root';
        defaultRootAttributes['href'] = 'javascript:void(0);';
        defaultRootAttributes['disabled'] = true;
        
        // overrride defaults
        rootAttributes.keys().each(function(key) {
                defaultRootAttributes[key] = rootAttributes[key];
            });
            
        var root = new Ext.tree.AsyncTreeNode(defaultRootAttributes);
        return root;
    },
    
    // Setup tree panel to save its state.
    // Will restore the state to saved state if found when called.
    setTreeState: function(tree) {
    
        var treeState = new TreePanelState(tree);
        treeState.init();
        tree.on('expandnode', treeState.onExpand, treeState);
        tree.on('collapsenode', treeState.onCollapse, treeState);
        treeState.restoreState(tree.root.getPath());
    }
    
        
} 

