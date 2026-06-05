package gg.eventalerts.sdk.websocket.handler;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;


public interface SocketHandler<N extends SocketHandlerName<?>> {
    @NotNull
    N getName();

    @NotNull
    Type getDataType();
}
