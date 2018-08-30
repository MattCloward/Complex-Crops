package complexcrops.blocks;

import java.util.Random;

import complexcrops.init.ModBlocks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCornTop extends BlockCornBottom
{
	public static final PropertyInteger CORN_TOP_AGE = PropertyInteger.create("age", 0, 4);
	
	public BlockCornTop(String name) 
	{
		super(name);
        this.disableStats();
	}
	
	protected PropertyInteger getAgeProperty()
    {
        return CORN_TOP_AGE;
    }
	
	public int getMaxAge()
    {
        return 4;
    }
	
	/**
     * Return true if the block can sustain a Bush
     */
    protected boolean canSustainBush(IBlockState state)
    {
        return state.getBlock() == ModBlocks.CORN_BOTTOM;
    }
    
    /**
     * This block does not update itself. The bottom corn block is in charge.
     *
     */
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {}
    
    public void grow(World worldIn, BlockPos pos, IBlockState state)
    {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
        int j = this.getMaxAge();

        if (i > j)
        {
            i = j;
        }

        worldIn.setBlockState(pos, this.withAge(i), 2);
    }
	
	/**
     * This block does not grow- the bottom corn block below it grows this block
     */
    public float getGrowthChance(World par1World, int par2, int par3, int par4)
    {
        return 0.0F;
    }
    
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState cornBottom = worldIn.getBlockState(pos.down());
        return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && (cornBottom.getBlock() == ModBlocks.CORN_BOTTOM && cornBottom.getBlock().getMetaFromState(cornBottom) >= ((BlockCornBottom) cornBottom.getBlock()).getMaxAge() - 1); 
    }
    
    /**
     * Set bottom corn block to air, then drop 0-2 seeds and 1-2 corn cobs
     */
    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
    	//set bottom block to air
    	((World) world).setBlockToAir(pos.down());
        int age = getAge(state);
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        //if at max age
        if (age >= this.getMaxAge())
        {
        	//drop 0-2 seeds
            for (int i = 0; i < 2 + fortune; ++i)
            {
                if (rand.nextInt(2 * getMaxAge()) <= age)
                {
                	drops.add(new ItemStack(this.getSeed(), 1, 0));
                }
            }
            //add 1-2 corn cobs
        	drops.add(new ItemStack(this.getCrop(), 1, 0));
        	for (int i = 0; i < 1 + fortune; ++i)
            {
                if (rand.nextInt(2 * getMaxAge()) <= age)
                {
                	drops.add(new ItemStack(this.getCrop(), 1, 0));
                }
            }
        }
        //if not ready to harvest, drops 1 corn seeds
        else
        {
        	drops.add(new ItemStack(this.getSeed(), 1, 0));
        }
    }
    
    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
    	return !this.isMaxAge(state);
    }
    
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {CORN_TOP_AGE});
    }
}