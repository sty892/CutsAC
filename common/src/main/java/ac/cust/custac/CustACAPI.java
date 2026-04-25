package ac.cust.custac;

import ac.grim.grimac.api.event.EventBus;
import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.internal.plugin.resolver.GrimExtensionManager;
import ac.grim.grimac.internal.event.OptimizedEventBus;
import ac.cust.custac.manager.AlertManagerImpl;
import ac.cust.custac.manager.DiscordManager;
import ac.cust.custac.manager.InitManager;
import ac.cust.custac.manager.SpectateManager;
import ac.cust.custac.manager.TickManager;
import ac.cust.custac.manager.config.BaseConfigManager;
import ac.cust.custac.manager.init.Initable;
import ac.cust.custac.manager.suspect.SuspectManager;
import ac.cust.custac.manager.violationdatabase.ViolationDatabaseManager;
import ac.cust.custac.platform.api.Platform;
import ac.cust.custac.platform.api.PlatformLoader;
import ac.cust.custac.platform.api.PlatformServer;
import ac.cust.custac.platform.api.command.CommandService;
import ac.cust.custac.platform.api.manager.ItemResetHandler;
import ac.cust.custac.platform.api.manager.MessagePlaceHolderManager;
import ac.cust.custac.platform.api.manager.PermissionRegistrationManager;
import ac.cust.custac.platform.api.manager.PlatformPluginManager;
import ac.cust.custac.platform.api.player.PlatformPlayerFactory;
import ac.cust.custac.platform.api.scheduler.PlatformScheduler;
import ac.cust.custac.platform.api.sender.SenderFactory;
import ac.cust.custac.utils.anticheat.PlayerDataManager;
import ac.cust.custac.utils.common.arguments.CommonCustACArguments;
import ac.cust.custac.utils.reflection.ReflectionUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
public final class CustACAPI {
    public static final CustACAPI INSTANCE = new CustACAPI();

    @Getter
    private final Platform platform = detectPlatform();
    private final BaseConfigManager configManager;
    private final AlertManagerImpl alertManager;
    private final SpectateManager spectateManager;
    private final DiscordManager discordManager;
    private final SuspectManager suspectManager;
    private final PlayerDataManager playerDataManager;
    private final TickManager tickManager;
    private final GrimExtensionManager extensionManager;
    private final EventBus eventBus;
    private final CustACExternalAPI externalAPI;
    private ViolationDatabaseManager violationDatabaseManager;
    private PlatformLoader loader;
    @Getter
    private InitManager initManager;
    private boolean initialized = false;

    private CustACAPI() {
        this.configManager = new BaseConfigManager();
        this.alertManager = new AlertManagerImpl();
        this.spectateManager = new SpectateManager();
        this.discordManager = new DiscordManager();
        this.suspectManager = new SuspectManager();
        this.playerDataManager = new PlayerDataManager();
        this.tickManager = new TickManager();
        this.extensionManager = new GrimExtensionManager();
        this.eventBus = new OptimizedEventBus(extensionManager);
        this.externalAPI = new CustACExternalAPI(this);
    }

    // the order matters
    private static Platform detectPlatform() {
        Platform override = CommonCustACArguments.PLATFORM_OVERRIDE.value();
        if (override != null) return override;
        if (ReflectionUtils.hasClass("io.papermc.paper.threadedregions.RegionizedServer")) return Platform.FOLIA;
        if (ReflectionUtils.hasClass("org.bukkit.Bukkit")) return Platform.BUKKIT;
        if (ReflectionUtils.hasClass("net.fabricmc.loader.api.FabricLoader")) return Platform.FABRIC;
        throw new IllegalStateException("Unknown platform!");
    }

    public void load(PlatformLoader platformLoader, Initable... platformSpecificInitables) {
        this.loader = platformLoader;
        this.violationDatabaseManager = new ViolationDatabaseManager(getGrimPlugin());
        this.initManager = new InitManager(loader.getPacketEvents(), platformSpecificInitables);
        this.initManager.load();
        this.initialized = true;
    }

    public void start() {
        checkInitialized();
        initManager.start();
    }

    public void stop() {
        checkInitialized();
        initManager.stop();
    }

    public PlatformScheduler getScheduler() {
        return loader.getScheduler();
    }

    public PlatformPlayerFactory getPlatformPlayerFactory() {
        return loader.getPlatformPlayerFactory();
    }

    public GrimPlugin getGrimPlugin() {
        return loader.getPlugin();
    }

    public SenderFactory<?> getSenderFactory() {
        return loader.getSenderFactory();
    }

    public ItemResetHandler getItemResetHandler() {
        return loader.getItemResetHandler();
    }

    public PlatformPluginManager getPluginManager() {
        return loader.getPluginManager();
    }

    public PlatformServer getPlatformServer() {
        return loader.getPlatformServer();
    }

    public @NotNull MessagePlaceHolderManager getMessagePlaceHolderManager() {
        return loader.getMessagePlaceHolderManager();
    }

    public CommandService getCommandService() {
        return loader.getCommandService();
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("CustACAPI has not been initialized!");
        }
    }

    public PermissionRegistrationManager getPermissionManager() {
        return loader.getPermissionManager();
    }

    public GrimExtensionManager getExtensionManager() {
        return extensionManager;
    }
}
