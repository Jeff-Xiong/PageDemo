$(function() {

    var pageSize = 10; // 每页显示多少条
    /**
     * 分页方式
     * local本地分页  服务器一次性查询出全部数据，在页面用脚本分页展示数据
     * server服务器分页  服务器只查询返回当前页的数据
     */
    var pagingMode = 'server';

    function isServerPage() {
        return pagingMode === 'server';
    }

    var tableDefined = [{ // 表头定义
        label: '对公单位名称',
        dataIdx: 'CNAME'
    }, {
        label: '客户号',
        dataIdx: 'OWNCLT'
    }, {
        label: '账号',
        dataIdx: 'RICHNBR'
    }, {
        label: '币种',
        dataIdx: 'CCYNBR'
    }, {
        label: '账户开立时间',
        dataIdx: 'OPENDATE'
    }, {
        label: '账户开立机构代码',
        dataIdx: 'OPENBRN'
    }, {
        label: '账户开立机构',
        dataIdx: 'BRNNAME'
    }, {
        label: '是否睡眠户',
        dataIdx: 'SLEEPTAG'
    }, {
        label: '状态',
        dataIdx: 'RICHSTATUS'
    }];

    //自执行函数创建表头
    (function() {
        var htmls = [];
        htmls.push('<tr>');
        htmls.push('<th>序号</th>');
        $.each(tableDefined, function(i, item) {
            htmls.push('<th>' + item.label + '</th>');
        });
        htmls.push('</tr>');
        $('#dataHead').html(htmls.join(''));
    })();

    function nextPage() {
        var pageNum = $('#dataBody').data('pagenum');
        showTableData(pageNum + 1);
    }

    function prevPage() {
        var pageNum = $('#dataBody').data('pagenum');
        showTableData(pageNum - 1);
    }

    function firstPage() {
        showTableData(1);
    }

    function lastPage() {
        showTableData('last');
    }

    function toPage(num) {
        showTableData(num);
    }

    function pageOperation(type, num) {
        var PageOperation = {
            nextPage: nextPage,
            prevPage: prevPage,
            firstPage: firstPage,
            lastPage: lastPage,
            toPage: toPage
        };
        PageOperation[type + 'Page'](num);
    }

    /**
     * 刷新分页工具条
     */
    function flushPageBar(info) {
        if (info) {
            if (info.emptyData) {
                $('.pageBtn').prop('disabled', true);
                $('#pageNum').val('');
                $('#pageNumMax').text('');
            } else {
                $('.pageBtn').each(function() {
                    var btn = $(this);
                    var inType = btn.data('type');
                    if (inType === 'first' || inType === 'prev') {
                        btn.prop('disabled', info.isFisrt);
                    } else if (inType === 'next' || inType === 'last') {
                        btn.prop('disabled', info.isLast);
                    }
                });
                $('#pageNum').val(info.pageNum);
                $('#pageNumMax').text(info.pageNumMax);
            }
        }
    }

    /**
     * 显示表格数据，会根据分页方式调用不同的方法获取数据后显示到页面上
     * pageNum 页码，第几页
     */
    function showTableData(pageNum) {
        var showData = function(config) {
            var records = config.records;
            var htmls = [];
            var startIdx = config.startIdx;
            if (config.emptyData) {
                $('#dataBody').html('<tr><td colspan="10" align="center">无数据</td></tr>');
            } else {
                $.each(records, function(r, record) {
                    htmls.push('<tr>');
                    htmls.push('<td>' + (++startIdx) + '</td>');
                    $.each(tableDefined, function(c, item) {
                        htmls.push('<td>' + (record[item.dataIdx] || '&nbsp;') + '</td>'); // 用“||”运算符判断空，这里没有数值类型的值，可以不考虑0
                    });
                    htmls.push('</tr>');
                });
                $('#dataBody').html(htmls.join(''));
                $('#dataBody').data('pagenum', config.pageNum);
            }
            flushPageBar(config);
        };

        if (isServerPage()) {
            serverCurrentPageData(pageNum, showData);
        } else {
            loaclCurrentPageData(pageNum, showData);
        }
    }

    /**
     * 显示遮罩
     */
    function showMask() {
        // 半秒后再显示遮罩
        return setInterval(function() {
            $('.mask').show();
        }, 500);
    }
    /**
     * 清除遮罩
     */
    function removeMask(inter) {
        clearInterval(inter);
        $('.mask').hide();
    }

    // 往后台请求数据
    function requestData(param, callback, pageNum) {
        var url = 'query';
        param = param || {};

        // 服务端分页，添加分页参数
        if (isServerPage()) {
            $.extend(param, {
                pageNum: pageNum,
                pageSize: pageSize
            });
        }

        var mask = showMask();
        $.post(url, param, function(res) {
            removeMask(mask);
            if (res.succ) {
                callback(res);
            } else {
                alert(res.msg);
            }
        }, 'json');
    }

    /**
     * 本地分页，获取当页数据
     * @param {Number} pageNum 页码
     * @param {Function} callback 获取完数据后的回调
     * @returns 
     */
    function loaclCurrentPageData(pageNum, callback) {
        var list = $('.dataTable').data('list');
        var total, // 数据条数
            pageNumMax, // 最大页数
            startIdx, // 开始索引数
            endIdx; // 结束索引数	
        var htmls = [];
        var record;
        var records = [];
        if (list) {
            total = list.length;
            if (total) {
                pageNumMax = Math.floor(total / pageSize) + (total % pageSize == 0 ? 0 : 1);
                if (pageNum === 'last') {
                    pageNum = pageNumMax;
                }
                if (pageNum < 1) {
                    pageNum = 1;
                }
                if (pageNum > pageNumMax) {
                    pageNum = pageNumMax;
                }
                startIdx = (pageNum - 1) * pageSize;
                endIdx = startIdx + pageSize;
                if (endIdx > total) {
                    endIdx = total;
                }

                for (; startIdx < endIdx; startIdx++) {
                    records.push(list[startIdx]);
                }

                callback({
                    pageNum: pageNum,
                    isFisrt: (pageNum === 1),
                    isLast: (pageNum === pageNumMax),
                    pageNumMax: pageNumMax,
                    startIdx: (pageNum - 1) * pageSize,
                    records: records
                });
            } else {
                callback({
                    emptyData: true
                });
            }
        }
    }

    /**
     * 服务器分页，获取当页数据
     * @param {Number} pageNum 页码
     * @param {Function} callback 获取完数据后的回调
     * @returns 
     */
    function serverCurrentPageData(pageNum, callback) {
        var param = $('.dataTable').data('param');
        var pageNumMax = $('#dataBody').data('pagenummax') || 1;
        if (pageNum === 'last') {
            pageNum = pageNumMax;
        }
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageNum > pageNumMax) {
            pageNum = pageNumMax;
        }
        var myCallback = function(res) {
            var pageInfo = res.page;
            var total = pageInfo.totalNum;
            var pageSize = pageInfo.pageSize;
            var pageNum = pageInfo.pageNum;
            var pageNumMax = Math.floor(total / pageSize) + (total % pageSize == 0 ? 0 : 1);
            $('#dataBody').data('pagenummax', pageNumMax);
            var startIdx = (pageNum - 1) * pageSize;;
            if (total) {
                callback({
                    pageNum: pageNum,
                    isFisrt: (pageNum === 1),
                    isLast: (pageNum === pageNumMax),
                    pageNumMax: pageNumMax,
                    startIdx: startIdx,
                    records: res.list
                });
            } else {
                callback({
                    emptyData: true
                });
            }
        }
        requestData(param, myCallback, pageNum);
    }


    // 查询按钮
    $('#queryBtn').on('click', function() {
        var openDateStart = $('#openDateStart').val();
        var openDateEnd = $('#openDateEnd').val();
        var openbrnCode = $.trim($('#openbrnCode').val());
        var openbrnName = $.trim($('#openbrnName').val());

        if (openDateStart && openDateEnd) {
            if (openDateStart > openDateEnd) {
                alert('账户开立时间起始时间大于结束时间！');
                return;
            }
        }

        var param = {
            openDateStart: openDateStart,
            openDateEnd: openDateEnd,
            openbrnCode: openbrnCode,
            openbrnName: openbrnName
        };

        if (isServerPage()) {
            $('.dataTable').data('param', param);
            showTableData(1);
        } else {
            requestData(param, function(res) {
                $('.dataTable').data('list', res.list);
                showTableData(1);
            });
        }
    });

    // 翻页按钮
    $('.pageBtn').on('click', function() {
        var type = $(this).data('type');
        pageOperation(type);
    });

    // 直接输入页码框
    $('#pageNum').on('change', function() {
        var value = $(this).val();
        value = parseInt(value);
        if (!isNaN(value)) {
            pageOperation('to', value);
        }
    });

});