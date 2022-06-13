package com.existencesmp.tablist

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.math.roundToInt

class Tablist : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()

        INSTANCE = this

        server.pluginManager.registerEvents(PlayerJoin(), this)
    }

    companion object {
        lateinit var INSTANCE: Tablist

        private val GREEN: TextColor = TextColor.color(0x66ff88)
        private val YELLOW: TextColor = TextColor.color(0xffba52)
        private val RED: TextColor = TextColor.color(0xff5252)

        val TASKS: MutableMap<UUID, Int> = mutableMapOf()

        fun renderTpsComponent(tps: Double): TextComponent {
            val roundedTps: Double = ((tps * 100.0).roundToInt() / 100.0).coerceAtMost(20.0)
            val color: TextColor = if (tps > 18.0) GREEN else if (tps > 16.0) YELLOW else RED
            return Component.text(roundedTps).color(color)
        }

        fun renderPingComponent(ping: Int): TextComponent {
            val color: TextColor = if (ping > 300) RED else if (ping > 150) YELLOW else GREEN
            return Component.text(ping).color(color)
        }

        fun renderComponentFor(player: Player): TextComponent {
            val component: TextComponent = renderTpsComponent(player.server.tps[0])
                .append(Component.text("tps - ").color(NamedTextColor.GRAY))
                .append(renderPingComponent(player.ping))
                .append(Component.text("ms").color(NamedTextColor.GRAY))
            return component
        }
    }
}

class PlayerJoin : Listener {
    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        val task: Int = player.server.scheduler.scheduleSyncRepeatingTask(Tablist.INSTANCE, Runnable { player.sendPlayerListFooter(Tablist.renderComponentFor(player)) }, 0, 100)
        Tablist.TASKS[player.identity().uuid()] = task
    }

    @EventHandler
    fun playerQuit(event: PlayerQuitEvent) {
        val player: Player = event.player
        Tablist.TASKS[player.identity().uuid()]?.let {
            player.server.scheduler.cancelTask(it)
            Tablist.TASKS.remove(player.identity().uuid())
        }
    }
}