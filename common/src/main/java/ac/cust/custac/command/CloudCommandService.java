package ac.cust.custac.command;

import ac.cust.custac.command.commands.*;
import ac.cust.custac.command.handler.CustACCommandFailureHandler;
import ac.cust.custac.platform.api.command.CommandService;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.utils.anticheat.MessageUtil;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.processors.requirements.RequirementApplicable;
import org.incendo.cloud.processors.requirements.RequirementApplicable.RequirementApplicableFactory;
import org.incendo.cloud.processors.requirements.RequirementPostprocessor;
import org.incendo.cloud.processors.requirements.Requirements;

import java.util.function.Function;
import java.util.function.Supplier;

public class CloudCommandService implements CommandService {

    public static final CloudKey<Requirements<Sender, SenderRequirement>> REQUIREMENT_KEY
            = CloudKey.of("requirements", new TypeToken<>() {});

    public static final RequirementApplicableFactory<Sender, SenderRequirement> REQUIREMENT_FACTORY
            = RequirementApplicable.factory(REQUIREMENT_KEY);

    private boolean commandsRegistered = false;

    private final Supplier<CommandManager<Sender>> commandManagerSupplier;
    private final CloudCommandAdapter commandAdapter;

    public CloudCommandService(Supplier<CommandManager<Sender>> commandManagerSupplier, CloudCommandAdapter commandAdapter) {
        this.commandManagerSupplier = commandManagerSupplier;
        this.commandAdapter = commandAdapter;
    }

    public void registerCommands() {
        if (commandsRegistered) return;
        CommandManager<Sender> commandManager = commandManagerSupplier.get();
        new CustACPerf().register(commandManager, commandAdapter);
        new CustACDebug().register(commandManager, commandAdapter);
        new CustACAlerts().register(commandManager, commandAdapter);
        new CustACProfile().register(commandManager, commandAdapter);
        new CustACSendAlert().register(commandManager, commandAdapter);
        new CustACHelp().register(commandManager, commandAdapter);
        new CustACHistory().register(commandManager, commandAdapter);
        new CustACReload().register(commandManager, commandAdapter);
        new CustACSpectate().register(commandManager, commandAdapter);
        new CustACStopSpectating().register(commandManager, commandAdapter);
        new CustACLog().register(commandManager, commandAdapter);
        new CustACVerbose().register(commandManager, commandAdapter);
        new CustACVersion().register(commandManager, commandAdapter);
        new CustACDump().register(commandManager, commandAdapter);
        new CustACBrands().register(commandManager, commandAdapter);
        new CustACList().register(commandManager, commandAdapter);
        new CustACTestWebhook().register(commandManager, commandAdapter);

        final RequirementPostprocessor<Sender, SenderRequirement>
                senderRequirementPostprocessor = RequirementPostprocessor.of(
                REQUIREMENT_KEY,
                new CustACCommandFailureHandler()
        );
        commandManager.registerCommandPostProcessor(senderRequirementPostprocessor);
        registerExceptionHandler(commandManager, InvalidSyntaxException.class, e -> MessageUtil.miniMessage(e.correctSyntax()));
        commandsRegistered = true;
    }

    protected <E extends Exception> void registerExceptionHandler(CommandManager<Sender> commandManager, Class<E> ex, Function<E, ComponentLike> toComponent) {
        commandManager.exceptionController().registerHandler(ex,
                (c) -> c.context().sender().sendMessage(toComponent.apply(c.exception()).asComponent().colorIfAbsent(NamedTextColor.RED))
        );
    }
}
