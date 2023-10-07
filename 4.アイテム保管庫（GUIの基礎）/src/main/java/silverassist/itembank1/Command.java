package silverassist.itembank1;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Bank bank;
    public Command(Bank bank){
        this.bank = bank;
        plugin = ItemBank1.getInstance();
        PluginCommand pluginCommand = plugin.getCommand("itembank");
        pluginCommand.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(!p.isOp())return true;
        bank.open(p);
        return true;
    }
}
