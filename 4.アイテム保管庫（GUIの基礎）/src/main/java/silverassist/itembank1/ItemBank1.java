package silverassist.itembank1;

import org.bukkit.plugin.java.JavaPlugin;

public final class ItemBank1 extends JavaPlugin {
    private static JavaPlugin plugin = null;

    @Override
    public void onEnable() {
        plugin = this;
        Bank bank = new Bank();
        new Command(bank);
        // Plugin startup logic

    }

    public static JavaPlugin getInstance(){return plugin;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
