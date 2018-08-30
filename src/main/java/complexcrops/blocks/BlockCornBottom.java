package complexcrops.blocks;

import java.util.Random;

import complexcrops.init.ModBlocks;
import complexcrops.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCornBottom extends BlockCrops
{
	/**
	 * The last age is only incremented when the top corn block has been placed.
	 * This is so the bottom block can still drop a seed when broken at "max age."
	 */
	public static final PropertyInteger CORN_AGE = PropertyInteger.create("age", 0, 5);
    private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.2D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.6D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.8D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
	
    public BlockCornBottom(String name)
    {
    	this.setDefaultState(this.blockState.getBaseState().withProperty(this.getAgeProperty(), Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setCreativeTab((CreativeTabs)null);
        this.setHardness(0.0F);
        this.setSoundType(SoundType.PLANT);
        this.disableStats();
        this.setUnlocalizedName(name);
		this.setRegistryName(name);
        
        ModBlocks.BLOCKS.add(this);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CROPS_AABB[((Integer)state.getValue(this.getAgeProperty())).intValue()];
    }

    protected PropertyInteger getAgeProperty()
    {
        return CORN_AGE;
    }

    public int getMaxAge()
    {
        return 5;
    }
    
    protected int getAge(IBlockState state)
    {
        return ((Integer)state.getValue(this.getAgeProperty())).intValue();
    }

    public IBlockState withAge(int age)
    {
        return this.getDefaultState().withProperty(this.getAgeProperty(), Integer.valueOf(age));
    }

    public boolean isMaxAge(IBlockState state)
    {
        return ((Integer)state.getValue(this.getAgeProperty())).intValue() >= this.getMaxAge();
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
            	int i = this.getAge(state);

            	if (i < this.getMaxAge() - 1)
                {
                    worldIn.setBlockState(pos, this.withAge(i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
            	// if the block's metadata is maxed out
            	else
            	{            	
                	// If the block above the bottom corn block is air, sets the block above to the corn crop top block 
                	if (worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR)
                	{
                		worldIn.setBlockState(pos.up(), ModBlocks.CORN_TOP.getDefaultState());
                        worldIn.setBlockState(pos, this.withAge(this.getMaxAge()), 2);
                	}
                	// If the block above the bottom corn block is a top corn block and isn't maxed out on metadata, +1 to the metadata of the block above 
                	else if (worldIn.getBlockState(pos.up()).getBlock() == ModBlocks.CORN_TOP)
                	{
                		IBlockState cornTop = worldIn.getBlockState(pos.up());
                		if (((BlockCornTop) cornTop.getBlock()).getAge(cornTop) < ((BlockCornTop) cornTop.getBlock()).getMaxAge())
                		{
                			IBlockState topState = worldIn.getBlockState(pos.up());
                			BlockCornTop topBlock = (BlockCornTop) worldIn.getBlockState(pos.up()).getBlock();
                			int topAge = topBlock.getAge(topState);
                			worldIn.setBlockState(pos.up(), topBlock.withAge(topAge + 1), 2);
                            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos.up(), topState, topState); 
                		}
                	}
            	}
            }
        }   
    }
    
    public void grow(World worldIn, BlockPos pos, IBlockState state)
    {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
        int j = this.getMaxAge() - 1;
    	int excessAge = i - j;
    	
    	if (excessAge <= 0)
    	{
            worldIn.setBlockState(pos, this.withAge(i), 2);
    	}
		//if bonemeal goes over max age, add excess age to corn block top above
    	else
    	{
    		//if block above is air, set it to cornTop
        	if (worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR)
        	{
        		worldIn.setBlockState(pos.up(), ModBlocks.CORN_TOP.getDefaultState());
                worldIn.setBlockState(pos, this.withAge(this.getMaxAge()), 2);
        		//subtract 1 from excess age
        		--excessAge;
        	}
        	//add excessAge to current cornTop age
        	if (excessAge > 0 && worldIn.getBlockState(pos.up()).getBlock() == ModBlocks.CORN_TOP)
        	{
        		IBlockState cornTopState = worldIn.getBlockState(pos.up());
        		BlockCornTop cornTopBlock = (BlockCornTop) worldIn.getBlockState(pos.up()).getBlock();
        		int cornTopAge = cornTopBlock.getAge(cornTopState);
        		if (cornTopAge < cornTopBlock.getMaxAge())
        		{
        			int newAge = cornTopAge + excessAge;
        			if (newAge > cornTopBlock.getMaxAge()) newAge = cornTopBlock.getMaxAge();
        	        worldIn.setBlockState(pos.up(), cornTopBlock.withAge(newAge), 2);
        		}
        	}
    	}
    }
    
    protected int getBonemealAgeIncrease(World worldIn)
    {
    	return super.getBonemealAgeIncrease(worldIn) / 4;
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
        
    /**
     * Generate a seed ItemStack for this crop.
     */
    protected Item getSeed()
    {
        return ModItems.CORN_SEEDS;
    }

    /**
     * Generate a crop produce ItemStack for this crop.
     */
    protected Item getCrop()
    {
        return ModItems.CORN_COB;
    }
    
    /**
     * If age is less than max age, drop one seed
     */
    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune) 
    {
        int age = getAge(state);
        
    	if (age < this.getMaxAge())
    	{
        	drops.add(new ItemStack(this.getSeed(), 1, 0));
    	}
    }
    
    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return this.isMaxAge(state) ? this.getCrop() : this.getSeed();
    }
    
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(this.getSeed());
    }
    
    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
    	//if this block is not at max age and the top corn block above is also not at max age, return true
    	IBlockState upState = worldIn.getBlockState(pos.up());
    	return this.getAge(state) <= this.getMaxAge() - 1 ? true : upState.getBlock() == Blocks.AIR ? true : upState.getBlock() == ModBlocks.CORN_TOP && !((BlockCornTop) upState.getBlock()).isMaxAge(upState);
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        this.grow(worldIn, pos, state);
    }
    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.withAge(meta);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return this.getAge(state);
    }
    
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {CORN_AGE});
    }
}