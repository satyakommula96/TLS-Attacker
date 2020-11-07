/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2020 Ruhr University Bochum, Paderborn University,
 * and Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import de.rub.nds.tlsattacker.core.protocol.message.ProtocolMessage;
import de.rub.nds.tlsattacker.core.record.AbstractRecord;
import java.util.List;

public interface ReceivingAction {

    public abstract List<ProtocolMessage> getReceivedMessages();

    public abstract List<AbstractRecord> getReceivedRecords();
}
