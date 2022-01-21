package xyz.nuark.tillassist;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("tillassist")
public class Tillassist {
    public Tillassist() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
