/*
Helper singleton TreeHelper

*/

if(typeof(TreeHelper) == 'undefined'){
    TreeHelper = new function(){
        this.class_type = typeof(this) +":TreeHelper";
        this.description = "Singleton for supporting common search and expand/collapse toggles for dynatrees.";
        this.search_index = 0;
        this.search_count = 0;
        this.timeout = null;
        this.search_term = "";
        this.search_id = "";
        this.context = null;
        this.search_input = null;
        this.initialized = false;
        
        /**
         * Registers the UI event handlers for the Expand/Collapse buttons and search inputs
         * 
         * @return  Returns true if connections are registered, false otherwise
         */
        this.init = function(){
            if(!this.initialized){
                this.initialized = true;
              //expand / collapse toggle link.
                jQuery(".open_all, .close_all").live('click', function(event){
                     jQuery("#" + jQuery(event.currentTarget).attr("data-tree-id")).dynatree("getRoot").visit(function(node){
                         node.expand(jQuery(event.currentTarget).hasClass("open_all"));
                     });
                });
                
                jQuery("input.searchTree").addClass("default");
                
                jQuery("input.searchTree").live('keyup', function(event){
                    //When we hit the enter key, we want to scroll to the next subsequent match wrapping to the first when we hit the last
                    if(event.keyCode == 13 /* ENTER KEY */){
                        if(TreeHelper.search_count > 0){
                            jQuery(TreeHelper.context).scrollTo(".found:eq(" + (++TreeHelper.search_index % TreeHelper.search_count) + ")");
                        }
                    }else{
                        //only search when the user has paused typing.  we define this as 275ms.
                        //This may need adjustment but falls within the typical 250-300ms defined by
                        //most usability experts
                        clearTimeout(TreeHelper.timeout);
                        jQuery(event.currentTarget).removeClass('error');
                        TreeHelper.search_term = event.currentTarget.value;
                        TreeHelper.timeout = setTimeout('TreeHelper.search()', 275);
                    }
                });
                
                jQuery("input.searchTree").live('focus', function(event){
                    TreeHelper.context = document.getElementById(jQuery(event.currentTarget).attr("data-tree-id"));
                    TreeHelper.search_input = jQuery(event.currentTarget);
                    if(TreeHelper.search_input.hasClass('default')){
                        TreeHelper.search_input.removeClass("default").val("");
                    }
                });
                
                jQuery("input.searchTree").live('blur', function(event){
                    TreeHelper.search_input = jQuery(event.currentTarget);
                    if(TreeHelper.search_input.val() == ""){
                        TreeHelper.search_input.addClass("default").removeClass("error").val(TreeHelper.search_input.attr("data-default-value"));
                    }
                });
                return true;
            }
            return false;
        };
        
        /**
         * Performs a search on the 'current' tree for a match on the value of the 'current' input
         */
        this.search = function(){
            this.search_input.addClass("working");
            this.search_index = 0;
            jQuery("span.found", jQuery(TreeHelper.context)).removeClass("found");
            var match = TreeHelper.search_input.val().toLowerCase();

            if(match.length > 0){
                jQuery(TreeHelper.context).dynatree("getRoot").visit(function(node){
                    if(node.data.title.toLowerCase().match(match)){
                        node.makeVisible();   //show this guy
                        if(node.span){
                            node.span.className += " found";
                        }
                    }
                }, false);
            }
            var hits = jQuery(".found", this.context);
            this.search_count = hits.length;
            if(hits[0]){
                jQuery(this.context).scrollTo(jQuery(".found:eq(0)", this.context)[0]);
            }else{
                if(this.search_term != ""){
                    this.search_input.addClass("error");
                }
            }
            this.search_input.removeClass("working");
        };
        
        this.redirect_node_data_href_onActivate = function(node){
            if(node.data && node.data.href){
                window.location = node.data.href;
            }
        };
        
        this.init();
    };
}