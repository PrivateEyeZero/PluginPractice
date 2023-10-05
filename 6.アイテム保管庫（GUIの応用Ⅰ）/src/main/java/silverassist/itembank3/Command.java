package silverassist.itembank3;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;
    public Command(){
        plugin = ItemBank3.getInstance();
        PluginCommand pluginCommand = plugin.getCommand("itembank");
        pluginCommand.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(!p.isOp())return true;
        new Bank(p).open();
        return true;
    }
}
