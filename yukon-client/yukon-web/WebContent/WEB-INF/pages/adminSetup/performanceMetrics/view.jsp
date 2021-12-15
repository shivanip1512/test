<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="performanceMetrics">

<script>
/**
 * Create a constructor for sparklines that takes some sensible defaults and merges in the individual
 * chart options. This function is also available from the jQuery plugin as $(element).highcharts('SparkLine').
 */
$(function () {
	Highcharts.SparkLine = function (a, b, c) {
	    const hasRenderToArg = typeof a === 'string' || a.nodeName;
	    let options = arguments[hasRenderToArg ? 1 : 0];
	    const defaultOptions = {
	        chart: {
	            renderTo: (options.chart && options.chart.renderTo) || (hasRenderToArg && a),
	            backgroundColor: null,
	            borderWidth: 0,
	            type: 'area',
	            width: 400,
	            height: 100,
	            style: {
	                overflow: 'visible'
	            },
	            // small optimalization, saves 1-2 ms each sparkline
	            skipClone: true,
	        },
	        title: {
	            text: ''
	        },
	        credits: {
	            enabled: false
	        },
	        xAxis: {
	            labels: {
	                enabled: false
	            },
	            title: {
	                text: null
	            },
	            startOnTick: false,
	            endOnTick: false,
	            tickPositions: []
	        },
	        yAxis: {
	            endOnTick: false,
	            startOnTick: false,
	            labels: {
	                enabled: false
	            },
	            title: {
	                text: null
	            },
	            tickPositions: [0]
	        },
	        legend: {
	            enabled: false
	        },
	        tooltip: {
	            hideDelay: 0,
	            outside: true,
	            shared: true
	        },
	        plotOptions: {
	            series: {
	                animation: false,
	                lineWidth: 1,
	                shadow: false,
	                states: {
	                    hover: {
	                        lineWidth: 1
	                    }
	                },
	                marker: {
	                    radius: 1,
	                    states: {
	                        hover: {
	                            radius: 2
	                        }
	                    }
	                },
	                fillOpacity: 0.25
	            },
	            column: {
	                negativeColor: '#910000',
	                borderColor: 'silver'
	            }
	        }
	    };

	    options = Highcharts.merge(defaultOptions, options);

	    return hasRenderToArg ?
	        new Highcharts.Chart(a, options, c) :
	        new Highcharts.Chart(options, b);
	};

	const start = +new Date(),
	    tds = Array.from(document.querySelectorAll('td[data-sparkline]')),
	    fullLen = tds.length;

	let n = 0;

	// Creating 153 sparkline charts is quite fast in modern browsers, but IE8 and mobile
	// can take some seconds, so we split the input into chunks and apply them in timeouts
	// in order avoid locking up the browser process and allow interaction.
	function doChunk() {
	    const time = +new Date(),
	        len = tds.length;

	    for (let i = 0; i < len; i += 1) {
	        const td = tds[i];
	        const stringdata = td.dataset.sparkline;
	        const arr = stringdata.split('; ');
	        const data = arr[0].split(', ').map(parseFloat);
	        const chart = {};

	        if (arr[1]) {
	            chart.type = arr[1];
	        }

	        Highcharts.SparkLine(td, {
	            series: [{
	                data: data,
	                pointStart: 1
	            }],
	            tooltip: {
	                headerFormat: '<span style="font-size: 10px">' + td.parentElement.querySelector('td').innerText + ', {point.x}:</span><br/>',
	                pointFormat: '<b>{point.y}.000</b>'
	            },
	            chart: chart
	        });

	        n += 1;

	        // If the process takes too much time, run a timeout to allow interaction with the browser
	        if (new Date() - time > 500) {
	            tds.splice(0, i + 1);
	            setTimeout(doChunk, 0);
	            break;
	        }
	    }
	}
	doChunk();
	
});


</script>

<style>
	
	.highcharts-tooltip > span {
	    background: white;
	    border: 1px solid silver;
	    border-radius: 3px;
	    box-shadow: 1px 1px 2px #888;
	    padding: 8px;
	}
</style>

    <hr>
        <div>
            <div class="dib vam">
                <i:inline key="yukon.common.filterBy"/>
            </div>
            <div class="dib vam">
                <dt:dateRange startValue="${now}" endValue="${twoMonthsFromNow}"/>
            </div>
            <div class="dib fr">
                <cti:button label="Apply" classes="primary"/>
            </div>
        </div>
    <hr>
    
    <div id="result"></div>
    
    <div class="column-12-12">
        <div class="column one">
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th>Point Name</th>
                        <th>Graph</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="metricHolder" items="${metrics}">
                        <tr class="js-row">
                            <td>
                                <a href="#">${metricHolder.point.pointName}</a>
                            </td>
                            <td data-sparkline="${metricHolder.pointValues}"></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="column two nogutter">
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th>Point Name</th>
                        <th>Graph</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="metricHolder" items="${metrics}">
                        <tr class="js-row">
                            <td>
                                <a href="#">${metricHolder.point.pointName}</a>
                            </td>
                            <td data-sparkline="${metricHolder.pointValues}"></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    
</cti:standardPage>