package gg.eventalerts.sdk.websocket.handler.action;

import gg.eventalerts.sdk.websocket.object.action.UpdateSubscriptionAction;
import org.jetbrains.annotations.NotNull;


public class UpdateSubscriptionActionHandler extends SocketActionHandler {
    @Override @NotNull
    public SocketActionName getName() {
        return SocketActionName.UPDATE_SUBSCRIPTION;
    }

    @Override @NotNull
    public Class<UpdateSubscriptionAction> getDataType() {
        return UpdateSubscriptionAction.class;
    }
}
