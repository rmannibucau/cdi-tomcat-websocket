package com.github.rmannibucau;

import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.CharChunk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

// mainly from tomcat
// see http://svn.apache.org/repos/asf/tomcat/tc7.0.x/trunk/test/org/apache/catalina/websocket/TestWebSocket.java
public class WebSocketClient {
    private OutputStream os;
    private InputStream is;
    private Socket socket ;
    private Writer writer ;
    private BufferedReader reader;

    public WebSocketClient(final  String host, final int port) {
        SocketAddress addr = new InetSocketAddress(host, port);
        socket = new Socket();
        try {
            socket.setSoTimeout(30000);
            socket.connect(addr, 10000);
            os = socket.getOutputStream();
            writer = new OutputStreamWriter(os, "UTF-8");
            is = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedReader getReader() {
        return reader;
    }

    public Writer getWriter() {
        return writer;
    }

    public void close() throws IOException {
        socket.close();
    }

    public String readMessage() throws IOException {
        final ByteChunk bc = new ByteChunk(125);
        final CharChunk cc = new CharChunk(125);

        // Skip first byte
        is.read();

        // Get payload length
        int len = is.read() & 0x7F;

        // Read payload
        int read = 0;
        while (read < len) {
            read = read + is.read(bc.getBytes(), read, len - read);
        }

        bc.setEnd(len);

        final B2CConverter b2c = new B2CConverter("UTF-8");
        b2c.convert(bc, cc, len);

        return cc.toString();
    }

    public WebSocketClient connect(final String s) throws IOException {
        writer.write("GET " + s + " HTTP/1.1\r\n");
        writer.write("Host: localhost\n");
        writer.write("Upgrade: websocket\r\n");
        writer.write("Connection: keep-alive, upgrade\r\n");
        writer.write("Sec-WebSocket-Version: 13\r\n");
        writer.write("Sec-WebSocket-Key: TODO\r\n");
        writer.write("\r\n");
        writer.flush();

        while (!"".equals(reader.readLine().trim())) {}

        return this;
    }
}
