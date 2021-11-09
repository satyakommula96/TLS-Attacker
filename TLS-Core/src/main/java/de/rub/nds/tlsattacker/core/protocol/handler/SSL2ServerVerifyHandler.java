/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import de.rub.nds.tlsattacker.core.protocol.message.SSL2ServerVerifyMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.SSL2ServerVerifyParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.SSL2ServerVerifyPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.HandshakeMessageSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.io.InputStream;

public class SSL2ServerVerifyHandler extends HandshakeMessageHandler<SSL2ServerVerifyMessage> {

    public SSL2ServerVerifyHandler(TlsContext context) {
        super(context);
    }

    @Override
    public SSL2ServerVerifyParser getParser(InputStream stream) {
        return new SSL2ServerVerifyParser(stream, tlsContext.getChooser().getSelectedProtocolVersion(),
            tlsContext.getConfig());
    }

    @Override
    public SSL2ServerVerifyPreparator getPreparator(SSL2ServerVerifyMessage message) {
        return new SSL2ServerVerifyPreparator(tlsContext.getChooser(), message);
    }

    @Override
    public void adjustTLSContext(SSL2ServerVerifyMessage message) {
    }

    @Override
    public HandshakeMessageSerializer<SSL2ServerVerifyMessage> getSerializer(SSL2ServerVerifyMessage message) {
        // We currently don't send ServerVerify messages, only receive them.
        return null;
    }

}
