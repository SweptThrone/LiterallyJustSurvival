package one.sweptthr.ljsplugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.*;

public class Main extends JavaPlugin {

	public static World ljsWorld;
	
	@Override
	public void onEnable() {
		getLogger().info("Enabled successfully!");
		getDataFolder().mkdir();
		getLogger().info("Data folder created successfully!");
		if ( Bukkit.getWorld( "LJSurvival" ) == null ) {
			WorldCreator creator = new WorldCreator( "LJSurvival" )
					.environment( World.Environment.valueOf( "NORMAL" ) )
					.generateStructures( true );
			creator.createWorld();
			getLogger().info("LJSurvival world successfully created!");
		}
		ljsWorld = Bukkit.getWorld( "LJSurvival" );
		getServer().getPluginManager().registerEvents( new DeathListener( this ), this );
		getServer().getPluginManager().registerEvents( new JoinListener( this ), this );
		getServer().getPluginManager().registerEvents( new RespawnListener( this ), this );
		getServer().getPluginManager().registerEvents( new WorldChangeListener( this ), this );
		getLogger().info( "Event listener registered." );
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Disabled successfully!");
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( command.getName().equalsIgnoreCase( "ljs" ) ) {
			if ( args.length == 0 ) {
				sender.sendMessage( "LJS Plugin by SweptThrone" );
			} else {
				if ( args[0].equalsIgnoreCase( "join" ) ) {
					if ( !( sender instanceof HumanEntity ) ) {
						sender.sendMessage( "§cOnly humans can use this command!" );
					} else {
						HumanEntity player = ( HumanEntity ) sender;
						if ( player.hasMetadata( "inLJS" ) && player.getMetadata( "inLJS" ).get( 0 ).asBoolean() ) {
							sender.sendMessage( "§cYou are already in the LJS world!" );
						} else {
							//save inventory
							File plyInv = new File( this.getDataFolder(), player.getUniqueId() + "_normal.yml" );
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
								sender.sendMessage( "§cAn error occurred while saving your info.  Check console." );
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
							File ljsDataFile = new File( this.getDataFolder(), player.getUniqueId() + "_ljs.yml" );
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
								
								sender.sendMessage( "§eYou have returned to the LJS world!" );
							} else {
								sender.sendMessage( "§eYou have joined the LJS world for the first time!" );
								Location ljsSpawn = new Location( ljsWorld, 
										ljsWorld.getSpawnLocation().getX(),
										ljsWorld.getSpawnLocation().getY(),
										ljsWorld.getSpawnLocation().getZ(), 0, 0);
								player.teleport( ljsSpawn );
							}
						player.setMetadata( "inLJS", new FixedMetadataValue( this, true ) );
						}
					}
				} else if ( args[0].equalsIgnoreCase( "leave" ) ) {
					
					if ( !( sender instanceof HumanEntity ) ) {
						sender.sendMessage( "§cOnly humans can use this command!" );
					} else {
						HumanEntity player = ( HumanEntity ) sender;
						if ( player.hasMetadata( "inLJS" ) && !player.getMetadata( "inLJS" ).get( 0 ).asBoolean() ) {
							sender.sendMessage( "§cYou are not in the LJS world!" );
						} else {
							//save inventory
							File plyInv = new File( this.getDataFolder(), player.getUniqueId() + "_ljs.yml" );
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
								sender.sendMessage( "§cAn error occurred while saving your info.  Check console." );
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
							File ljsDataFile = new File( this.getDataFolder(), player.getUniqueId() + "_normal.yml" );
							
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
							
							sender.sendMessage( "§eYou have returned to where you came!" );
						}
					player.setMetadata( "inLJS", new FixedMetadataValue( this, false ) );
					}
				} else {
					sender.sendMessage( "§cThat's not a valid command." );
				}
			}
			return true;
		}
		return false;
	}
	
}
