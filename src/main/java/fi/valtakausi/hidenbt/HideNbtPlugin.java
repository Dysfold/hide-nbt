package fi.valtakausi.hidenbt;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class HideNbtPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGH, PacketType.Play.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				List<ItemStack> items = packet.getItemListModifier().read(0);
				patchItems(items);
				event.setPacket(packet);
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGH, PacketType.Play.Server.SET_SLOT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				ItemStack stack = packet.getItemModifier().read(0);
				patchItem(stack);
				event.setPacket(packet);
			}
		});
	}
	
	private void patchItems(List<ItemStack> items) {
		for (ItemStack stack : items) {
			if (stack != null) {
				patchItem(stack);
			}
		}
	}
	
	private void patchItem(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return; // Nothing to hide here
		}
		NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
		compound.remove("PublicBukkitValues");
	}
}
