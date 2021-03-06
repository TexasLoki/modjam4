package fabricator77.scrapworld.machines;

import cpw.mods.fml.common.FMLLog;
import fabricator77.scrapworld.ScrapWorldBlocks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityMachine extends TileEntity implements IMachine, IInventory{
	private boolean ready = false;
	private boolean complete = false;
	private boolean powered = false;
	private ItemStack[] parts = new ItemStack[]{
			new ItemStack(ScrapWorldBlocks.powerItems, 1, 0),
			new ItemStack(ScrapWorldBlocks.powerItems, 1, 0),
			new ItemStack(ScrapWorldBlocks.powerItems, 1, 0)
	};
	private int numParts = 3;
	
	public int storedPower = 0;
	
	private ItemStack[] inv = new ItemStack[9];
	
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("ready"))
        {
            ready = tag.getBoolean("ready");
        }
        
        NBTTagList nbttaglist = tag.getTagList("Parts", 10);
        this.parts = new ItemStack[numParts];
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.parts.length)
            {
                this.parts[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
        
        nbttaglist = tag.getTagList("Inv", 10);
        this.inv = new ItemStack[inv.length];
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.inv.length)
            {
                this.inv[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }
	
	@Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setBoolean("ready", this.ready);
        
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.parts.length; ++i)
        {
            if (this.parts[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.parts[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        tag.setTag("Parts", nbttaglist);
        
        nbttaglist = new NBTTagList();
        for (int i = 0; i < this.inv.length; ++i)
        {
            if (this.inv[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.inv[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        tag.setTag("Inv", nbttaglist);
    }


	@Override
    public Packet getDescriptionPacket() {
    	NBTTagCompound tagCompound = new NBTTagCompound();
    	this.writeToNBT(tagCompound);
    	return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 2, tagCompound);
    }
     
    @Override
    public void onDataPacket(NetworkManager netManager, S35PacketUpdateTileEntity packet) {
    	readFromNBT(packet.func_148857_g());
    }

    @Override
    public void updateEntity() {
    	if (this.worldObj == null || this.worldObj.isRemote || this.worldObj.getTotalWorldTime() % 20L != 0L)
        {
    		return;
        }
    	//FMLLog.info("[ScrapWorld] Ticking TileEntityMachine");
    	 //TODO: Entity updates
    	// includes block being placed/loaded ?
    	checkIfComplete();
    	// if (!complete) return;
    	getPower();
    	if (storedPower > 0) operateCycle();
    	
    	//if (storedPower > 0) FMLLog.info("[ScrapWorld] StoredPower "+storedPower);
    	
    	this.markDirty();
    }
    
    //IMachine fields
	@Override
    public void operateCycle() {
    	int count = 0;
    	for (int i = 0; i < this.inv.length; ++i)
        {
    		// this.inv[i] = getStackInSlot(i);
    		// setInventorySlotContents(i, this.inv[i]);
    		if (this.inv[i] == null) {}
    		else {
    			count++;
    			int damage = this.inv[i].getItemDamage();
    			int stackSize = this.inv[i].stackSize;//TODO: charge stacked cells evenly
    			Item item = this.inv[i].getItem();
    			//FMLLog.info("[ScrapWorld] Found "+item);
    			//Attempt to change power cells
    			// if (Item.getIdFromItem(item) == Item.getIdFromItem(ScrapWorldBlocks.hvPowerCell)) {
    			if (item instanceof IBattery) {
    			//if (item.getUnlocalizedName().equals(ScrapWorldBlocks.hvPowerCell.getUnlocalizedName())  ) {
    				if (damage > 0) {
    					// stacked cells can require a lot of stored power to charge.
    					if (storedPower < stackSize) {
    						return; // try again next second
    					}
    					
    					int chargingRate = 256;
    					// take into account available power
    					if (storedPower < chargingRate) {
    						chargingRate = storedPower;
    					}
    					//alter charging rate if cells are stacked
    					if (chargingRate % stackSize > 0) {
    						chargingRate = chargingRate-(chargingRate % stackSize);
    					}
    					// divide charging rate over available cells
    					if (stackSize > 0) {
    						chargingRate = chargingRate / stackSize;
    					}
    					// finally drain the power actually used
    					storedPower = storedPower - (chargingRate * stackSize);
    					// FMLLog.info("[ScrapWorld] Charging "+this.inv[i].getItem());
    					this.inv[i].setItemDamage(damage - chargingRate);
    					
    					setInventorySlotContents(i, this.inv[i]);
    					// this.inv[i].getItem().notify();
    					return;
    				}
    			}
    			if (storedPower == 0) {
    				break;
    			}
    		}
        }
    	if (count > 0) {
    		//FMLLog.info("[ScrapWorld] TileEntityMachine.count="+count);
    	}

	}
	
	@Override
	public boolean isMachineReady() {
		if (ready && complete) return true;
		return false;
	}

	@Override
	public boolean isMachineComplete() {
		return complete;
	}

	@Override
	public boolean isMachinePowered() {
		return powered;
	}
    
    // Actual code
	public void checkIfComplete () {
		int missingParts = 0;
		//TODO: specific machines need specific parts in specific slots
		for (int i=0; i<parts.length; i++) {
			if (parts[i].stackSize == 0) {
				missingParts++;
			}
		}
		if (missingParts == 0) {
			complete = true;
		}
		this.markDirty();
	}
	
	public void getPower () {
		//TODO: is on mains power, or contains powerCell
	}

	
	public void onChunkUnload()
    {
		//TODO: save power status/network
    }

	// IInventory
	@Override
	public int getSizeInventory() {
		if (complete) {
			return inv.length;
		}
		return parts.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.inv[slot];
	}

	//Definately problems in here somewhere
	//cannot take items out of machine
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amount) {
				setInventorySlotContents(slot, null);
			}
			else {
				stack = stack.splitStack(amount);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		inv[slot] = item;

        if (item != null && item.stackSize > this.getInventoryStackLimit())
        {
        	item.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
	}

	@Override
	public String getInventoryName() {

		return "container."+this.getMachineName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return true;
	}

	@Override
	public String getMachineName() {
		return ((IMachineBlock)this.blockType).getMachineName();
	}
}
