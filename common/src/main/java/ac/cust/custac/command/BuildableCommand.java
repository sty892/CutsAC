package ac.cust.custac.command;

import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.sender.Sender;
import org.incendo.cloud.CommandManager;

public interface BuildableCommand {
    void register(CommandManager<Sender> manager, CloudCommandAdapter adapter);
}
