package syrena.foxo_origins.command.argument;


import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import io.github.eggohito.neo_apoli.context.ContextValidatable;
import io.github.eggohito.neo_apoli.context.ContextValidator;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import syrena.foxo_origins.origin.Origin;
import syrena.foxo_origins.origin.OriginManager;
import syrena.foxo_origins.registry.FoxoOriginsRegistry;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record OriginArgument(boolean allowTags) implements ArgumentType<OriginArgument.Result> {

    @Override
    public OriginArgument.Result parse(StringReader reader) throws CommandSyntaxException {

        if (reader.canRead() && reader.peek() == '#' && allowTags()) {

            reader.skip();
            ResourceLocation id = ResourceLocation.read(reader);

            return new OriginArgument.Result.Collection(TagKey.create(FoxoOriginsRegistry.ORIGIN, id));

        }

        else {
            ResourceLocation id = ResourceLocation.read(reader);
            return new OriginArgument.Result.Singleton(id);
        }

    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

        if (allowTags()) {
            SharedSuggestionProvider.suggestResource(OriginManager.getInstance().tags(), builder, "#");
        }

        return SharedSuggestionProvider.suggest(OriginManager.getInstance().keys(), builder, ResourceLocation::toString, id -> Component.literal(id.toString()));

    }

    public static OriginArgument origin() {
        return new OriginArgument(false);
    }

    public static OriginArgument originOrTag() {
        return new OriginArgument(true);
    }

    public static OriginArgument.Result getResult(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, OriginArgument.Result.class);
    }

    public static List<Holder<Origin>> getOrigins(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, OriginArgument.Result.class).get();
    }

    public static Holder<Origin> getOrigin(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return switch (getResult(context, name)) {
            case OriginArgument.Result.Singleton singleton ->
                    singleton.get().getFirst();
            case OriginArgument.Result.Collection collection ->
                    throw MiscUtil.createCommandException(() -> "Expected an origin, but got tag \"#" + collection.id() + "\"");
        };
    }

    public static List<Holder<Origin>> getTag(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return switch (getResult(context, name)) {
            case OriginArgument.Result.Singleton singleton ->
                    throw MiscUtil.createCommandException(() -> "Expected a tag, but got origin " + singleton.id() + "!");
            case OriginArgument.Result.Collection collection ->
                    collection.get();
        };
    }

    public static Either<OriginArgument.Result.Singleton, OriginArgument.Result.Collection> getOriginOrTag(CommandContext<CommandSourceStack> context, String name) {
        return switch (getResult(context, name)) {
            case OriginArgument.Result.Singleton singleton ->
                    Either.left(singleton);
            case OriginArgument.Result.Collection collection ->
                    Either.right(collection);
        };
    }

    public sealed interface Result extends ContextValidatable permits OriginArgument.Result.Singleton, OriginArgument.Result.Collection {

        List<Holder<Origin>> get() throws CommandSyntaxException;

        record Singleton(ResourceLocation id) implements OriginArgument.Result {

            @Override
            public List<Holder<Origin>> get() throws CommandSyntaxException {
                return List.of(OriginManager.getInstance().getAsResult(id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error)));
            }

            @Override
            public void validate(ContextValidator validator) {
                OriginManager.getInstance().getAsResult(id()).resultOrPartial(validator::reportProblem);
            }

        }

        record Collection(TagKey<Origin> tag) implements OriginArgument.Result {

            @Override
            public List<Holder<Origin>> get() throws CommandSyntaxException {
                return OriginManager.getInstance().getTagAsResult(this.id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
            }

            @Override
            public void validate(ContextValidator validator) {
                OriginManager.getInstance().getTagAsResult(this.id()).ifError(error -> validator.reportProblem(error.message()));
            }

            public ResourceLocation id() {
                return tag().location();
            }

        }

    }

    public enum Info implements ArgumentTypeInfo<OriginArgument, OriginArgument.Template> {

        INSTANCE;

        @Override
        public void serializeToNetwork(OriginArgument.Template template, FriendlyByteBuf buf) {
            buf.writeBoolean(template.allowTags());
        }

        @Override
        public @NotNull OriginArgument.Template deserializeFromNetwork(FriendlyByteBuf buf) {
            return new OriginArgument.Template(this, buf.readBoolean());
        }

        @Override
        public void serializeToJson(OriginArgument.Template template, JsonObject jsonObject) {
            jsonObject.addProperty("allow_tags", template.allowTags());
        }

        @Override
        public @NotNull OriginArgument.Template unpack(OriginArgument argumentType) {
            return new OriginArgument.Template(this, argumentType.allowTags());
        }

    }

    public record Template(OriginArgument.Info type, boolean allowTags) implements ArgumentTypeInfo.Template<OriginArgument> {

        @Override
        public @NotNull OriginArgument instantiate(CommandBuildContext commandBuildContext) {
            return new OriginArgument(allowTags());
        }

    }
}
