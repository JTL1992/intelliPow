var commonDialog = function(divId, options) {
	this.divId=divId;
	/** boolean or the string 'static'*/
	this.backdrop = true;
	this.keyboard = true;
	this.show = true;
	this.remote=false;
	this.url = "";
	this.modelTitle = "弹出组件";
	this.contentFun="";
	this.beforeOpenFun = "";
	this.saveFun="";
	this.cancelFun=""
	$.extend(this,options);
	var _this = this;
	this.dialogDiv = $("<div class='modal fade' id='"+this.divId+"_commonDialog' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>"+
						  "<div class='modal-dialog'>"+
						    "<div class='modal-content'>"+
						      "<div class='modal-header'>"+
						        "<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"+
						        "<h4 class='modal-title' id='myModalLabel'>"+this.modelTitle+"</h4>"+
						      "</div>"+
						      "<div class='modal-body'>"+
						      "</div>"+
						      "<div class='modal-footer'>"+
						        "<button type='button'  class='btn btn_select ' data-dismiss='modal'>取消</button>"+
						        "<button type='button' id='commonDialog_save_btn' class='btn btn-primary'>保存</button>"+
						      "</div>"+
						    "</div>"+
						  "</div>"+
						"</div>");
	
	this.initDialog  = function(){
		$("#"+_this.divId).append(_this.dialogDiv);
		if(_this.contentFun.length > 0) {
			eval(_this.contentFun+"(_this.dialogDiv.find('div[class=\"modal-body\"]'))");
		}
		if(_this.beforeOpenFun.length > 0) {
			$('#'+_this.divId+"_commonDialog").on('show.bs.modal', function () {
				eval(_this.beforeOpenFun+"(_this.dialogDiv.find('div[class=\"modal-body\"]'))");
			})
		}
		if(_this.saveFun.length > 0) {
			_this.dialogDiv.find("button[id='commonDialog_save_btn']").on("click",function(){
				eval(_this.saveFun+"(_this.dialogDiv)")
			});
		}
	}
	
	this.show = function() {
		$("#"+_this.divId+"_commonDialog").modal({
			backdrop:_this.backdrop,
			keyboard:_this.keyboard,
			show:_this.show,
			remote:_this.remote
		});
	}
	
	this.hide = function() {
		$("#"+_this.divId+"_commonDialog").modal("hide");
	}
	
	this.toggle = function() {
		$("#"+_this.divId+"_commonDialog").modal('toggle');
	}
	
	this.appendContent = function(content) {
		_this.dialogDiv.find("div[class='modal-body']").append(content);
	}
	
	this.initDialog();
	
}

var clickmedDialogs = {};


function clickmedCommonDialogToScan() {
	$("div[class*='clickmedCommonDialog']").each(function(){
		var configStr = $(this).attr("conf")
		eval("var configStr =" + configStr);
		var nowCommonDialog  = new commonDialog($(this).attr('id'), eval(configStr));
		clickmedDialogs[$(this).attr('id')] = nowCommonDialog;	
	})
}
$(function(){
	clickmedCommonDialogToScan();
})



var jqueryDialog = function(conf){
	this.title="弹出框";
	this.width=650;
	this.height="auto";
	this.myCallBack="";
	this.myCancelCallBack="";
	this.url="";
	this.srcData = "";
	this.fun = false;
	this.confirmBtn = true;
	this.confirmText = "确定";
	this.cancelBtn = true;
	this.cancelText = "取消";
	this.appendTo="html";
	this.params=null;
	$.extend(this,conf);
	_this = this;
	if(this.params != null) {
		var paramsStr = "";
		for(var key in this.params) {
			paramsStr = paramsStr + "&"+key+"="+this.params[key];
		}
		if(this.url.indexOf("?") == -1) {
			this.url = this.url+"?"
		}
		this.url = this.url + paramsStr;
	}
	this.div = $("<div id='common_jqueryDialog' class='dialog_div_'><iframe id='jqueryDialog_f_t' style='width:100%;height:"+this.height+";' thesrc='"+this.url+"'></iframe></div>");
	
	this.createBtn = function(){
		if($("#common_jqueryDialog").length>0) {
			$("#common_jqueryDialog").remove()
		}
		weboffice_hide();
		var obj = {};
		if(this.confirmBtn) {
			obj[this.confirmText] =  function() {
		    	  var flag = true;
			    	if(_this.myCallBack) {
			    		var data = _this.div.find("#jqueryDialog_f_t")[0].contentWindow.doCallBack();
			    		if(data != false) {
			    			if(_this.fun) {
			    				_this.myCallBack(data,_this.srcData,function(){
			    					weboffice_show();
			    				});
			    			} else {
			    				eval(_this.myCallBack+"("+$.toJSON(data)+")");
			    			}
			    		} else {
			    			flag =false;
			    		}
			    	}
			    	if(flag) {
			    		$("iframe",_this.div).remove();
			    		_this.div.remove();
			    	}
			    	
			    } 
		}
		
		if(this.cancelBtn) {
			obj[this.cancelText] = function() {
		    	  var flag = true;
			    	if(_this.myCancelCallBack) {
			    		var data = _this.div.find("#jqueryDialog_f_t")[0].contentWindow.doCancelCallBack();
			    		if(data != false) {
			    			
			    			if(_this.fun) {
			    				_this.myCancelCallBack(data,_this.srcData);
			    			} else {
			    				eval(_this.myCancelCallBack+"("+$.toJSON(data)+")");
			    			}
			    			
			    		} else {
			    			flag =false;
			    		}
			    	}
			    	if(flag) {
			    		$("iframe",_this.div).attr("src","");
			    		_this.div.remove();
			    	}
			    	weboffice_show();
			    	
		      }
		}
		return obj;
	}
	this.div.dialog({
		modal:true,
		title:this.title,
		width:_this.width,
	buttonsClass: ["btn btn_select","btn btn_select"],
	    buttons:this.createBtn()
	});
	$("body").scrollTop();
	 $(".ui-dialog-titlebar-close").remove();
	setTimeout(function(){
		_this.div.children().eq(0).attr("src",_this.url);
	},500)
}
