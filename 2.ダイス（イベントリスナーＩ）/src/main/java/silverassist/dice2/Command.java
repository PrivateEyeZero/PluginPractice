package silverassist.dice2;


import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;

    public Command(){
        plugin = Dice2.getInstance();
        PluginCommand command = plugin.getCommand("dice");
        command.setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        int rollMax = 6;  //目の最大値;
        if(args.length > 0 && args[0].matches("\\d+"))rollMax = Integer.parseInt(args[0]);
        // ---------------    -----------------------------
        // ┗引数が設定されているか ┗一つ目の引数が整数か
        int roll = (int) (Math.random()*rollMax) + 1;
        //               -----------------------
        //               [0]以上[rollMax]未満の範囲で乱数を生成
        sender.sendMessage("出た目: "+roll);
        return true;  //コマンドが終了したときはtrueを返す
    }
}
