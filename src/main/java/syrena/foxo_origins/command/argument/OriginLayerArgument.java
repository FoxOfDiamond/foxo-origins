package syrena.foxo_origins.command.argument;


import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import io.github.eggohito.kaya.context.ContextValidatable;
import io.github.eggohito.kaya.context.ContextValidator;
import io.github.eggohito.kaya.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import syrena.foxo_origins.origin_layer.OriginLayer;
import syrena.foxo_origins.origin_layer.OriginLayerManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record OriginLayerArgument(boolean allowTags) implements ArgumentType<OriginLayerArgument.Result> {

    @Override
    public OriginLayerArgument.Result parse(StringReader reader) throws CommandSyntaxException {

        if (reader.canRead() && reader.peek() == '#' && allowTags()) {

            reader.skip();
            ResourceLocation id = ResourceLocation.read(reader);

            return new OriginLayerArgument.Result.Collection(TagKey.create(FoxoOriginsRegistry.ORIGIN_LAYER, id));

        }

        else {
            ResourceLocation id = ResourceLocation.read(reader);
            return new OriginLayerArgument.Result.Singleton(id);
        }

    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

        if (allowTags()) {
            SharedSuggestionProvider.suggestResource(OriginLayerManager.getInstance().tags(), builder, "#");
        }

        return SharedSuggestionProvider.suggest(OriginLayerManager.getInstance().keys(), builder, ResourceLocation::toString, id -> Component.literal(id.toString()));

    }

    public static OriginLayerArgument originLayer() {
        return new OriginLayerArgument(false);
    }

    public static OriginLayerArgument originLayerOrTag() {
        return new OriginLayerArgument(true);
    }

    public static OriginLayerArgument.Result getResult(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, OriginLayerArgument.Result.class);
    }

    public static List<OriginLayer> getOriginLayers(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, OriginLayerArgument.Result.class).get();
    }

    public static OriginLayer getOriginLayer(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return switch (getResult(context, name)) {
            case OriginLayerArgument.Result.Singleton singleton ->
                    singleton.get().getFirst();
            case OriginLayerArgument.Result.Collection collection ->
                    throw MiscUtil.createCommandException(() -> "Expected an origin layer, but got tag \"#" + collection.id() + "\"");
        };
    }

    public static List<OriginLayer> getTag(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return switch (getResult(context, name)) {
            case OriginLayerArgument.Result.Singleton singleton ->
                    throw MiscUtil.createCommandException(() -> "Expected a tag, but got origin layer " + singleton.id() + "!");
            case OriginLayerArgument.Result.Collection collection ->
                    collection.get();
        };
    }

    public static Either<OriginLayerArgument.Result.Singleton, OriginLayerArgument.Result.Collection> getOriginLayerOrTag(CommandContext<CommandSourceStack> context, String name) {
        return switch (getResult(context, name)) {
            case OriginLayerArgument.Result.Singleton singleton ->
                    Either.left(singleton);
            case OriginLayerArgument.Result.Collection collection ->
                    Either.right(collection);
        };
    }

    public sealed interface Result extends ContextValidatable permits OriginLayerArgument.Result.Singleton, OriginLayerArgument.Result.Collection {

        List<OriginLayer> get() throws CommandSyntaxException;

        record Singleton(ResourceLocation id) implements OriginLayerArgument.Result {

            @Override
            public List<OriginLayer> get() throws CommandSyntaxException {
                return List.of(OriginLayerManager.getInstance().getAsResult(id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error)));
            }

            @Override
            public void validate(ContextValidator validator) {
                OriginLayerManager.getInstance().getAsResult(id()).resultOrPartial(validator::reportProblem);
            }

        }

        record Collection(TagKey<OriginLayer> tag) implements OriginLayerArgument.Result {

            @Override
            public List<OriginLayer> get() throws CommandSyntaxException {
                return OriginLayerManager.getInstance().getTagAsResult(this.id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
            }

            @Override
            public void validate(ContextValidator validator) {
                OriginLayerManager.getInstance().getTagAsResult(this.id()).ifError(error -> validator.reportProblem(error.message()));
            }

            public ResourceLocation id() {
                return tag().location();
            }

        }

    }

    public enum Info implements ArgumentTypeInfo<OriginLayerArgument, OriginLayerArgument.Template> {

        INSTANCE;

        @Override
        public void serializeToNetwork(OriginLayerArgument.Template template, FriendlyByteBuf buf) {
            buf.writeBoolean(template.allowTags());
        }

        @Override
        public @NotNull OriginLayerArgument.Template deserializeFromNetwork(FriendlyByteBuf buf) {
            return new OriginLayerArgument.Template(this, buf.readBoolean());
        }

        @Override
        public void serializeToJson(OriginLayerArgument.Template template, JsonObject jsonObject) {
            jsonObject.addProperty("allow_tags", template.allowTags());
        }

        @Override
        public @NotNull OriginLayerArgument.Template unpack(OriginLayerArgument argumentType) {
            return new OriginLayerArgument.Template(this, argumentType.allowTags());
        }

    }

    public record Template(OriginLayerArgument.Info type, boolean allowTags) implements ArgumentTypeInfo.Template<OriginLayerArgument> {

        @Override
        public @NotNull OriginLayerArgument instantiate(CommandBuildContext commandBuildContext) {
            return new OriginLayerArgument(allowTags());
        }

    }
}
