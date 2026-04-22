package ac.cust.custac.platform.bukkit;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.CustACExternalAPI;
import ac.grim.grimac.api.CustACAPIProvider;
import ac.grim.grimac.api.GrimAbstractAPI;
import ac.grim.grimac.api.event.EventBus;
import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.cust.custac.command.CloudCommandService;
import ac.grim.grimac.internal.platform.bukkit.resolver.BukkitResolverRegistrar;
import ac.cust.custac.manager.init.Initable;
import ac.cust.custac.manager.init.start.ExemptOnlinePlayersOnReload;
import ac.cust.custac.manager.init.start.StartableInitable;
import ac.cust.custac.platform.api.Platform;
import ac.cust.custac.platform.api.PlatformLoader;
import ac.cust.custac.platform.api.PlatformServer;
import ac.cust.custac.platform.api.command.CommandService;
import ac.cust.custac.platform.api.manager.ItemResetHandler;
import ac.cust.custac.platform.api.manager.MessagePlaceHolderManager;
import ac.cust.custac.platform.api.manager.PlatformPluginManager;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.player.PlatformPlayerFactory;
import ac.cust.custac.platform.api.scheduler.PlatformScheduler;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.platform.api.sender.SenderFactory;
import ac.cust.custac.platform.bukkit.initables.BukkitBStats;
import ac.cust.custac.platform.bukkit.initables.BukkitEventManager;
import ac.cust.custac.platform.bukkit.initables.BukkitTickEndEvent;
import ac.cust.custac.platform.bukkit.manager.BukkitItemResetHandler;
import ac.cust.custac.platform.bukkit.manager.BukkitMessagePlaceHolderManager;
import ac.cust.custac.platform.bukkit.manager.BukkitParserDescriptorFactory;
import ac.cust.custac.platform.bukkit.manager.BukkitPermissionRegistrationManager;
import ac.cust.custac.platform.bukkit.manager.BukkitPlatformPluginManager;
import ac.cust.custac.platform.bukkit.player.BukkitPlatformPlayerFactory;
import ac.cust.custac.platform.bukkit.scheduler.bukkit.BukkitPlatformScheduler;
import ac.cust.custac.platform.bukkit.scheduler.folia.FoliaPlatformScheduler;
import ac.cust.custac.platform.bukkit.sender.BukkitSenderFactory;
import ac.cust.custac.platform.bukkit.utils.placeholder.PlaceholderAPIExpansion;
import ac.cust.custac.utils.anticheat.LogUtil;
import ac.cust.custac.utils.lazy.LazyHolder;
import com.github.retrooper.packetevents.PacketEventsAPI;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.brigadier.BrigadierSetting;
import org.incendo.cloud.brigadier.CloudBrigadierManager;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;


public final class CustACBukkitLoaderPlugin extends JavaPlugin implements PlatformLoader {

    public static CustACBukkitLoaderPlugin LOADER;

    private final LazyHolder<PlatformScheduler> scheduler = LazyHolder.simple(this::createScheduler);
    private final LazyHolder<PacketEventsAPI<?>> packetEvents = LazyHolder.simple(() -> SpigotPacketEventsBuilder.build(this));
    private final LazyHolder<BukkitSenderFactory> senderFactory = LazyHolder.simple(BukkitSenderFactory::new);
    private final LazyHolder<ItemResetHandler> itemResetHandler = LazyHolder.simple(BukkitItemResetHandler::new);
    private final LazyHolder<CommandService> commandService = LazyHolder.simple(this::createCommandService);
    private final CloudCommandAdapter commandAdapter = new BukkitParserDescriptorFactory();

    @Getter private final PlatformPlayerFactory platformPlayerFactory = new BukkitPlatformPlayerFactory();
    @Getter private final PlatformPluginManager pluginManager = new BukkitPlatformPluginManager();
    @Getter private final GrimPlugin plugin;
    @Getter private final PlatformServer platformServer = new BukkitPlatformServer();
    @Getter private final MessagePlaceHolderManager messagePlaceHolderManager = new BukkitMessagePlaceHolderManager();
    @Getter private final BukkitPermissionRegistrationManager permissionManager = new BukkitPermissionRegistrationManager();

    public CustACBukkitLoaderPlugin() {
        BukkitResolverRegistrar registrar = new BukkitResolverRegistrar();
        registrar.registerAll(CustACAPI.INSTANCE.getExtensionManager());
        this.plugin = registrar.resolvePlugin(this);
    }

    @Override
    public void onLoad() {
        LOADER = this;
        CustACAPI.INSTANCE.load(this, this.getBukkitInitTasks());
    }

    private Initable[] getBukkitInitTasks() {
        return new Initable[] {
                new ExemptOnlinePlayersOnReload(),
                new BukkitEventManager(),
                new BukkitTickEndEvent(),
                new BukkitBStats(),
                (StartableInitable) () -> {
                    if (BukkitMessagePlaceHolderManager.hasPlaceholderAPI) {
                        new PlaceholderAPIExpansion().register();
                    }
                }
        };
    }

    @Override
    public void onEnable() {
        CustACAPI.INSTANCE.start();
    }

    @Override
    public void onDisable() {
        CustACAPI.INSTANCE.stop();
    }

