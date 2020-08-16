package zmaster587.advancedRocketry.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class WorldGenSwampTree extends MapGenBase {

	Map<BlockPos, BlockState> cachedCanopy;
	Map<BlockPos, BlockState> cachedRoots;
	private final static double arcSize = 16.0;
	int chancePerChunk;

	public WorldGenSwampTree(int chancePerChunk) {
		super();
		chancePerChunk= 10;
		cachedCanopy = new HashMap<BlockPos, BlockState>();
		cachedRoots = new HashMap<BlockPos, BlockState>();
		this.chancePerChunk = chancePerChunk;
		buildCanopy();
		buildRoots();
	}

	private void buildRoots() {
		cachedRoots.clear();
		for (double Yangle = 0; Yangle < 2*Math.PI; Yangle+=Math.PI/3.0){
			// Yangle = 0.0;//Math.PI/4.0;
			int yOffset = (int)(1.25*arcSize*Math.sin(Math.PI)) + 1;
			int xOffset = (int)(1.1*arcSize*Math.cos(Math.PI)*Math.cos(Yangle));
			int zOffset = (int)(1.1*arcSize*Math.cos(Math.PI)*Math.sin(Yangle));


			for(double angle = Math.PI; angle > 0; angle -= Math.PI/40.0) {
				int yy = (int)(1.25*arcSize*Math.sin(angle));
				double xzRadius = (0.75*arcSize*Math.cos(angle));
				int xx = (int) (xzRadius*Math.cos(Yangle));
				int zz = (int) (xzRadius*Math.sin(Yangle));

				if(!cachedRoots.containsKey(new BlockPos(2 + xx - xOffset, yy - yOffset +2,  zz- zOffset)))
					cachedRoots.put(  new BlockPos(2 + xx - xOffset, yy - yOffset +2,  zz- zOffset), Blocks.LOG.getDefaultState().with(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y));
				if(!cachedRoots.containsKey(new BlockPos(3 + xx - xOffset, yy - yOffset +2,  zz- zOffset)))
					cachedRoots.put(new BlockPos(3 + xx - xOffset, yy - yOffset +2,  zz- zOffset), Blocks.LOG.getDefaultState().with(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y));
				if(!cachedRoots.containsKey(new BlockPos(2 + xx - xOffset, yy - yOffset +2, 1 + zz- zOffset)))
					cachedRoots.put( new BlockPos(2 + xx - xOffset, yy - yOffset +2, 1 + zz- zOffset), Blocks.LOG.getDefaultState().with(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y));
				if(!cachedRoots.containsKey(new BlockPos(2 + xx - xOffset,  yy - yOffset +3, 1 + zz- zOffset)))
					cachedRoots.put( new BlockPos(2 + xx - xOffset,  yy - yOffset +3, 1 + zz- zOffset), Blocks.LOG.getDefaultState().with(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y));
				if(!cachedRoots.containsKey(new BlockPos(1 + xx - xOffset  , yy - yOffset +2, zz- zOffset)))
				cachedRoots.put(new BlockPos(1 + xx - xOffset  , yy - yOffset +2, zz- zOffset), Blocks.LOG.getDefaultState().with(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y));
				if(!cachedRoots.containsKey(new BlockPos(2 + xx - xOffset, yy - yOffset +2, zz- zOffset - 1)))
					cachedRoots.put( new BlockPos(2 + xx - xOffset, yy - yOffset +2, zz- zOffset - 1), Blocks.LOG.getDefaultState().with(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y));
			}
		}
	}

	private void buildCanopy() {
		cachedCanopy.clear();
		//Gen the canopy
		for (double Yangle = 0; Yangle < 2*Math.PI; Yangle+=Math.PI/512.0){
			// Yangle = 0.0;//Math.PI/4.0;
			int yOffset = (int)(arcSize*Math.sin(1.5*Math.PI/2.0));
			int xOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.cos(Yangle));
			int zOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.sin(Yangle));


			for(double angle = 1.5*Math.PI/2.0; angle > -Math.PI/6.0; angle -= Math.PI/128.0) {
				int yy = (int)(arcSize*Math.sin(angle));
				double xzRadius = (1.3*arcSize*Math.cos(angle));
				int xx = (int) (xzRadius*Math.cos(Yangle));
				int zz = (int) (xzRadius*Math.sin(Yangle));

				for(int yyy = -2 ; yyy < 4; yyy++)
					if(!cachedCanopy.containsKey(new BlockPos(2 + xx - xOffset, yyy + yy - yOffset +2, zz- zOffset)))
					cachedCanopy.put(new BlockPos(2 + xx - xOffset, yyy + yy - yOffset +2, zz- zOffset), Blocks.LEAVES.getDefaultState());
				//world.setBlock( x + 2 + xx - xOffset - radius/2, treeHeight -3 + yy - yOffset +2, z + zz- zOffset, Blocks.vine, 0,2);
			}

		}
	}

	@Override
	protected void recursiveGenerate(World worldIn, int rangeX,
			int rangeZ, int chunkX, int chunkZ, ChunkPrimer blocks) {
		if(rand.nextInt(chancePerChunk) == Math.abs(rangeX) % chancePerChunk && rand.nextInt(chancePerChunk) == Math.abs(rangeZ) % chancePerChunk) {

			int x = (rangeX - chunkX)*16;
			int z =  (rangeZ- chunkZ)*16;
			int y = 56;
			
			
			int treeHeight = rand.nextInt(10) + 40;
			int radius = 4;

			int edgeRadius = 1;
			int numDiag = edgeRadius + 1;

			int meta = 3;
			BlockState block = Blocks.LOG.getDefaultState();
			int currentEdgeRadius;

			final float SHAPE = 0.1f;

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * treeHeight )) + ((1f-SHAPE)*edgeRadius));

			y++;


			for(int yOff = -20; yOff < treeHeight; yOff++) {

				currentEdgeRadius = (int)((SHAPE*(edgeRadius * (treeHeight - yOff))) + ((1f-SHAPE)*edgeRadius));

				//Generate the top trapezoid
				for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						setBlock(new BlockPos(x + xOff, y + yOff, z + zOff), block, blocks);
					}
					currentEdgeRadius++;
				}

				//Generate square segment
				for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						setBlock(new BlockPos(x + xOff, y + yOff, z + zOff), block, blocks);
					}
				}

				//Generate the bottom trapezoid
				for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
					currentEdgeRadius--;
					for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
						setBlock(new BlockPos(x + xOff, y + yOff, z + zOff), block, blocks);
					}
				}
			}

			//Canopy
			for(Entry<BlockPos, BlockState> entry : cachedCanopy.entrySet())
				setBlock( entry.getKey().add(x - radius/2, y + treeHeight, z), entry.getValue(), blocks);

			//Generate Logs
			for (double Yangle = 0; Yangle < 2*Math.PI; Yangle+=Math.PI/8.0){
				// Yangle = 0.0;//Math.PI/4.0;
				int yOffset = (int)(arcSize*Math.sin(1.5*Math.PI/2.0));
				int xOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.cos(Yangle));
				int zOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.sin(Yangle));


				for(double angle = 1.5*Math.PI/2.0; angle > -Math.PI/6.0; angle -= Math.PI/40.0) {
					int yy = (int)(arcSize*Math.sin(angle));
					double xzRadius = (1.25*arcSize*Math.cos(angle));
					int xx = (int) (xzRadius*Math.cos(Yangle));
					int zz = (int) (xzRadius*Math.sin(Yangle));

					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 3 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 1 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +3, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset + 1), Blocks.LOG.getDefaultState(), blocks);
					setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset - 1), Blocks.LOG.getDefaultState(), blocks);

				}


				//Generate the hangy things
				if(rand.nextInt(4) == 0) {

					int yy = (int)(arcSize*Math.sin(Math.PI/3.0));
					double xzRadius = (1.25*arcSize*Math.cos(Math.PI/2.0));
					int xx = (int) (xzRadius*Math.cos(Yangle));
					int zz = (int) (xzRadius*Math.sin(Yangle));
					int xxx = xx;
					int zzz = zz;
					//Leaf caps on bottom
					for(zz = -1; zz < 2; zz++)
						for(xx = -1; xx < 2; xx++)
							setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight - 10 + yy - yOffset +2, z + zz- zOffset), Blocks.LEAVES.getDefaultState(), blocks);
					xx=xxx;
					zz=zzz;
					//Descending 
					for(int yyy = 0; yyy < 10; yyy++) {


						for(zz = -2; zz < 3; zz++)
							for(xx = -2; xx < 3; xx++)
								setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight - yyy + yy - yOffset +2, z + zz- zOffset), Blocks.LEAVES.getDefaultState(), blocks);
						xx=xxx;
						zz=zzz;

						setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 3 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 1 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +3, z + zz- zOffset), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset + 1), Blocks.LOG.getDefaultState(), blocks);
						setBlock( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yyy - yOffset +2, z + zz- zOffset - 1), Blocks.LOG.getDefaultState(), blocks);
					}
				}

			}


			//roots

			for(Entry<BlockPos, BlockState> entry : cachedRoots.entrySet())
				setBlock( entry.getKey().add( + x - radius/2, y, z), entry.getValue(), blocks);
		}
	}
	
	protected void func_151538_a(World world2, int rangeX,
			int rangeZ, int chunkX, int chunkZ,
			Block[] blocks) {
		

	}

	private void setBlock(BlockPos pos, BlockState block, ChunkPrimer blocks) {
		
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return;
		
		blocks.setBlockState(x, y, z, block);
	}
	
	private BlockState getBlock(BlockPos pos, Block block, ChunkPrimer blocks) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 255)
			return Blocks.AIR.getDefaultState();
		
		return blocks.getBlockState(x, y, z);
	}
	
	
	public boolean generate(World world, Random rand, int x, int y, int z)
	{

		int treeHeight = rand.nextInt(10) + 40;
		int radius = 4;
		boolean flag = true;

		int edgeRadius = 1;
		int numDiag = edgeRadius + 1;

		int meta = 3;
		BlockState block = Blocks.LOG.getDefaultState();
		int currentEdgeRadius;

		final float SHAPE = 0.1f;

		currentEdgeRadius = (int)((SHAPE*(edgeRadius * treeHeight )) + ((1f-SHAPE)*edgeRadius));

		//Make the base of the crystal
		//Generate the top trapezoid
		for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {

				for(BlockPos yOff = world.getHeight(new BlockPos(x + xOff, 0, z + zOff)); yOff.getY() < y; yOff = yOff.up()) //Fills the gaps under the crystal
					world.setBlockState(yOff, block);
				world.setBlockState(new BlockPos(x + xOff, y, z + zOff), block);
			}
			currentEdgeRadius++;
		}

		//Generate square segment
		for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {

				for(BlockPos yOff = world.getHeight(new BlockPos(x + xOff, 0, z + zOff)); yOff.getY() < y; yOff = yOff.up()) //Fills the gaps under the crystal
					world.setBlockState(yOff, block);
				world.setBlockState(new BlockPos(x + xOff, y, z + zOff), block);
			}
		}

		//Generate the bottom trapezoid
		for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
			currentEdgeRadius--;
			for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
				for(BlockPos yOff = world.getHeight(new BlockPos(x + xOff, 0, z + zOff)); yOff.getY() < y; yOff = yOff.up()) //Fills the gaps under the crystal
					world.setBlockState(yOff, block);
				world.setBlockState(new BlockPos(x + xOff, y, z + zOff), block);
			}
		}

		y++;


		for(int yOff = 0; yOff < treeHeight; yOff++) {

			currentEdgeRadius = (int)((SHAPE*(edgeRadius * (treeHeight - yOff))) + ((1f-SHAPE)*edgeRadius));

			//Generate the top trapezoid
			for(int zOff = -numDiag - currentEdgeRadius/2; zOff <= -currentEdgeRadius/2; zOff++) {

				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlockState(new BlockPos(x + xOff, y + yOff, z + zOff), block);  //meta?
				}
				currentEdgeRadius++;
			}

			//Generate square segment
			for(int zOff = -currentEdgeRadius/2; zOff <= currentEdgeRadius/2; zOff++) {
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlockState(new BlockPos(x + xOff, y + yOff, z + zOff), block);  //meta?
				}
			}

			//Generate the bottom trapezoid
			for(int zOff = currentEdgeRadius/2; zOff <= numDiag + currentEdgeRadius/2; zOff++) {
				currentEdgeRadius--;
				for(int xOff = -numDiag -currentEdgeRadius/2; xOff <=  numDiag + currentEdgeRadius/2; xOff++) {
					world.setBlockState(new BlockPos(x + xOff, y + yOff, z + zOff), block);  //meta?
				}
			}
		}

		//Canopy
		for(Entry<BlockPos, BlockState> entry : cachedCanopy.entrySet())
			world.setBlockState(entry.getKey().add(x - radius/2, y + treeHeight, z), entry.getValue(), 2);

		//Generate Logs
		for (double Yangle = 0; Yangle < 2*Math.PI; Yangle+=Math.PI/8.0){
			// Yangle = 0.0;//Math.PI/4.0;
			int yOffset = (int)(arcSize*Math.sin(1.5*Math.PI/2.0));
			int xOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.cos(Yangle));
			int zOffset = (int)(1.25*arcSize*Math.cos(1.5*Math.PI/2.0)*Math.sin(Yangle));


			for(double angle = 1.5*Math.PI/2.0; angle > -Math.PI/6.0; angle -= Math.PI/40.0) {
				int yy = (int)(arcSize*Math.sin(angle));
				double xzRadius = (1.25*arcSize*Math.cos(angle));
				int xx = (int) (xzRadius*Math.cos(Yangle));
				int zz = (int) (xzRadius*Math.sin(Yangle));

				world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 3 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 1 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +3, z + zz- zOffset), Blocks.LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset + 1), Blocks.LOG.getDefaultState(), 5); //meta
				world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y + treeHeight + yy - yOffset +2, z + zz- zOffset - 1), Blocks.LOG.getDefaultState(), 5); //meta

			}


			//Generate the hangy things
			if(rand.nextInt(4) == 0) {

				int yy = (int)(arcSize*Math.sin(Math.PI/3.0));
				double xzRadius = (1.25*arcSize*Math.cos(Math.PI/2.0));
				int xx = (int) (xzRadius*Math.cos(Yangle));
				int zz = (int) (xzRadius*Math.sin(Yangle));
				int xxx = xx;
				int zzz = zz;
				//Leaf caps on bottom
				for(zz = -1; zz < 2; zz++)
					for(xx = -1; xx < 2; xx++)
						world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2,y + treeHeight - 10 + yy - yOffset +2, z + zz- zOffset), Blocks.LEAVES.getDefaultState(), 5);
				xx=xxx;
				zz=zzz;
				//Descending 
				for(int yyy = 0; yyy < 10; yyy++) {


					for(zz = -2; zz < 3; zz++)
						for(xx = -2; xx < 3; xx++)
							world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight - yyy + yy - yOffset +2, z + zz- zOffset), Blocks.LEAVES.getDefaultState(), 5);
					xx=xxx;
					zz=zzz;

					world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), 5);
					world.setBlockState( new BlockPos(x + 3 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), 5);
					world.setBlockState( new BlockPos(x + 1 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset), Blocks.LOG.getDefaultState(), 5);
					world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +3, z + zz- zOffset), Blocks.LOG.getDefaultState(), 5);
					world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset + 1), Blocks.LOG.getDefaultState(),5);
					world.setBlockState( new BlockPos(x + 2 + xx - xOffset - radius/2, y +treeHeight + yy - yyy - yOffset +2, z + zz- zOffset - 1), Blocks.LOG.getDefaultState(), 5);
				}
			}

		}


		//roots

		for(Entry<BlockPos, BlockState> entry : cachedRoots.entrySet())
			world.setBlockState( entry.getKey().add( x - radius/2, y , z), entry.getValue(),2);

		return true;
	}



	//Just a helper macro
	private void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
	{
		
		world.getBlockState(new BlockPos(x,y,z)).getBlock().onPlantGrow(world.getBlockState(new BlockPos(x,y,z)), world, new BlockPos(x, y, z), new BlockPos(sourceX, sourceY, sourceZ));
	}
}
