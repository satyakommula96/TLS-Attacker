/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.constants.ExtensionByteLength;
import de.rub.nds.tlsattacker.core.protocol.message.extension.HeartbeatExtensionMessage;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HeartbeatExtensionParser extends ExtensionParser<HeartbeatExtensionMessage> {

    private static final Logger LOGGER = LogManager.getLogger();

    public HeartbeatExtensionParser(InputStream stream, TlsContext tlsContext) {
        super(stream, tlsContext);
    }

    @Override
    public void parseExtensionMessageContent(HeartbeatExtensionMessage msg) {
        LOGGER.debug("Parsing HeartbeatExtensionMessage");
        parseHeartbeatMode(msg);
    }

    /**
     * Reads the next bytes as the HeartbeatMode of the Extension and writes them in the message
     *
     * @param msg
     *            Message to write in
     */
    private void parseHeartbeatMode(HeartbeatExtensionMessage msg) {
        msg.setHeartbeatMode(parseByteArrayField(ExtensionByteLength.HEARTBEAT_MODE));
        LOGGER.debug("HeartbeatMode: " + ArrayConverter.bytesToHexString(msg.getHeartbeatMode().getValue()));
    }

}
