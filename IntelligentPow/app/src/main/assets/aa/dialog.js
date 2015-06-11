//组织机构搜索
function C_doOrgSelect(cfg) {
	var obj = {
			title : '组织机构选择',
			height : "320px",
			width : "450px",
			url : '/spms/view/dialog/orgDialog.html',
			fun : true,
			myCallBack : function(data) {
					
			}
		}
	if(typeof cfg == "function") { 
		obj.myCallBack = cfg;
	} else {
		$.extend(obj,cfg)
	}
	nowSearchRole = new jqueryDialog(obj);
	$(".dialog_div_").parent().addClass("wf_top");
}

//区域搜索
//cfg:{
//   params:{
//				pid:xxx
//			}
//}
function C_doAreaSelect(cfg) {
	var obj = {
			title : '区域机构',
			height : "320px",
			width : "450px",
			url : '/spms/view/dialog/areaDialog.html',
			fun : true,
			myCallBack : function(data) {
					
			}
		}
	if(typeof cfg == "function") {
		obj.myCallBack = cfg;
	} else {
		$.extend(obj,cfg)
	}
	nowSearchRole = new jqueryDialog(obj);
	$(".dialog_div_").parent().addClass("wf_top");
}

//人员搜索
//cfg:{
//   params:{
//				pid:xxx
//			}
//}
function C_doUserSelect(cfg) {
	var obj = {
		title : '选人',
		height : "320px",
		width : "450px",
		url : '/spms/view/dialog/userDialog.html',
		fun : true,
		myCallBack : function(data) {

		}
	}
	if(typeof cfg == "function") {
		obj.myCallBack = cfg;
	} else {
		$.extend(obj,cfg)
	}
	nowSearchRole = new jqueryDialog(obj);
	$(".dialog_div_").parent().addClass("wf_top");
}