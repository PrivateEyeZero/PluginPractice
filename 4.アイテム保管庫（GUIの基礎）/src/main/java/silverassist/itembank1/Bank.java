package silverassist.itembank1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Bank implements Listener {
    private static final int GUI_SIZE = 45;  //9の倍数

    private final JavaPlugin plugin;
    private final ItemStack[] items = new ItemStack[GUI_SIZE];  //長さが決まってるときはListより配列
    //private static final List<ItemStack> items = new ArrayList<>();
    private final Set<Player> opener = new HashSet<>();  //Setは同じものを入れられない

    public Bank(){
        plugin = ItemBank1.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public void open(Player p){
        Inventory inv = Bukkit.createInventory(p,GUI_SIZE,"§d§lアイテムバンク");
        for(int i =0;i<GUI_SIZE;i++) inv.setItem(i,items[i]);
        opener.add(p);
        p.openInventory(inv);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if(!opener.contains(p))return;
        opener.remove(p);
        Inventory inv = e.getInventory();
        for(int i = 0;i<GUI_SIZE;i++)items[i] = inv.getItem(i);
    }
}
