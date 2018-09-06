FROM openliberty/open-liberty:microProfile1

ENTRYPOINT ["/opt/ol/wlp/bin/server", "run"]
CMD ["defaultServer"]

COPY app/target/liberty/wlp/usr/servers/defaultServer/server.xml app/target/liberty/wlp/usr/servers/defaultServer/bootstrap.properties /opt/ol/wlp/usr/servers/defaultServer/
COPY app/target/liberty/wlp/usr/servers/defaultServer/apps/rest.war /opt/ol/wlp/usr/servers/defaultServer/apps/

