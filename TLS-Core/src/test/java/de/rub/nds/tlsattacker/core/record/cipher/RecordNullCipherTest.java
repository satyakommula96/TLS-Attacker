/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.record.cipher;

import de.rub.nds.tlsattacker.core.record.Record;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class RecordNullCipherTest {

    private RecordNullCipher recordCipher;
    private byte[] data;
    private Record record;

    @Before
    public void setUp() {
        recordCipher = new RecordNullCipher(new TlsContext());
        data = new byte[] { 1, 2 };
        record = new Record();
    }

    /**
     * Test of encrypt method, of class RecordNullCipher.
     */
    @Test
    public void testEncrypt() {
        record.setCleanProtocolMessageBytes(data);
        recordCipher.encrypt(record);
        assertArrayEquals(record.getProtocolMessageBytes().getValue(), data);
    }

    /**
     * Test of decrypt method, of class RecordNullCipher.
     */
    @Test
    public void testDecrypt() {
        record.setProtocolMessageBytes(data);
        recordCipher.decrypt(record);
        assertArrayEquals(data, record.getCleanProtocolMessageBytes().getValue());
    }
}
