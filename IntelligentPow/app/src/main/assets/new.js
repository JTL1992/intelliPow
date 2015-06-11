/**
 * @description: new
 * @author: Li JianHui (sillynemo)
 * @create: 14-10-9 下午3:58
 * @update: 14-11-2 0:20
 */

	var delaytime = 5000; // 间隔时间
	var rbtnGlobal = 1;
    var h = document.documentElement.clientHeight;
    var w = document.documentElement.clientWidth;
    $("#container").css("height",h-70+"px").css("width",w+"px");
    //   造数据    /////////////////////////////////////////////////////////////////////////////////////
    var lastPower = null; //即时功率
    var lastTime = null; //即时时间戳
    var endTime = null;  //每次返回数据的最后时间戳
    //    画图     /////////////////////////////////////////////////////

    var preLineData = [], baseData = null;
    var paras = document.body.baseURI.split("?");
$(function () {
    Highcharts.setOptions({ global: { useUTC: false } });
    if(paras&&paras[1]){

        var reqUrl ="http://"+paras[2]+"/spms/app/acEleRecord?type=2&rbtn=1&"+paras[1];
        $.ajax({
            url:reqUrl,
            success: function (result) {
                //if(typeof result === "string"){
                  //  baseData = JSON.parse(result);
                //}else{
                    baseData = result;
                //}
             //console.log("@@@@@@@@@@@@!!!");
				$('#ss').append(
					"<div id='switchRangeDiv' style='position:absolute; left:10px; top:69px; z-index:99;'>"+
					"<input id='rBtn1' class='cBtn' type='button' onclick='getDataF(\"1\")' value='当前'/>"+
					"<input id='rBtn2' class='cBtn' type='button' onclick='getDataF(\"2\")' value='当天'/>"+
					"<input id='rBtn3' class='cBtn' type='button' onclick='getDataF(\"3\")' value='当月'/>"+
					"<input id='rBtn4' class='cBtn' type='button' onclick='getDataF(\"4\")' value='当年'/>"+
					"</div>");
                draw(baseData);
				
				setTimeout("getAcNewEleRecord()",delaytime);
            }
        });
    }

});
 function draw(baseData) {

        var categories = [], seriesData = [];
        for (var i = 0, l = baseData.length; i < l; i++) {
            categories[i] = baseData[i][0];
            seriesData[i] = baseData[i][1];

            if(i == baseData.length-1){
                  endTime = baseData[i][0];
            }
        }
		
        $('#container').highcharts('StockChart',{
            chart: {
                type: 'column',
                alignTicks: false,
                backgroundColor : "rgba(0,0,0,0)"
            },
            navigator:{
                enabled:true
            },
            tooltip: {
                            valueSuffix: '瓦时'
                        },
            rangeSelector: {
                buttonTheme: { // styles for the buttons
                    fill: 'none',
                    stroke: 'none',
                    'stroke-width': 0,
                    r: 8,
                    style: {
                        color: '#039',
                        fontWeight: 'bold'
                    },
                    states: {
                        hover: {
                        },
                        select: {
                            fill: '#039',
                            style: {
                                color: 'white'
                            }
                        }
                    }
                },
                buttonSpacing:20,
                buttons: [],
                inputEnabled : false,
                labelStyle: {
                    color: 'silver',
                    fontWeight: 'bold'
                },
                selected: 0
            },
            title: {
                align:"left",
                text: '用电情况',
                style:{color:"#FAFAFA"}
            },
            subtitle: {
                text: ''
            },

            legend:{
                enabled:true,
                verticalAlign:"top"
            },

            plotOptions:{
                column:{
                    dataGrouping:{
                        groupPixelWidth:200,
                        units: [['minute',[5]], ['hour',[1]], ['day',[1]], ['month',[1, 3, 6]]]
                    }
                }
            },
            series: [{

                name: '用电量',
                type: 'column',
                data: baseData,
                color:"rgb(255,147,52)"
            }],
            lang: {
                noData: "暂无数据"
            },
            noData: {
                style: {
                    fontWeight: 'bold',
                    fontSize: '15px',
                    color: '#303030'
                }
            },
            exporting : {
                enabled : false
            },
            credits:{
                enabled:false // 禁用版权信息
            },
			yAxis: {
				opposite: false,
				labels: {
					style: {
						color: "#000000"
					}
				},
				min: 0
			}
        });

		window.location.href = "http://127.0.0.1/param?loaded=true";
    }

