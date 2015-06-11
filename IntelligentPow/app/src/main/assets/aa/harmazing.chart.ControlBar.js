/**
 * @description: 数据控制条 v0.1 此版本专门定制后期修改为通用的
 *  //TODO 需要后期改为通用的
 *  harmazing.chart.ControlBar
 * @author: Li JianHui (sillynemo)
 * @create: 14-9-19 下午1:51
 */

(function () {
    /**
     * 控件类
     * @param {Object}config 控件配置项
     * @constructor
     */
    var ControlBar = function (config) {
        this.getConfig(config);
//        this.drawAxis();
        this.init();
        this.addListener();
    };

    /**
     * 获取控件配置项
     */
    ControlBar.prototype.getConfig = function (config) {
        var me = this;
        me._renderTo = config.renderTo;
        me._wdDiv = config.wdDiv;
        me._gwId = config.gwId;
        me._deviceid = config.deviceid;
        me._callback = config.callbackFunction;
        me._endCallback = config.endCallbackFunction;
        me._defaultValue = config._defaultValue || 0;
        var h = me._height || 222;
        var w = me._width || 36
        me._height = h;
        me._width = w;
        me._min = config.min || 16;
        me._max = config.max || 32;
    };

    /**
     * 初始化控件
     */
    ControlBar.prototype.init = function () {
        var me = this, renderTo = me._renderTo, defaultValue = me._defaultValue;
        var height = me._height, width = me._width;
        var bgDiv = $("<div></div>"); // 背景DIV
        var con = $("<div></div>")
            .css("position", "relative")
            .css("width", width + "px").css("margin-top", "-20px")
            .css("height", height + "px");
        $(renderTo).empty().append(con);
        con.append(bgDiv);
        bgDiv.css("position", "absolute")
            .css("top", width / 2 + "px")
            .css("height", (height - width) + "px")
            .css("width", width / 2 + "px")
            .css("left", 0 + "px")
            .css("background-image", "url(../../resources/spms/pc2.png)")
            .css("background-size", "cover");
        me._bgDiv = bgDiv;

        var buttonDiv = $("<div></div>"); // 调节按钮DIV
        buttonDiv.addClass("harmazing_chart_control_bar_button");
        con.append(buttonDiv);
        buttonDiv.css("position", "absolute")
            .css("top", defaultValue * (height - width) + "px")
            .css("height", width + "px")
            .css("width", width + "px")
            .css("left", -7 + "px")
            .css("background-image", "url(../../resources/spms/pc1.png)")
            .css("background-size", "cover");
        me._buttonDiv = buttonDiv;
        //  me.changeDefaultValue(defaultValue);
    };
    ControlBar.prototype.changeDefaultValue = function (value) {

        var me = this, defaultValue = value;
        var height = me._height, width = me._width;
        me._defaultValue = value;
        me._buttonDiv.css("position", "absolute")
            .css("top", defaultValue * (height - width) + "px")

    };
    /**
     * 添加监听器
     */
    ControlBar.prototype.addListener = function () {
        var me = this, buttonDiv = me._buttonDiv, startY, height = me._height, width = me._width;
        var mc = new Hammer(buttonDiv[0]);
        mc.get('pan').set({ direction: Hammer.DIRECTION_ALL });

        mc.on("panstart", onPanStart);
        mc.on("panend", onPanEnd);
        mc.on("panmove", onPan);


        function onPanStart() {
            clearTimeout(continueRefresh);
            startY = +buttonDiv.css("top").replace("px", "")
            buttonDiv.addClass("drag");

        }

        function onPan(ev) {
            var y = startY + ev.deltaY;
            y = Math.max(0, y);
            y = Math.min(y, height - width);
            buttonDiv.css("top", y + "px");
            me._startValue = ""
            me.callback(y);
            y = (me._buttonDiv.css("top").replace("px", ""));
            y = parseInt(y)
            var act = me._max - (me._max - me._min) * y / (me._height - me._width);
            act = Math.floor(act);
            var wddiv = me._wdDiv;
            var ten = Math.floor(act / 10);
            var one = act % 10;
            var span0 = d3.select(wddiv).selectAll("span")[0][0];
            var span1 = d3.select(wddiv).selectAll("span")[0][1];
            $(span0).removeClass().addClass("wd_" + ten);
            $(span1).removeClass().addClass("wd_" + one);

        }

        function onPanEnd(ev) {
            var y = startY + ev.deltaY;
            y = Math.max(0, y);
            y = Math.min(y, height - width);
            buttonDiv.removeClass("drag");
            me.endCallback(y);
            y = (me._buttonDiv.css("top").replace("px", ""));
            y = parseInt(y)

            // 当前温度
            var act = me._max - (me._max - me._min) * y / (me._height - me._width);
            act = Math.floor(act);

            me._startValue = me._max - (me._max - me._min) * me._defaultValue;
            me._startValue = Math.round(me._startValue)
        //    console.log(me._startValue);
        //    console.log(act);
            //温度换算的高度

            var remain = ( me._startValue - me._max) / (-(me._max - me._min));
            var wddiv = me._wdDiv;
            var ten = Math.floor(act / 10);
            var one = act % 10;
            var span0 = d3.select(wddiv).selectAll("span")[0][0];
            var span1 = d3.select(wddiv).selectAll("span")[0][1]

            var self = {
                gwid: me._gwId,
                oldtemp: me._startValue,
                newtemp: act,
                deviceid: me._deviceid
            }
       //     console.log(self);
            var URL = "/spmsuc/setAcTemp";
            clearTimeout(continueRefresh);
            doJsonRequest(URL, self, function (dat) {
                if (dat.result) {
                    me._defaultValue = (act - me._max) / (-(me._max - me._min));
                    $(span0).removeClass().addClass("wd_" + ten);
                    $(span1).removeClass().addClass("wd_" + one);
                } else {
                    //  me.changeDefaultValue(remain);
                    var tenError = Math.floor(me._startValue / 10);
                    var oneError = me._startValue % 10;
                    $(span0).removeClass().addClass("wd_" + tenError);
                    $(span1).removeClass().addClass("wd_" + oneError);
                }
                continueRefresh = setTimeout(function () {
                    doRefresh();
                }, 5000);
            }, {
                showWaiting: true,
                handdingInfo: "温度更改中...",
                successInfo: "更改成功...",
                failtureInfo: "更改失败.."
            });
        }
    };

    /**
     * 绘制坐标轴
     */
    ControlBar.prototype.drawAxis = function () {
        var me = this, renderTo = me._renderTo, width = me._width, height = me._height;
        var x = d3.scale.linear()
            .range([0, height - width])
            .domain([16, 32]);
        var axis = d3.svg.axis()
            .scale(x)
            .orient("left")
            .tickValues([16, 20, 24, 28, 32]);
        d3.select(renderTo)
            .append("div")
            .style("width", "100%")
            .style("height", "100%")
//            .style("z-index",9)
            .style("position", "absolute")
            .append("svg")
            .attr("class", "axis")
            .attr("width", width)
            .attr("height", height)
//            .style("margin-top","18px")
//            .attr("transform", "translate(0,18)")
            .append("g")
            .attr("transform", "translate(30," + width / 2 + ")")
            .call(axis);
        d3.select("path").style("fill", "none");//.style("stroke","black");
        d3.selectAll("line").style("fill", "none");//.style("stroke","black");
    };

    /**
     * 回调函数
     */
    ControlBar.prototype.callback = function (yValue) {
        var me = this, callback = me._callback, height = me._height, width = me._width;
        if (typeof callback === "function") {
            callback((yValue) / (height - width));
        }
    };
    ControlBar.prototype.endCallback = function (yValue) {
        var me = this, callback = me._endCallback, height = me._height, width = me._width;
        if (typeof callback === "function") {
            callback((yValue) / (height - width));
        }
    };

    // 初始化命名空间
    if (!window.Harmazing) {
        window.Harmazing = {};
    }
    if (!window.Harmazing.chart) {
        window.Harmazing.chart = {};
    }
    // 把该工具放到命名空间下
    window.Harmazing.chart.ControlBar = ControlBar;


    var ControlBar1 = function (config) {
        this.getConfig(config);
//        this.drawAxis();
        this.init();
        this.addListener();
    };

    /**
     * 获取控件配置项
     */
    ControlBar1.prototype.getConfig = function (config) {
        var me = this;
        me._fsDiv = config.fsDiv;
        me._gwId = config.gwId;
        me._deviceid = config.deviceid;
        me._renderTo = config.renderTo;
        me._callback = config.callbackFunction;
        me._endCallback = config.endCallbackFunction;
        me._defaultValue = config._defaultValue || 0;
        var h = me._height || 222;
        var w = me._width || 36
        me._height = h;
        me._width = w;
        me._min = config.min || 16;
        me._max = config.max || 32;
    };

    /**
     * 初始化控件
     */
    ControlBar1.prototype.init = function () {
        var me = this, renderTo = me._renderTo, defaultValue = me._defaultValue;
        var height = me._height, width = me._width;
        var bgDiv = $("<div></div>"); // 背景DIV
        var con = $("<div></div>")
            .css("position", "relative")
            .css("width", width + "px").css("margin-top", "-20px")
            .css("height", height + "px");
        $(renderTo).empty().append(con);
        con.append(bgDiv);
        bgDiv.css("position", "absolute")
            .css("top", width / 2 + "px")
            .css("height", (height - width) + "px")
            .css("width", width / 2 + "px")
            .css("left", 0 + "px")
            .css("background-image", "url(../../resources/spms/pc2.png)")
            .css("background-size", "cover");
        me._bgDiv = bgDiv;

        var buttonDiv = $("<div></div>"); // 调节按钮DIV
        buttonDiv.addClass("harmazing_chart_control_bar_button");
        con.append(buttonDiv);
        buttonDiv.css("position", "absolute")
            .css("top", defaultValue * (height - width) + "px")
            .css("height", width + "px")
            .css("width", width + "px")
            .css("left", -7 + "px")
            .css("background-image", "url(../../resources/spms/pc1.png)")
            .css("background-size", "cover");
        me._buttonDiv = buttonDiv;
        //  me.changeDefaultValue(defaultValue);
    };
    ControlBar1.prototype.changeDefaultValue = function (value) {

        var me = this, defaultValue = value;
        var height = me._height, width = me._width;

        me._buttonDiv.css("position", "absolute")
            .css("top", defaultValue * (height - width) + "px")

    };
    /**
     * 添加监听器
     */
    ControlBar1.prototype.addListener = function () {
        var me = this, buttonDiv = me._buttonDiv, startY, height = me._height, width = me._width;
        var mc = new Hammer(buttonDiv[0]);
        mc.get('pan').set({ direction: Hammer.DIRECTION_ALL });

        mc.on("panstart", onPanStart);
        mc.on("panend", onPanEnd);
        mc.on("panmove", onPan);

        function onPanStart() {
            clearTimeout(continueRefresh);
            startY = +buttonDiv.css("top").replace("px", "")
            buttonDiv.addClass("drag");
        }

        function onPan(ev) {
            var y = startY + ev.deltaY;
            y = Math.max(0, y);
            y = Math.min(y, height - width);
            buttonDiv.css("top", y + "px");
            me.callback(y);
            y = (me._buttonDiv.css("top").replace("px", ""));
            y = parseInt(y);
            var act = me._max - (me._max - me._min) * y / (me._height - me._width);
            act = Math.floor(act);
            act = Math.min(act, 7);
            var fsDiv = me._fsDiv;
            $(fsDiv).removeClass().addClass("content_kt_nr_fs_right_pic content_kt_nr_fs_right_picNaN content_kt_nr_fs_right_pic" + act)
        }

        function onPanEnd(ev) {
            var y = startY + ev.deltaY;
            y = Math.max(0, y);
            y = Math.min(y, height - width);
            buttonDiv.removeClass("drag");
            me.endCallback(y);
            y = (me._buttonDiv.css("top").replace("px", ""));
            y = parseInt(y)
            console.log(y);
            // 当前温度
            var act = me._max - (me._max - me._min) * y / (me._height - me._width);
            act = Math.floor(act);
            act = Math.min(act, 7);
            me._startValue = me._max - (me._max - me._min) * me._defaultValue;
            me._startValue = Math.floor(me._startValue)
            var remain = ( me._startValue - me._max) / (-(me._max - me._min));
            console.log(me._startValue);
            console.log(act);
            //温度换算的高度


            //温度换算的高度
            var value = (act - me._max) / (-(me._max - me._min));
            var fsDiv = me._fsDiv;
            var self = {
                gwid: me._gwId,
                oldfs: me._startValue,
                newfs: act,
                deviceid: me._deviceid
            }
         //   console.log(self);
            var URL = "/spmsuc/setAcFs";
            clearTimeout(continueRefresh);
            doJsonRequest(URL, self, function (dat) {
                if (dat.result) {
                    me._defaultValue = (act - me._max) / (-(me._max - me._min));
                    $(fsDiv).removeClass().addClass("content_kt_nr_fs_right_pic content_kt_nr_fs_right_picNaN content_kt_nr_fs_right_pic" + act)
                } else {
                    $(fsDiv).removeClass().addClass("content_kt_nr_fs_right_pic content_kt_nr_fs_right_picNaN content_kt_nr_fs_right_pic" + me._startValue)
                }
                continueRefresh = setTimeout(function () {
                    doRefresh();
                }, 5000);
            }, {
                showWaiting: true,
                handdingInfo: "风速更改中...",
                successInfo: "更改成功...",
                failtureInfo: "更改失败.."
            });

        }
    };

    /**
     * 绘制坐标轴
     */
    ControlBar1.prototype.drawAxis = function () {
        var me = this, renderTo = me._renderTo, width = me._width, height = me._height;
        var x = d3.scale.linear()
            .range([0, height - width])
            .domain([16, 32]);
        var axis = d3.svg.axis()
            .scale(x)
            .orient("left")
            .tickValues([16, 20, 24, 28, 32]);
        d3.select(renderTo)
            .append("div")
            .style("width", "100%")
            .style("height", "100%")
//            .style("z-index",9)
            .style("position", "absolute")
            .append("svg")
            .attr("class", "axis")
            .attr("width", width)
            .attr("height", height)
//            .style("margin-top","18px")
//            .attr("transform", "translate(0,18)")
            .append("g")
            .attr("transform", "translate(30," + width / 2 + ")")
            .call(axis);
        d3.select("path").style("fill", "none");//.style("stroke","black");
        d3.selectAll("line").style("fill", "none");//.style("stroke","black");
    };

    /**
     * 回调函数
     */
    ControlBar1.prototype.callback = function (yValue) {
        var me = this, callback = me._callback, height = me._height, width = me._width;
        if (typeof callback === "function") {
            callback((yValue) / (height - width));
        }
    };
    ControlBar1.prototype.endCallback = function (yValue) {
        var me = this, callback = me._endCallback, height = me._height, width = me._width;
        if (typeof callback === "function") {
            callback((yValue) / (height - width));
        }
    };

    // 初始化命名空间
    if (!window.Harmazing) {
        window.Harmazing = {};
    }
    if (!window.Harmazing.chart) {
        window.Harmazing.chart = {};
    }
    // 把该工具放到命名空间下
    window.Harmazing.chart.ControlBar1 = ControlBar1;
})();