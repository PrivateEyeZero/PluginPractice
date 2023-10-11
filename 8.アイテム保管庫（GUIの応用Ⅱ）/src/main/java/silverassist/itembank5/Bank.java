package silverassist.itembank5;

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class Bank implements Listener {
    private static final int BODY_SIZE = 45;  //9の倍数
    private static final String YML_KEY = "bankData";
    private static final ItemStack BG_NORMAL = Util.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,"§r");
    private static final ItemStack BG_BACK = Util.createItem(Material.RED_STAINED_GLASS_PANE,"§c§l戻る");
    private static final ItemStack BG_NEXT = Util.createItem(Material.LIME_STAINED_GLASS_PANE,"§c§l次へ");
    private static final Map<Integer,ItemStack> ITEMS = new HashMap<>();
    private static FileConfiguration YML = ItemBank5.getYml();
    private static boolean isRunningSetup = false;

    private final JavaPlugin plugin;
    private final Player p;
    private final boolean isEdit;
    private boolean isUpdate = false;
    private int page = 0;
    //private ItemStack[] items = new ItemStack[BODY_SIZE];  //長さが決まってるときはListより配列
    //private static final List<ItemStack> items = new ArrayList<>();

    public Bank(Player p, boolean isEdit){
        plugin = ItemBank5.getInstance();
        plugin.getServer().getPluginManager().registerEvents(isEdit ? new EditEvent() : new UtilizeEvent(),plugin);
        this.p = p;
        this.isEdit = isEdit;
    }

    public void open(){
        if(isRunningSetup){
            p.sendMessage("§c現在セットアップ中です。時間を空けて再度お試しください");
            return;
        }
        Inventory inv = Bukkit.createInventory(p, BODY_SIZE + 9,"§d§lアイテムバンク page."+(page+1));
        for(int i = 0; i< BODY_SIZE; i++)inv.setItem(i,null);
        ITEMS.keySet().stream()
                .filter(id->(id>=BODY_SIZE*page && id<BODY_SIZE*(page+1)))
                .forEach(id->inv.setItem(id%45,ITEMS.get(id)));

        for(int i = BODY_SIZE; i< BODY_SIZE + 9; i++)inv.setItem(i,BG_NORMAL);
        if(page>0)inv.setItem(BODY_SIZE,BG_BACK);
        int lastKey = ITEMS.keySet().stream().sorted().collect(Collectors.toCollection (LinkedList::new)).getLast();
        if(page<lastKey/BODY_SIZE || isEdit)inv.setItem(BODY_SIZE+8,BG_NEXT);


        isUpdate = true; //更新処理中状態に
        p.openInventory(inv);
        isUpdate=false; //更新処理中状態解除
    }

    private void save(Inventory inv){
        int page = this.page;  //非同期中にページが変わるおそれがあるのでコピー
        Bukkit.getScheduler().runTaskAsynchronously(plugin,()->{
            for(int i = BODY_SIZE *page; i<(BODY_SIZE *(page+1)); i++){
                ItemStack item = inv.getItem(i% BODY_SIZE);
                //System.err.println(i+" - "+item);
                if(item==null){ YML.set(YML_KEY+"."+i,null);ITEMS.remove(i);}
                else{ YML.set(YML_KEY+"."+i,item);ITEMS.put(i,item);}
            }
            plugin.saveConfig();
        });
    }

    //Bankのデータを変数管理化
    //最初に実行！
    public static void setup(){
        YML = ItemBank5.getYml();
        if(YML==null)return;

        isRunningSetup = true;  //セットアップ中にする
        //セットアップはやや重いので非同期で実行
        Bukkit.getScheduler().runTaskAsynchronously(ItemBank5.getInstance(),()->{
            if(YML.get(YML_KEY)!=null) {
                ConfigurationSection cs = YML.getConfigurationSection(YML_KEY);
                cs.getKeys(false)  //keyを全取得
                        .stream()
                        .map(Integer::parseInt)  //keyを全て数字(int)に
                        .forEach(id -> ITEMS.put(id, YML.getItemStack(YML_KEY + "." + id)));  //keyに沿ってGUIを埋める
            }
            isRunningSetup = false;
        });
    }

    private class EditEvent implements Listener{
        @EventHandler
        public void onClose(InventoryCloseEvent e){
            Player p= (Player) e.getPlayer();
            if(!Bank.this.p.equals(p))return;
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
                case BODY_SIZE ->{if(e.getCurrentItem().equals(BG_BACK)){save(e.getClickedInventory()); page--;}}
                case BODY_SIZE + 8 ->{save(e.getClickedInventory());page++;}
                default ->{return;}
            }
            open();
        }
    }

    private class UtilizeEvent implements Listener{
        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(
                    !e.getWhoClicked().equals(p)  //違う人による操作の場合
                            || e.getCurrentItem()==null  //関係のないところをクリックした場合
                            || !e.getInventory().getType().equals(InventoryType.CHEST)  //クリックした部分が開いているものでない場合
            )return;
            e.setCancelled(true);
            int slot = e.getSlot();

            if(slot < BODY_SIZE)p.getInventory().addItem(new ItemStack(e.getCurrentItem()){{setAmount(getMaxStackSize());}});
            else{
                switch (slot){
                    case BODY_SIZE ->{if(e.getCurrentItem().equals(BG_BACK)){save(e.getClickedInventory()); page--;}}
                    case BODY_SIZE + 8 ->{if(e.getCurrentItem().equals(BG_BACK)){save(e.getClickedInventory());page++;}}
                    default ->{return;}
                }
                open();
            }

        }
    }




}
