function setupTabbedControl(id, controlName) {

	var tabPanelId = $('tabbedControl_' + id);
	var contentContainer = $('contentContainer_' + id);
	var contentDivs = $A(contentContainer.childElements());
	
	// need to determine which content radio is selected before main build loop
	var contentIdx = 0;
	var initalContentIdx = 0;
	contentDivs.each(function(el) {
		var initiallySelectedAttr = el.readAttribute('initiallySelected');
		if (initiallySelectedAttr == 'true') {
			initalContentIdx = contentIdx;
		}
		contentIdx++;
	});
	
	
	// build item array
	var contentItems = $A();
	contentDivs.each(function(el) {

		var contentId = el.readAttribute('id');
		var selectorName = el.readAttribute('selectorName');
			
		var contentItem = $H();
		contentItem['title'] = selectorName;
		contentItem['contentEl'] = contentId;
		
		contentItems.push(contentItem);
	});
	
	var tabs = new Ext.TabPanel({
		
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
