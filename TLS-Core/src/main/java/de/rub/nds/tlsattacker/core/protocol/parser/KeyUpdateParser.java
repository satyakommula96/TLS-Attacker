/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser;

import de.rub.nds.tlsattacker.core.constants.HandshakeByteLength;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.KeyUpdateRequest;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.KeyUpdateMessage;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KeyUpdateParser extends HandshakeMessageParser<KeyUpdateMessage> {
    private static final Logger LOGGER = LogManager.getLogger();

    public KeyUpdateParser(InputStream stream, ProtocolVersion version, TlsContext tlsContext) {
        super(stream, version, tlsContext);
    }

    @Override
    protected void parseHandshakeMessageContent(KeyUpdateMessage msg) {
        LOGGER.debug("Parsing KeyUpdateMessage");
        parseUpdateRequest(msg);
    }

    private void parseUpdateRequest(KeyUpdateMessage msg) {
        byte requestMode = parseByteField(HandshakeByteLength.KEY_UPDATE_LENGTH);
        if (requestMode == KeyUpdateRequest.UPDATE_REQUESTED.getValue()) {
            msg.setRequestMode(KeyUpdateRequest.UPDATE_REQUESTED);
        } else {
            msg.setRequestMode(KeyUpdateRequest.UPDATE_NOT_REQUESTED);
        }
        LOGGER.debug("KeyUpdateValue: " + msg.getRequestMode().getValue());

    }

}
