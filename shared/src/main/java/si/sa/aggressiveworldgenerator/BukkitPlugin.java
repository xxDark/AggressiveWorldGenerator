package si.sa.aggressiveworldgenerator;

import com.google.common.collect.MapMaker;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;

public final class BukkitPlugin extends JavaPlugin {

    private final Map<World, AggressiveWorldGenerator> generatorMap = new MapMaker()
            .weakKeys()
            .makeMap();
    private Constructor<? extends AggressiveWorldGenerator> generator;

    @Override
    public void onEnable() {
        try {
            String nmsVersion = getServer().getClass().getDeclaredField("console").getType().getPackage().getName().split("\\.")[3];
            Class<? extends AggressiveWorldGenerator> clazz = (Class<? extends AggressiveWorldGenerator>) getClassLoader().loadClass("si.sa.aggressiveworldgenerator.AggressiveWorldGenerator_" + nmsVersion);
            generator = (Constructor<? extends AggressiveWorldGenerator>) clazz.getDeclaredConstructors()[0];
            generator.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException ex) {
            getLogger().log(Level.SEVERE, "Exception while loading plugin", ex);
            setEnabled(false);
            return;
        }
        getServer().getCommandMap().register("aggressiveworldgenerator", new BukkitCommand("aggressiveworldgenerator") {

            @Override
            public boolean execute(CommandSender sender, String __, String[] args) {
                if (!(sender instanceof Player) || !sender.hasPermission("aggressiveworldgenerator.use")) return false;
                switch (args[0].toLowerCase()) {
                    case "status": {
                        generatorMap.values().stream().map(AggressiveWorldGenerator::getStatus).forEach(sender::sendMessage);
                        break;
                    }
                    case "stop": {
                        if (generatorMap.remove(((Player) sender).getWorld()) != null) {
                            sender.sendMessage("§aCancelled generation");
                        }
                        break;
                    }
                    case "pause": {
                        AggressiveWorldGenerator generator = generatorMap.get(((Player) sender).getWorld());
                        if (generator != null) {
                            generator.paused = true;
                            sender.sendMessage("§aGeneration paused");
                        }
                        break;
                    }
                    case "resume": {
                        AggressiveWorldGenerator generator = generatorMap.get(((Player) sender).getWorld());
                        if (generator != null) {
                            generator.paused = false;
                            sender.sendMessage("§aGeneration resumed");
                        }
                        break;
                    }
                    case "start": {
                        World world = ((Player) sender).getWorld();
                        if (generatorMap.containsKey(world)) {
                            sender.sendMessage("§cAlready have a generator for this world!");
                            break;
                        }
                        AggressiveWorldGenerator generator;
                        try {
                            generator = BukkitPlugin.this.generator.newInstance(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                            throw new RuntimeException(ex);
                        }
                        generatorMap.put(world, generator);
                        sender.sendMessage("§aGenerator added");
                        break;
                    }
                }
                return true;
            }
        });
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Map<World, AggressiveWorldGenerator> generatorMap = BukkitPlugin.this.generatorMap;
                if (generatorMap.isEmpty()) return;
                if (generatorMap.entrySet().removeIf(e -> e.getValue().tick())) {
                    getLogger().info("One of the generators did it's job!");
                }
            }
        }, 0, 1);
    }

    @Override
    public void onDisable() {
        generatorMap.clear();
    }
}
