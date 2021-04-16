/**
 * 小组
 * @Author: 郭红明
 */
layui.define(['carousel', 'jquery', 'element', 'flow', 'laytpl', 'element', 'laypage', 'form', 'util'], function (exports) {
    var carousel = layui.carousel
        , $ = layui.$
        , flow = layui.flow
        , laytpl = layui.laytpl
        , element = layui.element
        , laypage = layui.laypage
        , form = layui.form
        , util = layui.util
        , admin = layui.admin;
    // 设置首页页码
    var newpage = 1;//最新页码
    var newSpage = 1;//最新已解决页码
    var limit = 1;
    var group={
        //加载小组数据
        groupdata:function () {
            admin.req({
                type:'post',
                url:'/comm/getgroup',
                success:function (res) {
                    var layhot = document.getElementById('group-mod').innerHTML;
                    laytpl(layhot).render(res.data,function (html) {
                        $("body #grouptype").append(html);
                    });
                }
            });
        },
        // 加载最新话题
        getnewTopic:function () {
            admin.req({
                type:'post',
                url:'/comm/getnewtopic',
                data:{'limit':limit,'page':newpage},
                success:function (res) {
                    var layhot = document.getElementById('topic-mod').innerHTML;
                    laytpl(layhot).render(res.data,function (html) {
                        $("body #topic").html(html);
                    });
                    // 执行分页
                    laypage.render({
                        elem: 'topic1'
                        , count: res.count
                        , first: '首页'
                        , last: '尾页'
                        , prev: '<em>←</em>'
                        , next: '<em>→</em>'
                        , limit: limit
                        , jump: function (obj, first) {
                            newpage = obj.curr;
                            limit = obj.limit;
                            if (!first) {
                                admin.req({
                                    type:'post',
                                    url:'/comm/getnewtopic',
                                    data:{'limit':limit,'page':newpage},
                                    success: function (res) {
                                        var layhot = document.getElementById('topic-mod').innerHTML;
                                        laytpl(layhot).render(res.data,function (html) {
                                            $("body #topic").html(html);
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    exports('group', group);
});