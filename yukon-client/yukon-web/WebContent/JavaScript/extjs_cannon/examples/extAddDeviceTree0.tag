<%-- treeId will be used as part of the name of the state cookie (among other internal things I assume) --%>
<%-- treeCss is the path to a css file containing classes to override standard Cannon tree.css styles, if not specified --%>
<%--    /JavaScript/extjs_cannon/resources/css/tree.css is used (which contains general Cannon overrrides of Ext's tree.css) --%>
<%-- clickCallback is the name of a js function to be called when tre nodes are clicked. Should be of the form clickCallback(node, event){} --%>
<%@ attribute name="treeId" required="true" type="java.lang.String"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="rootVisible" required="true" type="java.lang.String"%>
<%@ attribute name="treeCss" required="false" type="java.lang.String"%>
<%@ attribute name="clickCallback" required="false" type="java.lang.String"%>

<%-- Popup window stuff --%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="modal" required="true" type="java.lang.String"%>

<%-- Required to retrieve JSON from a url --%>
<%-- JSON should be a list of children of the root node (root node is created in javascript below) --%>
<%-- Use baseParams to append parameters to your dataUrl, baseParams should be a array dictionary as a string --%>
<%-- Since you'll be creating the root node in js, things like it's text, href, disable need to be set using rootAttributes parameter --%>
<%-- rootAttributes should be convertable to a js dictionary --%>
<%@ attribute name="dataUrl" required="false" type="java.lang.String"%>
<%@ attribute name="baseParams" required="false" type="java.lang.String"%>
<%@ attribute name="rootAttributes" required="false" type="java.lang.String"%>

<%-- Required to statically set the JSON --%>
<%-- JSON should be a dictionary starting with attributes of the root node (root node is supplied by you) --%>
<%@ attribute name="dataJson" required="false" type="java.lang.String"%>

<%-- Tag Libs --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%-- CSS & JavaScript includes --%>
<c:choose>
    <c:when test="${not empty treeCss}">
        <cti:includeCss link="${treeCss}"/>
    </c:when>
    <c:otherwise>
        <cti:includeCss link="/JavaScript/extjs_cannon/resources/css/tree.css"/>
    </c:otherwise>
</c:choose>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/TreePanelState.js"/>

<%-- name of the div element to render the tree in, ok if it changes all the time --%>
<cti:uniqueIdentifier prefix="exttreepopupbutton" var="buttonId"/>

<%-- the button --%>
<input type="button" id="${buttonId}" value="${title}"/>

<script type="text/javascript">

    Ext.BLANK_IMAGE_URL = '/JavaScript/extjs/resources/images/default/s.gif';
    
    // add button click
    var button = Ext.get('${buttonId}');
    button.on('click', function(){
    
        // TREE ------------------------------------------------------------------------------------
        
        // fetch JSON data from a URL when dataUrl parameter is used
        // use local JSON expected in dataJson parameter otherwise
        <c:choose>
            <c:when test="${not empty dataUrl}">
            
                var treeLoader = new Ext.tree.TreeLoader({
                        dataUrl:"${dataUrl}"
                        <c:if test="${not empty baseParams}">
                            , baseParams:${baseParams}
                        </c:if>
                    })
                    
                var rootAttributes = $H(${rootAttributes});
                rootAttributes.set('id', "${treeId}_root");
                var root = new Ext.tree.AsyncTreeNode(rootAttributes);
                    
            </c:when>
            <c:otherwise>
            
                var treeLoader = new Ext.tree.TreeLoader();
                var root = new Ext.tree.AsyncTreeNode(${dataJson});
                
            </c:otherwise>
        </c:choose>
    
        // tree panel
        var tree = new Ext.tree.TreePanel({
                id:"${treeId}",
                useArrows:false,
                autoScroll:true,
                animate:false,
                enableDD:false,
                border:false,
                rootVisible:${rootVisible},
                pathSeperator:'>',
                loader: treeLoader,
                region: 'center',
                margins:'3 0 3 3',
                cmargins:'3 3 3 3',
                width: ${width}
            });
        tree.setRootNode(root);
        
        // manage tree state
        var treeState = new TreePanelState(tree);
        treeState.init();
        tree.on('expandnode', treeState.onExpand, treeState);
        tree.on('collapsenode', treeState.onCollapse, treeState);
        treeState.restoreState(tree.root.getPath());
        
        // GRID ------------------------------------------------------------------------------------
        
        
        var groupIds = $A();
      
        // sm  
        var checkColumn = new Ext.grid.CheckboxSelectionModel();
        
        var cm = new Ext.grid.ColumnModel([
            checkColumn,
            {
               id:'groupName',
               header: "Group Name",
               dataIndex: 'groupName',
               width: ${(width) - 150}
            }
        ]);
        
        // group type
        var Group = Ext.data.Record.create([
                {name: 'checked', type:'bool'},
               {name: 'groupName', type: 'string'}
               
          ]);
          
        // store
        var store = new Ext.data.Store({
            proxy:new Ext.data.MemoryProxy(),
            reader: new Ext.data.XmlReader({record: 'group'}, Group)
        });
        
        
        // click callback
        function appendGroupToStore(node, event) {
        
            if (groupIds.indexOf(node.id) < 0) {
            
                node.disable();
            
                var g = new Group({
                    checked:true,
                    groupName:node.text
                });
                
                var idx = store.getCount();
                store.insert(idx, g);
                checkColumn.selectRow(idx, true);
                
                groupIds.push(node.id);
            }
        }
        tree.on('click', appendGroupToStore);
        
        // create the editor grid
        var grid = new Ext.grid.EditorGridPanel({
            store: store,
            cm: cm,
            sm:checkColumn,
            title:'Groups To Be Added To',
            width:${width},
            height:${height},
            region: 'east',
            margins:'3 0 3 3',
            cmargins:'3 3 3 3'
            
        });
        
        function gridClick(node, event) {
            alert(node.id);
        }
        //grid.on('click', gridClick);

        // trigger the data store load
        store.load();
        
        function makeGroupIds() {
        
            var okGroupIds = $A();
            
            for (var i = 0; i < groupIds.length; i++) {
                if (checkColumn.isSelected(i)) {
                    okGroupIds.push(groupIds[i]);
                }
            }
        
            $('groupIds').value = okGroupIds.join(',');
            commitGroupIds();
        }
       
        // WINDOW ----------------------------------------------------------------------------------
        
        // Panel for the east
        var nav = new Ext.Panel({
            title: 'Groups To Add Device To',
            region: 'east',
            split: true,
            width: ${width},
            collapsible: true,
            margins:'3 0 3 3',
            cmargins:'3 3 3 3'
        });
        
        // window
        var win = new Ext.Window({
            title: '${title}',
            width:${width * 2},
            height:${height},
            layout: 'border',
            modal:${modal},
            items:[tree, grid],
            buttons:[
                {text:'Submit', handler:function(){makeGroupIds();win.hide()}},
                {text:'Close', handler:function(){win.hide();}}
            ]
        });

        win.show(this);
    
    });
    
</script>

