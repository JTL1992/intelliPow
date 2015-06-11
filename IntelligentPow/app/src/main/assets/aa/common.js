var baseRedHeadUrl = "/cmcp/reddocs/";
var jqueryDialogNUM=1;
var attachment_regixType = /(\.|\/)(docx|doc|xlsx|xls|pptx|pptx|ppt|txt|pdf|bmp|jpg|tiff|gif|pcx|tga|exif|fpx|svg|psd|cdr|png|pcd|dxf|ufo|eps|ai|raw|pub|dwg|dws|dwt|dxf|rar|zip)$/i;
var attachment_maxSize = 21000000;
(function($) {
$("ol li a").each(function(){  
          var xx=$(this).html();  
          var id=$(this).attr("id");
          if(id !=""){
           $(this).replaceWith("<span id="+id+">"+xx+"</span>");
          }
          $(this).replaceWith(xx);  
      });  
    $.extend({
        /**
         * @see 将javascript数据类型转换为json字符串
         * @param 待转换对象,支持  object,array,string,function,number,boolean,regexp
         * @return 返回json字符串
         */
        toJSON: function(object) {
            return JSON.stringify(object)
        },
        toast: function(msg,time) {
                var divObj = $('<div id="loadingMaskLayerDiv" style="filter:alpha(opacity=50);display:none;width:100%;height:100%;z-index:1000;background-color:gray;opacity:0.5;position:fixed;left:0px;top:0px;"><div class="loading"><span id="windowMaskLayerInfo" style="position:initial; "></span></div></div>');
                $('body').append(divObj);
                $('#windowMaskLayerInfo',divObj).text(msg);
                divObj.show();
                if(time) {
                    time = time;
                } else {
                    time = 2000;
                }
                setTimeout(function(){
                    divObj.remove();
                },time)
        },
        alert: function(customConf) {
    		if($("#common_alert").length>0) {
    			$("#common_alert").remove()
    		}
            var conf = {
                title: '提示信息',
                msg: '',
                height: 180,
                confirmClick: ''
            }
            if (typeof (customConf) == "object") {
                $.extend(conf, customConf);
            } else {
                conf.msg = customConf;
            }


            var divObj = $("<div id='common_alert' title='" + conf.title + "' z-index='9999'>" + conf.msg + "</div>");
            divObj.dialog({
                resizable: false,
                height: conf.height,
                modal: true,
                buttons: {
                    "确定": function() {
                        $(this).dialog("close");
                        if (conf.confirmClick.length > 0) {
                            eval(conf.confirmClick + "()");
                        }else if(typeof(conf.confirmClick)=="function"){
                        	conf.confirmClick();
                        }
                    }
                }
            });
            $(".ui-dialog-titlebar-close").remove();
        },
        confirm: function(customConf) {
            weboffice_hide();

            var conf = {
                title: '提示信息',
                msg: '',
                height: 180,
                confirmBtn: true,
                cancelBtn: true,
                closeBtn: true,
                confirmClick: '',
                data: {}
            }
            if (typeof (customConf) == "object") {
                $.extend(conf, customConf);
            } else {
                conf.msg = customConf;
            }
            var btnObj = {};
            if (conf.confirmBtn) {
                btnObj["确定"] = function() {
                    $(this).dialog("close");
                    if (typeof conf.confirmClick == "function") {
                        conf.confirmClick(conf.data,true);
                    } else {
                        if (conf.confirmClick&&(conf.confirmClick.length > 0)) {
                            eval(conf.confirmClick + "(" + $.toJSON(conf.data) + ",true)");
                        }
                    }
                    weboffice_show();

                }
            }

            if (conf.cancelBtn) {
                btnObj["取消"] = function() {
                    $(this).dialog("close");
                    if (typeof conf.cancelClick == "function") {
                        conf.cancelClick(conf.data,true);
                    } else {
                        if (conf.cancelClick&&(conf.cancelClick.length > 0)) {
                            eval(conf.cancelClick + "(" + $.toJSON(conf.data) + ",true)");
                        }
                    }
                    weboffice_show();

                }
            }
            var divObj = $("<div id='common_confirm' title='" + conf.title + "'>" + conf.msg + "</div>");
            divObj.dialog({
                resizable: false,
                height: conf.height,
                modal: true,
                buttons: btnObj
            });

            if (!conf.closeBtn) {
                $(".ui-dialog-titlebar-close").remove();
            }
            $(".ui-dialog-titlebar-close").remove();
        }
    })

    $.fn.extend({
        initMulSelect: function(value) {
            var arr = value.split(",");
            $(this).find("option").each(function() {
                if ($.inArray($(this).val(), arr) != -1) {
                    $(this).replaceWith("<option value='" + $(this).val() + "' selected>" + $(this).html() + "</option>");
                }
            })
        },
        initSpanDict: function(value) {
            var arr = value.split(",");
            $(this).find("span").each(function() {
                if ($.inArray($(this).attr("value"), arr) != -1) {
                    $(this).show();
                }
            })
        },
        initRadio: function(value) {
            $(this).each(function() {
                if ($(this).val() == value) {
                    $(this).attr("checked", "checked");
                }
            })
        }
    })

    if ($.datepicker) {
        //对引入datepicker组件的页面进行渲染
        $.datepicker.setDefaults(
                {
                    showAnim: "blind",
                    showButtonPanel: true,
                    dateFormat: "yy-mm-dd",
                    showOn: "button",
                    buttonText: "选择",
                    monthNames: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
                    monthNamesShort: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
                    dayNames: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
                    dayNamesMin: ["日", "一", "二", "三", "四", "五", "六"],
                    changeMonth: true,
                    changeYear: true,
                    closeText: "关闭",
                    currentText: "今天"
                })
    }

})(jQuery)

