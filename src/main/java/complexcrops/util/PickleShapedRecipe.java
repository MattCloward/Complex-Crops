package complexcrops.util;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import complexcrops.init.ModItems;
import complexcrops.items.ItemPicklePotion;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * A shaped recipe class that copies a potion's potion effects to the output pickles. The potions effects are reduced somewhat in strength.
 * @author
 */
public class PickleShapedRecipe extends ShapedOreRecipe implements IRecipe
{
	public PickleShapedRecipe(@Nullable final ResourceLocation group, final ItemStack result, final CraftingHelper.ShapedPrimer primer)
	{
		super(group, result, primer);
	}

	@Override
	public ItemStack getCraftingResult(final InventoryCrafting inv)
	{
		ItemStack output = super.getCraftingResult(inv); // Get the default output

		if (!output.isEmpty()) 
		{
			//holder for all potential effects of pickle ingredients in recipe 
			Collection<PotionEffect> pickleEffects = Lists.<PotionEffect>newArrayList();
			//holder for all potential effects of the potion ingredient in recipe 
			Collection<PotionEffect> potionEffects = Lists.<PotionEffect>newArrayList();
			//holder for all potential effects of all the ingredients in recipe 
			Collection<PotionEffect> allEffects = Lists.<PotionEffect>newArrayList();
			//if the potion ingredient has no effect
			boolean noEffectPotion = false;
			//TODO doesn't seem to be carrying over- value does what it should, but is not accurate of name
			//number of times a pickle has been used in a recipe previouslys
			int timesCrafted = 0;
			//the color of the final pickle (addition of all colored ingredients aka: potions and pickles)
			int color = 0;
			
			for (int i = 0; i < inv.getSizeInventory(); i++) // For each slot in the crafting inventory,
			{
				final ItemStack ingredient = inv.getStackInSlot(i); // Get the ingredient in the slot
				
				if (!ingredient.isEmpty()) //If the ingredient is something
				{
					if (ingredient.getItem() instanceof ItemPicklePotion) //If the ingredient is a pickle,
					{
						pickleEffects = PicklePotionUtils.addItemEffectsToList(pickleEffects, ingredient); //Add the pickle's effects to the pickle effects list
						color += PicklePotionUtils.getColor(ingredient); //Add the color of the pickle to the final pickle color
			            NBTTagCompound nbttagcompound = (NBTTagCompound)MoreObjects.firstNonNull(ingredient.getTagCompound(), new NBTTagCompound()); //get and set the timesCrafted
						timesCrafted = nbttagcompound.getInteger("TimesCrafted") <= timesCrafted ? timesCrafted : nbttagcompound.getInteger("TimesCrafted");

					}
					else if (ingredient.getItem() instanceof ItemPotion) //If the ingredient is a potion item,
					{
						PicklePotionUtils.setNameAndLore(ingredient, output); // Clone its name and lore and replace "Potion" with "Pickle" for both upper and lowercase
						if (PicklePotionUtils.getEffectsFromStack(ingredient).isEmpty()) //Store whether this potion had any effects
						{
							noEffectPotion = true;
						}
						else
						{
							potionEffects = PicklePotionUtils.addItemEffectsToList(potionEffects, ingredient); //Add the potion's effects to the potion effects list
							color += PicklePotionUtils.getColor(ingredient); //Add the color of the potion to the final pickle color
						}
					}
				}
			}
			if (noEffectPotion && pickleEffects.isEmpty()) //If neither the pickle ingredients nor the potion had effects
			{
				ItemStack previousOutput = output.copy();
				output = new ItemStack(ModItems.PICKLE, 4); //Make the recipe return regular pickles
				PicklePotionUtils.setNameAndLore(previousOutput, output);
			}
			else
			{
				if (!potionEffects.isEmpty())
				{
					potionEffects = PicklePotionUtils.nerfDuration(potionEffects, 0, PicklePotionUtils.DURATION_MODIFIER); //Divide all potion effect durations by Duration Modifier
					allEffects = PicklePotionUtils.addItemEffectsToList(allEffects, potionEffects); //Add the potion effects to the allEffects list
				}

				if (!pickleEffects.isEmpty())
				{
					pickleEffects = PicklePotionUtils.nerfDuration(pickleEffects, timesCrafted, 0.25); //Divide all pickle effect durations by 4 * timesCrafted
					allEffects = PicklePotionUtils.addItemEffectsToList(allEffects, pickleEffects); //Add the pickle effects to the allEffects list
				}
				
				PicklePotionUtils.addNerfedEffectsToStack(allEffects, output, 0, 1.0); //Then add the pickle effects to the output
				
				PicklePotionUtils.setColor(color, output); //Set the color of the pickle
			}
		}
		return output; // Return the modified output
	}

	@Override
	public String getGroup() 
	{
		return group == null ? "" : group.toString();
	}

	public static class Factory implements IRecipeFactory 
	{
		@Override
		public IRecipe parse(final JsonContext context, final JsonObject json)
		{
			final String group = JsonUtils.getString(json, "group", "");
			final CraftingHelper.ShapedPrimer primer = RecipeUtil.parseShaped(context, json);
			final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

			return new PickleShapedRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
		}
	}
	
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
		return customGetRemainingItems(inv);
    }
	
	/**
     * Returns an empty bottle for all pickles crafted.
     *
     * @param inv Crafting inventory
     * @return Crafting inventory contents after the recipe.
     */
    public static NonNullList<ItemStack> customGetRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++)
        {
        	if (inv.getStackInSlot(i).getItem() instanceof ItemPotion)
        	{
                ret.set(i, new ItemStack(Items.GLASS_BOTTLE));
        	}
        	else
        	{
                ret.set(i, ForgeHooks.getContainerItem(inv.getStackInSlot(i)));
        	}
        }
        return ret;
    }
}
