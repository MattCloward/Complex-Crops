package complexcrops.items;

import complexcrops.ComplexCrops;
import complexcrops.init.ModItems;
import complexcrops.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

public class ItemCustomFood extends ItemFood implements IHasModel	
{
	public ItemCustomFood(String name, int amount, float saturation, boolean isWolfFood)
	{
		super(amount, saturation, isWolfFood);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.FOOD);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public void registerModels()
	{
		ComplexCrops.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
