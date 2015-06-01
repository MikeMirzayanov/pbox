<#-- @ftlvariable name="packageFrames" type="java.lang.String[]" -->
<#import "macros/common.ftl" as common>

<@common.page>
<style>
    .container-top {
        margin-top: 40px;
    }

    .container-top form {
        margin-top: 2em;
        text-align: right;
    }

    .div-package {
        padding-top: 0;
    }

    .div-package h2 {
        font-weight: bolder;
    }

    .div-package .left a {
        color: #000000;
    }

    .div-package img {
        margin-top: 20px;
        max-width: 100px;
    }

    .div-package .left {
        text-align: center;
    }

    .div-package .authors {
    }

    .div-package .tags {
    }

    .div-package .install {
        margin-top: 32px;
    }

    .div-package .install span.command {
        background-color: midnightblue;
        border-radius: 3px;
        border: 3px solid #808080;
        font-family: "Lucida Console", monospace;
        color: #ffffff;
        font-weight: bold;
        padding: 5px;
    }

    .div-package .install span.descriptor {
        float: right;
    }

    .div-package .dotted {
        border-bottom: 2px dashed darkslategray;
    }

    .div-package pre {
        margin-top: 20px;
    }

    .div-package .showPboxXml {
        cursor: pointer;
    }
</style>
<div class="container container-top">
    <div class="row">
        <div class="col-md-8">
            <h1 class="page-header">Packages<#if packageFrames?? && packageFrames?size!=0>
                <small>${packageFrames?size}</small></#if></h1>
        </div>
        <div class="col-md-4">
            <div class="">
                <form class="form-inline" role="form" action="<@link name="PackagesPage"/>" method="post">
                    <div class="form-group">
                        <input type="hidden" name="action" value="search">
                        <input class="form-control" id="query" name="query" placeholder="Find packages">
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

    <#if invalidQuery?? && invalidQuery>
    <div class="container container-top">
        <div class="row">
            <div class="col-md-12">
                <div class="alert alert-danger" role="alert">{{Invalid query, try again}}.</div>
            </div>
        </div>
    </div>
    </#if>

    <#if !packageFrames?? || packageFrames?size=0>
    <div class="container container-top">
        <div class="row">
            <div class="col-md-12">
                <div class="alert alert-info" role="alert">{{No packages have been found}}.</div>
            </div>
        </div>
    </div>
    </#if>

    <#if packageFrames??>
        <#list packageFrames as packageFrame>
        ${packageFrame}
        </#list>
    </#if>

<script>
    $(function () {
        $(".div-package .showPboxXml").click(function () {
            var i = $(this).parent().find("i");
            $(this).parents().eq(3).find("pre").css(
                    "display",
                    i.hasClass("fa-caret-right") ? "block" : "none"
            );
            i.toggleClass("fa-caret-right");
            i.toggleClass("fa-caret-down");
            return false;
        });
    });
</script>
</@common.page>