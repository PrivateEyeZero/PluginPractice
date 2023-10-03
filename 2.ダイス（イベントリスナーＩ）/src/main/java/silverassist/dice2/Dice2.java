package silverassist.dice2;

import org.bukkit.plugin.java.JavaPlugin;

public final class Dice2 extends JavaPlugin {
    private static JavaPlugin plugin = null;  //privateにして外部から書き換えられないようにする
    @Override
    public void onEnable() {
        plugin = this;  //このJavaPluginのインスタンスを保持
        new Command();  //Commandのクラスを起動
        new Dice();  //diceクラスの起動
    }

    public static JavaPlugin getInstance(){return plugin;}  //インスタンスを取得する関数

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
