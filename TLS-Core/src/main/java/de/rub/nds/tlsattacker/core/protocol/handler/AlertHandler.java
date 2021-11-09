/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import de.rub.nds.tlsattacker.core.constants.AlertLevel;
import de.rub.nds.tlsattacker.core.protocol.message.AlertMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.AlertParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.AlertPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.AlertSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlertHandler extends TlsMessageHandler<AlertMessage> {

    private static final Logger LOGGER = LogManager.getLogger();

    public AlertHandler(TlsContext tlsContext) {
        super(tlsContext);
    }

    @Override
    public AlertParser getParser(InputStream stream) {
        return new AlertParser(stream, tlsContext.getChooser().getLastRecordVersion(), tlsContext.getConfig());
    }

    @Override
    public AlertPreparator getPreparator(AlertMessage message) {
        return new AlertPreparator(tlsContext.getChooser(), message);
    }

    @Override
    public AlertSerializer getSerializer(AlertMessage message) {
        return new AlertSerializer(message, tlsContext.getChooser().getSelectedProtocolVersion());
    }

    @Override
    public void adjustTLSContext(AlertMessage message) {
        if (tlsContext.getTalkingConnectionEndType() == tlsContext.getChooser().getMyConnectionPeer()
            && AlertLevel.FATAL.getValue() == message.getLevel().getValue()) {
            LOGGER.debug("Setting received Fatal Alert in Context");
            tlsContext.setReceivedFatalAlert(true);
        }
    }
}
