package one.sweptthr.ljsplugin;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class JoinListener implements Listener {

	private Main plugin;
	
	public JoinListener( Main plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLJSPlayerJoin( PlayerJoinEvent event ) {
		HumanEntity player = event.getPlayer();
		if ( player.getLocation().getWorld().getName() == "LJSurvival" ) {
			player.setMetadata( "inLJS", new FixedMetadataValue( this.plugin, true ) );
			player.sendMessage( "§eYou rejoined and were returned to LJS." );
		}
	}
	
}
