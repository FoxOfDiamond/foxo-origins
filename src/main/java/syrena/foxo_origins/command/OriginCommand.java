package syrena.foxo_origins.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import syrena.foxo_origins.FoxoOrigins;
import syrena.foxo_origins.attachment.entity.FoxoOriginsAttachment;
import syrena.foxo_origins.command.argument.OriginArgument;
import syrena.foxo_origins.command.argument.OriginLayerArgument;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.*;

import static net.minecraft.commands.Commands.*;

public class OriginCommand {
    public static final ResourceLocation DEFAULT_LAYER = ResourceLocation.fromNamespaceAndPath("neo-origins","foxo_origins");
    public static void register(CommandNode<CommandSourceStack> rootNode, CommandBuildContext context) {

        CommandNode<CommandSourceStack> baseNode = literal("origin")
                .requires(source -> source.hasPermission(2))
                .build();

        baseNode.addChild(SetArgument.node(context));
        baseNode.addChild(GetArgument.node(context));

        rootNode.addChild(baseNode);

    }

    static final class SetArgument {
        static CommandNode<CommandSourceStack> node(CommandBuildContext context) {
            var node = literal("set")
                    .then(argument("targets", EntityArgument.players())
                            .then(argument("origin", OriginArgument.origin())
                                    .executes(SetArgument::setWithDefaultLayer)
                                    .then(argument("layer", OriginLayerArgument.originLayer())
                                            .executes(SetArgument::setWithLayer))));

            return node.build();
        }
        static int setWithDefaultLayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            OriginLayer layer = OriginLayerManager.getInstance().get(DEFAULT_LAYER);
            return setWithLayer(context,layer);
        }
        static int setWithLayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

            OriginLayer layer = OriginLayerArgument.getOriginLayer(context,"layer");
            return setWithLayer(context,layer);
        }
        static int setWithLayer(CommandContext<CommandSourceStack> context, OriginLayer layer) throws CommandSyntaxException {
            CommandSourceStack source = context.getSource();

            Origin origin = OriginArgument.getOrigin(context,"origin");
            List<ServerPlayer> targets = new ObjectArrayList<>(EntityArgument.getPlayers(context,"targets"));
            List<ServerPlayer> processedTargets = new ArrayList<>();
            for(ServerPlayer player: targets){
                FoxoOriginsAttachment attachment = player.getAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT);
                Origin existingOrigin = null;
                if (attachment != null) existingOrigin = attachment.getMatchingOrigin(layer);
                if (existingOrigin != null && existingOrigin.equals(origin)) continue;

                FoxoOriginsAttachment newAttachment = attachment.newWithOriginSet(layer,origin);
                player.setAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT, newAttachment);

                processedTargets.add(player);
            }
            if (processedTargets.isEmpty()) {
                if (targets.isEmpty()) {
                    source.sendFailure(Component.translatableEscape("commands.foxo-origins.origin.failure.no_targets","set"));
                } else if (targets.size() == 1) {
                    source.sendFailure(Component.translatableEscape("commands.foxo-origins.origin.set.failure.single",targets.getFirst().getName(),origin.name(),layer.name()));
                } else {
                    source.sendFailure(Component.translatableEscape("commands.foxo-origins.origin.set.failure.multiple",targets.size(),origin.name(),layer.name()));
                }
            } else if (processedTargets.size() == 1){
                source.sendSuccess(() -> Component.translatableEscape("commands.foxo-origins.origin.set.success.single",processedTargets.getFirst().getName(),layer.name(),origin.name()),true);
            } else {
                source.sendSuccess(() -> Component.translatableEscape("commands.foxo-origins.origin.set.success.multiple",processedTargets.size(),layer.name(),origin.name()),true);
            }
            return processedTargets.size();
        }
    }

    static final class GetArgument {
        static CommandNode<CommandSourceStack> node(CommandBuildContext context) {
            var node = literal("get")
                    .then(argument("target", EntityArgument.player())
                            .executes(GetArgument::getWithDefaultLayer)
                            .then(argument("layer", OriginLayerArgument.originLayer())
                                    .executes(GetArgument::getWithLayer)));

            return node.build();
        }
        static int getWithDefaultLayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            OriginLayer layer = OriginLayerManager.getInstance().get(DEFAULT_LAYER);
            return getWithLayer(context,layer);
        }
        static int getWithLayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

            OriginLayer layer = OriginLayerArgument.getOriginLayer(context,"layer");
            return getWithLayer(context,layer);
        }
        static int getWithLayer(CommandContext<CommandSourceStack> context, OriginLayer layer) throws CommandSyntaxException {
            CommandSourceStack source = context.getSource();

            ServerPlayer player = EntityArgument.getPlayer(context,"target");

            FoxoOriginsAttachment attachment = player.getAttached(FoxoOriginsRegistry.ORIGIN_ATTACHMENT);

            if (attachment == null) {
                source.sendFailure(Component.translatableEscape("commands.foxo-origins.origin.get.failure.no_attachment",player.getName(),layer));
                return 0;
            }
            Origin origin = attachment.getMatchingOrigin(layer);
            if (origin == null) {
                source.sendFailure(Component.translatableEscape("commands.foxo-origins.origin.get.failure.not_set",player.getName()));
                return 0;
            }
            source.sendSuccess(() -> Component.translatableEscape("commands.foxo-origins.origin.get.info",player.getName(),origin.name(),layer.name()),true);
            return 1;
        }
    }
}