/**
 * 锁屏用
 */
function hmBlockUI(){
	var divObj = $('<div id="loadingMaskLayerDiv" style="filter:alpha(opacity=50);display:none;width:100%;height:100%;z-index:1000;background-color:gray;opacity:0.5;position:fixed;left:0px;top:0px;"><div class="loading"><i></i><span align="center" vertical-align="center" id="windowMaskLayerInfo"></span></div></div>');
	$('body').append(divObj);
	//$('#windowMaskLayerInfo').html("处理中");
	divObj.show();
}

function hmUnBlockUI(){
	$("#loadingMaskLayerDiv").remove();
}

/**
	获取相应数据
*/
function getDataF(rbtn){
	$("#rBtn1").css("background-color","rgb(0, 51, 153)");
	$("#rBtn2").css("background-color","rgb(0, 51, 153)");
	$("#rBtn3").css("background-color","rgb(0, 51, 153)");
	$("#rBtn4").css("background-color","rgb(0, 51, 153)");
	$("#rBtn" + rbtn).css("background-color","rgb(252, 234, 153)");

	rbtnGlobal = parseInt(rbtn);
	

	

	hmBlockUI();
	 var reqUrl ="http://"+paras[2]+"/spms/app/acEleRecord?type=2&rbtn="+rbtn+"&"+paras[1];
        $.ajax({
            url:reqUrl,
            success: function (result) {
                //if(typeof result === "string"){
                  //  baseData = JSON.parse(result);
                //}else{
                    baseData = result;
                //}
             //console.log("@@@@@@@@@@@@!!!");
				
                draw(baseData);
				hmUnBlockUI();

				
				setTimeout("getAcNewEleRecord()",delaytime);
            }
	});
}



function getAcNewEleRecord(){
	if(paras&&paras[1]){
            var reqUrl ="http://"+paras[2]+"/spms/app/acNewEleRecord?type=2&rbtn="+rbtnGlobal+"&"+paras[1]+"&endTime="+endTime;
			//hmBlockUI();
            $.ajax({
                url:reqUrl,
                success: function (result) {
                console.log("@@@@@@@@@@@@!!!000");
                    if(typeof result === "string"){
                        baseData = JSON.parse(result);
                    }else{
                        baseData = result;
                    }
                    for(var i=0;i<baseData.length;i++){
                        lastTime = baseData[i][0];
                        lastPower = baseData[i][1];
                        //preLineData.push([lastTime, lastPower]);
                        if(i == baseData.length-1){
                            endTime = baseData[i][0];
                        }
                    }
                    redraw(baseData);
                    setTimeout("getAcNewEleRecord()",delaytime);
                }
				//hmUnBlockUI();

            });
        }
}
 /*
     * 画图函数
     */
    function redraw(baseData){
        //console.log("@@@@@@@@@@@@!!!111");
        var h = $('#container').highcharts();
		h.series[0].setData(baseData);
       /* for( var key in baseData ){
        	var dldata = h.series[0].options.data;
        	var lastponit = dldata[dldata.length - 1];
        	var lasttime = new  Date(lastponit[0]);
        	var newtime = new Date(baseData[key][0]);
        	if(lasttime.getMinutes() == newtime.getMinutes()){
        	//console.log("@@@@@@@@@@@@!!!222");
        		dldata.pop();
				//h.series[0].setData(dldata);
				x = baseData[key][0];
                y = baseData[key][1];
                h.series[0].addPoint([x, y], true, false);
        	}else if(lasttime.getMinutes() <  newtime.getMinutes()){
        	//console.log("@@@@@@@@@@@@!!!333");
        		x = baseData[key][0];
                y = baseData[key][1];
                h.series[0].addPoint([x, y], true, false);
        	}else{
        		x = baseData[key][0];
                y = baseData[key][1];
                h.series[0].addPoint([x, y], true, false);
        	}
            
        }*/
    }



