<!DOCTYPE HTML>
<html>
<head>
    <link href="//cdn.bootcss.com/highlight.js/9.12.0/styles/agate.min.css" rel="stylesheet">
    <script src="//cdn.bootcss.com/highlight.js/9.12.0/highlight.min.js"></script>
    <script>hljs.initHighlightingOnLoad();</script>
</head>
<body>
    <pre>
        <code class="bash">
            <#if lines?exists>
                <#list lines as line>
                    ${line}
                </#list>
            </#if>
        </code>
    </pre>
</body>
</html>