package ru.ryabtsev.starship.network;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultHttpServer implements ApplicationHttpServer {

    private static final int STOP_COMMAND_DELAY = 1;

    private final HttpServer httpServer;

    public DefaultHttpServer(final InetSocketAddress address, final int backlog) {
        try {
            httpServer = HttpServer.create(address, backlog);
            final ExecutorService executorService = Executors.newCachedThreadPool();
            httpServer.setExecutor(executorService);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void registerHandler(final String path, final HttpHandler httpHandler) {
        httpServer.createContext(path, httpHandler);
    }

    @Override
    public void unregisterHandler(final String path) {
        httpServer.removeContext(path);
    }

    @Override
    public void start() {
        httpServer.start();
    }

    @Override
    public void stop() {
        httpServer.stop(STOP_COMMAND_DELAY);
    }
}
