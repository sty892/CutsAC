package ac.cust.custac.utils.common.arguments;

import ac.cust.custac.platform.api.Platform;

import static ac.cust.custac.utils.common.arguments.ArgumentUtils.platform;
import static ac.cust.custac.utils.common.arguments.ArgumentUtils.string;

public class CommonCustACArguments {

    private final static SystemArgumentFactory FACTORY = SystemArgumentFactory.Builder.of("CustAC")
            .optionModifier(builder -> builder.key("CustAC" + builder.options().getKey()))
            .supportEnv()
            .build();

    public final static SystemArgument<Boolean> KICK_ON_TRANSACTION_ERRORS = FACTORY.create(string("KickOnTransactionTaskErrors", false));
    public final static SystemArgument<String> API_URL = FACTORY.create(string("APIUrl", "https://api.custac.ac/v1/server/"));
    public final static SystemArgument<String> PASTE_URL = FACTORY.create(string("PasteUrl", "https://paste.custac.ac/"));
    public final static SystemArgument<Platform> PLATFORM_OVERRIDE = FACTORY.create(platform("PlatformOverride"));

    /**
     * Enables "Fast Bypass" mode for chat messages sent by CustACAC.
     * <p>
     * <b>BENEFIT:</b> Messages are sent directly as packets, significantly improving
     * performance and reducing server overhead especially when lots of alerts are being sent.
     * <p>
     * <b>TRADE-OFF:</b> This completely bypasses the platform's event system (e.g., Bukkit's chat events).
     * Other plugins will NOT be able to see, format, or cancel these messages.
     * <p>
     * This setting is opt-in (default: false) and requires a server restart to change.
     */
    public final static SystemArgument<Boolean> USE_CHAT_FAST_BYPASS = FACTORY.create(string("ChatFastBypass", true));
}