/**
 * @see 获取url参数 如http://localhost:8080/cmcp/a?username=sss
 * @param ?后面的key，如上面的username
 * @return  如果存在该key返回对应值，否则直接返回null
 */
function getURLParam(ParamName, parentSearch)
{
    var reg = new RegExp("(^|&)" + ParamName + "=([^&]*)(&|$)");
    var r = null;
    if (parentSearch) {
        r = decodeURI(parent.window.location.search).substr(1).match(reg);
    } else {
        r = decodeURI(window.location.search).substr(1).match(reg);
    }
    if (r != null) {
        return unescape(r[2]);
    }
    return null;
}

/**
 * @see 获取项目base路径（http://www.xxx.sss/cmcp）
 * @return  项目base路径
 */
function getRootPath() {
    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPaht = curWwwPath.substring(0, pos);
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    return(localhostPaht + projectName);
}

var diseaseNameTargetCodes = [];

var baseUrl = getRootPath();

var clickmedAdminPath = "";
var clickmedBaseUrl = baseUrl;
function doRequest(url, data, callBack, conf) {
    var tempasync = true;
//    var divObj = $("<div><div class='progress-label'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></div>");
    var divObj = $('<div id="loadingMaskLayerDiv" style="filter:alpha(opacity=50);display:none;width:100%;height:100%;z-index:1000;background-color:gray;opacity:0.5;position:fixed;left:0px;top:0px;"><div class="loading"><i></i><span id="windowMaskLayerInfo"></span></div></div>');
    if (conf) {
        if (conf.conf != "undefined") {
            tempasync = conf.async;
        }
        if (conf.showWaiting) {
        	$('body').append(divObj);
        	$('#windowMaskLayerInfo',divObj).text("处理中");
        	divObj.show();
//            divObj.dialog({
//                modal: true,
//                height: 50,
//                closeOnEscape: false
//            })
//            divObj.attr("class", "").prev().remove();
//            divObj.progressbar({value: false})
        }
    }
    $.ajax({
        type: "POST",
        url: clickmedBaseUrl + clickmedAdminPath + url,
        timeout: 600000,
        data: data,
        dataType: "json",
        async: tempasync,
        success: function(nowData, textStatus, XMLHttpRequest) {
        	$('#windowMaskLayerInfo',divObj).text("处理成功");
        	setTimeout(function(){
        		divObj.remove();
        	},1000)

            var obj = {};
            obj.data = nowData;
            obj.result = true;
            if (textStatus != "success") {
                obj.result = false;
            }
            callBack(obj, textStatus, XMLHttpRequest);
        },
        error: function(msg) {
        	$('#windowMaskLayerInfo',divObj).text("处理失败");
        	setTimeout(function(){
        		divObj.remove();
        	},1000)
        }
    });
}



