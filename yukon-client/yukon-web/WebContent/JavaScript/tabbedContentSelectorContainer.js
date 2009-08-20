function setupTabbedControl(tabPanelId, contentItems, initalContentIdx) {
	
	new Ext.TabPanel({
		
	    renderTo: tabPanelId,
	    activeTab: initalContentIdx,
	    autoSizeTabs:true,
	    autoHeight:true,
        autoWidth:true,
        layoutOnTabChange:true,
        defaults: { autoHeight: true },
		items:contentItems,
		plain:true
	    
	});
}
