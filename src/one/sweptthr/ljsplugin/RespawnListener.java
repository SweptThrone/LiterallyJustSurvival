package one.sweptthr.ljsplugin;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class RespawnListener implements Listener {

	private Main plugin;
	
	public RespawnListener( Main plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLJSPlayerRespawn( PlayerRespawnEvent event ) {
		HumanEntity player = event.getPlayer();
		if ( player.getBedLocation().getWorld().getName() == "LJSurvival" ) {
			player.setMetadata( "inLJS", new FixedMetadataValue( this.plugin, true ) );
			player.teleport( player.getBedLocation() );
			player.sendMessage( "§eYou died in LJS.  You were respawned at your bed in LJS." );
		}
	}
	
}
