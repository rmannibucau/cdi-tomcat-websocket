package com.github.rmannibucau.sse.test;

import com.github.rmannibucau.sse.SSESender;
import com.github.rmannibucau.sse.cdi.impl.SSEExtension;
import com.github.rmannibucau.sse.impl.ContextManager;
import org.apache.ziplock.IO;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.Extension;
import javax.servlet.ServletContainerInitializer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

@RunWith(Arquillian.class)
public class SSETest {
    @ArquillianResource
    private URL url;

    @Deployment(testable = false)
    public static WebArchive war() {
        return ShrinkWrap.create(WebArchive.class, "sse.war")
                    .addClasses(InServlet.class, SSEImpl.class)
                    .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
                    .addAsWebResource(new ClassLoaderAsset("index.html"), ArchivePaths.create("index.html"))
                    .addAsLibraries(ShrinkWrap.create(JavaArchive.class)
                            .addAsServiceProvider(ServletContainerInitializer.class, ContextManager.class)
                            .addAsServiceProvider(Extension.class, SSEExtension.class)
                            .addPackages(true, SSESender.class.getPackage()));
    }

    @Test
    public void checkWs() throws Exception {
        final SocketAddress addr = new InetSocketAddress(url.getHost(), url.getPort());
        final Socket socket = new Socket();

        socket.setSoTimeout(30000);
        socket.connect(addr, 10000);

        final OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        // connect
        writer.write("GET /sse/impl HTTP/1.1\r\n");
        writer.write("Host: localhost\n");
        writer.write("Upgrade: websocket\r\n");
        writer.write("Connection: keep-alive, upgrade\r\n");
        writer.write("Sec-WebSocket-Version: 13\r\n");
        writer.write("Sec-WebSocket-Key: TODO\r\n");
        writer.write("\r\n");
        writer.flush();

        while (!"".equals(reader.readLine().trim())) {}

        Thread.sleep(1000); // wait a bit for the connection before sending the message

        assertEquals("ok", IO.slurp(new URL(url.toExternalForm() + "in"))); // GET on /in servlet to feed some data

        Thread.sleep(3000); // wait the message is received

        try {
            assertThat(reader.readLine(), containsString("In message"));
        } finally {
            socket.close();
        }
    }
}