/**
 * 显示出页面遮罩层
 * @param info 显示信息
 */
function loadingMaskLayerMM(info){
	if(!info)
		info = '请稍等！';
	$('body').find('#windowMaskLayerInfo').text(info);
	$('body').find('#loadingMaskLayerDiv').show(500);
}

/**
 * @see 使用ajax请求后台调用方法
 * @param  url 对应后台controller中的mapping
 * 				   data 对应后台controller中的方法参数
 * 				   callback 回调函数，会给回调函数传入一个对象如为data，
 * 								如果data.result为true的话则说明响应成功，否则为相应失败
 * 								其中data.data为后台返回的数据
 * 				   conf 配置对象，其中async用来定义是同步还是异步
 * 										  showWaiting用来定义是否显示进度条
 * @return  如果存在该key返回对应值，否则直接返回null
 */
function doJsonRequest(url, data, callBack, conf) {
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
        url: clickmedBaseUrl + clickmedAdminPath + url,
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
            		$.alert(nowData.common_error_info.message);
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
                        if (window.parent) {
                        	if(window.parent.parent) {
                        		window.parent.parent.location.href = clickmedBaseUrl + clickmedAdminPath + "/doLogin";
                        	} else {
                        		window.parent.location.href = clickmedBaseUrl + clickmedAdminPath + "/doLogin";
                        	}
                        } else {
                            window.location.href = clickmedBaseUrl + clickmedAdminPath + "/doLogin";
                        }
                    }

                } else {
//                    	$('#windowMaskLayerInfo',divObj).text("处理失败");
//                    	  window.location.href = clickmedBaseUrl + clickmedAdminPath + "/view/common/index.html"
                }
            }
        }
    });
}

Date.prototype.format = function(format) {
    if (this.getTime() == 0) {
        return null;
    }
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    }
    if (!format) {
        format = "yyyy-M-d"
    }
    if (/(y+)/.test(format))
        format = format.replace(RegExp.$1,
                (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(format))
            format = format.replace(RegExp.$1,
                    RegExp.$1.length == 1 ? o[k] :
                    ("00" + o[k]).substr(("" + o[k]).length));
    return format;
}

Date.prototype.getTheYear = function() {
    return this.getFullYear();
}

Date.prototype.getTheMonth = function() {
    var str = (this.getMonth()+1).toString();
    if (str.length <= 1) {
        str = "0" + str;
    }
    return str;
}

Date.prototype.getTheDate = function() {
    var str = this.getDate().toString();
    if (str.length <= 1) {
        str = "0" + str;
    }
    return str;
}
Date.prototype.getTheHour = function() {
    var str = this.getHours().toString();
    if (str.length <= 1) {
        str = "0" + str;
    }
    return str;
}

Date.prototype.getTheMinute = function() {
    var str = this.getMinutes().toString();
    if (str.length <= 1) {
        str = "0" + str;
    }
    return str;
}

$(function() {
    initDict()
})
function initDict() {
    var dictNames = new Array();
    $("[dictSrc]").each(function() {
        dictNames.push($(this).attr("dictSrc"));
    })

    if (dictNames.length > 0) {
        doJsonRequest("/dict/getDicts", dictNames, function(data, status) {
            if (data.result) {
                var data = data.data;
                initDictData(data, dictNames)
            } else {
                $.alert("数据字典获取失败.");
            }
        }, {async: false, noFrame: true})
    }
}

