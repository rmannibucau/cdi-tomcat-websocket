package com.github.rmannibucau.event.test;

import com.github.rmannibucau.WebSocketClient;
import com.github.rmannibucau.event.WebSocketEvent;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.junit.Assert.assertEquals;

/*
 * 1. connect on websocket
 * 2. assert the server answer a message on OnOpenEvent
 */
@RunWith(Arquillian.class)
public class EventTest {
    @ArquillianResource
    private URL url;

    @Deployment(testable = false)
    public static WebArchive war() {
        return ShrinkWrap.create(WebArchive.class, "event.war")
                    .addClasses(EventEndpointImpl.class, WSObserver.class)
                    .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
                    .addAsLibraries(ShrinkWrap.create(JavaArchive.class)
                            .addPackages(true, WebSocketEvent.class.getPackage()));
    }

    @Test
    public void checkEvent() throws Exception {
        final WebSocketClient client = new WebSocketClient(url.getHost(), url.getPort()).connect("/event/websocket");
        try {
            assertEquals("Open event\r\n", client.readMessage());
        } finally {
            client.close();
        }
    }
}
