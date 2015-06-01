<#import "macros/common.ftl" as common>

<@common.page>
<div id="headerwrap">
    <div class="container">
        <div class="row">
            <div class="col-lg-6">
                <h1>
                    Windows Package Manager
                </h1>
                <div class="install">
                    Easy to install <span class="note">(run in administrative terminal)</span>
                    <div class="terminal"><span>C:\&gt;</span> @powershell -NoProfile -ExecutionPolicy unrestricted
                        -Command "iex ((new-object net.webclient).DownloadString('http://repo.pbox.me/files/i.ps1'))"
                        && set PATH=%PATH%;%ALLUSERSPROFILE%\pbox</div>
                </div>
                <div class="search-packages">
                    <form class="form-inline" role="form" action="<@link name="PackagesPage"/>" method="post">
                        <div class="form-group">
                            <input type="hidden" name="action" value="search">
                            <input class="form-control" id="query" name="query" placeholder="Find packages">
                        </div>
                        <button type="submit" class="btn btn-warning btn-lg">Search!</button>
                    </form>
                </div>
            </div><!-- /col-lg-6 -->
            <div class="col-lg-6">
                <img class="img-responsive" src="${static}/assets/img/magic-box-400.png" alt="">
            </div><!-- /col-lg-6 -->
        </div><!-- /row -->
    </div><!-- /container -->
</div><!-- /headerwrap -->

<div class="container">
    <div class="row mt centered">
        <div class="col-lg-6 col-lg-offset-3">
            <div class="all-you-need">Just pbox it</div>
            <code>pbox install &lt;package&gt;</code>
        </div>
    </div><!-- /row -->

    <div class="row mt centered examples">
        <div class="col-lg-4">
            <p><strong>MinGW+MSYS</strong> is a free and open source development environment for native Microsoft Windows applications.
                It includes a port of the <span class="code">g++</span> and a collection of GNU utilities such as <span class="code">bash</span>,
                <span class="code">make</span>, <span class="code">gawk</span> and <span class="code">grep</span> to allow building of applications and programs which depend on traditionally UNIX tools to be present.
            </p>
            <code>pbox install mingw</code>
        </div><!--/col-lg-4 -->

        <div class="col-lg-4">
            <p><strong>Python</strong> is a widely used general-purpose, high-level programming language with duck, dynamic and strong typing.
                Python supports multiple programming paradigms, including object-oriented, imperative and functional programming or procedural styles.</p>
            <code>pbox install python2</code>
            <div class="or">and/or</div>
            <code>pbox install python3</code>
        </div><!--/col-lg-4 -->

        <div class="col-lg-4">
            <p><strong>Far Manager</strong> is a superior file manager and editor. Far Manager's default interface combines two file panels with a command prompt.
            </p>
            <code>pbox install far</code>
        </div><!--/col-lg-4 -->
    </div><!-- /row -->
</div><!-- /container -->
</@common.page>