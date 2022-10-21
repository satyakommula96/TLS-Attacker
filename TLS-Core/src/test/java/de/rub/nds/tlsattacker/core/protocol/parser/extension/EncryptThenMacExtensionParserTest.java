/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.protocol.message.extension.EncryptThenMacExtensionMessage;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class EncryptThenMacExtensionParserTest
    extends AbstractExtensionParserTest<EncryptThenMacExtensionMessage, EncryptThenMacExtensionParser> {

    public EncryptThenMacExtensionParserTest() {
        super(EncryptThenMacExtensionMessage.class, EncryptThenMacExtensionParser::new);
    }

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(new byte[] { 0x00, 0x16, 0x00, 0x00 }, List.of(), ExtensionType.ENCRYPT_THEN_MAC,
            0, List.of()));
    }
}
