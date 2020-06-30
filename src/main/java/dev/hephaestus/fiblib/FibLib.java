package dev.hephaestus.fiblib;

import dev.hephaestus.fiblib.blocks.BlockFib;
import dev.hephaestus.fiblib.blocks.BlockTracker;
import dev.hephaestus.fiblib.blocks.LookupTable;
import dev.hephaestus.fiblib.items.ItemFib;
import dev.hephaestus.fiblib.items.TemplateItemFib;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.ChunkComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class FibLib implements ModInitializer {
	public static final String MOD_ID = "fiblib";
	private static final String MOD_NAME = "FibLib";

	private static final Logger LOGGER = LogManager.getLogger();
    public static boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

	public static void log(String msg) {
		log("%s", msg);
	}

	public static void log(String format, Object... args) {
		LOGGER.info(String.format("[%s] %s", MOD_NAME, String.format(format, args)));
	}

	public static void debug(String msg) {
		debug("%s", msg);
	}

	public static void debug(String format, Object... args) {
		if (DEBUG) LOGGER.info(String.format("[%s] %s", MOD_NAME, String.format(format, args)));
	}

	@Override
	public void onInitialize() {
		Blocks.initialize();
		FibLib.log("Initialized");
		Items.register(new TemplateItemFib(new ItemStack(net.minecraft.item.Items.APPLE), new ItemStack(net.minecraft.item.Items.DIAMOND)) {
			@Override
			public boolean condition(ServerPlayerEntity player) {
				return true;
			}
		});
	}

	public static class Blocks {
		public static final ComponentType<BlockTracker> TRACKER =
				ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier("fiblib:blocks.tracker"), BlockTracker.class);

		public static void initialize() {
			ChunkComponentCallback.EVENT.register((chunk, components) -> components.put(TRACKER, new BlockTracker(chunk)));
		}

		private static final LookupTable LOOKUPS = new LookupTable();

		private static final HashMap<BlockState, BlockFib> FIBS = new HashMap<>();

		public static void tick() {
			LOOKUPS.update();
		}

		public static int getVersion() {return LOOKUPS.getVersion();}

		// API methods
		/**
		 * Use this function to register Fibs that aren't created with scripts.
		 *
		 * @param fib   the fib itself
		 */
		public static void register(BlockFib fib) {
			FIBS.put(fib.getInput(), fib);
			FibLib.log("Registered a BlockFib for %s", fib.getInput().getBlock().getTranslationKey());
		}

		/**
		 * Returns the result of any fibs on a given BlockState
		 *
		 * @param state  the state of the block we're inquiring about. Note that because this is passed to a BlockFib, other
		 *               aspects of the state than the Block may be used in determining the output
		 * @param player the player who we will be fibbing to
		 * @return the result of the fib. This is what the player will get told the block is
		 */
		public static BlockState get(BlockState state, @Nullable ServerPlayerEntity player) {
			if (!FIBS.containsKey(state)) return state;

			BlockFib fib = FIBS.get(state);

			return LOOKUPS.get(fib, state, player);
		}

		/**
		 * Returns whether or not a fib exists for the given state.
		 *
		 * @param state the state to inquire about
		 * @return the result of the inquiry
		 */
		public static boolean contains(BlockState state) {
			return FIBS.containsKey(state);
		}
	}

	public static class Items {
		public static final List<ItemFib> FIBS = new ArrayList<>();

		/**
		 * Registers a new item fib.
		 * @param fib The fib to register.
		 */
		public static void register(ItemFib fib) {
			FIBS.add(fib);
		}

		/**
		 * Transforms an ItemStack to respect registered fibs.
		 * @param stack The stack to transform
		 * @param player The player to pass to the fib.
		 * @return The transformed stack. Will be identical to the original stack if no fibs matched.
		 */
		public static ItemStack transform(ItemStack stack, ServerPlayerEntity player) {
			ItemFib fib = get(stack, player);
			if(fib == null)
				return stack;
			return fib.transform(player, stack);
		}

		/**
		 * Gets an ItemFib matching a player and stack.
		 * @param stack The stack to match against the registered fibs.
		 * @param player The player being fibbed to.
		 * @return The fib found, or null if a matching fib is not found.
		 */
		@Nullable
		public static ItemFib get(ItemStack stack, ServerPlayerEntity player) {
			for(ItemFib fib : FIBS) {
				if(fib.matches(player, stack))
					return fib;
			}
			return null;
		}
	}
}
