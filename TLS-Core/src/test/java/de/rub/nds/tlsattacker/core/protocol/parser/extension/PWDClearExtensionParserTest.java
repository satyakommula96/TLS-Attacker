/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.protocol.message.extension.PWDClearExtensionMessage;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class PWDClearExtensionParserTest {

    @Parameterized.Parameters
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] {
            { ArrayConverter.hexStringToByteArray("001e00050466726564"), ExtensionType.PWD_CLEAR, 5, 4, "fred" } });
    }

    private final byte[] expectedBytes;
    private final ExtensionType type;
    private final int extensionLength;
    private final int usernameLength;
    private final String username;

    public PWDClearExtensionParserTest(byte[] expectedBytes, ExtensionType type, int extensionLength,
        int usernameLength, String username) {
        this.expectedBytes = expectedBytes;
        this.type = type;
        this.extensionLength = extensionLength;
        this.usernameLength = usernameLength;
        this.username = username;
    }

    @Test
    public void testParseExtensionMessageContent() {
        PWDClearExtensionParser parser =
            new PWDClearExtensionParser(new ByteArrayInputStream(expectedBytes), Config.createConfig());
        PWDClearExtensionMessage msg = parser.parse();
        assertArrayEquals(type.getValue(), msg.getExtensionType().getValue());
        assertEquals(extensionLength, (long) msg.getExtensionLength().getValue());
        assertEquals(usernameLength, (long) msg.getUsernameLength().getValue());
        assertEquals(username, msg.getUsername().getValue());
    }
}
