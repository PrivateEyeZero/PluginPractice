package silverassist.dice1;

import org.bukkit.plugin.java.JavaPlugin;

public final class Dice1 extends JavaPlugin {
    private static JavaPlugin plugin = null;
    @Override
    public void onEnable() {
        plugin = this;  //このJavaPluginのインスタンスを保持

        // Plugin startup logic

    }

    public static JavaPlugin getInstance(){return plugin;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
