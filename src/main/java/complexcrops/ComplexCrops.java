package complexcrops;

import complexcrops.init.ModItems;
import complexcrops.init.ModRecipes;
import complexcrops.proxy.CommonProxy;
import complexcrops.util.Reference;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class ComplexCrops {

	@Instance
	public static ComplexCrops instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@EventHandler
	public static void PreInit(FMLPreInitializationEvent event)
	{
		//add smelting recipes
		ModRecipes.init();
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		proxy.init();
		//seeds are dropped by breaking tallgrass
		MinecraftForge.addGrassSeed(new ItemStack(ModItems.CORN_SEEDS), 2);
		MinecraftForge.addGrassSeed(new ItemStack(ModItems.RICE), 2);
		MinecraftForge.addGrassSeed(new ItemStack(ModItems.CUCUMBER_SEEDS), 2);
	}
	
	@EventHandler
	public static void PostInit(FMLPostInitializationEvent event)
	{
		
	}
}
