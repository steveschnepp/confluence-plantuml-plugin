Confluence PlantUML Plugin
==========================

How to build the plugin

    atlas-mvn install

How to deploy the plugin

    atlas-mvn atlassian-pdk:install \
        -Datlassian.pdk.server.username=admin \
        -Datlassian.pdk.server.password=secret \
        -Datlassian.pdk.server.url=http://server:PORT \
        -Datlassian.plugin.key=puml