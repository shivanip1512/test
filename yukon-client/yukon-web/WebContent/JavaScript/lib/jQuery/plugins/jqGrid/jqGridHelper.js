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
                    caption: args.title,
                    colModel: args.colModel,
                    datatype: 'json',
                    height: args.height || 'auto',
                    width: args.width || 'auto',
                    jsonReader : {
                        root: "root",
                        page: "",
                        total: "",
                        records: "",
                        repeatitems: false,
                        cell: "",
                        id: 0
                      },
                    loadonce: true,
                    loadui: args.loadui, //currently not loading locale file
                    mtype: 'GET',
                    scroll: true,
                    toolbar: args.toolbar || [false, ""],
                    url: args.url
                  };
            
            jQuery.extend(opts, args);
            
            if(args.width == null || typeof(args.width) == 'undefined'){
                opts.autowidth = true;
                opts.shrinkToFit = 200;
            }
            
            jQuery(document.getElementById(args.id)).jqGrid(opts);
        },
        
        _createToolbar: function(args){
            jQuery(document.getElementById("t_" + args.id))
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

jQuery(window).delegate(".ui-jqgrid", 'onresize', function(event){
    debug('resize!');
    jQuery(this).jqGrid();
});