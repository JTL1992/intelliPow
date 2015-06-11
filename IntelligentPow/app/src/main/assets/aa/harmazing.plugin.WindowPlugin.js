/**
 * @description: harmazing.plugin.WindowPlugin
 * @author: Li JianHui (sillynemo)
 * @create: 14-9-4 上午11:59
 */

(function () {
    window.harmazingAlert = function (msg, title, callback) {
        if (window.isMobile.any()) {
            navigator.notification.alert(msg, function () {
                if (typeof callback === "function") {
                    callback();
                }
            }, title || "和美温馨提示", "确定");
        } else {
            alert(msg);
            if (typeof callback === "function") {
                callback();
            }
        }
    };

    /**
     * 获取url中的参数数组
     * @returns {Array}
     */
    window.getUrlParams = function () {
        //获取当前URL地址
        var search = window.location.search;
        var tempArray = search.substr(1, search.length).split("&");
        var paramsArray = [];
        if (tempArray != null) {
            for (var i = 0; i < tempArray.length; i++) {
                // 用=进行拆分，但不包括==
                var reg = /[=|^==]/;
                //用&替换reg
                var set1 = tempArray[i].replace(reg, '&');
                //以&分割获取
                var tmpStr2 = set1.split('&');
                var array = [];
                array[tmpStr2[0]] = tmpStr2[1];
                // 将array添加到paramsArray中，并返回长度
                paramsArray.push(array);
            }
        }
        // 返回参数数组
        return paramsArray;
    };

    /**
     * 获取指定参数
     * @param {String} name 参数名
     * @returns {String|null}
     */
    window.getParamValue = function (name) {
        var paramsArray = window.getUrlParams();
        if (paramsArray != null) {
            for (var i = 0; i < paramsArray.length; i++) {
                for (var j in paramsArray[i]) {
                    if (j == name) {
                        return paramsArray[i][name];
                    }
                }
            }
        }
        return null;
    };

    /**
     增加遮盖层
     * @param {String}msg 提示信息，默认为loading
     * @returns {Document} 黑色层
     */
    window.addMask = function (msg,renderTo) {
        var render = renderTo||d3.select("body").node();
        var pmask = d3.select(render).select(".harmazingWinMask");
        var height = !renderTo?document.documentElement.offsetHeight:render.offsetHeight;
        var width = !renderTo?document.documentElement.offsetWidth:render.offsetWidth;
        if (!pmask.empty()) {
            var index = pmask.datum();
            var mask = pmask.datum(index + 1);
        } else {
            var mask = d3.select(render).append("div").classed("harmazingWinMask", true).datum(0);

            mask.style("width", width+"px").style("height", height+"px").style("top", "0px")
                .style("background", "black").style("opacity", "0.7")
                .style("z-index", 9).style("position", "absolute");

            // 加进度条
            var progress = d3.select(render).append("div").classed("harmazingWinMaskProgress", true)
                .style("position", "absolute")
                .style("width", "100%").style("height", "100%").style("top", "0px");
            var text = mask.append("div").html(msg||"loading...")
                .style("position", "absolute")
                .style("font-size", "32px")
                .style("color", "white").style("top", (progress.node().clientHeight *.65)+"px")
                .style("width", "100%")
                .style("text-align", "center");
            var opts = {
                lines: 18, // The number of lines to draw
                length: 20, // The length of each line
                width: 10, // The line thickness
                radius: 40, // The radius of the inner circle
                corners: 1, // Corner roundness (0..1)
                rotate: 0, // The rotation offset
                direction: 1, // 1: clockwise, -1: counterclockwise
                color: '#FFF', // #rgb or #rrggbb or array of colors
                speed: 1, // Rounds per second
                trail: 60, // Afterglow percentage
                shadow: false, // Whether to render a shadow
                hwaccel: false, // Whether to use hardware acceleration
                className: 'spinner', // The CSS class to assign to the spinner
                zIndex: 11 // The z-index (defaults to 2000000000)
            };
            var spinner = new Spinner(opts).spin(progress.node());
        }
        return mask;
    };



    window.removeMask = function () {
        var mask = d3.selectAll(".harmazingWinMask");
        if (!mask.empty()) {
            var index = mask.datum();
            if (index) {
                mask.datum(index - 1);
            } else {
                mask.remove();
                var progress = d3.selectAll(".harmazingWinMaskProgress");
                if (!progress.empty()) {
                    progress.remove();
                }
            }
        }

        return null;
    }
})();