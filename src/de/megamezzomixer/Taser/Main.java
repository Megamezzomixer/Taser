package de.megamezzomixer.Taser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin implements Listener{
	HashMap<String, Boolean> tased = new HashMap<String, Boolean>();
	HashMap<String, Boolean> coolDown = new HashMap<String, Boolean>();
	
@Override
public void onEnable(){
		
		Log.info("Taser by Megamezzomixer ENABLED");
		getServer().getPluginManager().registerEvents(this, this);
		this.getConfig().addDefault("General.onlyPlayerCommand", "&4This Command can only be done as a Player.");
		this.getConfig().addDefault("General.notOnline", "&4This Player is not Online.");
		this.getConfig().addDefault("Taser.enabled", true);
		this.getConfig().addDefault("Taser.stunTime", 5);
		this.getConfig().addDefault("Taser.taserShootCooldown", 2);
		this.getConfig().addDefault("Taser.blindness", true);
		this.getConfig().addDefault("Taser.sound", true);
		this.getConfig().addDefault("Taser.gotStunnedMessage", "&4You are getting stunned");
		this.getConfig().addDefault("Taser.taserEndMessage", "&2You can move again");
		this.getConfig().addDefault("Taser.noPermission", "&4You don't know how to use the Taser. (No Permission)");
		this.getConfig().addDefault("Taser.antiSpamMessage", "&4The Taser is charging up...");
		this.getConfig().addDefault("Taser.disabledMessage", "&4The Taser is disabled.");
		this.getConfig().addDefault("Taser.loreText", "&3Is used to stun people");
		this.getConfig().addDefault("Taser.addedToInv", "&2The Taser is added to your inventory.");
		this.getConfig().options().copyDefaults(true);
	    saveConfig();
		
	}

    @Override
    public void onDisable(){
    	Log.info("Taser by Megamezzomixer DISABLED");
    }
    
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(cmd.getName().equalsIgnoreCase("taser")){
    		if(args.length == 0) {
    		sender.sendMessage("§3~=============================~");
    		sender.sendMessage("§6               Taser Plugin");
    		sender.sendMessage("§3               developed by");
    		sender.sendMessage("§b              Megamezzomixer");
    		sender.sendMessage("§3~=============================~");
    		sender.sendMessage("§6 /taser give §f- Adds a Taser in the inventory");
    		sender.sendMessage("§6 /taser reload §f- Reloads the config file");
    		return true;
    	}
    	
    	if(args.length == 1 && args[0].equalsIgnoreCase("give")){
    		boolean taserEnabled = this.getConfig().getBoolean("Taser.enabled");
    		if(taserEnabled == false){
        		String msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Taser.disabledMessage"));
        		sender.sendMessage(msg);
    			return true;
    		}
    		if(!(sender instanceof Player)){
    		String msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("General.onlyPlayerCommand"));
    		sender.sendMessage(msg);
    		}
    		if(!sender.hasPermission("taser.give") && !sender.hasPermission("taser.*")){
    			String msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Taser.noPermission"));
    			sender.sendMessage(msg);
    			return true;
    		}else {
    			ItemStack taserItem = new ItemStack(Material.STICK, 1);
    			ItemMeta meta = (ItemMeta) taserItem.getItemMeta();
    			List<String> lore = new ArrayList<String>();
    			lore.add(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Taser.loreText")));
    			meta.setLore(lore);
    			meta.setDisplayName("§6Taser");
    			taserItem.setItemMeta(meta);
    			Player p = (Player) sender;
    			p.getInventory().addItem(taserItem);
    			p.updateInventory();
    			String msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Taser.addedToInv"));
    			sender.sendMessage(msg);
    			return true;
    		}
    	}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("reload"))
		{
			
			if(sender.hasPermission("taser.reload") | sender.hasPermission("taser.*")){
				reloadConfig();
				sender.sendMessage("§2Config file reloaded.");	
			}
			else {
				sender.sendMessage("§c You have no permission to reload the config.");
			}
			return true;
		}
    }
    	return false;
    }
	
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
      Player p = e.getPlayer();
      if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){ 
    	  int time = getConfig().getInt("Taser.taserShootCooldown");
    	  if(!(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.STICK)){
    		  return;
    	  }
    	  if(!(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§6Taser") | e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("&#186;6Taser"))){
    		  p.sendMessage(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName());
    		  return;
    	  }
  		boolean taserEnabled = this.getConfig().getBoolean("Taser.enabled");
  		if(taserEnabled == false){
  			p.sendMessage("§cTaser is disabled.");
  			return;
  		}
    	  if(!p.hasPermission("taser.use") & !p.hasPermission("taser.*")){
    		  p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Taser.noPermission")));
    		  return;
    	  }
    	  if(!(coolDown.containsKey(e.getPlayer().getName()))){
    		  
    	  p.launchProjectile(Snowball.class).setMetadata("taser", new FixedMetadataValue(this, true));
    	    World w = p.getWorld();
    	    w.playSound(p.getLocation(), Sound.NOTE_STICKS, 10, 1);
    	    coolDown.put(e.getPlayer().getName(), true);
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				public void run() {
					coolDown.remove(e.getPlayer().getName());
				  }
				}, time * 20L);// 60 L == 3 sec, 20 ticks == 1 sec
    	  }else{
				String msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Taser.antiSpamMessage"));
    		  p.sendMessage(msg);
    		  return;
    	  }
      }
  }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
    		if(e.getDamager().hasMetadata("taser")){
    			if(tased.containsKey(e.getEntity().getName())){
    				return;
    			}
    			Player p = (Player)e.getEntity();
    			World w = p.getWorld();
    			if(p.hasPermission("taser.exempt"))
    			{
    				w.playSound(p.getLocation(), Sound.FIZZ, 0.5F, 1);
    				e.setCancelled(true);
    				return;
    			}
    			int time = getConfig().getInt("Taser.stunTime");
    			tased.put(e.getEntity().getName(), true);
				String msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Taser.gotStunnedMessage"));
				e.getEntity().sendMessage(msg);
    			
    			if(this.getConfig().getBoolean("Taser.sound"))
    			{
    			
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 2);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 2);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 2);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 2);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
        	    w.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
    			}
    			if(this.getConfig().getBoolean("Taser.blindness"))
    			{
    			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 5));
    			}
    			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
    				public void run() {
    					tased.remove(e.getEntity().getName());
    					String msg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Taser.taserEndMessage"));
    					e.getEntity().sendMessage(msg);
    				  }
    				}, time * 20L);// 60 L == 3 sec, 20 ticks == 1 sec
    			
    			return;
    	}
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	
		ItemStack taserItem = new ItemStack(Material.STICK, 1);
		ItemMeta meta = (ItemMeta) taserItem.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Taser.loreText")));
		meta.setLore(lore);
		meta.setDisplayName("§6Taser");
		taserItem.setItemMeta(meta);
		
		event.getPlayer().getInventory().remove(taserItem);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
    	if(tased.containsKey(e.getPlayer().getName())){
    		Location loc = e.getFrom();
    		loc.setX(loc.getBlockX()+0.5);
    		loc.setY(loc.getBlockY());
    		loc.setZ(loc.getBlockZ()+0.5);
    		if (!(e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR))) {
    		e.getPlayer().teleport(loc);
    		e.setCancelled(true);
    		}
    	}
	}
}