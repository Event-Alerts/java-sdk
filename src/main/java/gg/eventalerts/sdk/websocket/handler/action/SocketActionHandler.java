package gg.eventalerts.sdk.websocket.handler.action;

import gg.eventalerts.sdk.websocket.handler.SocketHandler;
import org.jetbrains.annotations.NotNull;


public abstract class SocketActionHandler implements SocketHandler<SocketActionName> {
    @Override @NotNull
    public abstract SocketActionName getName();
}
