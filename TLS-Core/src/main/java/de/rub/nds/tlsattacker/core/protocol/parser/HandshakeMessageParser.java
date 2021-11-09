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
import de.rub.nds.tlsattacker.core.constants.HandshakeByteLength;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.HandshakeMessage;
import de.rub.nds.tlsattacker.core.protocol.message.extension.ExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.ExtensionListParser;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An abstract Parser class for HandshakeMessages
 *
 * @param <T>
 *            Type of the HandshakeMessages to parse
 */
public abstract class HandshakeMessageParser<T extends HandshakeMessage> extends TlsMessageParser<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The expected value for the Type field of the Message
     */
    private final HandshakeMessageType expectedType;

    private ProtocolVersion version;

    /**
     * Constructor for the Parser class
     *
     * @param stream
     * @param expectedType
     *                     The expected type of the parsed HandshakeMessage
     * @param version
     *                     The Version with which this message should be parsed
     * @param config
     *                     A Config used in the current context
     */
    public HandshakeMessageParser(InputStream stream, HandshakeMessageType expectedType, ProtocolVersion version,
        Config config) {
        super(stream, version, config);
        this.expectedType = expectedType;
        this.version = version;
    }

    /**
     * Reads the next bytes as a HandshakeMessageType and writes them in the message
     *
     * @param message
     *                Message to write in
     */
    private void parseType(HandshakeMessage message) {
        message.setType(parseByteField(HandshakeByteLength.MESSAGE_TYPE));
        if (message.getType().getValue() != expectedType.getValue() && expectedType != HandshakeMessageType.UNKNOWN) {
            LOGGER.warn("Parsed wrong message type. Parsed:" + message.getType().getValue() + " but expected:"
                + expectedType.getValue());
        }
        LOGGER.debug("Type:" + message.getType().getValue());
    }

    /**
     * Reads the next bytes as the MessageLength and writes them in the message
     *
     * @param message
     *                Message to write in
     */
    private void parseLength(HandshakeMessage message) {
        message.setLength(parseIntField(HandshakeByteLength.MESSAGE_LENGTH_FIELD));
        LOGGER.debug("Length:" + message.getLength().getValue());
    }

    @Override
    protected T parseMessageContent() {
        T msg = createHandshakeMessage();
        parseType(msg);
        parseLength(msg);
        parseHandshakeMessageContent(msg);
        return msg;
    }

    protected abstract void parseHandshakeMessageContent(T msg);

    protected abstract T createHandshakeMessage();

    /**
     * Reads the next bytes as the ExtensionLength and writes them in the message
     *
     * @param message
     *                Message to write in
     */
    protected void parseExtensionLength(T message) {
        message.setExtensionsLength(parseIntField(HandshakeByteLength.EXTENSION_LENGTH));
        LOGGER.debug("ExtensionLength:" + message.getExtensionsLength().getValue());
    }

    /**
     * Reads the next bytes as the ExtensionBytes and writes them in the message and adds parsed Extensions to the
     * message
     *
     * @param message
     *                                 Message to write in
     * @param version
     * @param talkingConnectionEndType
     * @param helloRetryRequestHint
     */
    protected void parseExtensionBytes(T message, ProtocolVersion version, ConnectionEndType talkingConnectionEndType,
        boolean helloRetryRequestHint) {
        byte[] extensionBytes = parseByteArrayField(message.getExtensionsLength().getValue());
        message.setExtensionBytes(extensionBytes);
        LOGGER.debug("ExtensionBytes:" + ArrayConverter.bytesToHexString(extensionBytes, false));

        ByteArrayInputStream innerStream = new ByteArrayInputStream(extensionBytes);
        ExtensionListParser parser =
            new ExtensionListParser(innerStream, config, talkingConnectionEndType, version, helloRetryRequestHint);
        List<ExtensionMessage> extensionMessages = parser.parse();

        message.setExtensions(extensionMessages);
    }

    /**
     * Checks if the message has an ExtensionLength field, by checking if there are more bytes in the inputstream
     *
     * @param  message
     *                 Message to check
     * @return         True if the message has an Extension field
     */
    protected boolean hasExtensionLengthField(T message) {
        return getBytesLeft() > 0;
    }

    /**
     * Checks if the ExtensionsLengthField has a value greater than Zero, eg. if there are Extensions present.
     *
     * @param  message
     *                 Message to check
     * @return         True if the message has Extensions
     */
    protected boolean hasExtensions(T message) {
        return message.getExtensionsLength().getValue() > 0;
    }

    @Override
    protected ProtocolVersion getVersion() {
        return version;
    }

    protected void setVersion(ProtocolVersion version) {
        this.version = version;
    }
}
