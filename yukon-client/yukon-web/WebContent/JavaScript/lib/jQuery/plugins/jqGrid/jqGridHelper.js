/**
*   jqGrid helper - constructor functions for Yukon
*/


Yukon.GridHelper = {
        createGrid: function(args){
            this._createBasicGrid(args);
            if(args.toolbar){
                this._createToolbar(args);
            }
            
            if(args.refreshRate){
                setInterval('Yukon.GridHelper._refresh("'+ args.id +'")', args.refreshRate)
            }
        },

        _createBasicGrid: function(args){
            
            var opts = {
                    altRows: true,
                    autowidth: true,
                    caption: args.title,
                    colModel: args.colModel,
                    datatype: 'json',
                    height: args.height || 'auto',
                    width: args.width || 'auto',
                    jsonReader : {
                        root: "rows",
                        page: "page",
                        repeatitems: false,
                        cell: ""
                      },
                    loadonce: true,
                    loadui: args.loadui, //currently not loading locale file
                    mtype: 'GET',
                    
                    /*pagination*/
                    pagination:true,
                    page: 1,
                	rowNum: 50,
                	rowList: [20,50,100],
                	loadonce:true,
                	viewrecords: false,
                	gridview: true,
                	pagerpos: 'right',
                	/*end pagination*/

                	scroll: false,
                    toolbar: args.toolbar || [false, ""],
                    url: args.url
                  };
            
            jQuery.extend(opts, args);
            
            if (args.width == null) {
                opts.autowidth = true;
                opts.shrinkToFit = true;
            }
            
            jQuery(document.getElementById(args.id)).jqGrid(opts);
            jQuery(window).trigger('resize');
        },
        
        _createToolbar: function(args){
            jQuery(document.getElementById("t_" + args.id)).prev()
            .append(document.getElementById(args.id + "_toolbar_buttons"));
            
            //now create the buttons (jQueryUI)
            jQuery(document.getElementById(args.id + "_toolbar_buttons"))
            .find("a").each(function(index, a){
                a = jQuery(a);
                a.button({icons:{primary:a[0].className}});
            });
            
            //hookup the refresh action
            jQuery(document.getElementById(args.id + "_toolbar_buttons"))
            .find("a.refresh").click(function(event){
                event.stopPropagation();
                jQuery(document.getElementById(args.id))
                .setGridParam({datatype:'json', page:1}) //need to reset the datatype since we are using 'loadonce'
                .trigger("reloadGrid");
                return false;
            });
        },
        
        _refresh: function(selector){
            jQuery(document.getElementById(selector))
            .setGridParam({datatype:'json', page:1}) //need to reset the datatype since we are using 'loadonce'
            .trigger("reloadGrid");
        }
};

var jqGridHelper = Yukon.GridHelper;

jQuery(window).resize(function() {
	jQuery('.ui-jqgrid-btable').each(function(index, elem){
	var parent = jQuery(elem).closest(".jqgrid-container");
		if(parent.length > 0){
			jQuery(elem).setGridWidth(parent.width(), true);
		}
	});
});