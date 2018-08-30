package complexcrops.blocks;

import java.util.Random;

import complexcrops.init.ModBlocks;
import complexcrops.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCucumberStem extends BlockStem implements IGrowable
{
	public static final PropertyInteger CUCUMBER_AGE = PropertyInteger.create("age", 0, 7);
    public static final PropertyDirection FACING = BlockTorch.FACING;
    private final Block crop;
    protected static final AxisAlignedBB[] STEM_AABB = new AxisAlignedBB[] {new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.125D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.25D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.375D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.5D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.625D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.75D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.875D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D)};

    public BlockCucumberStem(String name, Block crop)
    {
    	super(crop);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CUCUMBER_AGE, Integer.valueOf(0)).withProperty(FACING, EnumFacing.UP));
        this.crop = crop;
        this.setTickRandomly(true);
        this.setCreativeTab((CreativeTabs)null);
        setUnlocalizedName(name);
		setRegistryName(name);
		
        ModBlocks.BLOCKS.add(this);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return STEM_AABB[((Integer)state.getValue(CUCUMBER_AGE)).intValue()];
    }
    
    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        int i = ((Integer)state.getValue(CUCUMBER_AGE)).intValue();
        state = state.withProperty(FACING, EnumFacing.UP);
        if (worldIn.getBlockState(pos.up()).getBlock() == this.crop && i == 7)
        {
        	EnumFacing enumfacing = ((BlockCucumberVine) worldIn.getBlockState(pos.up()).getBlock()).getFacing(worldIn.getBlockState(pos.up()));
            state = state.withProperty(FACING, enumfacing);
        }

        return state;
    }
    
    /**
     * Return true if the block can sustain a Bush
     */
    protected boolean canSustainBush(IBlockState state)
    {
        return state.getBlock() == Blocks.FARMLAND;
    }
    
    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (worldIn.getLightFromNeighbors(pos.up()) >= 9)
        {
            float f = this.getGrowthChance(this, worldIn, pos);

            if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(25.0F / f) + 1) == 0))
            {
            	int i = ((Integer)state.getValue(CUCUMBER_AGE)).intValue();

                if (i < 7)
                {
                    IBlockState newState = state.withProperty(CUCUMBER_AGE, Integer.valueOf(i + 1));
                    worldIn.setBlockState(pos, newState, 2);
    	        	net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
                //if metadata is 7+, and vine has not been placed
                else
                {	
                	//if block above is air
                	if (worldIn.isAirBlock(pos.up()))
                	{
                		int x = pos.getX();
                		int y = pos.getY();
                		int z = pos.getZ();
                		IBlockState state1 = worldIn.getBlockState(new BlockPos(x + 1, y + 1, z));
                		IBlockState state2 = worldIn.getBlockState(new BlockPos(x - 1, y + 1, z));
                		IBlockState state3 = worldIn.getBlockState(new BlockPos(x, y + 1, z + 1));
                		IBlockState state4 = worldIn.getBlockState(new BlockPos(x, y + 1 , z - 1));
                        
                        if (state1 == Blocks.AIR && state2 == Blocks.AIR && state1 == Blocks.AIR && state3 == Blocks.AIR && state4 == Blocks.AIR)
                        {
                        	return;
                        }
                        
                        //checks blocks above around it to find a place to put the vine.  Favors blocks in this order (north, east, west south)                    
                        //changed for new metadata system
                        IBlockState newBlock = null;
                        while (newBlock == null)
                        {
                        	if (state4.isOpaqueCube())
                            {
                            	//north
                            	newBlock = ModBlocks.CUCUMBER_VINE.getDefaultState().withProperty(BlockCucumberVine.FACING, EnumFacing.NORTH);
                            }
                        	else if (state1.isOpaqueCube())
                            {
                            	//east
                            	newBlock = ModBlocks.CUCUMBER_VINE.getDefaultState().withProperty(BlockCucumberVine.FACING, EnumFacing.EAST);
                            }
                        	else if (state2.isOpaqueCube())
                            {
                        		//west
                            	newBlock = ModBlocks.CUCUMBER_VINE.getDefaultState().withProperty(BlockCucumberVine.FACING, EnumFacing.WEST);
                            }	
                            else if (state3.isOpaqueCube())
                            {
                            	//south
                            	newBlock = ModBlocks.CUCUMBER_VINE.getDefaultState().withProperty(BlockCucumberVine.FACING, EnumFacing.SOUTH);
                            }
                            else
                            {
                            	break;
                            }
                        }
                        
                        //set block to cucumbervine with correct block found above
                        //set for new metadata system
                        if (newBlock != null)
                        {
                        	worldIn.setBlockState(pos.up(), newBlock, 3);
                            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                        }
                	}
                }
            }
        }
    }
    
    protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos)
    {
        float f = 1.0F;
        BlockPos blockpos = pos.down();

        for (int i = -1; i <= 1; ++i)
        {
            for (int j = -1; j <= 1; ++j)
            {
                float f1 = 0.0F;
                IBlockState iblockstate = worldIn.getBlockState(blockpos.add(i, 0, j));

                if (iblockstate.getBlock().canSustainPlant(iblockstate, worldIn, blockpos.add(i, 0, j), net.minecraft.util.EnumFacing.UP, (net.minecraftforge.common.IPlantable)blockIn))
                {
                    f1 = 1.0F;

                    if (iblockstate.getBlock().isFertile(worldIn, blockpos.add(i, 0, j)))
                    {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0)
                {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = pos.north();
        BlockPos blockpos2 = pos.south();
        BlockPos blockpos3 = pos.west();
        BlockPos blockpos4 = pos.east();
        boolean flag = blockIn == worldIn.getBlockState(blockpos3).getBlock() || blockIn == worldIn.getBlockState(blockpos4).getBlock();
        boolean flag1 = blockIn == worldIn.getBlockState(blockpos1).getBlock() || blockIn == worldIn.getBlockState(blockpos2).getBlock();

        if (flag && flag1)
        {
            f /= 2.0F;
        }
        else
        {
            boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.south()).getBlock() || blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();

            if (flag2)
            {
                f /= 2.0F;
            }
        }

        return f;
    }
    
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());
        return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && soil.getBlock() == Blocks.FARMLAND;
    }
    
    public void growStem(World worldIn, BlockPos pos, IBlockState state)
    {
        int i = ((Integer)state.getValue(CUCUMBER_AGE)).intValue() + MathHelper.getInt(worldIn.rand, 2, 5);
        worldIn.setBlockState(pos, state.withProperty(CUCUMBER_AGE, Integer.valueOf(Math.min(7, i))), 2);
    }
    
    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }
    
    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        {
            Item item = this.getSeedItem();

            if (item != null)
            {
                int i = ((Integer)state.getValue(CUCUMBER_AGE)).intValue();

                for (int j = 0; j < 3; ++j)
                {
                    if (RANDOM.nextInt(15) <= i)
                    {
                        drops.add(new ItemStack(item));
                    }
                }
            }
        }
    }
    
    protected Item getSeedItem()
    {
        return ModItems.CUCUMBER_SEEDS;
    }
    
    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }
    
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        Item item = this.getSeedItem();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
    
    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return ((Integer)state.getValue(CUCUMBER_AGE)).intValue() != 7;
    }
    
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        this.growStem(worldIn, pos, state);
    }
    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(CUCUMBER_AGE, Integer.valueOf(meta));
    }
    
    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(CUCUMBER_AGE)).intValue();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {CUCUMBER_AGE, FACING});
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return ModItems.CUCUMBER_SEEDS;
    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
    	return null;
    }
}