/*--------全局变量--------*/
var Curvenum = 0;//曲线数量
var num = 0;//曲线数量
var appting = "";//用来设定曲线是修改该是添加新的
var deviceId = "";
var userId = "";
var address = "";
var isDevice = "";
var url = window.location.href;
var params = url.split("?")[1].split("&");

for(var i = 0; i<params.length;i++){
	var paramName = params[i].split("=")[0];
	if(paramName == "deviceId"){
		deviceId = params[i].split("=")[1];
	}
	if(paramName == "userId"){
		userId = params[i].split("=")[1];
	}
	if(paramName == "url"){
		address = params[i].split("=")[1];
	}
	if(paramName == "num"){
		num = params[i].split("=")[1];
	}
	if(paramName == "isDevice"){
	    isDevice = params[i].split("=")[1];
	}
}//设备ID
var CurveObj = {};

$(function(){
	$("#container").css("width",(document.documentElement.clientWidth-50)+"px").css("height",(document.documentElement.clientHeight*0.85)+"px")
});
/*
openLineChart();
--------打开折线图----------
function openLineChart(){
	
	$('#container').remove();
	$('#container').append("<span>当前没有舒适曲线<span/>");
}
*/
$(function(){
	flush();
	lo(CurveObj[num].id);
	//lo('1');
})
var clocking = new Array();
var temp = new Array();
var obj ;
/*-----------加载单条曲线数据-------------*/
function lo(Curveids){
	ajaxj("/app/QueryCurveSetting", {Curveid:Curveids,userId:userId}, function (data) {
		obj = data.data;
		clocking = new Array();
		temp = new Array();
		for(var j = 0 ; j < obj.length ; j++){
			//clocking[j] =  obj[j].on_off == 0  ?  obj[j].clocking+',关'  :  obj[j].clocking;
			clocking[j] = obj[j].clocking;
			var r = {
                y: 25,
                marker: {
					fillColor: 'red',
					lineColor: 'red',
					states:{//鼠标经过
						hover:{
						  fillColor: 'red',//点填充色
						  lineColor: 'red'//点边框色
						}
					}
                    //symbol: 'url(/demo/img/sun.png)'
                }
            }
			temp[j] =  (     obj[j].on_off == 0  ?   r   :   parseInt( (   (obj[j].temp == null || obj[j].temp == "") ? 0 : obj[j].temp   ) )   )      ;
		}
		//alert(temp[0]+"-"+temp[1]+"-"+temp[2]+"-"+temp[3]);
		if(obj.length > 0){
			showCh();
			$('#edit').empty();
			weekSetforCurve();
		}else{
			//$('#edit').remove();
			$('#container').empty();
			$('#edit').empty();
			$('#container').append("<div style='color:red;font-size:500%;'><span>当前没有舒睡曲线<span/></div>");
		}
		
		//showcharts();
	});
}
function showCh(){
	//$('#container').remove();
	$('#container').highcharts({
		
	 	chart: {
            	backgroundColor: {
                	linearGradient: [0, 0, 500, 500],
                stops: [
                    [0, '#FFFFFF']
                ]
            },            
            legend: {
	            layout: 'vertical',
	            backgroundColor: 'none',
	            floating: true,
	            align: 'left',
	            x: 100,
	            verticalAlign: 'top',
	            y: 70
        	}     
        },
        title: {
        	style:{
        			color: '#000000',
        			fontWeigth:900,
					fontSize: '50px'
        	},
        	text: '舒睡曲线设置'+(parseInt(num)+1)+'',
        },
        xAxis: {
			
        	categories: clocking,
        	labels:{
            	style: {
					color: '#000000',//刻度颜色
					fontSize: '40px'
				}
            }
            
        },
		plotOptions: { 
			series: { 
				cursor: 'pointer',
				marker: {  
					radius: 30
				},
				dataLabels:{ 
					enabled: true,
					y:25,
                    x:0,
					style:{
						fontSize:'40px'
					} 
				}
			},
			line:{
				lineWidth: 5,
				point:{
					events: { 
						click: function() { 
							window.location.href = "http://127.0.0.1/param?r="+
	"{\"num\":\""+num+"\",\"type\":\"node\",\"id\":\""+obj[this.x].id+"\",\"on_off\":"+
	obj[this.x].on_off+",\"windspeed\":"+obj[this.x].windspeed+",\"mode\":"+obj[this.x].mode+",\"temp\":"+obj[this.x].temp+",\"clocking\":\"" + obj[this.x].clocking + "\"}";
							
						} 
					}
				}
			}
		},
        yAxis: {
			allowDecimals:false,
        	title: {
        		text: '温度(℃)',
        		color:'#000000',
				style:{
					fontSize: '40px'
				}
        	},
				plotOptions: {
            series: {
                showCheckbox: false
            }
},
        	labels:{
            	style: {
					color: '#000000',//刻度颜色,
					fontSize: '40px'
				}
            },
			lineColor: '#c0c0c0',
            lineWidth: 1
        },
		legend:{
			enabled:false
			},
      series: [{
            data: temp,
           // step: false,//left,right,center,true,默认false
            name: '<font style=\"font-size:40px\">温度</font>'
        }]
    });
    $('#charts').hide();
}
/*--------曲线图翻页--------*/
function nextPage(lor){
	if(lor == "l"){
		if(num == 0){//调整曲线数量 - 
			num = Curvenum;
		}else{
			num = parseInt(num)-1;	
		}
		lo(CurveObj[num].id);
	}else{
		if(num >= Curvenum){//调整曲线数量 + 
			num = 0;
		}else{
			num = parseInt(num)+1;	
		}
		lo(CurveObj[num].id);
	}
	flush();
	window.location.href = "http://127.0.0.1/param?r={\"type\":\"page\",\"num\":\""+num+"\"}";
}

