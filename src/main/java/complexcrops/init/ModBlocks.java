package complexcrops.init;

import java.util.ArrayList;
import java.util.List;

import complexcrops.blocks.BlockCornBottom;
import complexcrops.blocks.BlockCornTop;
import complexcrops.blocks.BlockCucumberStem;
import complexcrops.blocks.BlockCucumberVine;
import complexcrops.blocks.BlockRice;
import net.minecraft.block.Block;

public class ModBlocks
{
	public static final List<Block> BLOCKS = new ArrayList<Block>();

	public static final BlockCornBottom CORN_BOTTOM = (BlockCornBottom) new BlockCornBottom("corn_bottom");
	public static final BlockCornTop CORN_TOP = (BlockCornTop) new BlockCornTop("corn_top");
	public static final BlockRice RICE = (BlockRice) new BlockRice("rice");
	public static final BlockCucumberVine CUCUMBER_VINE = (BlockCucumberVine) new BlockCucumberVine("cucumber_vine");
	public static final BlockCucumberStem CUCUMBER_STEM = (BlockCucumberStem) new BlockCucumberStem("cucumber_stem", CUCUMBER_VINE);
}
