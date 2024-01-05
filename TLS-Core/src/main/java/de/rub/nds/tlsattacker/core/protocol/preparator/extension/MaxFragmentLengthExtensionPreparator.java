/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.protocol.preparator.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.MaxFragmentLengthExtensionMessage;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxFragmentLengthExtensionPreparator
        extends ExtensionPreparator<MaxFragmentLengthExtensionMessage> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final MaxFragmentLengthExtensionMessage message;

    public MaxFragmentLengthExtensionPreparator(
            Chooser chooser, MaxFragmentLengthExtensionMessage message) {
        super(chooser, message);
        this.message = message;
    }

    @Override
    public void prepareExtensionContent() {
        LOGGER.debug("Preparing MaxFragmentLengthExtensionMessage");
        message.setMaxFragmentLength(
                chooser.getConfig().getDefaultMaxFragmentLength().getArrayValue());
    }
}
