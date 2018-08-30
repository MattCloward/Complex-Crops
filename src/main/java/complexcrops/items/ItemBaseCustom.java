package complexcrops.items;

import complexcrops.ComplexCrops;
import complexcrops.init.ModItems;
import complexcrops.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBaseCustom extends Item implements IHasModel	
{
	public ItemBaseCustom(String name, CreativeTabs creativeTab)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(creativeTab);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public void registerModels()
	{
		ComplexCrops.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
