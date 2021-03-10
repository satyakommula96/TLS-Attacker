/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.preparator;

import de.rub.nds.tlsattacker.core.protocol.message.ClientKeyExchangeMessage;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;

/**
 * @param <T>
 *            The ClientKeyExchangeMessage that should be prepared
 */
public abstract class ClientKeyExchangePreparator<T extends ClientKeyExchangeMessage>
    extends HandshakeMessagePreparator<ClientKeyExchangeMessage> {

    public ClientKeyExchangePreparator(Chooser chooser, ClientKeyExchangeMessage message) {
        super(chooser, message);
    }
}
