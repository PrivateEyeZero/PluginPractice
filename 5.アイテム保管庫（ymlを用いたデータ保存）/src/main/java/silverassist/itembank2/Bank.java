package silverassist.itembank2;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Bank implements Listener {
    private static final int GUI_SIZE = 45;  //9の倍数
    private static final String YML_KEY = "bankData";

    private final JavaPlugin plugin;
    private final FileConfiguration yml;
    private final Player p;
    //private ItemStack[] items = new ItemStack[GUI_SIZE];  //長さが決まってるときはListより配列
    //private static final List<ItemStack> items = new ArrayList<>();

    public Bank(Player p){
        plugin = ItemBank2.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
        this.p = p;
        yml = ItemBank2.getYml();
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(p,GUI_SIZE,"§d§lアイテムバンク");

        if(yml.get(YML_KEY)!=null){
            ConfigurationSection cs = yml.getConfigurationSection(YML_KEY);
            cs.getKeys(false).forEach(key->inv.setItem(Integer.parseInt(key),cs.getItemStack(key)));
        }
        p.openInventory(inv);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(!p.equals(e.getPlayer()))return;
        Inventory inv = e.getInventory();

        for(int i = 0;i<GUI_SIZE;i++){
            ItemStack item = inv.getItem(i);
            if(item==null)yml.set(YML_KEY+"."+i,null);
            else yml.set(YML_KEY+"."+i,item);
        }
        plugin.saveConfig();
        HandlerList.unregisterAll(this);
    }
}
