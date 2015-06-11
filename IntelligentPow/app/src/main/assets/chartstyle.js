Highcharts.theme = {
    chart: {
        alignTicks: false
    },
    credits: {
        enabled: false,
        text: "万户项目"
    },
    lang: {
        rangeSelectorZoom: "",
        rangeSelectorFrom: "从",
        rangeSelectorTo: "到",
        weekdays: ["周日","周一", "周二", "周三", "周四", "周五", "周六"],
        shortMonths: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
        months: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"]
    },
    rangeSelector: {
        inputDateFormat: '%Y年%b%e日',
        buttonTheme: {
            width: 58},
        buttons: [{
                type: 'week',
                count: 1,
                text: '周'
            }, {
                type: 'month',
                count: 1,
                text: '月'
            },  {
                type: 'year',
                count: 1,
                text: '年'
            }]
    },
    tooltip: {
        dateTimeLabelFormats: {
            millisecond: '%H:%M:%S',
            second: '%H:%M:%S',
            minute: '%H:%M',
            hour: '%H:%M',
            day: '%b%e日',
            week: '%b%e',
            month: '%y年%b',
            year: '%Y年'
        }
    },
    xAxis: {
        dateTimeLabelFormats: {
            millisecond: '%H:%M:%S',
            second: '%H:%M:%S',
            minute: '%H:%M',
            hour: '%H:%M',
            day: '%b%e日',
            week: '%b%e',
            month: '%y年%b',
            year: '%Y年'
        }
    },
    yAxis: {

       tickPixelInterval: 40
    },
    plotOptions:{
        line:{
            pointPlacement:"on"
        }
    }
};

// Apply the theme
var highchartsOptions = Highcharts.setOptions(Highcharts.theme);