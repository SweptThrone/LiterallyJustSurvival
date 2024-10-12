package one.sweptthr.ljsplugin;
import java.io.File;
//import java.io.IOException;

import org.bukkit.GameMode;
//import org.bukkit.Location;
//import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
//import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

public class DeathListener implements Listener {

	private Main plugin;
	
	public DeathListener( Main plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLJSPlayerDeath( PlayerDeathEvent event ) {
		HumanEntity player = event.getEntity();
		if ( player.hasMetadata( "inLJS" ) && player.getMetadata( "inLJS" ).get( 0 ).asBoolean() ) {
			player.setMetadata( "inLJS", new FixedMetadataValue( this.plugin, false ) );
			
				//save inventory
			
				File plyInv = new File( this.plugin.getDataFolder(), player.getUniqueId() + "_ljs.yml" );
				plyInv.delete();
				
				//send player to world
				File ljsDataFile = new File( this.plugin.getDataFolder(), player.getUniqueId() + "_normal.yml" );
				
				YamlConfiguration ljsData = YamlConfiguration.loadConfiguration( ljsDataFile );
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
			
			event.getEntity().sendMessage( "§eYou died in LJS.  Feel free to return at any time." );
		}
	}
	
}
