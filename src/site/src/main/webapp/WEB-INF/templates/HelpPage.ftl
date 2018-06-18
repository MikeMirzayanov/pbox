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

    .terminal {
        font-size: 16px;
        background-color: black;
        border: 3px solid gray;
        padding: 3px;
        color: #ffffff;
        display: block;
        font-family: "Lucida Console",monospace;
    }

    .terminal span {
        webkit-touch-callout: none;
        -webkit-user-select: none;
        -khtml-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
    }
</style>
<div class="container container-top">
    <div class="row">
        <div class="col-md-8">
            <h1 class="page-header">How to use</h1>
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

<div class="container container-top">
    <div class="row">
        <div class="col-md-10 col-md-offset-1">
            <p>
                To install just run in command line: <span class="terminal"><span>C:\&gt;</span> @powershell -NoProfile -ExecutionPolicy unrestricted
                -Command "iex ((new-object net.webclient).DownloadString('http://repo.pbox.me/files/i.ps1'))"
                && set PATH=%PATH%;%ALLUSERSPROFILE%\pbox</span>
            </p>

            <h2>Installation</h2>
            <p>
                To install a package: <span class="terminal">pbox install far</span>
            </p>
            <p>
                For most packages you can specify homedir: <span class="terminal">pbox install jdk8 --homedir=C:\Programs\JDK8</span>
            </p>
            <p>
                If a package has 32 and 64 bit editions PBOX chooses edition automatically, but you can force the exact architecture: <span class="terminal">pbox install go --arch=32</span>
            </p>
            <p>
                By default PBOX installs the latest version, but you can specify exact version: <span class="terminal">pbox install dmd --version=2.066.1</span>
            </p>

            <h2>Uninstallation</h2>
            <p>
                To uninstall a package: <span class="terminal">pbox uninstall far</span>
            </p>


            <h2>Other usages</h2>
            <table class="table table-bordered table-striped">
                <tbody>
                <tr>
                    <td>
                        Ask PBOX to &quot;forget&quot; an installed package (the package will not be uninstalled).
                    </td>
                    <td style="width: 50%;">
                        <span class="terminal">pbox forget far</span>
                    </td>
                </tr>
                <tr>
                    <td>
                        Prints information about the latest version of a package.
                    </td>
                    <td>
                        <span class="terminal">pbox info far</span>
                    </td>
                </tr>
                <tr>
                    <td>
                        Prints information about the given version of a package.
                    </td>
                    <td>
                        <span class="terminal">pbox info far --version=3.0.4242</span>
                    </td>
                </tr>
                <tr>
                    <td>
                        Finds packages in the repository by query. Takes in account only latest package versions.
                    </td>
                    <td>
                        <span class="terminal">pbox find &lt;query&gt;</span>
                        or
                        <span class="terminal">pbox search &lt;query&gt;</span>
                    </td>
                </tr>
                <tr>
                    <td>
                        Finds among all versions of all packages.
                    </td>
                    <td>
                        <span class="terminal">pbox find &lt;query&gt; --all</span>
                        or
                        <span class="terminal">pbox search &lt;query&gt; --all</span>
                    </td>
                </tr>
                <tr>
                    <td>
                        Prints list of all packages (latest versions).
                    </td>
                    <td>
                        <span class="terminal">pbox list</span>
                    </td>
                </tr>
                <tr>
                    <td>
                        Prints list of all packages (all versions).
                    </td>
                    <td>
                        <span class="terminal">pbox list --all</span>
                    </td>
                </tr>
                <tr>
                    <td>
                        Prints list of all installed packages.
                    </td>
                    <td>
                        <span class="terminal">pbox list-installed</span>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

</@common.page>