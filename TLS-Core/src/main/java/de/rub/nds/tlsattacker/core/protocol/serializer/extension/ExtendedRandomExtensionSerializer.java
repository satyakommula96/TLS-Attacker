/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2020 Ruhr University Bochum, Paderborn University,
 * and Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.ExtendedRandomExtensionMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class which serializes the Extended Random Extension for Usage as in Handshake Messages, as defined as in
 * https://tools.ietf.org/html/draft-rescorla-tls-extended-random-02
 */
public class ExtendedRandomExtensionSerializer extends ExtensionSerializer<ExtendedRandomExtensionMessage> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ExtendedRandomExtensionMessage message;

    public ExtendedRandomExtensionSerializer(ExtendedRandomExtensionMessage message) {
        super(message);
        this.message = message;
    }

    @Override
    public byte[] serializeExtensionContent() {
        appendBytes(message.getExtendedRandom().getValue());
        LOGGER.debug("Serialized Extended Random of length " + message.getExtendedRandom().getValue().length);
        return getAlreadySerialized();
    }
}
