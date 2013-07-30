// The following function creates a table given an array of row data
// and an array that specifies the order of the columns. An example
// dataArray may look like this:
//    [{'firstname': 'Tom', 'lastname': 'Mack'},
//     {'firstname': 'Jon', 'lastname': 'Dayton'},
//     {'firstname': 'Aaron', 'lastname': 'Solberg'}]
// An example outputCols might then look like this:
//    [{'title': 'First Name', 'field': 'firstname'},
//     {'title': 'Last Name', 'field': 'lastname'}]
// Note that the keys and values from the dataArray are completely
// arbitrary. On the other hand, the outputCols array must have 
// 'title', 'field', and (optionally) 'link' keys for each entry
// and the value of the 'field' key must match a key in the dataArray.
// The rowCallback is a function of the following form:
//    function(rowElement, data) {
//        if (data.firstname == 'Tom') {
//            rowElement.addClassName('awesome'); # causes <tr class="awesome"> ... </tr> to be output
//        }
//     }
// An unattached table DOM element is returned from this function.
function createHtmlTableFromJson(dataArray, outputCols, rowCallback) {
    var resultTable = document.createElement("table");
    var resultTableHead = document.createElement("thead");
    resultTable.appendChild(resultTableHead);
    var headRow = document.createElement("tr");
    resultTableHead.appendChild(headRow);
    for (var col = 0; col < outputCols.length; col++) {
        var headCell = document.createElement("th");
        headCell.appendChild(document.createTextNode(outputCols[col].title));
        headRow.appendChild(headCell);
    }
    var resultTableBody = document.createElement("tbody");
    resultTable.appendChild(resultTableBody);
    for (var i=0; i < dataArray.length; i++) {
      var tableRow = document.createElement("tr");
      (rowCallback || Prototype.emptyFunction)(tableRow, dataArray[i]);
      resultTableBody.appendChild(tableRow);
      for (var col = 0; col < outputCols.length; col++) {
          var tableCell = document.createElement("td");
          var node = tableCell;
          tableRow.appendChild(tableCell);
          if (outputCols[col].link) {
              var link = document.createElement("a");
              node = link;
              tableCell.appendChild(link);
              var linkFuncGenerate = outputCols[col].link;
              var linkFunc = linkFuncGenerate(dataArray[i], link);
              
              if(linkFunc != null) {
              	  link.setAttribute("href", "javascript:void(0)");
	              Event.observe(link,'click',linkFunc);
              }
          }
          
          var dataString = dataArray[i][outputCols[col].field];
          if(dataString === undefined) {
          	dataString = outputCols[col].field;
          }
          var displayDataString = dataString;
          var maxLen = outputCols[col].maxLen;
          if (maxLen && dataString.length > maxLen) {
        	  displayDataString = dataString.truncate(maxLen, '...');
        	  tableCell.setAttribute("title", dataString);
          }
          
          //in case we want to display static text but maintain a value
          var text = document.createTextNode(displayDataString);
          node.appendChild(text);
      }
    }
    return resultTable;
}
