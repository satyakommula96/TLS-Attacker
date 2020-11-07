/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2020 Ruhr University Bochum, Paderborn University,
 * and Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import de.rub.nds.tlsattacker.core.dtls.MessageFragmenter;
import de.rub.nds.tlsattacker.core.exceptions.AdjustmentException;
import de.rub.nds.tlsattacker.core.protocol.message.DtlsHandshakeMessageFragment;
import de.rub.nds.tlsattacker.core.protocol.message.HandshakeMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ProtocolMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.Parser;
import de.rub.nds.tlsattacker.core.protocol.parser.ParserResult;
import de.rub.nds.tlsattacker.core.protocol.parser.ProtocolMessageParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.Preparator;
import de.rub.nds.tlsattacker.core.protocol.preparator.ProtocolMessagePreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.ProtocolMessageSerializer;
import de.rub.nds.tlsattacker.core.protocol.serializer.Serializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @param <MessageT>
 * The ProtocolMessage that should be handled
 */
public abstract class ProtocolMessageHandler<MessageT extends ProtocolMessage> extends Handler<MessageT> {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * tls context
     */
    protected final TlsContext tlsContext;

    /**
     * @param tlsContext
     * The Context which should be Adjusted with this Handler
     */
    public ProtocolMessageHandler(TlsContext tlsContext) {
        this.tlsContext = tlsContext;
    }

    /**
     * Prepare message for sending. This method invokes before and after method
     * hooks.
     *
     * @param message
     * The Message that should be prepared
     * @return message in bytes
     */
    public byte[] prepareMessage(MessageT message) {
        return prepareMessage(message, true);
    }

    /**
     * Prepare message for sending. This method invokes before and after method
     * hooks.
     *
     * @param message
     * The message that should be prepared
     * @param withPrepare
     * if the prepare function should be called or only the rest
     * @return message in bytes
     */
    public byte[] prepareMessage(MessageT message, boolean withPrepare) {
        if (withPrepare) {
            Preparator preparator = getPreparator(message);
            preparator.prepare();
            preparator.afterPrepare();
            Serializer serializer = getSerializer(message);
            byte[] completeMessage = serializer.serialize();
            message.setCompleteResultingMessage(completeMessage);
        }
        try {
            if (message.getAdjustContext()) {
                if (tlsContext.getConfig().getDefaultSelectedProtocolVersion().isDTLS() && message.isHandshakeMessage()
                    && !message.isDtlsHandshakeMessageFragment()) {
                    tlsContext.increaseDtlsWriteHandshakeMessageSequence();
                }
            }
            updateDigest(message);
            if (message.getAdjustContext()) {

                adjustTLSContext(message);
            }
        } catch (AdjustmentException e) {
            LOGGER.warn("Could not adjust TLSContext");
            LOGGER.debug(e);
        }

        return message.getCompleteResultingMessage().getValue();
    }

    /**
     * Parses a byteArray from a Position into a MessageObject and returns the
     * parsed MessageObjet and parser position in a parser result. The current
     * Chooser is adjusted as
     *
     * @param message
     * The byte[] messages which should be parsed
     * @param pointer
     * The pointer (startposition) into the message bytes
     * @return The Parser result
     */
    public ParserResult parseMessage(byte[] message, int pointer, boolean onlyParse) {
        Parser<MessageT> parser = getParser(message, pointer);
        MessageT parsedMessage = parser.parse();

        if (tlsContext.getChooser().getSelectedProtocolVersion().isDTLS() && parsedMessage instanceof HandshakeMessage
            && !(parsedMessage instanceof DtlsHandshakeMessageFragment)) {
            ((HandshakeMessage) parsedMessage).setMessageSequence(tlsContext.getDtlsReadHandshakeMessageSequence());
        }
        try {
            if (!onlyParse) {
                prepareAfterParse(parsedMessage);
                updateDigest(parsedMessage);
                adjustTLSContext(parsedMessage);
            }

        } catch (AdjustmentException | UnsupportedOperationException e) {
            LOGGER.warn("Could not adjust TLSContext");
            LOGGER.debug(e);
        }
        return new ParserResult(parsedMessage, parser.getPointer());
    }

    private void updateDigest(ProtocolMessage message) {
        if (message.isHandshakeMessage() && ((HandshakeMessage) message).getIncludeInDigest()) {
            if (tlsContext.getChooser().getSelectedProtocolVersion().isDTLS()) {
                DtlsHandshakeMessageFragment fragment =
                    new MessageFragmenter(tlsContext.getConfig().getDtlsMaximumFragmentLength()).wrapInSingleFragment(
                        (HandshakeMessage) message, tlsContext);
                tlsContext.getDigest().append(fragment.getCompleteResultingMessage().getValue());
            } else {
                tlsContext.getDigest().append(message.getCompleteResultingMessage().getValue());
            }
            LOGGER.debug("Included in digest: " + message.toCompactString());
        }
    }

    @Override
    public abstract ProtocolMessageParser getParser(byte[] message, int pointer);

    @Override
    public abstract ProtocolMessagePreparator getPreparator(MessageT message);

    @Override
    public abstract ProtocolMessageSerializer getSerializer(MessageT message);

    /**
     * Adjusts the TLS Context according to the received or sending
     * ProtocolMessage
     *
     * @param message
     * The Message for which this context should be adjusted
     */
    public abstract void adjustTLSContext(MessageT message);

    public void adjustTlsContextAfterSerialize(MessageT message) {
    }

    public void prepareAfterParse(MessageT message) {
        ProtocolMessagePreparator prep = getPreparator(message);
        prep.prepareAfterParse(tlsContext.isReversePrepareAfterParse());
    }

    @Override
    protected final void adjustContext(MessageT message) {
        adjustTLSContext(message);
    }
}
