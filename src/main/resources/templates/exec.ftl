<!DOCTYPE HTML>
<html>
<head>
    <link href="//cdn.bootcss.com/highlight.js/9.12.0/styles/agate.min.css" rel="stylesheet">
    <script src="//cdn.bootcss.com/highlight.js/9.12.0/highlight.min.js"></script>
    <script src="//cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script src="//cdn.bootcss.com/sockjs-client/1.1.4/sockjs.min.js"></script>
</head>
<body>
<input type="button" value="环境检查" id="go"/><br>
<input type="button" value="准备环境" id="prepare" />
<input type="text" name="message" id="message" />
<input type="button" value="ws" id="ws" />
<div class="result"></div>
<pre>
<code class="shell">
</code>
</pre>
<script>
    hljs.initHighlightingOnLoad();
    $(function () {
        var c = $('.shell');
        $(document).on('click', "#go", function (e) {
            $.ajax({
                url: "/checkEnv",
                method: 'get'
            }).done(function (data) {
                var s = '<ul>';
                for (var k in data) {
                    s = s + '<li>' + k + ':' + data[k] + '</li>';
                }
                s = s + '</ul>';
                $('.result').html(s);
            });
        });
        $(document).on('click', '#prepare', function (e) {
            $.ajax({
                url: "/prepareEnv",
                method: 'get'
            }).done(function (data) {
                var s = '<ul>';
                for (var k in data) {
                    s = s + '<li>' + k + ':' + data[k] + '</li>';
                }
                s = s + '</ul>';
                $('.result').html(s);
            });
        });
        ws = new SockJS('/echo');
        ws.onopen = function () {
            console.log('Info: WebSocket connection opened.');
        };
        ws.onmessage = function (event) {
            log(event.data);
        };
        ws.onclose = function () {
            console.log('Info: WebSocket connection closed.');
        };
        $(document).on('click', '#ws' , function (e) {
            if (ws != null) {
                var message = $('#message').val();
                ws.send(message);
                log("$ " + message);
            } else {
                alert('WebSocket connection not established, please connect.');
            }
        });
        function log(message) {
            c.append(message + "\n");
            c.scrollTop(c[0].scrollHeight);
            $('pre code').each(function(i, block) {
                hljs.highlightBlock(block);
            });
        }
    });
</script>
</body>
</html>