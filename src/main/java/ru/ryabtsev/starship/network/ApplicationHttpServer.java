package ru.ryabtsev.starship.network;

import com.sun.net.httpserver.HttpHandler;

public interface ApplicationHttpServer {
    void registerHandler(String path, HttpHandler httpHandler);

    void unregisterHandler(String path);

    void start();

    void stop();
}
