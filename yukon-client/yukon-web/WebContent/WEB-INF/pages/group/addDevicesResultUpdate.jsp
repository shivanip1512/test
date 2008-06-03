<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:bulkProcessResult result="${results}" headerText="Results of bulk device add"/>

<script type="text/javascript">
	
	function updateResults(){
		new Ajax.Updater($('resultsDiv'), "/spring/group/editor/updateBulkDeviceAdd", {"method":"get", "evalScripts":true});
	}

	function checkUpdate(){		
		if(${!results.complete}){
			setTimeout("updateResults()", 2000);
		}
	}
	
	checkUpdate();
	
</script>