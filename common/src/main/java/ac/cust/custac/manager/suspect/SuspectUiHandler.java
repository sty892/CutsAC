package ac.cust.custac.manager.suspect;

import ac.cust.custac.platform.api.sender.Sender;

public interface SuspectUiHandler {
    boolean openSuspects(Sender sender);

    default boolean playReplay(Sender sender, SuspectFlag flag) {
        return false;
    }
}
