package complexcrops.proxy;

import complexcrops.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy
{
	public void registerItemRenderer(Item item, int meta, String id)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}
	
	public void init()
	{		
		//pickle colors registerer
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor()
       		{
            		public int colorMultiplier(ItemStack stack, int tintIndex)
            		{
                		return tintIndex == 0 ? PotionUtils.getColor(stack) : -1;
            		}
        	}, ModItems.PICKLE_POTION);
	}
}