    @Override
    public PlatformScheduler getScheduler() {
        return scheduler.get();
    }

    @Override
    public PacketEventsAPI<?> getPacketEvents() {
        return packetEvents.get();
    }

    @Override
    public ItemResetHandler getItemResetHandler() {
        return itemResetHandler.get();
    }

    @Override
    public CommandService getCommandService() {
        return commandService.get();
    }

    @Override
    public SenderFactory<CommandSender> getSenderFactory() {
        return senderFactory.get();
    }

    @Override
    public void registerAPIService() {
        final CustACExternalAPI externalAPI = CustACAPI.INSTANCE.getExternalAPI();
        final EventBus eventBus = externalAPI.getEventBus();
        final ac.grim.grimac.api.plugin.GrimPlugin context = CustACAPI.INSTANCE.getGrimPlugin();

        eventBus.subscribe(context, ac.grim.grimac.api.event.events.GrimJoinEvent.class, (event) -> {
            // ac.cust.custac.api.events.GrimJoinEvent bukkitEvent =
            //        new ac.cust.custac.api.events.GrimJoinEvent(event.getUser());

            // Bukkit.getPluginManager().callEvent(bukkitEvent);
        });

        eventBus.subscribe(context, ac.grim.grimac.api.event.events.GrimQuitEvent.class, (event) -> {
            // ac.cust.custac.api.events.GrimQuitEvent bukkitEvent =
            //        new ac.cust.custac.api.events.GrimQuitEvent(event.getUser());

            // Bukkit.getPluginManager().callEvent(bukkitEvent);
        });

        eventBus.subscribe(context, ac.grim.grimac.api.event.events.GrimReloadEvent.class, (event) -> {
            // ac.cust.custac.api.events.GrimReloadEvent bukkitEvent =
            //        new ac.cust.custac.api.events.GrimReloadEvent(event.isSuccess());

            // Bukkit.getPluginManager().callEvent(bukkitEvent);
        });

        eventBus.subscribe(context, ac.grim.grimac.api.event.events.FlagEvent.class, (event) -> {
            /* ac.cust.custac.api.events.FlagEvent bukkitEvent =
                    new ac.cust.custac.api.events.FlagEvent(
                            event.getUser(),
                            event.getCheck(),
                            event.getVerbose()
                    );

            Bukkit.getPluginManager().callEvent(bukkitEvent);

            if (bukkitEvent.isCancelled()) {
                event.setCancelled(true);
            } */
        });

        eventBus.subscribe(context, ac.grim.grimac.api.event.events.CommandExecuteEvent.class, (event) -> {
            /* ac.cust.custac.api.events.CommandExecuteEvent bukkitEvent =
                    new ac.cust.custac.api.events.CommandExecuteEvent(
                            event.getUser(),
                            event.getCheck(),
                            event.getVerbose(),
                            event.getCommand()
                    );

            Bukkit.getPluginManager().callEvent(bukkitEvent);

            if (bukkitEvent.isCancelled()) {
                event.setCancelled(true);
            } */
        });

        eventBus.subscribe(context, ac.grim.grimac.api.event.events.CompletePredictionEvent.class, (event) -> {
            // Note: New event doesn't have verbose, passing null or check name is standard fallback
            /* ac.cust.custac.api.events.CompletePredictionEvent bukkitEvent =
                    new ac.cust.custac.api.events.CompletePredictionEvent(
                            event.getUser(),
                            event.getCheck(),
                            "",
                            event.getOffset()
                    );

            Bukkit.getPluginManager().callEvent(bukkitEvent);

            if (bukkitEvent.isCancelled()) {
                event.setCancelled(true);
            } */
        });

        // CustACAPIProvider.init(externalAPI);
        Bukkit.getServicesManager().register(GrimAbstractAPI.class, externalAPI, CustACBukkitLoaderPlugin.LOADER, ServicePriority.Normal);
    }

    private PlatformScheduler createScheduler() {
        return CustACAPI.INSTANCE.getPlatform() == Platform.FOLIA ? new FoliaPlatformScheduler() : new BukkitPlatformScheduler();
    }

    private CommandService createCommandService() {
        try {
            return new CloudCommandService(this::createCloudCommandManager, commandAdapter);
        } catch (Throwable t) {
            LogUtil.warn("CRITICAL: Failed to initialize Command Framework. " +
                    "CustAC will continue to run with no commands.", t);
            return () -> {};
        }
    }

    private CommandManager<Sender> createCloudCommandManager() {
        LegacyPaperCommandManager<Sender> manager = new LegacyPaperCommandManager<>(
                this,
                ExecutionCoordinator.simpleCoordinator(),
                senderFactory.get()
        );
        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            try {
                manager.registerBrigadier();
                CloudBrigadierManager<Sender, ?> cbm = manager.brigadierManager();
                cbm.settings().set(BrigadierSetting.FORCE_EXECUTABLE, true);
            } catch (Throwable t) {
                LogUtil.error("Failed to register Brigadier native completions. Falling back to standard completions.", t);
            }
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }
        return manager;
    }

    public BukkitSenderFactory getBukkitSenderFactory() {
        return LOADER.senderFactory.get();
    }
}
