package com.github.rmannibucau.sse.test;

import com.github.rmannibucau.WebSocketClient;
import com.github.rmannibucau.sse.SSESender;
import com.github.rmannibucau.sse.cdi.impl.SSEExtension;
import com.github.rmannibucau.sse.impl.ContextManager;
import org.apache.ziplock.IO;
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

import javax.enterprise.inject.spi.Extension;
import javax.servlet.ServletContainerInitializer;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

/*
 * 1. connect on websocket
 * 2. call InServlet to make the server send a message
 * 3. check the received message
 */
@RunWith(Arquillian.class)
public class SSETest {
    @ArquillianResource
    private URL url;

    @Deployment(testable = false)
    public static WebArchive war() {
        return ShrinkWrap.create(WebArchive.class, "sse.war")
                    .addClasses(InServlet.class, SSEImpl.class)
                    .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
                    .addAsLibraries(ShrinkWrap.create(JavaArchive.class)
                            .addAsServiceProvider(ServletContainerInitializer.class, ContextManager.class)
                            .addAsServiceProvider(Extension.class, SSEExtension.class)
                            .addPackages(true, SSESender.class.getPackage()));
    }

    @Test
    public void checkWs() throws Exception {
        final WebSocketClient client = new WebSocketClient(url.getHost(), url.getPort()).connect("/sse/impl");

        Thread.sleep(1000); // wait a bit for the connection before sending the message

        assertEquals("ok", IO.slurp(new URL(url.toExternalForm() + "in"))); // GET on /in servlet to feed some data

        Thread.sleep(3000); // wait the message is received

        try {
            assertThat(client.readMessage(), containsString("In message"));
        } finally {
            client.close();
        }
    }
}
