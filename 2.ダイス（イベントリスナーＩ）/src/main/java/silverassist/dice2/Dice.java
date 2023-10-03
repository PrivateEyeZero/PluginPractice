package silverassist.dice2;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Dice implements Listener {
    private final JavaPlugin plugin;
    public Dice(){
        plugin = Dice2.getInstance();

        //このクラスのイベントをサーバに登録
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDiceUse(PlayerInteractEvent e){
        //右クリック以外のイベントなら終了
        if(!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))return;

        Player p = e.getPlayer(); //プレイヤを取得
        //クリックした人のインベントリのメインハンドに当たる部分のアイテムが「コンジット」以外なら終了
        if(!p.getInventory().getItemInMainHand().getType().equals(Material.CONDUIT))return;
        int roll = spin(6);
        p.sendMessage("出た目: "+roll);
    }

    //インスタンス内のモノに関与するものでなく、どこからでもアクセスしても問題ない処理なので静的(static)関数化
    public static int spin(int max){
        return (int)(Math.random() * max + 1);
    }
}
