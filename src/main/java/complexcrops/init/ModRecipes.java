package complexcrops.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModRecipes 
{
	public static final List<IRecipe> RECIPES = new ArrayList<IRecipe>();
	
	public static void init()
	{
	//smelting recipes
		//popcorn recipe
		GameRegistry.addSmelting(ModItems.CORN_SEEDS, new ItemStack(ModItems.POPCORN, 1), 0.1F);
		//cooked corn recipe
		GameRegistry.addSmelting(ModItems.CORN_COB, new ItemStack(ModItems.COOKED_CORN, 1), 0.35F);
	}
}
