<#-- @ftlvariable name="homedir" type="java.lang.String" -->
<#-- @ftlvariable name="versions" type="java.lang.String[]" -->
<#-- @ftlvariable name="tags" type="java.lang.String[]" -->
<#-- @ftlvariable name="p" type="me.pbox.site.model.Package" -->
<#-- @ftlvariable name="static" type="java.lang.String" -->

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="well div-package">
                <div class="row">
                    <div class="col-md-2 left">
                        <div>
                            <a href="<@link name="PackagesPage" packageName=p.name/>"><img title="${p.title}" src="${p.iconUrl}"/></a>
                        </div>
                        <div>
                            <a href="<@link name="PackagesPage" packageName=p.name/>"><strong>${p.name}</strong></a>
                        </div>
                    </div>
                    <div class="col-md-10">
                        <h2 class="pull-right"><a href="<@link name="PackagesPage" packageName=p.name version=version/>">${p.version}</a></h2>
                        <h1><a href="<@link name="PackagesPage" packageName=p.name/>">${p.title}</a></h1>

                        <div class="description">
                            <p>${p.description}</p>
                        </div>

                        <div class="size">
                            Size: <#if (p.sizeKilobytes<1024)>${p.sizeKilobytes} KB<#else>${(p.sizeKilobytes/1024)?round} MB</#if>
                        </div>

                        <div class="authors">
                            Authors: ${p.authors}
                        </div>

                        <div class="authors">
                            Tags:
                            <#list tags as tag>
                                <a href="<@link name="PackagesPage" tag=tag/>">${tag}</a><#if tag_has_next>,</#if>
                            </#list>
                        </div>

                        <div class="versions">
                            Versions:
                            <#list versions as version>
                                <a href="<@link name="PackagesPage" packageName=p.name version=version/>">${version}</a><#if version_has_next>,</#if>
                            </#list>
                        </div>
                        <div class="defaults">Default path: ${homedir}</div>

                        <div class="install">
                            <span class="command">pbox install ${p.name}</span>
                            <span class="descriptor showPboxXml"><i class="fa fa-caret-right"></i> <span class="dotted">Show pbox.xml</span></span>
                        </div>

                        <pre style="display: none;" class="prettyprint lang-xml">${p.descriptor?html}</pre>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>