function initDictData(data, dictNames) {
    for (var i = 0; i < dictNames.length; i++) {
        $("[dictSrc='" + dictNames[i] + "']").each(function() {

            var objArr = data[dictNames[i]];
            var tempAppendStr = "";
            for (var j = 0; j < objArr.length; j++) {
                if (this.nodeName == "SELECT") {
                    tempAppendStr = tempAppendStr + "<option value='" + objArr[j].code + "'>" + objArr[j].value + "</option>"
                }
                if (this.nodeName == "SPAN") {
                    tempAppendStr = tempAppendStr + "<span value='" + objArr[j].code + "' style='display:none'>" + objArr[j].value + "</span>"
                }
                if (this.nodeName == "INPUT") {
                    var disabled = false;
                    if ($(this).attr("disabled")) {
                        disabled = true
                    }
                    tempAppendStr = tempAppendStr + "<input type='radio'  " + (disabled ? "disabled" : "") + "  name = '" + $(this).attr("name") + "' value='" + objArr[j].code + "'/><span class='black'>" + objArr[j].value + "</span>&nbsp;"
                }
            }

            if (this.nodeName == "INPUT") {
                var name = $(this).attr("name");
                $(this).replaceWith(tempAppendStr);
                $("input[name='" + name + "']").eq(0).attr("checked", "checked");
            } else {
                $(this).append(tempAppendStr);
            }
        })
    }
}

function getUserInfoInFrame() {
    var userInfo = {
        userName: $("#topFrame", window.parent.document)[0].contentWindow.userName,
        userCode: $("#topFrame", window.parent.document)[0].contentWindow.userCode
    }
    return userInfo;
}
function goBack() {
    window.history.back();
}


//function goMain(){
//	if(noGoMain != "undefined") {
//	} else if (window.parent.length == 0) {
//		window.parent.location.href = getRootPath()+"/doLogin";
//	}
//}
//goMain();

/**
 * 文本框根据输入内容自适应高度
 * @param                {HTMLElement}        输入框元素
 * @param                {Number}                设置光标与输入框保持的距离(默认0)
 * @param                {Number}                设置最大高度(可选)
 */
var autoTextarea = function(elem, extra, maxHeight) {
    extra = extra || 0;
    var isFirefox = !!document.getBoxObjectFor || 'mozInnerScreenX' in window,
            isOpera = !!window.opera && !!window.opera.toString().indexOf('Opera'),
            addEvent = function(type, callback) {
                elem.addEventListener ?
                        elem.addEventListener(type, callback, false) :
                        elem.attachEvent('on' + type, callback);
            },
            getStyle = elem.currentStyle ? function(name) {
                var val = elem.currentStyle[name];

                if (name === 'height' && val.search(/px/i) !== 1) {
                    var rect = elem.getBoundingClientRect();
                    return rect.bottom - rect.top -
                            parseFloat(getStyle('paddingTop')) -
                            parseFloat(getStyle('paddingBottom')) + 'px';
                }
                ;

                return val;
            } : function(name) {
        return getComputedStyle(elem, null)[name];
    },
            minHeight = parseFloat(getStyle('height'));

    elem.style.resize = 'none';

    var change = function() {
        var scrollTop, height,
                padding = 0,
                style = elem.style;

        if (elem._length === elem.value.length)
            return;
        elem._length = elem.value.length;

        if (!isFirefox && !isOpera) {
            padding = parseInt(getStyle('paddingTop')) + parseInt(getStyle('paddingBottom'));
        }
        ;
        scrollTop = document.body.scrollTop || document.documentElement.scrollTop;

        elem.style.height = minHeight + 'px';
        if (elem.scrollHeight > minHeight) {
            if (maxHeight && elem.scrollHeight > maxHeight) {
                height = maxHeight - padding;
                style.overflowY = 'auto';
            } else {
                height = elem.scrollHeight - padding;
                style.overflowY = 'hidden';
            }
            ;
            style.height = height + extra + 'px';
            scrollTop += parseInt(style.height) - elem.currHeight;
            document.body.scrollTop = scrollTop;
            document.documentElement.scrollTop = scrollTop;
            elem.currHeight = parseInt(style.height);
        }
        ;
    };

    addEvent('propertychange', change);
    addEvent('input', change);
    addEvent('focus', change);
    change();
};
function goOtherPage(href) {

    $("a[href='../../view/report/" + href + "']", $(parent.frames['leftFrame'].document)).children("li").trigger("click");
}
function getCurrenUserInfo(callBack) {
    doJsonRequest("/user/getCurrentUserInfo", null, function(data) {
        if (data.result) {
            callBack(data.data);
        } else {
            alert("获取用户信息出错！");
        }
    }, {noFrame: true});
}

