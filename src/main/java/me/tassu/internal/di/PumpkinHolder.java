package me.tassu.internal.di;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.tassu.internal.cfg.GeneralMessages;
import me.tassu.internal.cfg.MainConfig;
import org.spongepowered.api.plugin.PluginContainer;

public class PumpkinHolder {

    private static PumpkinHolder instance = null;
    public static PumpkinHolder getInstance() { return instance; }
    public PumpkinHolder() { instance = this; }

    public Injector injector;

    @Inject public GeneralMessages messages;
    @Inject public MainConfig mainConfig;

    @Inject public PluginContainer container;

}
