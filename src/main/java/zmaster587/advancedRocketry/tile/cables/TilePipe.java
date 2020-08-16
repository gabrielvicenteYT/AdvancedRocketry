package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;

public class TilePipe extends TileEntity {

	int networkID;
	boolean initialized, destroyed;

	static boolean debug = false;
	boolean connectedSides[];

	public TilePipe(TileEntityType<?> type) {
		super(type);
		initialized = false;
		destroyed = false;
		connectedSides = new boolean[6];
	}


	public void initialize(int id) {
		networkID = id;
		initialized = true;
		getNetworkHandler().getNetwork(id).addPipeToNetwork(this);
	}

	@Override
	public void remove() {
		super.remove();
		removePipeFromSystem();

	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		removePipeFromSystem();
	}

	public void removePipeFromSystem() {
		if(!isInitialized())
			return;

		for(Direction dir : Direction.values()) {
			TileEntity tile = world.getTileEntity(this.getPos().offset(dir));
			if(tile != null)
				getNetworkHandler().removeFromAllTypes(this, tile);
		}

		//Fix NPE on chunk unload
		if(getNetworkHandler().getNetwork(networkID) != null) {
			getNetworkHandler().getNetwork(networkID).removePipeFromNetwork(this);
			//Recreate the network until a clean way to tranverse nets in unloaded chunk can be found
			getNetworkHandler().removeNetworkByID(networkID);
		}
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		
		byte sides = 0;
		
		for(int i = 0; i < 6; i++) {
			if(connectedSides[i])
				sides += 1<<i;
		}
		
		nbt.putByte("conn", sides);
	
		return nbt;
		
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        
        byte sides = tag.getByte("conn");
        
		for(int i = 0; i < 6; i++) {
			connectedSides[i] = (sides & (1<<i)) != 0;
		}
	}


	@Override
	public void markDirty() {
		super.markDirty();

		if(!world.isRemote) {
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
		}
	}

	public void onPlaced() {

		for(Direction dir : Direction.values()) {
			TileEntity tile = world.getTileEntity(getPos().offset(dir));


			if(tile != null) {
				if(tile instanceof TilePipe && tile.getClass() == this.getClass()) {
					TilePipe pipe = (TilePipe)tile;
					if(this.destroyed)
						continue;

					if(isInitialized() && pipe.isInitialized() && pipe.getNetworkID() != networkID)
						getNetworkHandler().mergeNetworks(networkID,  pipe.getNetworkID());
					else if(!isInitialized() && pipe.isInitialized()) {
						initialize(pipe.getNetworkID());
					}
					connectedSides[dir.ordinal()] = true;
				}
			}
		}


		if(!isInitialized()) {
			initialize(getNetworkHandler().getNewNetworkID());
		}

		linkSystems();
	}

	public void linkSystems() {
		for(Direction dir : Direction.values()) {
			TileEntity tile = world.getTileEntity(getPos().offset(dir));

			if(tile != null) {
				attemptLink(dir, tile);
			}
		}
	}

	protected void attemptLink(Direction dir, TileEntity tile) {
		//If the pipe can inject or extract, add to the cache
		//if(!(tile instanceof IFluidHandler))
		//return;

		if(canExtract(dir, tile) && (world.getRedstonePowerFromNeighbors(pos) > 0 || world.getStrongPower(pos) > 0)) {
			if(world.isRemote)
				connectedSides[dir.ordinal()]=true;
			else {
				getNetworkHandler().removeFromAllTypes(this, tile);
				getNetworkHandler().addSource(this,tile,dir);
				connectedSides[dir.ordinal()]=true;
			}
		}

		if(canInject(dir, tile) && world.getRedstonePowerFromNeighbors(pos) == 0 && world.getStrongPower(pos) == 0) {
			if(world.isRemote)
				connectedSides[dir.ordinal()]=true;
			else {
				getNetworkHandler().removeFromAllTypes(this, tile);
				getNetworkHandler().addSink(this, tile,dir);
				connectedSides[dir.ordinal()]=true;
			}
		}
	}

	public int getNetworkID() { return networkID; }

	public boolean isInitialized() { return initialized && getNetworkHandler().doesNetworkExist(networkID); }

	public void onNeighborTileChange(BlockPos pos) {

		//if(worldObj.isRemote)
		//return;


		TileEntity tile = world.getTileEntity(pos);

		if(!world.isRemote && !getNetworkHandler().doesNetworkExist(networkID)) {
			initialized = false;
		}

		if(tile != null) {

			//If two networks touch, merge them
			if(tile instanceof TilePipe && tile.getClass() == this.getClass()) {

				TilePipe pipe = ((TilePipe) tile);

				if(world.isRemote) {
					Direction dir = null;
					for(Direction dir2 : Direction.values()) {

						if(getPos().offset(dir2).compareTo(pos) == 0)
							dir = dir2;
					}
					if(dir != null)
						connectedSides[dir.ordinal()] = true;
				}
				else {

					if(this.destroyed)
						return;

					if(pipe.isInitialized()) {
						if(!isInitialized()) {
							initialize(pipe.getNetworkID());
							linkSystems();
							markDirty();
							
							if(debug && !world.isRemote)
								System.out.println(" pos1 " + getPos());

						} else if(pipe.getNetworkID() != networkID)
							mergeNetworks(pipe.getNetworkID(), networkID);
					}
					else if(pipe.destroyed) {
						getNetworkHandler().removeNetworkByID(pipe.networkID);

						if( debug && !world.isRemote)
							System.out.println(" pos2 " + getPos());
						
						onPlaced();
						markDirty();
					}
					else if(isInitialized()) {
						if(debug && !world.isRemote)
							System.out.println(" pos3 " + getPos());
						
						pipe.initialize(networkID);
					}
					else {		
						if(debug && !world.isRemote)
							System.out.println(" pos4 " + getPos());
						onPlaced();
						markDirty();
					}
				}
				
				Direction dir = null;
				for(Direction dir2 : Direction.values()) {

					if(getPos().offset(dir2).compareTo(pos) == 0)
						connectedSides[dir2.ordinal()] = true;
				}
			}
			else {
				if(!world.isRemote && !isInitialized()) {
					networkID = getNetworkHandler().getNewNetworkID();
					initialized = true;
				}

				Direction dir = null;
				for(Direction dir2 : Direction.values()) {
					if(getPos().offset(dir2).compareTo(pos) == 0)
						dir = dir2;
				}

				//If the pipe can inject or extract, add to the cache
				if(dir != null)
					attemptLink(dir, tile);
			}
		}
		else {
			Direction dir = null;
			for(Direction dir2 : Direction.values()) {

				if(getPos().offset(dir2).compareTo(pos) == 0)
					dir = dir2;
			}
			if(dir != null)
				connectedSides[dir.ordinal()] = false;
		}


			
	}

	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.liquidNetwork;
	}

	public boolean canConnect(int side) {
		return connectedSides[side];
	}

	public boolean canExtract(Direction dir, TileEntity e) {
		return false;
	}

	public boolean canInject(Direction dir, TileEntity e) {
		return false;
	}

	public void mergeNetworks(int a, int b) {
		networkID = getNetworkHandler().mergeNetworks(a, b);
		this.markDirty();
	}

	@Override
	public String toString() {
		return "ID: " + networkID + "   " + getNetworkHandler().toString(networkID);
	}

	public void setDestroyed() {
		destroyed = true;
	}

	public void setInvalid() {
		initialized = false;
		//markDirty();
	}
}