function reSizeLeft() {
    var height = document.documentElement.clientHeight;
    $(".left_list").attr("style", "min-height:" + (height - 60) + "px");

}

function reSizeRight() {
    var height = document.documentElement.clientHeight;
    $(".right_con").attr("style", "min-height:" + (height - 53) + "px");
}


$(function() {
    $("body").show();
    if ($(".left_list").length != 0) {
        reSizeLeft();
        $("body").attr("onresize", "reSizeLeft()");
    }
    if ($(".right_con").length != 0) {
        reSizeRight();
        $("body").attr("onresize", "reSizeRight()");
    }
})



if (typeof (Highcharts) != "undefined") {
    Highcharts.setOptions({
        lang: {
            drillUpText: "返回:{series.name}."
        }
    });
}

function getPropertyArrayFromObjList(ObjectArr, key) {
    var objs = [];
    for (var i = 0; i < ObjectArr.length; i++) {
        objs.push(ObjectArr[i][key])
    }

    return objs;
}


function getTheDate(longtime, pattern) {
    var tempPattern = "yyyy-M-d hh:mm"
    if (pattern) {
        tempPattern = pattern;
    }
    return new Date(new Date().getTime() + longtime * 1000).format(tempPattern)
}

function weboffice_hide() {
    if ($("[name='weboffice']:visible").length > 0) {
        $("[name='weboffice']:visible").hide();
    }
}

function weboffice_show() {
    if ($("[name='weboffice']:hidden").length > 0) {
        $("[name='weboffice']:hidden").show();
    }
}

String.prototype.endWith = function(s) {
    if (s == null || s == "" || this.length == 0 || s.length > this.length)
        return false;
    if (this.substring(this.length - s.length) == s)
        return true;
    else
        return false;
    return true;
}

String.prototype.startWith = function(s) {
    if (s == null || s == "" || this.length == 0 || s.length > this.length)
        return false;
    if (this.substr(0, s.length) == s)
        return true;
    else
        return false;
    return true;
}

function intervalFormat(time) {
    var day = Math.floor(time / 86400.0);
    var hour = Math.floor((time % 86400) / 3600.0);
    var min = Math.round(((time % 86400) % 3600) / 60.0);
    var dateStr = "";
    if (day != 0) {
        dateStr = dateStr + day + "天";
    }
    if (hour != 0) {
        dateStr = dateStr + hour + "小时";
    }
    if (min != 0) {
        dateStr = dateStr + min + "分钟";
    }
    if((day==0)&&(hour==0)&&(min==0)) {
    	dateStr="小于1分钟";
    }
    return dateStr
}

function handleLongString(str, length, postfix) {

    var theRealLength = 0;
    var theReturnStr = "";
    length =1000;
    for (i = 0; i < str.length; i++) {
        theRealLength++
        if (theRealLength <= length) {
            theReturnStr = theReturnStr + str[i];
        } else {
            theReturnStr = theReturnStr + postfix;
            break;
        }
        if (str[i].match(/[^\x00-\xff]/ig) != null) {
            theRealLength++;
        }
    }
    return theReturnStr;
}

function getStrLength(str) {

    var theRealLength = 0;
    for (i = 0; i < str.length; i++) {
        theRealLength++
        if (str[i].match(/[^\x00-\xff]/ig) != null) {
            theRealLength++;
        }
    }
    return theRealLength;
}

function addAfterAction() {
	$(".error", $("table[role='presentation']")).each(function(){
		if($(this).text().length > 0) {
			$(this).parent().parent().find(".start").hide();
		}

	})
}

function validLength(str){
	if(str!=''){
		if(str.indexOf('[')){
			str = str.split('[')[0];
		}
	}
	return str.length;
}

