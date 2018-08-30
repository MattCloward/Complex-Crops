package complexcrops.init;

import java.util.ArrayList;
import java.util.List;

import complexcrops.items.ItemBaseCustom;
import complexcrops.items.ItemCustomFood;
import complexcrops.items.ItemCustomSeeds;
import complexcrops.items.ItemPicklePotion;
import complexcrops.items.ItemRice;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;

public class ModItems 
{
	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	//food
	public static final ItemCustomFood CORN_COB = (ItemCustomFood) new ItemCustomFood("corn_cob", 2, 0.2F, false);
	public static final ItemCustomFood COOKED_CORN = (ItemCustomFood)(new ItemCustomFood("cooked_corn", 5, 0.5F, false));
	public static final ItemCustomFood BUTTERED_CORN = (ItemCustomFood)(new ItemCustomFood("buttered_corn", 7, 0.6F, false));
	public static final ItemCustomFood POPCORN = (ItemCustomFood)(new ItemCustomFood("popcorn", 1, 0.1F, false));
	public static final ItemCustomFood BUTTERED_POPCORN = (ItemCustomFood)(new ItemCustomFood("buttered_popcorn", 3, 0.2F, false));
	public static final ItemCustomFood CORNBREAD = (ItemCustomFood)(new ItemCustomFood("cornbread", 5, 0.6F, false));
	public static final ItemCustomFood BUTTERED_CORNBREAD = (ItemCustomFood)(new ItemCustomFood("buttered_cornbread", 7, 0.7F, false));
	public static final ItemCustomFood BUTTERED_BREAD = (ItemCustomFood)(new ItemCustomFood("buttered_bread", 7, 0.7F, false));
	public static final ItemCustomFood CUCUMBER = (ItemCustomFood)(new ItemCustomFood("cucumber", 4, 0.4F, false));

	//seeds
	public static final ItemCustomSeeds CORN_SEEDS = (ItemCustomSeeds)(new ItemCustomSeeds("corn_seeds", ModBlocks.CORN_BOTTOM, Blocks.FARMLAND));
	public static final ItemRice RICE = (ItemRice)(new ItemRice("rice", 2, 0.2F, ModBlocks.RICE, Blocks.WATER));
	public static final ItemCustomSeeds CUCUMBER_SEEDS = (ItemCustomSeeds)(new ItemCustomSeeds("cucumber_seeds", ModBlocks.CUCUMBER_STEM, Blocks.FARMLAND));

	//other
	public static final ItemBaseCustom BUTTER = (ItemBaseCustom) new ItemBaseCustom("butter", CreativeTabs.FOOD);
	
	//pickles and potions!
	public static final ItemCustomFood PICKLE = (ItemCustomFood) new ItemCustomFood("pickle", 6, 0.6F, false);
	public static final ItemPicklePotion PICKLE_POTION = (ItemPicklePotion) new ItemPicklePotion("pickle_potion", MobEffects.SPEED, 3600, 299);
}