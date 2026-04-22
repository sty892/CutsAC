package ac.cust.custac.utils.inventory.inventory;

import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.inventory.Inventory;
import ac.cust.custac.utils.inventory.InventoryStorage;
import ac.cust.custac.utils.inventory.slot.Slot;
import com.github.retrooper.packetevents.protocol.item.ItemStack;

public class DispenserMenu extends AbstractContainerMenu {
    public DispenserMenu(CustACPlayer player, Inventory playerInventory) {
        super(player, playerInventory);

        InventoryStorage containerStorage = new InventoryStorage(9);

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(containerStorage, i));
        }

        addFourRowPlayerInventory();
    }

    @Override
    public ItemStack quickMoveStack(int slotID) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotID);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotID < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            if (itemstack1.getAmount() == itemstack.getAmount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}
