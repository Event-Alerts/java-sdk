package gg.eventalerts.sdk.websocket.handler;

import org.jetbrains.annotations.NotNull;


public interface SocketHandlerName<M extends SocketHandler<?>> {
    @NotNull
    Class<M> getHandlerClass();
}
