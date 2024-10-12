package one.sweptthr.ljsplugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

public class WorldChangeListener implements Listener {

private Main plugin;
	
	public WorldChangeListener( Main plugin ) {
		this.plugin = plugin;
	}
	
	// a lot of this could be alleviated and condensed a LOT by using functions,
	// but this is the first time i've done anything of this scale in Java,
	// and i'm not fully sure how i would define functions scope-wise
	
	// also this doesn't work anyway, and i'm not fully sure why
	@EventHandler
	public void onLJSPlayerWorldChanged( PlayerChangedWorldEvent event ) {
		HumanEntity player = event.getPlayer();
		if ( event.getFrom().getName() == "LJSurvival" ) {
			player.setMetadata( "inLJS", new FixedMetadataValue( this.plugin, false ) );
			player.sendMessage( "§cAn improper world changed was detected, your normal data was loaded." );
			player.sendMessage( "§cIn the future, use /ljs leave to leave LJS." );
			
			File plyInv = new File( this.plugin.getDataFolder(), player.getUniqueId() + "_ljs.yml" );
			YamlConfiguration config = new YamlConfiguration();
			for ( int i = 0; i < 40; i++ ) {
				ItemStack[] items = player.getInventory().getContents(); //inventory.getContents() array of ItemStack, .getItem(index)
				config.set( Integer.toString( i ), items[i] );
			}
			config.set( "offhand", player.getInventory().getItemInOffHand() );
			//save position
			config.set( "position", player.getLocation() );
			//save gamemode
			config.set( "gamemode", player.getGameMode().toString() );
			//save exp
			config.set( "exp", ((Player) player).getExp() );
			config.set( "level", ((Player) player).getLevel() );
			//save hp and hunger for that sick consistency
			config.set( "health", player.getHealth() );
			config.set( "hunger", ((Player) player).getFoodLevel() );
			config.set( "saturation", ((Player) player).getSaturation() );
			//save potion effects
			int numPot = -1;
			for ( PotionEffect pe : player.getActivePotionEffects() ) {
				numPot++;
				config.set( "potions" + numPot, pe );
			}
			config.set( "numPots", numPot );
			//save file
			try {
				config.save( plyInv );
			} catch (IOException e) {
				e.printStackTrace();
				player.sendMessage( "§cAn error occurred while saving your info.  Check console." );
			}
			//clear current stuff
			player.getInventory().clear();
			((Player) player).setLevel( 0 );
			((Player) player).setExp( 0 );
			//set to survival
			for ( PotionEffect pe : player.getActivePotionEffects() ) {
				player.removePotionEffect( pe.getType() );
			}
			//send player to world
			File ljsDataFile = new File( this.plugin.getDataFolder(), player.getUniqueId() + "_normal.yml" );
			
			YamlConfiguration ljsData = YamlConfiguration.loadConfiguration( ljsDataFile );
			Location ljsTPTo = (Location) ljsData.get( "position" );
			player.teleport( ljsTPTo );
			for ( int i = 0; i < 40; i++ ) {
				player.getInventory().setItem( i, ( ItemStack ) ljsData.get( Integer.toString( i ) ) );
			}
			player.getInventory().setItemInOffHand( ( ItemStack ) ljsData.get( "offhand" ) );
			player.setGameMode( GameMode.valueOf( ljsData.get( "gamemode" ).toString() ) );
			( (Player) player ).setLevel( (int) ljsData.get( "level" ) );
			( (Player) player ).setExp( (( Double ) ljsData.get( "exp" )).floatValue() );
			( (Player) player ).setHealth( ( Double ) ljsData.get( "health" ) );
			( (Player) player ).setFoodLevel( ( Integer ) ljsData.get( "hunger" ) );
			( (Player) player ).setSaturation( (( Double ) ljsData.get( "saturation" )).floatValue() );
			//potions
			for ( int i = 0; i < ( ( Integer ) ljsData.get( "numPots" ) ) + 1; i++ ) {
				player.addPotionEffect( ( PotionEffect ) ljsData.get( "potions" + i ) );
			}
		}
		if ( player.getWorld().getName() == "LJSurvival" ) {
			player.setMetadata( "inLJS", new FixedMetadataValue( this.plugin, true ) );
			player.sendMessage( "§cAn improper world changed was detected, your LJS data was loaded." );
			player.sendMessage( "§cIn the future, use /ljs join to join LJS." );
			
			//save inventory
			File plyInv = new File( this.plugin.getDataFolder(), player.getUniqueId() + "_normal.yml" );
			YamlConfiguration config = new YamlConfiguration();
			for ( int i = 0; i < 40; i++ ) {
				ItemStack[] items = player.getInventory().getContents(); //inventory.getContents() array of ItemStack, .getItem(index)
				config.set( Integer.toString( i ), items[i] );
			}
			config.set( "offhand", player.getInventory().getItemInOffHand() );
			//save position
			config.set( "position", player.getLocation() );
			//save gamemode
			config.set( "gamemode", player.getGameMode().toString() );
			//save exp
			config.set( "exp", ((Player) player).getExp() );
			config.set( "level", ((Player) player).getLevel() );
			//save hp and hunger for that sick consistency
			config.set( "health", player.getHealth() );
			config.set( "hunger", ((Player) player).getFoodLevel() );
			config.set( "saturation", ((Player) player).getSaturation() );
			//save potion effects
			int numPot = -1;
			for ( PotionEffect pe : player.getActivePotionEffects() ) {
				numPot++;
				config.set( "potions" + numPot, pe );
			}
			config.set( "numPots", numPot );
			//save file
			try {
				config.save( plyInv );
			} catch (IOException e) {
				e.printStackTrace();
				player.sendMessage( "§cAn error occurred while saving your info.  Check console." );
			}
			//clear current stuff
			player.getInventory().clear();
			((Player) player).setLevel( 0 );
			((Player) player).setExp( 0 );
			for ( PotionEffect pe : player.getActivePotionEffects() ) {
				player.removePotionEffect( pe.getType() );
			}
			//set to survival
			player.setGameMode( GameMode.SURVIVAL );
			//send player to world
			File ljsDataFile = new File( this.plugin.getDataFolder(), player.getUniqueId() + "_ljs.yml" );
			if ( ljsDataFile.exists() ) {
				YamlConfiguration ljsData = YamlConfiguration.loadConfiguration( ljsDataFile );
				Location ljsTPTo = (Location) ljsData.get( "position" );
				player.teleport( ljsTPTo );
				for ( int i = 0; i < 40; i++ ) {
					player.getInventory().setItem( i, ( ItemStack ) ljsData.get( Integer.toString( i ) ) );
				}
				player.getInventory().setItemInOffHand( ( ItemStack ) ljsData.get( "offhand" ) );
				( (Player) player ).setLevel( (int) ljsData.get( "level" ) );
				( (Player) player ).setExp( (( Double ) ljsData.get( "exp" )).floatValue() );
				( (Player) player ).setHealth( ( Double ) ljsData.get( "health" ) );
				( (Player) player ).setFoodLevel( ( Integer ) ljsData.get( "hunger" ) );
				( (Player) player ).setSaturation( (( Double ) ljsData.get( "saturation" )).floatValue() );
				//potions
				for ( int i = 0; i < ( ( Integer ) ljsData.get( "numPots" ) ) + 1; i++ ) {
					player.addPotionEffect( ( PotionEffect ) ljsData.get( "potions" + i ) );
				}
				
				player.sendMessage( "§eYou have returned to the LJS world!" );
			} else {
				player.sendMessage( "§eYou have joined the LJS world for the first time!" );
				World ljsWorld = one.sweptthr.ljsplugin.Main.ljsWorld;
				Location ljsSpawn = new Location( ljsWorld, 
						ljsWorld.getSpawnLocation().getX(),
						ljsWorld.getSpawnLocation().getY(),
						ljsWorld.getSpawnLocation().getZ(), 0, 0);
				player.teleport( ljsSpawn );
			}
			
		}
	}
	
}
