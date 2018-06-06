package me.tassu.cfg;

import com.google.common.io.CharStreams;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import me.tassu.Pumpkin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class DefaultConfigurationTest {

    private static Config defaultConfig = null;

    @BeforeClass
    public static void readDefaultConfig() throws IOException {
        InputStream input = Pumpkin.class.getResourceAsStream("/pumpkin.conf");
        String loadedConfig = CharStreams.toString(new InputStreamReader(input));

        defaultConfig = ConfigFactory.parseString(loadedConfig);
    }

    @Test
    public void testConfigMeta() {
        assertTrue(defaultConfig.hasPathOrNull("pumpkin.meta.config-version"));
        assertFalse(defaultConfig.getIsNull("pumpkin.meta.config-version"));
        assertEquals(1, defaultConfig.getInt("pumpkin.meta.config-version"));
    }

}