/*----实时刷新----*/
function flush(){
	//刷新折线图
	ajaxj("/app/QueryCurve", {deviceId:deviceId,userId:userId}, function (data) {
			CurveObj = data.data;
			Curvenum = CurveObj.length;
			//lo(CurveObj[num].id);
			
	},{async : false});
	/*if( deviceId != null && "" != deviceId ){
		/*--重新加载曲线数量--
		ajaxj("/app/QueryCurveNum", {deviceId:deviceId,userId:userId}, function (nums) {//获取曲线数量
			if(parseInt(nums.data)== 0){
				Curvenum=0;
			}else{
				Curvenum = parseInt(nums.data)-1;
			}
		},{async : false});
	}*/
}


function repeat1(){
	var curveId = $(".RepeatClassId").attr("val");
	if(typeof(monday)== "undefined") monday = 0;
	if(typeof(tuesday) == "undefined") tuesday = 0;
	if(typeof(wednesday) == "undefined") wednesday = 0;
	if(typeof(thursday) == "undefined") thursday = 0;
	if(typeof(firday) == "undefined") firday = 0;
	if(typeof(saturday) == "undefined") saturday = 0;
	if(typeof(sunday) == "undefined") sunday = 0;
	window.location.href = "http://127.0.0.1/param?r={\"num\":\""+num+"\",\"type\":\"rpe\",\"id\":\""+CurveObj[num].id+"\"}";
}
/*---------加载按钮（曲线图）--------*/
function weekSetforCurve(){
	var obj = CurveObj[num];
	var str2 = "<input type=\"submit\" name=\"button\" id=\"button\" onclick=\"edit1()\" class=\"btn-add\" value=\"添加节点\" />";
	if(isDevice == "1"){
	    str2 = str2 + "<input type=\"submit\" name=\"button\" id=\"button\" onclick=\"repeat1()\" class=\"btn-repeat\" value=\"重复日期\" />";
	}
	$("#edit").append(str2);
	if(isDevice != "1"){
	    $(".btn-add").css("width","95%");
	}
		/*var str = "";
	if(deviceId != ""){
		if(obj!=null){
			if(obj.monday!=null && obj.monday!="0"){
				str = str + "&nbsp<span class=\"mondayRepeatClass\" style=\"background-color:#808080\" val=\""+obj.monday+"\">周一&nbsp</span>";			
			}
			if(obj.tuesday!=null && obj.tuesday!="0"){
				str = str + "<span class=\"tuesdayRepeatClass\" style=\"background-color:#808080\" val=\""+obj.tuesday+"\">周二&nbsp</span>";	
			}
			if(obj.wednesday!=null && obj.wednesday!="0"){
				str = str + "<span class=\"wednesdayRepeatClass\" style=\"background-color:#808080\" val=\""+obj.wednesday+"\">周三&nbsp</span>";	
			}
			if(obj.thursday!=null && obj.thursday!="0"){
				str = str + "<span class=\"thursdayRepeatClass\" style=\"background-color:#808080\" val=\""+obj.thursday+"\">周四&nbsp</span>";	
			}
			if(obj.firday!=null && obj.firday!="0"){
				str = str + "<span class=\"firdayRepeatClass\" style=\"background-color:#808080\" val=\""+obj.firday+"\">周五&nbsp</span>";	
			}
			if(obj.saturday!=null && obj.saturday!="0"){
				str = str + "<span class=\"saturdayRepeatClass\" style=\"background-color:#808080\" val=\""+obj.saturday+"\">周六&nbsp</span>";	
			}
			if(obj.sunday!=null && obj.sunday!="0"){
				str = str + "<span class=\"sundayRepeatClass\" style=\"background-color:#808080\" val=\""+obj.sunday+"\">周日&nbsp</span>";	
			}
		}
		$(".repeatClass").append(str); 
	}*/
}
function edit1(){
	window.location.href = "http://127.0.0.1/param?r={\"type\":\"add\",\"num\":\""+num+"\",\"id\":\""+CurveObj[num].id+"\"}";
}
function del(id){
	window.location.href = "http://127.0.0.1/param?r={\"num\":\""+num+"\",\"type\":\"delete\",\"id\":\""+id+"\"}";
}

