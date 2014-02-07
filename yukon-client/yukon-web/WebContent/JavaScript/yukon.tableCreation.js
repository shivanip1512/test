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
Yukon.namespace('Yukon.CreateHtmlTableFromJson');

Yukon.CreateHtmlTableFromJson = (function () {
    var mod;

    mod = {
        createHtmlTableFromJson: function (dataArray, outputCols, rowCallback) {
            var resultTable = document.createElement("table"),
                resultTableHead = document.createElement("thead"),
                resultTableBody,
                headRow,
                col,
                headCell,
                i,
                tableRow,
                tableCell,
                node,
                link,
                linkFuncGenerate,
                linkFunc,
                dataString,
                displayDataString,
                maxLen,
                text,
                emptyFunction = function () {};
            resultTable.appendChild(resultTableHead);
            headRow = document.createElement("tr");
            resultTableHead.appendChild(headRow);
            for (col = 0; col < outputCols.length; col++) {
                headCell = document.createElement("th");
                headCell.appendChild(document.createTextNode(outputCols[col].title));
                headRow.appendChild(headCell);
            }
            resultTableBody = document.createElement("tbody");
            resultTable.appendChild(resultTableBody);
            for (i = 0; i < dataArray.length; i++) {
                tableRow = document.createElement("tr");
                (rowCallback || emptyFunction)(tableRow, dataArray[i]);
                resultTableBody.appendChild(tableRow);
                for (col = 0; col < outputCols.length; col++) {
                    tableCell = document.createElement("td");
                    node = tableCell;
                    tableRow.appendChild(tableCell);
                    if (outputCols[col].link) {
                        link = document.createElement("a");
                        node = link;
                        tableCell.appendChild(link);
                        linkFuncGenerate = outputCols[col].link;
                        linkFunc = linkFuncGenerate(dataArray[i], link);
        
                        if (linkFunc !== null) {
                            link.setAttribute("href", "javascript:void(0)");
                            jQuery(link).on('click', linkFunc);
                        }
                    }
        
                    dataString = dataArray[i][outputCols[col].field];
                    if (dataString === undefined) {
                        dataString = outputCols[col].field;
                    }
                    displayDataString = dataString;
                    maxLen = outputCols[col].maxLen;
                    if (maxLen && dataString.length > maxLen) {
                        displayDataString = dataString.truncate(maxLen, '...');
                        tableCell.setAttribute("title", dataString);
                    }
        
                    //in case we want to display static text but maintain a value
                    text = document.createTextNode(displayDataString);
                    node.appendChild(text);
                }
            }
            return resultTable;
        }
    };
    return mod;
})();
