package org.satellite.dev.progiple.mcchunkloader.chunkloaders.particles;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.novasparkle.lunaspring.API.Util.utilities.LunaMath;

import java.util.Objects;

@RequiredArgsConstructor
public class SpawnParticleTask extends BukkitRunnable {
    private final ConfigurationSection section;
    private final Location location;

    private boolean isActive = false;

    @Override @SneakyThrows @SuppressWarnings("all")
    public void run() {
        this.isActive = true;

        Vector vector = this.location.clone().add(0.5, 0, 0.5).toVector();
        double radius = section.getDouble("radius");
        double y = this.location.getBlockY();

        String[] split = Objects.requireNonNull(section.getString("color")).split(", ");
        Color color = Color.fromBGR(LunaMath.toInt(split[0]), LunaMath.toInt(split[1]), LunaMath.toInt(split[2]));

        int msTime = this.section.getInt("scroll_time");

        double additiveHeight = 0;
        while (true) {
            if (!this.isActive) return;

            Particle.DustOptions dustOptions = new Particle.DustOptions(color, 0.85F);
            ParticleLine particleLine = new ParticleLine(radius, dustOptions);

            particleLine.spawn(vector, this.location.getWorld(), (y - radius) + additiveHeight);

            additiveHeight = y - radius + additiveHeight >= y + radius * 2 ?
                    0 : additiveHeight + this.section.getDouble("one_scroll_blocks");
            Thread.sleep(msTime <= 10 ? 250 : msTime);
        }
    }

    public void cancel() {
        this.isActive = false;
    }
}