function doLowwerToUpper(number) {
		var lower = ["0","1","2","3","4","5","6","7","8","9"];
		var upper = ['零','一','二','三','四','五','六','七','八','九']
		var str = number.toString().split("");
		if(str.length == 1) {
			var index = $.inArray(str[0],lower);
			return upper[index];
		}
		if(str.length == 2) {
			var index0 = $.inArray(str[0],lower);
			var index1 = $.inArray(str[1],lower);
			if(str[0] =="1") {
				if(str[1] =='0') {
					return "十";
				} else {
					return "十"+upper[index1];
				}
			} else {
				if(str[1] =='0') {
					return upper[index0]+"十";
				} else {
					return upper[index0]+"十"+upper[index1];
				}

			}
		}
		if(str.length == 3) {
			var index0 = $.inArray(str[0],lower);
			var index1 = $.inArray(str[1],lower);
			var index2 = $.inArray(str[2],lower);
			if((str[1] =="0")&&(str[2] =="0")) {
				return upper[index0]+"百";
			} else if((str[1] =="0")){
				return upper[index0]+"百零"+upper[index2];
			} else if((str[2]=="0")) {
				return upper[index0]+"百"+upper[index1]+"十";
			}else {
				return upper[index0]+"百"+upper[index1]+"十"+upper[index2];
			}
		}
}

String.prototype.startWith=function(str){
	  var reg=new RegExp("^"+str);
	  return reg.test(this);
	}

String.prototype.endWith=function(str){
  var reg=new RegExp(str+"$");
  return reg.test(this);
}

$(".theNumber").each(function(){
	$(this).attr("onkeyup","value=value.replace(/[^\\d]/g,'')");
	$(this).attr("onbeforepaste","clipboardData.setData('text',clipboardData.getData('text').replace(/[^\\d]/g,''))");

});

//$("body").hide();
// add goBack
$(function(){
    var locationHref =window.location.href;
    if(locationHref.indexOf("draft_")>-1||locationHref.indexOf("_draft")>-1
        ||locationHref.indexOf("_create")>-1 ||locationHref.indexOf("canGoBack")>-1
        ||locationHref.indexOf("meet_content_lib_list")>-1
        ||locationHref.indexOf("org_tree")>-1||locationHref.indexOf("&close=true")>-1){

    }
    else{
        var goBack =$("<span></span>").addClass("goBack").css({
            "position":"absolute",
            "right":"12px",
            "cursor": "pointer",
            "text-decoration":"underline",
            "font-size":"14px",
            "top":"9px"
        }).html("返回");
        var button = $("#buttonList button");
         button.each(function(){
             if($(this).html().indexOf("返回")>-1){
                 $(this).remove();
             }
         });
            $(".panel-title").append(goBack)
            //   $("#buttonList").prepend(goBack);
            $(".goBack").on("click",function(){
                if(history.length>1){
                    history.go(-1);
                }
               else{
            	   window.open('','_self');
                    window.close();
                }
            });


    }

});

Array.prototype.map = function(fn,scope) {
	var result = [],ri = 0;
	for (var i = 0,n = this.length; i < n; i++){
	  if(i in this){
	    result[ri++]  = fn.call(scope ,this[i],i,this);
	  }
	}
	return result;
};

function getBeoreMonth(monthCount) {
    var arr = [];
    if(parseInt(new Date().format("M"))> monthCount) {
        arr.push(parseInt(new Date().format("yyyy")));
        arr.push(parseInt(new Date().format("M")) - monthCount);
    } else {
        arr.push(parseInt(new Date().format("yyyy"))-1);
        arr.push(12+parseInt(new Date().format("M")) -monthCount);
    }
    return arr;
}

function getAfterMonth(monthCount) {
    var arr = [];
    if(parseInt(new Date().format("M")+ monthCount) <=12) {
        arr.push(parseInt(new Date().format("yyyy")));
        arr.push(parseInt(new Date().format("M")) - monthCount);
    } else {
        arr.push(parseInt(new Date().format("yyyy"))+1);
        arr.push(new Date().format("M")+ monthCount -12);
    }
    return arr;
}

/**
 * 锁屏用
 */
function hmBlockUI(){
	var divObj = $('<div id="loadingMaskLayerDiv" style="filter:alpha(opacity=50);display:none;width:100%;height:100%;z-index:1000;background-color:gray;opacity:0.5;position:fixed;left:0px;top:0px;"><div class="loading"><i></i><span id="windowMaskLayerInfo"></span></div></div>');
	$('body').append(divObj);
	$('#windowMaskLayerInfo').html("处理中");
	divObj.show();
}

function hmUnBlockUI(){
	$("#loadingMaskLayerDiv").remove();
}
