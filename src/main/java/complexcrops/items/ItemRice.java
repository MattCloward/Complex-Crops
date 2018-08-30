package complexcrops.items;

import complexcrops.ComplexCrops;
import complexcrops.init.ModBlocks;
import complexcrops.init.ModItems;
import complexcrops.util.IHasModel;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class ItemRice extends ItemFood implements IPlantable, IHasModel
{
	private final Block crops;
    /** Block ID of the soil this seed food should be planted on. */
    private Block soilId;
    
    public ItemRice(String name, int healAmount, float saturation, Block crops, Block soil)
    {
        super(healAmount, saturation, false);
        this.crops = crops;
        this.soilId = soil;
        this.setCreativeTab(CreativeTabs.MATERIALS);
        setUnlocalizedName(name);
		setRegistryName(name);
        
		ModItems.ITEMS.add(this);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand handIn, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	ItemStack itemstack = playerIn.getHeldItem(handIn);
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);

        if (raytraceresult == null)
        {
            return EnumActionResult.PASS;
        }
        else
        {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!worldIn.isBlockModifiable(playerIn, blockpos) || !playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack))
                {
                    return EnumActionResult.FAIL;
                }

                BlockPos blockpos1 = blockpos.up();
                BlockPos blockpos2 = blockpos.down();
                IBlockState iblockstate = worldIn.getBlockState(blockpos);

                //if looking at top of water and air is above water and water is only 1 block deep
                if (iblockstate.getMaterial() == Material.WATER && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0 && worldIn.isAirBlock(blockpos1) && worldIn.getBlockState(blockpos2).getMaterial() != Material.WATER)
                {
                    // special case for handling block placement- adapted from code in lily pads
                    net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(worldIn, blockpos1);
                    worldIn.setBlockState(blockpos1, ModBlocks.RICE.getDefaultState());
                    if (net.minecraftforge.event.ForgeEventFactory.onPlayerBlockPlace(playerIn, blocksnapshot, net.minecraft.util.EnumFacing.UP, handIn).isCanceled())
                    {
                        blocksnapshot.restore(true, false);
                        return EnumActionResult.FAIL;
                    }

                    worldIn.setBlockState(blockpos1, ModBlocks.RICE.getDefaultState(), 11);

                    if (playerIn instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)playerIn, blockpos1, itemstack);
                    }

                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        itemstack.shrink(1);
                    }

                    playerIn.addStat(StatList.getObjectUseStats(this));
                    return EnumActionResult.SUCCESS;
                }
            }
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return net.minecraftforge.common.EnumPlantType.Water;
    }

    @Override
    public net.minecraft.block.state.IBlockState getPlant(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return this.crops.getDefaultState();
    }
    
	@Override
	public void registerModels()
	{
		ComplexCrops.proxy.registerItemRenderer(this, 0, "inventory");
	}
}