package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.github.mikesafonov.smpp.config.SmppProperties;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.concurrent.Executors;

import static com.github.mikesafonov.smpp.core.utils.Utils.getOrDefault;
import static com.github.mikesafonov.smpp.core.utils.Utils.validateName;
import static java.util.Objects.requireNonNull;

/**
 * Factory for {@link ConnectionManager}
 */
public class ConnectionManagerFactory {

    public ConnectionManager transmitter(@NotBlank String name,
                                         @NotNull SmppProperties.Defaults defaults,
                                         @NotNull SmppProperties.SMSC smsc) {
        validateName(name);
        requireNonNull(defaults);
        requireNonNull(smsc);

        boolean loggingBytes = getOrDefault(smsc.getLoggingBytes(), defaults.isLoggingBytes());
        boolean loggingPdu = getOrDefault(smsc.getLoggingPdu(), defaults.isLoggingPdu());
        int windowsSize = getOrDefault(smsc.getWindowSize(), defaults.getWindowSize());
        int maxTry = getOrDefault(smsc.getMaxTry(), defaults.getMaxTry());

        TransmitterConfiguration transmitterConfiguration = new TransmitterConfiguration(name,
            smsc.getCredentials(), loggingBytes, loggingPdu, windowsSize, smsc.getSystemType());

        DefaultSmppClient client = new DefaultSmppClient();
        return new TransmitterConnectionManager(client, transmitterConfiguration, maxTry);
    }

    public ConnectionManager receiver(@NotBlank String name,
                                      @NotNull SmppProperties.Defaults defaults,
                                      @NotNull SmppProperties.SMSC smsc,
                                      @Nullable SmppSessionHandler smppSessionHandler) {
        validateName(name);
        requireNonNull(defaults);
        requireNonNull(smsc);

        boolean loggingBytes = getOrDefault(smsc.getLoggingBytes(), defaults.isLoggingBytes());
        boolean loggingPdu = getOrDefault(smsc.getLoggingPdu(), defaults.isLoggingPdu());
        long rebindPeriod = getOrDefault(smsc.getRebindPeriod(), defaults.getRebindPeriod()).getSeconds();

        ReceiverConfiguration receiverConfiguration = new ReceiverConfiguration(name, smsc.getCredentials(),
            loggingBytes, loggingPdu);
        DefaultSmppClient client = new DefaultSmppClient();

        return new ReceiverConnectionManager(
            client, receiverConfiguration,
            smppSessionHandler,
            rebindPeriod,
            Executors.newSingleThreadScheduledExecutor()
        );
    }

    public ConnectionManager transceiver(@NotBlank String name,
                                         @NotNull SmppProperties.Defaults defaults,
                                         @NotNull SmppProperties.SMSC smsc,
                                         @Nullable SmppSessionHandler smppSessionHandler) {
        validateName(name);
        requireNonNull(defaults);
        requireNonNull(smsc);

        boolean loggingBytes = getOrDefault(smsc.getLoggingBytes(), defaults.isLoggingBytes());
        boolean loggingPdu = getOrDefault(smsc.getLoggingPdu(), defaults.isLoggingPdu());
        int windowsSize = getOrDefault(smsc.getWindowSize(), defaults.getWindowSize());
        int maxTry = getOrDefault(smsc.getMaxTry(), defaults.getMaxTry());


        TransceiverConfiguration configuration = new TransceiverConfiguration(name, smsc.getCredentials(),
            loggingBytes, loggingPdu, windowsSize, smsc.getSystemType());
        DefaultSmppClient client = new DefaultSmppClient();

        return new TransceiverConnectionManager(client, configuration, smppSessionHandler, maxTry);

    }
}
