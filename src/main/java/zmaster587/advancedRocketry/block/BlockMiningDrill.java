package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.IMiningDrill;
import zmaster587.libVulpes.block.BlockFullyRotatable;

public class BlockMiningDrill extends BlockFullyRotatable implements IMiningDrill {

	public BlockMiningDrill(Properties properties) {
		super(properties);
		//super(TileDrill.class, zmaster587.libVulpes.inventory.GuiHandler.guiId.MODULAR.ordinal());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return false;
	}

	@Override
	public float getMiningSpeed(World world, BlockPos pos) {
		return world.isAirBlock(pos.add(0,1,0)) && world.isAirBlock(pos.add(0,2,0)) ? 0.02f : 0.01f;
	}

	@Override
	public int powerConsumption() {
		return 0;
	}

}
