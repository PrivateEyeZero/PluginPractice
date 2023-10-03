package silverassist.dice3;


import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;

    public Command(){
        plugin = Dice3.getInstance();
        PluginCommand command = plugin.getCommand("dice");
        command.setExecutor(this);
        command.setTabCompleter(new Tab());  //tabクラスをこのコマンドのtab補完用として登録
    }


    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(args.length<1 || !(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(args.length > 0){
            switch (args[0]){
                case "get" ->{
                    ItemStack item = new ItemStack(Material.CONDUIT);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName("§d§lダイス");
                    item.setItemMeta(itemMeta);
                    p.getInventory().addItem(item);
                }
                case "spin" -> {
                    int rollMax = 6;  //目の最大値;
                    if(args.length > 1 && args[1].matches("\\d+"))rollMax = Integer.parseInt(args[1]);
                    int roll = Dice.spin(rollMax);
                    sender.sendMessage("出た目: "+roll);
                }
            }
        }
        return true;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
            return switch (args.length){
                case 1-> List.of("get","spin").stream().filter(e->e.matches("^"+args[0]+".*")).collect(Collectors.toList());
                default -> null;
            };
        }
    }
}
