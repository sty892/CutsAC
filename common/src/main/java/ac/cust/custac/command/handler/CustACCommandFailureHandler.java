package ac.cust.custac.command.handler;

import ac.cust.custac.command.SenderRequirement;
import ac.cust.custac.platform.api.sender.Sender;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.RequirementFailureHandler;
import org.jetbrains.annotations.NotNull;

public class CustACCommandFailureHandler implements RequirementFailureHandler<Sender, SenderRequirement> {
    @Override
    public void handleFailure(@NotNull CommandContext<Sender> context, @NotNull SenderRequirement requirement) {
        context.sender().sendMessage(requirement.errorMessage(context.sender()));
    }
}
