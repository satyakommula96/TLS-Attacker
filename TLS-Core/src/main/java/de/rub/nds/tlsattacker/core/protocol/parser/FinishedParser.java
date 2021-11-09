/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.FinishedMessage;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FinishedParser extends HandshakeMessageParser<FinishedMessage> {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Constructor for the Parser class
     *
     * @param stream
     * @param config
     * @param version
     *                Version of the Protocol
     */
    public FinishedParser(InputStream stream, ProtocolVersion version, Config config) {
        super(stream, HandshakeMessageType.FINISHED, version, config);
    }

    @Override
    protected void parseHandshakeMessageContent(FinishedMessage msg) {
        LOGGER.debug("Parsing FinishedMessage");
        parseVerifyData(msg);
    }

    @Override
    protected FinishedMessage createHandshakeMessage() {
        return new FinishedMessage();
    }

    /**
     * Reads the next bytes as the VerifyData and writes them in the message
     *
     * @param msg
     *            Message to write in
     */
    private void parseVerifyData(FinishedMessage msg) {
        msg.setVerifyData(parseByteArrayField(msg.getLength().getValue()));
        LOGGER.debug("VerifyData: " + ArrayConverter.bytesToHexString(msg.getVerifyData().getValue()));
    }

}
