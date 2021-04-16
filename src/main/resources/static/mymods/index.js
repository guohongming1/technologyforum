/*
*
*  @name 自定义模块
*  @author 郭红明 2020-4-5
*
* */

layui.define(['layer', 'laytpl', 'form', 'element', 'upload', 'util'],function(exports){
    var $ = layui.jquery
        ,layer = layui.layer
        ,laytpl = layui.laytpl
        ,form = layui.form
        ,element = layui.element
        ,upload = layui.upload
        ,util = layui.util
        ,device = layui.device();

    // 自定义编辑器使用，用户确定和获取光标
    var sel,range;

    //阻止IE7以下访问
    if(device.ie && device.ie < 8){
        layer.alert('如果您非得使用 IE 浏览器访问Fly社区，那么请使用 IE8+');
    }
    // 图片出错显示
    $('img').error(function () {
        $(this).attr('src',"/myfile/image/error.png");
        img.onerror = null;// 控制不要一直跳动
    });
    //失去焦点时获取光标的位置
    function getblur(){
        layui.sel = window.getSelection();
        layui.range = layui.sel.getRangeAt(0);
        layui.range.deleteContents();
    }
    $('.editor-focus').click(function () {
        $('body .fxAnswer').focus();
    });
    $('body').on('blur', '.fxAnswer', function(){
        layui.sel = window.getSelection();
        layui.range = layui.sel.getRangeAt(0);
        // :删除range的内容片段。
        layui.range.deleteContents();
    });
    // 将html插入到富文本中
    layui.insertHtmlAtCaret = function(html){
        if (window.getSelection) {
            // IE9 and non-IE
            if (layui.sel.getRangeAt && layui.sel.rangeCount) {
                var el = document.createElement("div");
                el.innerHTML = html;
                // 创建文档碎片节点
                var frag = document.createDocumentFragment(), node, lastNode;
                while ((node = el.firstChild)) {
                    lastNode = frag.appendChild(node);
                }
                layui.range.insertNode(frag);
                // Preserve the selection
                if (lastNode) {
                    // 克隆一个range的内容片段。
                    layui.range = layui.range.cloneRange();
                    // 把范围的开始点设置为紧邻指定的 lastNode 节点的位置。
                    layui.range.setStartAfter(lastNode);
                    // 向边界点折叠range，即是设置光标位置，toStart默认为false，表示光标定位在节点末尾。true表示光标定位在节点起点。
                    layui.range.collapse(true);
                    // 从当前selection对象中移除所有的range对象,取消所有的选择只 留下anchorNode 和focusNode属性并将其设置为null。
                    layui.sel.removeAllRanges();
                    // 将一个范围添加到Selection对象中
                    layui.sel.addRange(layui.range);
                }
            }
        } else if (document.selection && document.selection.type != "Control") {
            // IE < 9
            document.selection.createRange().pasteHTML(html);
        }
    }
    layui.focusInsert = function(obj, str){
        var result, val = obj.value;
        obj.focus();
        if(document.selection){ //ie
            result = document.selection.createRange();
            document.selection.empty();
            result.text = str;
        } else {
            result = [val.substring(0, obj.selectionStart), str, val.substr(obj.selectionEnd)];
            obj.focus();
            obj.value = result.join('');
        }
    };
    //创建监听函数
    layui.xhrOnProgress=function(fun) {
        xhrOnProgress.onprogress = fun; //绑定监听
        //使用闭包实现监听绑
        return function() {
            //通过$.ajaxSettings.xhr();获得XMLHttpRequest对象
            var xhr = $.ajaxSettings.xhr();
            //判断监听函数是否为函数
            if (typeof xhrOnProgress.onprogress !== 'function')
                return xhr;
            //如果有监听函数并且xhr对象支持绑定时就把监听函数绑定上去
            if (xhrOnProgress.onprogress && xhr.upload) {
                xhr.upload.onprogress = xhrOnProgress.onprogress;
            }
            return xhr;
        }
    }
    var travel={
        //计算字符长度
        charLen: function(val) {
            var arr = val.split(''), len = 0;
            for (var i = 0; i < val.length; i++) {
                arr[i].charCodeAt(0) < 299 ? len++ : len += 2;
            }
            return len;
        }
        // 配置编辑器  自定义编辑器
        ,layEditor: function(options){
            var html = ['<div class="layui-unselect fly-edit">'
                ,'<span type="face" title="插入表情"><i class="iconfont icon-yxj-expression" style="top: 1px;"></i></span>'
                ,'<span type="picture" title="插入图片：img[src]"><i class="iconfont icon-tupian"></i></span>'
                ,'<span type="href" title="超链接格式：a(href)[text]"><i class="iconfont icon-lianjie"></i></span>'
                ,'<span type="hr" title="插入水平线">hr</span>'
                ,'<span type="h3" title="插入标题">h3</span>'
                ,'<span type="yulan" title="预览"><i class="iconfont icon-yulan1"></i></span>'
                ,'</div>'].join('');

            var log = {}, mod = {
                face: function(editor, self){ //插入表情
                    var str = '', ul, face = travel.faces;
                    for(var key in face){
                        str += '<li title="'+ key +'"><img src="'+ face[key] +'"></li>';
                    }
                    str = '<ul id="LAY-editface" class="layui-clear">'+ str +'</ul>';
                    layer.tips(str, self, {  // tips层弹出表情框 str内容 self选择器
                        tips: 3
                        ,time: 0
                        ,skin: 'layui-edit-face'
                    });
                    $(document).on('click', function(){  // 点击任何一个地方关闭
                        layer.closeAll('tips');
                    });
                    $('#LAY-editface li').on('click', function(){
                        var title = $(this).attr('title');
                        layui.insertHtmlAtCaret('<img alt="'+ title +'" title="'+ title +'" src="' + face[title] + '">');
                    });
                }
                ,picture: function(editor){ //插入图片 TODO
                    layer.open({
                        type: 1
                        ,id: 'fly-jie-upload'
                        ,title: '插入图片'
                        ,area: 'auto'
                        ,shade: false
                        ,area: '465px'
                        ,fixed: false
                        ,offset: [
                            editor.offset().top - $(window).scrollTop() + 'px'
                            ,editor.offset().left + 'px'
                        ]
                        ,skin: 'layui-layer-border'
                        ,content: ['<ul class="layui-form layui-form-pane" style="margin: 20px;">'
                            ,'<li class="layui-form-item">'
                            ,'<label class="layui-form-label">URL</label>'
                            ,'<div class="layui-input-inline">'
                            ,'<input required name="image" placeholder="支持直接粘贴远程图片地址" value="" class="layui-input">'
                            ,'</div>'
                            ,'<button type="button" class="layui-btn layui-btn-primary" id="uploadImg"><i class="layui-icon">&#xe67c;</i>上传图片</button>'
                            ,'</li>'
                            ,'<li class="layui-form-item" style="text-align: center;">'
                            ,'<button type="button" lay-submit lay-filter="uploadImages" class="layui-btn">确认</button>'
                            ,'</li>'
                            ,'</ul>'].join('')
                        ,success: function(layero, index){
                            var image =  layero.find('input[name="image"]');

                            //执行上传实例
                            upload.render({
                                elem: '#uploadImg'
                                ,url: '/uploadImg/'
                                ,size: 100000
                                ,accept: 'file'
                                ,before:function(){
                                    element.progress('js_upload_progress', '0%');//设置页面进度条
                                    layui.jindu=layer.open({
                                        type: 1,
                                        title: '上传进度',
                                        closeBtn: 1, //不显示关闭按钮
                                        area: ['300px', '100px'],
                                        shadeClose: false, //开启遮罩关闭
                                        content: $("#uploadLoadingDiv").html(),
                                        offset: '100px'
                                    });
                                }
                                ,xhr:layui.xhrOnProgress
                                ,progress:function(value){//上传进度回调 value进度值
                                    element.progress('js_upload_progress', value+'%');//设置页面进度条
                                }
                                // ,accept:'images'
                                // ,acceptMime:'image/*'
                                // ,progress: function(n, elem){
                                //     var percent = n + '%' //获取进度百分比
                                //     element.progress('demo', percent); //可配合 layui 进度条元素使用
                                //
                                //     //以下系 layui 2.5.6 新增
                                //     console.log(n); //得到当前触发的元素 DOM 对象。可通过该元素定义的属性值匹配到对应的进度条。
                                // }
                                // ,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
                                //     layer.load(); //上传loading
                                // }
                                ,done: function(res){
                                    layer.close(layui.jindu);// 关闭进度条
                                    //layer.closeAll('loading'); //关闭loading
                                    if(res.code == 1){
                                        image.val(res.data.src);
                                        layer.msg("上传成功")
                                    } else {
                                        layer.msg(res.msg, {icon: 5});
                                    }
                                }
                                ,error: function(index, upload){
                                    layer.closeAll('loading'); //关闭loading
                                    layer.msg("上传失败")
                                }
                            });

                            form.on('submit(uploadImages)', function(data){
                                var field = data.field;
                                if(!field.image) return image.focus();
                                layui.insertHtmlAtCaret('<img src="'+field.image + '"> ');
                                layer.close(index);
                            });
                        }
                    });
                }
                ,href: function(editor){ //超链接
                    layer.open({
                        type: 1
                        ,id: 'fly-jie-链接'
                        ,title: '插入超链接'
                        ,area: 'auto'
                        ,shade: false
                        ,fixed: false
                        ,offset: [
                            editor.offset().top - $(window).scrollTop() + 'px'
                            ,editor.offset().left + 'px'
                        ]
                        ,skin: 'layui-layer-border'
                        ,content: [,'<div class="layui-form layui-form-pane">'
                                   ,'<div class="layui-form-item">'
                            ,'<label class="layui-form-label">链接地址</label>'
                            ,'<div class="layui-input-block">'
                            ,'<input type="text" name="url" required  placeholder="链接地址" autocomplete="off" class="layui-input">'
                            ,'</div>'
                            ,'<label class="layui-form-label">名称</label>'
                            ,'<div class="layui-input-block">'
                            ,'<input type="text" name="title"  placeholder="地址名称(选填)" autocomplete="off" class="layui-input">'
                            ,'</div>'
                            ,'<div class="layui-input-block" style="margin: 10px 10px 0px 100px">'
                            ,'<button type="button" lay-submit lay-filter="lianjie" class="layui-btn">确认</button>'
                            ,'</div>'
                            ,'</div>'
                            ,'</div>'].join('')
                        ,success: function(layero, index){
                            form.on('submit(lianjie)', function(data){
                                var url =  layero.find('input[name="url"]').val();
                                var title =  layero.find('input[name="title"]').val();
                                if(!/^http(s*):\/\/[\S]/.test(url)){
                                            layer.tips('这根本不是个链接，不要骗我。', this, {tips:1})
                                            return;
                                        }
                                if(null != title && title.trim() != ""){
                                    layui.insertHtmlAtCaret('<a href="'+url+'">'+title+'</a>');
                                }else{
                                    layui.insertHtmlAtCaret('<a href="'+url+'">'+url+'</a>');
                                }
                                layer.close(index);
                            });
                        }
                    });
                    // layer.prompt({
                    //     title: '请输入合法链接'
                    //     ,shade: false
                    //     ,fixed: false
                    //     ,id: 'LAY_flyedit_href'
                    //     ,offset: [
                    //         editor.offset().top - $(window).scrollTop() + 'px'
                    //         ,editor.offset().left + 'px'
                    //     ]
                    // }, function(val, index, elem){
                    //     if(!/^http(s*):\/\/[\S]/.test(val)){
                    //         layer.tips('这根本不是个链接，不要骗我。', elem, {tips:1})
                    //         return;
                    //     }
                    //     layui.insertHtmlAtCaret('<a href="'+val+'">'+val+'</a>');
                    //     layer.close(index);
                    // });
                }
                ,hr: function(editor){ //插入水平分割线
                    layui.insertHtmlAtCaret('<hr>');
                }
                ,h3:function (editor) {
                    layer.prompt({
                        title: '请输入标题'
                        ,shade: false
                        ,fixed: false
                        ,id: 'LAY_flyedit_href'
                        ,offset: [
                            editor.offset().top - $(window).scrollTop() + 'px'
                            ,editor.offset().left + 'px'
                        ]
                    }, function(val, index, elem){
                        if(val != null && val != ""){
                            layui.insertHtmlAtCaret('<h2>'+val+'</h2>');
                            layer.close(index);
                        }
                    });
                }
                ,yulan: function(editor){ //预览
                    var content = editor.html();
                    layer.open({
                        type: 1
                        ,title: '预览'
                        ,shade: false
                        ,area: ['100%', '100%']
                        ,scrollbar: false
                        ,content: '<div class="detail-body" style="margin:20px;">'+ content +'</div>'
                    });
                }
            };

            layui.use('face', function(face){
                options = options || {};
                travel.faces = face;
                $(options.elem).each(function(index){
                    var that = this, othis = $(that), parent = othis.parent();
                    parent.prepend(html);
                    parent.find('.fly-edit span').on('click', function(event){
                        var type = $(this).attr('type');
                        mod[type].call(that, othis, this);
                        if(type === 'face'){
                            event.stopPropagation()
                        }
                    });
                });
            });

        }
    }
    //加载编辑器
    travel.layEditor({
        elem: '.fly-editor'
    });
    /* 上传攻略头图*/
    var headImgSrc = $('#LAY_headImgSrc');
    upload.render({
        elem: '#uploadHeadImgbtn'
        ,url: '/uploadImg/'
        ,size: 50000
        ,accept: 'file'
        ,before:function(){
            layer.load(); //上传loading
        }
        ,done: function(res){
            layer.closeAll('loading'); //关闭loading
            if(res.code == 1){
                headImgSrc.val(res.data.src);
                $(".head_img").html('<img src="'+res.data.src + '" width="200px" height="200px"> ');
                $(".head_img").show();
                layer.msg("上传成功")
            } else {
                layer.msg(res.msg, {icon: 5});
            }
        }
        ,error: function(index, upload){
            layer.closeAll('loading'); //关闭loading
            layer.msg("上传失败")
        }
    });
    /* 上传用户头像*/
    upload.render({
        elem: '#uploadUserHeadImgbtn'
        ,url: '/uploadImg/'
        ,size: 50000
        ,accept: 'file'
        ,before:function(){
            layer.load(); //上传loading
        }
        ,done: function(res){
            layer.closeAll('loading'); //关闭loading
            if(res.code == 1){
                var userHeadImgSrc = res.data.src;
                $(".head_img").attr('src',userHeadImgSrc);
                layer.msg("上传成功")
                $.ajax({
                    type:'post',
                    url:'/user/reheadimg',
                    data:{'userHeadImgSrc':userHeadImgSrc},
                    success:function (ress) {
                        if(ress.code == 200){
                            layer.msg("修改成功");
                        }
                    },
                    error:function (ress) {
                        layer.msg(ress.msg)
                    }
                });
            } else {
                layer.msg(res.msg, {icon: 5});
            }
        }
        ,error: function(index, upload){
            layer.closeAll('loading'); //关闭loading
            layer.msg("上传失败")
        }
    });
    exports('travel', travel);
});