function ajaxj(url, data, callBack, conf) {

//    var tempasync = true;
//    var divObj = $("<div><div class='progress-label'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></div>");
    var divObj = $('<div id="loadingMaskLayerDiv" style="filter:alpha(opacity=50);display:none;width:100%;height:100%;z-index:1000;background-color:gray;opacity:0.5;position:fixed;left:0px;top:0px;"><div class="loading"><i></i><span id="windowMaskLayerInfo"></span></div></div>');
    var successInfo = ""
//    var theConf = {
//    		
//    }
//    if (conf) {
    	var theConf = {
    			async:true,
    			showWaiting:false,
    			handdingInfo:"处理中",
    			successInfo:"处理成功",
    			failtureInfo:"处理失败"
    			
    	}
    	$.extend(theConf,conf);
        if (theConf.showWaiting) {
        	$('body').append(divObj);
        	$('#windowMaskLayerInfo',divObj).text(theConf.handdingInfo);
        	divObj.show();
        }
//    } 

    $.ajax({
        type: "POST",
        url: "http://" + address + "/spms" + url,
        timeout: 600000,
        data: JSON.stringify(data),
        async: theConf.async,
        dataType: "json",
        contentType: "application/json",
        success: function(nowData, textStatus, XMLHttpRequest) {
            var info = theConf.successInfo;
            if(nowData) {
                if(nowData.message) {
                    if(nowData.message.errorMsg) {
                        info=theConf.failtureInfo;
                    }
                }
            }
        	$('#windowMaskLayerInfo',divObj).text();
        	setTimeout(function(){
        		divObj.remove();
        	},1000)
            var obj = {};
            obj.data = nowData;
            obj.result = true;
            if (textStatus != "success") {
                obj.result = false;
            }

            if(nowData.common_error_info) {
            	if(nowData.common_error_info.exceptionType == "0001") {
            		window.location.href=nowData.common_error_info.action;
            	} else if(nowData.common_error_info.exceptionType == "0002") {
            		//$.alert(nowData.common_error_info.message);
            	}

            } else {
            	callBack(obj, textStatus, XMLHttpRequest);
            }
        },
		error: function(msg, a, b, c) {
        	$('#windowMaskLayerInfo',divObj).text(theConf.failtureInfo);
        	setTimeout(function(){
        		divObj.remove();
        	},1000)
    	    if (msg.status == 0) {
                return
            } else {
                if (msg.statusText == "OK" && msg.status == "200") {
                    if (conf && conf.noFrame) {
                    } else {
                        /*if (window.parent) {
                        	if(window.parent.parent) {
                        		window.parent.parent.location.href = clickmedBaseUrl + clickmedAdminPath + "/doLogin";
                        	} else {
                        		window.parent.location.href = clickmedBaseUrl + clickmedAdminPath + "/doLogin";
                        	}
                        } else {
                            window.location.href = clickmedBaseUrl + clickmedAdminPath + "/doLogin";
                        }*/
                    }

                } else {
					window.location.href = "http://127.0.0.1/param?r={\"type\":\"error\"}";
//                    	$('#windowMaskLayerInfo',divObj).text("处理失败");
//                    	  window.location.href = clickmedBaseUrl + clickmedAdminPath + "/view/common/index.html"
                }
            }
        }
    });
}

