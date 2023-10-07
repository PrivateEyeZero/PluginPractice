package silverassist.itembank3;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

public class Bank implements Listener {
    private static final int BODY_SIZE = 45;  //9の倍数
    private static final String YML_KEY = "bankData";
    private static final ItemStack BG_NORMAL = Util.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,"§r");
    private static final ItemStack BG_BACK = Util.createItem(Material.RED_STAINED_GLASS_PANE,"§c§l戻る");
    private static final ItemStack BG_NEXT = Util.createItem(Material.LIME_STAINED_GLASS_PANE,"§c§l次へ");

    private final JavaPlugin plugin;
    private final FileConfiguration yml;
    private final Player p;
    private boolean isUpdate = false;
    private int page = 0;
    //private ItemStack[] items = new ItemStack[BODY_SIZE];  //長さが決まってるときはListより配列
    //private static final List<ItemStack> items = new ArrayList<>();

    public Bank(Player p){
        plugin = ItemBank3.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
        this.p = p;
        yml = ItemBank3.getYml();  //ymlはstatic変数でいいかも
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(p, BODY_SIZE + 9,"§d§lアイテムバンク page."+(page+1));
        for(int i = 0; i< BODY_SIZE; i++)inv.setItem(i,null);
        if(yml.get(YML_KEY)!=null){
            ConfigurationSection cs = yml.getConfigurationSection(YML_KEY);
            cs.getKeys(false)  //keyを全取得
                    .stream()
                    .map(Integer::parseInt)  //keyを全て数字(int)に
                    .filter(e->(e> BODY_SIZE *page && e< BODY_SIZE *(page+1)))  //現在のページの範囲のkeyにあたるものだけに絞る
                    .forEach(key->inv.setItem(key%45,cs.getItemStack(String.valueOf(key))));  //keyに沿ってGUIを埋める
        }
        for(int i = BODY_SIZE; i< BODY_SIZE + 9; i++)inv.setItem(i,BG_NORMAL);
        if(page>0)inv.setItem(BODY_SIZE,BG_BACK);
        inv.setItem(BODY_SIZE +8,BG_NEXT);
        isUpdate = true; //更新処理中状態に
        p.openInventory(inv);
        isUpdate=false; //更新処理中状態解除
    }

    private void save(Inventory inv){
        for(int i = BODY_SIZE *page; i<(BODY_SIZE *(page+1)); i++){
            ItemStack item = inv.getItem(i% BODY_SIZE);
            if(item==null)yml.set(YML_KEY+"."+i,null);
            else yml.set(YML_KEY+"."+i,item);
        }
        plugin.saveConfig();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if(!this.p.equals(p))return;
        save(e.getInventory());
        if(!isUpdate) HandlerList.unregisterAll(this);  //更新処理によるcloseでなｋればイベントの削除
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(
                !e.getWhoClicked().equals(p)  //違う人による操作の場合
                || e.getCurrentItem()==null  //関係のないところをクリックした場合
                || !e.getInventory().getType().equals(InventoryType.CHEST)  //クリックした部分が開いているものでない場合
                || e.getSlot() < BODY_SIZE  //クリックした場所が処理対象外の場所の場合
        )return;
        e.setCancelled(true);
        switch (e.getSlot()){
            case BODY_SIZE ->{if(e.getCurrentItem().equals(BG_BACK))save(e.getClickedInventory()); page--;}
            case BODY_SIZE + 8 ->{save(e.getClickedInventory());page++;}
            default ->{return;}
        }
        open();
    }
}
