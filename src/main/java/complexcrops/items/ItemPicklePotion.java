package complexcrops.items;

import java.util.List;

import javax.annotation.Nullable;

import complexcrops.ComplexCrops;
import complexcrops.init.ModItems;
import complexcrops.util.IHasModel;
import complexcrops.util.PicklePotionUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPicklePotion extends ItemFood implements IHasModel 
{		
    public ItemPicklePotion(String name, Potion potion, int duration, int amplifier)
	{
	    super(6, 0.6F, false);
        this.maxStackSize = 16;
	    this.setCreativeTab(CreativeTabs.FOOD);
        this.setAlwaysEdible();
        setUnlocalizedName(name);
		setRegistryName(name);
        
        ModItems.ITEMS.add(this);
	}
	
	@SideOnly(Side.CLIENT)
    public ItemStack getDefaultInstance()
    {
		return new ItemStack(ModItems.PICKLE);
    }
    
    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {   
        if (entityLiving instanceof EntityPlayer)
        {
        	 //food part
        	EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            entityplayer = (EntityPlayer)entityLiving;
            entityplayer.getFoodStats().addStats(this, stack);
            worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            
            //potion part
            if (!worldIn.isRemote)
            {
                for (PotionEffect potioneffect : PicklePotionUtils.getEffectsFromStack(stack))
                {
                    if (potioneffect.getPotion().isInstant())
                    {
                    	potioneffect.getPotion().affectEntity(entityplayer, entityplayer, entityLiving, potioneffect.getAmplifier(), 1.0D);
                    }
                    else
                    {
                        entityLiving.addPotionEffect(new PotionEffect(potioneffect));
                    }
                }
            }

            entityplayer.addStat(StatList.getObjectUseStats(this));
        }
        
        //if the entity is not in creative, shrink the stack
        if (!(entityLiving instanceof EntityPlayer && ((EntityPlayer)entityLiving).capabilities.isCreativeMode))
        {
        	stack.shrink(1);
        }
        return stack;
    }
    
    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
    
    public String getItemStackDisplayName(ItemStack stack)
    {
        return I18n.translateToLocal(PotionUtils.getPotionFromItem(stack).getNamePrefixed("potion.effect."));
    }
    
    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        PicklePotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
    }
    
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            for (PotionType potiontype : PotionType.REGISTRY)
            {
                if (potiontype != PotionTypes.EMPTY && potiontype != PotionTypes.WATER && potiontype != PotionTypes.MUNDANE && potiontype != PotionTypes.THICK && potiontype != PotionTypes.AWKWARD)
                {
                	ItemStack newItemStack = new ItemStack(this);
                    NBTTagCompound nbttagcompound1 = newItemStack.hasTagCompound() ? newItemStack.getTagCompound() : new NBTTagCompound();
                    nbttagcompound1.setLong("CustomPotionColor", PicklePotionUtils.getPotionColor(potiontype));
                    newItemStack.setTagCompound(nbttagcompound1);
                    PicklePotionUtils.appendEffects(newItemStack, potiontype.getEffects());
                    PicklePotionUtils.setName(I18n.translateToLocal(potiontype.getNamePrefixed("potion.effect.")), newItemStack);
                	items.add(newItemStack);
                }
            }
        }
    }
	
	@Override
	public void registerModels()
	{
		ComplexCrops.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
