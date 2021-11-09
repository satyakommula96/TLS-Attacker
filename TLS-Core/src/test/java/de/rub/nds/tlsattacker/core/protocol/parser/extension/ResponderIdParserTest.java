/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.statusrequestv2.ResponderId;
import java.io.ByteArrayInputStream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ResponderIdParserTest {

    private final Integer idLength = 6;
    private final byte[] id = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
    private final byte[] payloadBytes = new byte[] { 0x00, 0x06, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
    private ResponderIdParser parser;
    private ResponderId parsedId;

    @Test
    public void testParser() {
        parser = new ResponderIdParser(new ByteArrayInputStream(payloadBytes));
        parsedId = parser.parse();

        assertEquals(idLength, parsedId.getIdLength().getValue());
        assertArrayEquals(id, parsedId.getId().getValue());
    }
}
