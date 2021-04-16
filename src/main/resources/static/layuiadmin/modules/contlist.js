/**

 @Name：layuiAdmin 内容系统
 @Author：guohongming
 @Site：http://www.layui.com/admin/
 @License：LPPL

 */
layui.define(['table', 'form'], function (exports) {
    var $ = layui.$
        , table = layui.table
        , form = layui.form
        , admin = layui.admin;


    //攻略管理
    table.render({
        elem: '#LAY-app-content-list'
        , url: '/admin/strategy-data' //模拟接口
        , cols: [[
            {type: 'checkbox', fixed: 'left'}
            , {field: 'id', width: 100, title: '文章ID', sort: true}
            // ,{field: 'label', title: '文章标签', minWidth: 100}
            , {field: 'title', title: '文章标题'}
            ,{field: 'userId', title: '作者ID'}
            , {field: 'date', title: '上传时间', sort: true}
            , {field: 'pushFlag', title: '发布状态', templet: '#buttonTpl', minWidth: 80, align: 'center'}
            , {title: '操作', minWidth: 150, align: 'center', fixed: 'right', toolbar: '#table-content-list'}
        ]]
        , page: true
        , limit: 10
        , limits: [10, 15, 20, 25, 30]
        , text: {
            none: "搜索的内容不存在"
        }
    });

    //监听工具条
    table.on('tool(LAY-app-content-list)', function (obj) {
        var data = obj.data;

        if (obj.event === 'del') {
            layer.confirm('确定删除此文章？', function (index) {
                obj.del();
                var arr = [];
                arr.push(obj.data);
                admin.req({
                    url: '/admin/strateBatchdelete'
                    , type: 'post'
                    , contentType: 'application/json'
                    , data: JSON.stringify(arr)
                });
                layer.close(index);
            });
        } else if (obj.event === 'edit') {
            var id = obj.data.id;
        }
    });
    //问答管理
    table.render({
        elem: '#LAY-app-question-list'
        , url: '/admin/question-data' //模拟接口
        , cols: [[
            {type: 'checkbox', fixed: 'left'}
            , {field: 'id', width: 100, title: '文章ID', sort: true}
            // ,{field: 'label', title: '文章标签', minWidth: 100}
            , {field: 'title', title: '标题'}
            ,{field: 'userId', title: '作者ID'}
            , {field: 'date', title: '上传时间', sort: true}
            , {field: 'pushFlag', title: '问题状态', templet: '#buttonTpl', minWidth: 80, align: 'center'}
            , {title: '操作', minWidth: 150, align: 'center', fixed: 'right', toolbar: '#table-question-list'}
        ]]
        , page: true
        , limit: 10
        , limits: [10, 15, 20, 25, 30]
        , text: {
            none: "搜索的内容不存在"
        }
    });

    //监听工具条
    table.on('tool(LAY-app-question-list)', function (obj) {
        var data = obj.data;

        if (obj.event === 'del') {
            layer.confirm('确定删除此问答？', function (index) {
                obj.del();
                var arr = [];
                arr.push(obj.data);
                admin.req({
                    url: '/admin/questionBatchdel'
                    , type: 'post'
                    , contentType: 'application/json'
                    , data: JSON.stringify(arr)
                });
                layer.close(index);
            });
        } else if (obj.event === 'edit') {
            var id = obj.data.id;
        }
    });
    //小组审核
    table.render({
        elem: '#LAY-app-group-list'
        , url: '/admin/groupReview-data' //模拟接口
        , cols: [[
            {type: 'checkbox', fixed: 'left'}
            , {field: 'id', width: 100, title: '小组ID', sort: true}
            // ,{field: 'label', title: '文章标签', minWidth: 100}
            , {field: 'title', title: '标题'}
            ,{field: 'userId', title: '作者ID'}
            , {field: 'date', title: '创建时间', sort: true}
            ,{field: 'content', title: '作者ID'}
            , {title: '操作', minWidth: 150, align: 'center', fixed: 'right', toolbar: '#table-group-list'}
        ]]
        , page: true
        , limit: 10
        , limits: [10, 15, 20, 25, 30]
        , text: {
            none: "无审核小组"
        }
    });

    //监听工具条
    table.on('tool(LAY-app-group-list)', function (obj) {
        var data = obj.data;

        if (obj.event === 'acpt') {
            layer.confirm('审核通过确认？', function (index) {
                obj.del();
                admin.req({
                    url: '/admin/shenhe'
                    , type: 'post'
                    , data: {'id':data.id,'flag':'1'}
                });
                layer.close(index);
            });
        }
        if (obj.event === 'reject') {
            layer.confirm('审核驳回确认？', function (index) {
                obj.del();
                admin.req({
                    url: '/admin/shenhe'
                    , type: 'post'
                    , data: {'id':data.id,'flag':'0'}
                });
                layer.close(index);
            });
        }
    });
    //用户管理
    table.render({
        elem: '#LAY-app-userinfo-list'
        , url: '/admin/userinfo-data' //模拟接口
        , cols: [[
            {type: 'checkbox', fixed: 'left'}
            , {field: 'id', width: 100, title: '用户ID', sort: true}
            // ,{field: 'label', title: '文章标签', minWidth: 100}
            , {field: 'name', title: '昵称'}
            ,{field: 'email', title: '邮箱'}
            , {field: 'sex', title: '性别', templet: '#buttonTpl', minWidth: 80, align: 'center'}
            , {field: 'role', title: '角色', templet: '#buttonTp2', minWidth: 80, align: 'center'}
            , {field: 'date', title: '创建时间', sort: true}
            , {title: '操作', minWidth: 150, align: 'center', fixed: 'right', toolbar: '#table-userinfo-list'}
        ]]
        , page: true
        , limit: 10
        , limits: [10, 15, 20, 25, 30]
        , text: {
            none: "搜索结果为空"
        }
    });

    //监听工具条
    table.on('tool(LAY-app-userinfo-list)', function (obj) {
        var data = obj.data;

        if (obj.event === 'lookpassword') {
            layer.open({
                title: '密码'
                ,content: data.password
            });

        }
        if (obj.event === 'edit') {

        }
        if (obj.event === 'del') {
            layer.confirm('确认删除选中用户？', function (index) {
                obj.del();
                var arr = [];
                arr.push(obj.data);
                admin.req({
                    url: '/admin/delBatchuser'
                    , type: 'post'
                    , contentType: 'application/json'
                    , data: JSON.stringify(arr)
                });
                layer.close(index);
            });
        }
    });
    //分类管理
    table.render({
        elem: '#LAY-app-content-tags'
        , url: layui.setter.base + 'json/content/tags.js' //模拟接口
        , cols: [[
            {type: 'numbers', fixed: 'left'}
            , {field: 'id', width: 100, title: 'ID', sort: true}
            , {field: 'tags', title: '分类名', minWidth: 100}
            , {title: '操作', width: 150, align: 'center', fixed: 'right', toolbar: '#layuiadmin-app-cont-tagsbar'}
        ]]
        , text: '对不起，加载出现异常！'
    });
    //监听工具条
    table.on('tool(LAY-app-content-tags)', function (obj) {
        var data = obj.data;
        if (obj.event === 'del') {
            layer.confirm('确定删除此分类？', function (index) {
                obj.del();
                layer.close(index);
            });
        } else if (obj.event === 'edit') {
            var tr = $(obj.tr);
            layer.open({
                type: 2
                , title: '编辑分类'
                , content: '../../../views/app/content/tagsform.html?id=' + data.id
                , area: ['450px', '200px']
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //获取iframe元素的值
                    var othis = layero.find('iframe').contents().find("#layuiadmin-app-form-tags")
                        , tags = othis.find('input[name="tags"]').val();

                    if (!tags.replace(/\s/g, '')) return;

                    obj.update({
                        tags: tags
                    });
                    layer.close(index);
                }
                , success: function (layero, index) {
                    //给iframe元素赋值
                    var othis = layero.find('iframe').contents().find("#layuiadmin-app-form-tags").click();
                    othis.find('input[name="tags"]').val(data.tags);
                }
            });
        }
    });

    //评论管理
    table.render({
        elem: '#LAY-app-content-comm'
        , url: layui.setter.base + 'json/content/comment.js' //模拟接口
        , cols: [[
            {type: 'checkbox', fixed: 'left'}
            , {field: 'id', width: 100, title: 'ID', sort: true}
            , {field: 'reviewers', title: '评论者', minWidth: 100}
            , {field: 'content', title: '评论内容', minWidth: 100}
            , {field: 'commtime', title: '评论时间', minWidth: 100, sort: true}
            , {title: '操作', width: 150, align: 'center', fixed: 'right', toolbar: '#table-content-com'}
        ]]
        , page: true
        , limit: 10
        , limits: [10, 15, 20, 25, 30]
        , text: '对不起，加载出现异常！'
    });

    //监听工具条
    table.on('tool(LAY-app-content-comm)', function (obj) {
        var data = obj.data;
        if (obj.event === 'del') {
            layer.confirm('确定删除此条评论？', function (index) {
                obj.del();
                layer.close(index);
            });
        } else if (obj.event === 'edit') {
            layer.open({
                type: 2
                , title: '编辑评论'
                , content: '../../../views/app/content/contform.html'
                , area: ['450px', '300px']
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    var iframeWindow = window['layui-layer-iframe' + index]
                        , submitID = 'layuiadmin-app-comm-submit'
                        , submit = layero.find('iframe').contents().find('#' + submitID);

                    //监听提交
                    iframeWindow.layui.form.on('submit(' + submitID + ')', function (data) {
                        var field = data.field; //获取提交的字段

                        //提交 Ajax 成功后，静态更新表格中的数据
                        //$.ajax({});
                        table.reload('LAY-app-content-comm'); //数据刷新
                        layer.close(index); //关闭弹层
                    });

                    submit.trigger('click');
                }
                , success: function (layero, index) {

                }
            });
        }
    });

    exports('contlist', {})
});