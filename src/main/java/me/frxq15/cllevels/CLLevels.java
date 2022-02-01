package me.frxq15.cllevels;

import me.frxq15.cllevels.Commands.LevelCommand;
import me.frxq15.cllevels.Commands.XPCommand;
import me.frxq15.cllevels.Commands.giveXPCommand;
import me.frxq15.cllevels.LevelManagement.LManager;
import me.frxq15.cllevels.LevelManagement.XPManager;
import me.frxq15.cllevels.SQLManagement.PlayerData;
import me.frxq15.cllevels.SQLManagement.SQLListeners;
import me.frxq15.cllevels.SQLManagement.SQLSetterGetter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class CLLevels extends JavaPlugin {
    public static File LevelsFile;
    public static FileConfiguration LevelsConfig;
    private static CLLevels instance;
    private static LManager lManager;
    private SQLSetterGetter sqlManager;
    private Connection connection;
    public String host, database, username, password, table;
    public int port;

    @Override
    public void onEnable() {
        instance = this;
        log("Plugin Enabling.");
        saveDefaultConfig();
        createLevelsFile();
        registry();
        startSavingTask();
    }
    void registry() {
        Bukkit.getPluginManager().registerEvents(new SQLListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new LManager(), this);
        Bukkit.getPluginManager().registerEvents(new XPManager(), this);
        getCommand("level").setExecutor(new LevelCommand());
        getCommand("xp").setExecutor(new XPCommand());
        getCommand("givexp").setExecutor(new giveXPCommand());
        sqlManager = new SQLSetterGetter();
        lManager = new LManager();
        SQLSetup();
    }

    @Override
    public void onDisable() {
        log("Plugin Disabled.");
        PlayerData.getAllPlayerData().forEach((uuid, playerData) -> sqlManager.setLevel(uuid, playerData.getLevel()));
        PlayerData.getAllPlayerData().forEach((uuid, playerData) -> sqlManager.setXP(uuid, playerData.getXP()));
    }
    public static CLLevels getInstance() { return instance; }
    public SQLSetterGetter getSqlManager() { return sqlManager; }
    public LManager getlManager() { return lManager; }
    public static String colourize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
    public static String formatMsg(String input) { return ChatColor.translateAlternateColorCodes('&', getInstance().getConfig().getString(input)); }
    public void log(String text) { Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[CLLevels] "+text);}
    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    public void SQLSetup() {
        host = getInstance().getConfig().getString("DATABASE." + "HOST");
        port = getInstance().getConfig().getInt("DATABASE." + "PORT");
        database = getInstance().getConfig().getString("DATABASE." + "DATABASE");
        username = getInstance().getConfig().getString("DATABASE." + "USERNAME");
        password = getInstance().getConfig().getString("DATABASE." + "PASSWORD");
        table = getInstance().getConfig().getString("DATABASE." + "TABLE");

        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password="+ password));
                log("Connected to MySQL successfully.");
                getSqlManager().createTable(table);

            }

        }catch(SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            log("Please setup your MySQL database in the config.yml.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public static FileConfiguration getLevelsFile() {
        return LevelsConfig;
    }
    public static void reloadLevelsFile() {
        LevelsConfig = YamlConfiguration.loadConfiguration(LevelsFile);
    }
    public static void saveLevelsFile() {
        try {
            LevelsConfig.save(LevelsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void createLevelsFile() {
        LevelsFile = new File(CLLevels.getInstance().getDataFolder(), "levels.yml");
        if (!LevelsFile.exists()) {
            LevelsFile.getParentFile().mkdirs();
            CLLevels.getInstance().saveResource("levels.yml", false);
        }
        LevelsConfig = new YamlConfiguration();
        try {
            LevelsConfig.load(LevelsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    private void startSavingTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> PlayerData.getAllPlayerData().forEach((uuid, playerData) -> sqlManager.setLevel(uuid, playerData.getLevel())), 20L * 60L * 5L, 20L * 60L * 5L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> PlayerData.getAllPlayerData().forEach((uuid, playerData) -> sqlManager.setXP(uuid, playerData.getXP())), 20L * 60L * 5L, 20L * 60L * 5L);
    }
}
