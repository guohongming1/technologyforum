/**
 * 问答库
 * @Author: 小七
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
    // 模拟首页数据
    var question = {
        //加载热门数据
        hotquestion:function () {
            admin.req({
                type:'post',
                url:'/comm/questionHot',
                success:function (res) {
                       var layhot = document.getElementById('question-mod').innerHTML;
                       laytpl(layhot).render(res.data,function (html) {
                          $("#mod").html(html); 
                       });
                }
            });
        },
        // 加载答题排行榜
        userComQuestionRank:function () {
            admin.req({
                type:'post',
                url:'/comm/quesrank-list',
                success:function (res) {
                    if(res.data == undefined || res.data == null || ""==res.data){
                        $("#questionrank").html('<div class="fly-none" style="min-height: 50px; padding:30px 0; height:auto;"><i style="font-size:14px;">暂未有排名</i></div>');
                    }else {
                        var layhot = document.getElementById('comrank-mod').innerHTML;
                        laytpl(layhot).render(res.data, function (html) {
                            $("#questionrank").html(html);
                        });
                    }
                }
            });
        },
        //加载最新数据
        getnewQuestion:function () {
            admin.req({
                type:'post',
                url:'/comm/getnewQuestion',
                data:{'limit':limit,'page':newpage},
                success:function (res) {
                        var layhot = document.getElementById('question-mod').innerHTML;
                        laytpl(layhot).render(res.data,function (html) {
                            $("body #mod").html(html);
                        });
                        // 执行分页
                        laypage.render({
                            elem: 'newpage'
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
                                        url:'/comm/getnewQuestion',
                                        data:{'limit':limit,'page':newpage},
                                        success: function (res) {
                                            var layhot = document.getElementById('question-mod').innerHTML;
                                            laytpl(layhot).render(res.data,function (html) {
                                                $("body #mod").html(html);
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
            });
        },
        //加载最新已解决提问
        getnewSQuestion:function () {
            admin.req({
                type:'post',
                url:'/comm/getnewSQuestion',
                data:{'limit':1,'page':newSpage},
                success:function (res) {
                        var layhot = document.getElementById('question-mod').innerHTML;
                        laytpl(layhot).render(res.data,function (html) {
                            $("body #mod").html(html);
                        });
                        // 执行分页
                        laypage.render({
                            elem: 'newSpage'
                            , count: res.count
                            , first: '首页'
                            , last: '尾页'
                            , prev: '<em>←</em>'
                            , next: '<em>→</em>'
                            , limit: limit
                            , jump: function (obj, first) {
                                newSpage = obj.curr;
                                limit = obj.limit;
                                if (!first) {
                                    admin.req({
                                        type:'post',
                                        url:'/comm/getnewSQuestion',
                                        data:{'limit':limit,'page':newSpage},
                                        success: function (res) {
                                            var layhot = document.getElementById('question-mod').innerHTML;
                                            laytpl(layhot).render(res.data,function (html) {
                                                $("body #mod").html(html);
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
            });
        },
        // 更多求解
        moreQuestion:function () {
            
        }
    }
    exports('question', question);
});