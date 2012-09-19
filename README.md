[![Build Status](https://secure.travis-ci.org/rmannibucau/cdi-tomcat-sse.png)](http://travis-ci.org/rmannibucau/cdi-tomcat-sse)

CDI Tomcat SSE
=========================

SSE
----

SSE is the fact to send from the server data to the client. This is often done using a servlet with an infinite
loop and the content type text/event-stream.

However using a servlet is not a good solution for common cases.

With the new websocket technology we can answer the same need a bit differently using them to send
from the server to the client with a kind of pub/sub mecanism.

Build
-----

    mvn clean install

Usage
-----

First define a com.github.rmannibucau.sse.SSEWebSocket (either in web.xml or through servlet 3 annotations):

    @WebServlet(urlPatterns = "/impl")
    public class SSEImpl extends SSEWebSocket {}

If you extend the SSEWebSocket (not mandatory) you have access to some hooks linked to client connections:

    protected void onOpen();
    protected void onClose(final int status);

Then you can inject (needs CDI) a SSESender which will broadcast information you want to all client:

    @Inject
    @SSE("/impl")
    private SSESender sse;

The client are matched from the @SSE parameter (url patterns/mapping).

This means you can have a client page with several update zone without any issues.

On the client side simply use websocket API as usual to connect on the websocket endpoint and update your page
in onmessage callback.
