package complexcrops.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import complexcrops.init.ModBlocks;
import complexcrops.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCucumberVine extends BlockHorizontal implements net.minecraftforge.common.IShearable
{
	public static final PropertyInteger CUCUMBER_VINE_AGE = PropertyInteger.create("age", 0, 3);
	protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.9375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.9375D, 1.0D, 1.0D, 1.0D);

    public BlockCucumberVine(String name)
    {
        super(Material.VINE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH).withProperty(CUCUMBER_VINE_AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setCreativeTab((CreativeTabs)null);
        this.setHardness(0.0F);
        this.setSoundType(SoundType.PLANT);
        this.disableStats();
        this.setUnlocalizedName(name);
		this.setRegistryName(name);
		
        ModBlocks.BLOCKS.add(this);
    }
    
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch ((EnumFacing)state.getValue(FACING))
        {
            case SOUTH:
                return SOUTH_AABB;
            case NORTH:
            default:
                return NORTH_AABB;
            case WEST:
                return WEST_AABB;
            case EAST:
                return EAST_AABB;
        }
    }
    
    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }
    
    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        EnumFacing enumfacing = EnumFacing.fromAngle((double)placer.rotationYaw);
        worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
    }
    
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    
    /**
     * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
     */
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }
    
    /**
     * Check whether this Block can be placed at pos, while aiming at the specified side of an adjacent block
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return side != EnumFacing.DOWN && side != EnumFacing.UP && this.canAttachTo(worldIn, pos, side);
    }
    
    public boolean canAttachTo(World p_193395_1_, BlockPos p_193395_2_, EnumFacing p_193395_3_)
    {
        Block block = p_193395_1_.getBlockState(p_193395_2_.up()).getBlock();
        return this.isAcceptableNeighbor(p_193395_1_, p_193395_2_.offset(p_193395_3_.getOpposite()), p_193395_3_) && (block == Blocks.AIR || block == Blocks.VINE || this.isAcceptableNeighbor(p_193395_1_, p_193395_2_.up(), EnumFacing.UP));
    }

    private boolean isAcceptableNeighbor(World p_193396_1_, BlockPos p_193396_2_, EnumFacing p_193396_3_)
    {
        IBlockState iblockstate = p_193396_1_.getBlockState(p_193396_2_);
        return iblockstate.getBlockFaceShape(p_193396_1_, p_193396_2_, p_193396_3_) == BlockFaceShape.SOLID && !isExceptBlockForAttaching(iblockstate.getBlock());
    }
    
    protected static boolean isExceptBlockForAttaching(Block p_193397_0_)
    {
        return p_193397_0_ instanceof BlockShulkerBox || p_193397_0_ == Blocks.BEACON || p_193397_0_ == Blocks.CAULDRON || p_193397_0_ == Blocks.GLASS || p_193397_0_ == Blocks.STAINED_GLASS || p_193397_0_ == Blocks.PISTON || p_193397_0_ == Blocks.STICKY_PISTON || p_193397_0_ == Blocks.PISTON_HEAD || p_193397_0_ == Blocks.TRAPDOOR;
    }
    
    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            this.dropBlock(worldIn, pos, state);
        }
    }
    
    private void dropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        this.dropBlockAsItem(worldIn, pos, state, 0);
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
        super.getDrops(drops, world, pos, state, fortune);
        int i = this.getAge(state);

        if (i == this.getMaxAge())
        {
            drops.add(new ItemStack(ModItems.CUCUMBER, 1));
        }
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModItems.CUCUMBER_SEEDS);
    }
    
    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
    	if (!this.canBlockStay(worldIn, pos, state))
        {
            this.dropBlock(worldIn, pos, state);
        }
    	if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (worldIn.getLightFromNeighbors(pos.up()) >= 9)
        {
        	float f = this.getGrowthChance(state.getBlock(), worldIn, pos);
            
        	if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(25.0F / f) + 1) == 0))
            {
        		int i = this.getAge(state);
            	
                //if age is less than max, add one to it
                if (i < this.getMaxAge())
                {               	 	
    	        	worldIn.setBlockState(pos, this.withAge(i + 1).withProperty(FACING, this.getFacing(state)), 2);
    	        	net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
                //if vine can be placed above, place it
                else if (i >= this.getMaxAge() - 1 && worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR)
                {
                	//check to see if the vine has reached it's max height (no more than 3 vines above one cucumber stem)
               		int k = 1;
                 	boolean flag = false;
                 	//if there are no "obstructions" below and there are no more than three vines below, the vine can grow
                 	BlockPos posTemp = pos;
                 	for (int j = 0; j < 4 ; ++j)
                 	{
                 		posTemp = posTemp.down();
                 		if (worldIn.getBlockState(posTemp).getBlock() == ModBlocks.CUCUMBER_STEM && worldIn.getBlockState(posTemp).getBlock().getMetaFromState(worldIn.getBlockState(posTemp)) >= 7)
                 		{
                 			flag = true;
                 			break;
                 		}
                 		else if (worldIn.getBlockState(posTemp).getBlock() == this && this.getAge(worldIn.getBlockState(posTemp)) >= this.getMaxAge())
                 		{
                 			++k;
                 		}
                 		else
                 		{
                 			break;
                 		}
                 	}
                 	if (flag == true && k < 3)
                 	{
                 		//if the block to grow on is solid, set above block to cucumber vine
         				IBlockState state1 = null;
         				EnumFacing facing = getFacing(state);
         				if (facing == EnumFacing.NORTH)
         				{
                             state1 = worldIn.getBlockState(pos.up().north());
         				}
         				else if (facing == EnumFacing.EAST)
         				{
             				state1 = worldIn.getBlockState(pos.up().east());
         				}
         				else if (facing == EnumFacing.SOUTH)
         				{
                             state1 = worldIn.getBlockState(pos.up().south());
         				}
         				else if (facing == EnumFacing.WEST)
         				{
                             state1 = worldIn.getBlockState(pos.up().west());
         				}
         				
                        if (state1 != null && state1.isOpaqueCube())
                        {
                        	IBlockState newState = this.withAge(0).withProperty(FACING, facing);
                        	worldIn.setBlockState(pos.up(), newState, 2);
            	        	net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                        }
                 	}
                }
            }
        }
    }
    
    /**Gets the growth rate of the cucumber stem it is attached to */
    protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos)
    {
        float f = 0.0F;
        BlockPos posTemp = pos;
    	for (int i = 0; i < 4 ; ++i)
    	{
    		posTemp = posTemp.down();
    		if (worldIn.getBlockState(posTemp).getBlock() == ModBlocks.CUCUMBER_STEM)
    		{
    			f = ((BlockCucumberStem) worldIn.getBlockState(posTemp).getBlock()).getGrowthChance(worldIn.getBlockState(posTemp).getBlock(), worldIn, posTemp);
    			break;
    		}
    	}
        return f;
    }
    
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        BlockPos posSide = pos.offset((EnumFacing)state.getValue(FACING));
        IBlockState iBlockStateSide = worldIn.getBlockState(posSide);
        IBlockState iBlockStateDown = worldIn.getBlockState(pos.down());

        boolean sideOkay = iBlockStateSide != Blocks.AIR && iBlockStateSide.isOpaqueCube();
        boolean downOkay = (iBlockStateDown.getBlock() == this || iBlockStateDown.getBlock() == ModBlocks.CUCUMBER_STEM);

        return sideOkay && downOkay;
    }
    
    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (!facing.getAxis().isHorizontal())
        {
            facing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, facing.getOpposite()).withProperty(CUCUMBER_VINE_AGE, Integer.valueOf(0));
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(CUCUMBER_VINE_AGE, Integer.valueOf((meta & 15) >> 2));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
        i = i | ((Integer)state.getValue(CUCUMBER_VINE_AGE)).intValue() << 2;
        return i;
    }
    
    protected PropertyInteger getAgeProperty()
    {
        return CUCUMBER_VINE_AGE;
    }

    public int getMaxAge()
    {
        return 3;
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
    
    protected PropertyDirection getFacingProperty()
    {
        return FACING;
    }
    
    protected EnumFacing getFacing(IBlockState state)
    {
        return state.getValue(this.getFacingProperty());
    }
    
    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return this.getSeed();
    }
    
    /**
     * Generate a seed ItemStack for this crop.
     */
    protected Item getSeed()
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

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }
    
    /*************************FORGE START***********************************/
    @Override public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity){ return true; }
    @Override public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){ return true; }
    @Override
    public java.util.List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return java.util.Arrays.asList(new ItemStack(this, 1));
    }
    /*************************FORGE END***********************************/

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }
    
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {CUCUMBER_VINE_AGE, FACING});
    }
}