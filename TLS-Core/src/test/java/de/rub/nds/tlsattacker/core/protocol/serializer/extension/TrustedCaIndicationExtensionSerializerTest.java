/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.TrustedCaIndicationExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.message.extension.trustedauthority.TrustedAuthority;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.TrustedCaIndicationExtensionParserTest;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.TrustedAuthorityPreparator;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.util.Collection;
import java.util.List;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TrustedCaIndicationExtensionSerializerTest {

    @Parameterized.Parameters
    public static Collection<Object[]> generateData() {
        return TrustedCaIndicationExtensionParserTest.generateData();
    }

    private final byte[] extensionBytes;
    private final List<TrustedAuthority> trustedAuthoritiesList;
    private final int trustedAuthoritiesLength;

    public TrustedCaIndicationExtensionSerializerTest(byte[] extensionBytes,
        List<TrustedAuthority> trustedAuthoritiesList, int trustedAuthoritiesLength) {
        this.extensionBytes = extensionBytes;
        this.trustedAuthoritiesList = trustedAuthoritiesList;
        this.trustedAuthoritiesLength = trustedAuthoritiesLength;
    }

    @Test
    public void testSerializeBytes() {
        TrustedCaIndicationExtensionMessage msg = new TrustedCaIndicationExtensionMessage();
        for (TrustedAuthority item : trustedAuthoritiesList) {
            TrustedAuthorityPreparator preparator = new TrustedAuthorityPreparator(new TlsContext().getChooser(), item);
            preparator.prepare();
        }

        msg.setTrustedAuthoritiesLength(trustedAuthoritiesLength);
        msg.setTrustedAuthorities(trustedAuthoritiesList);

        TrustedCaIndicationExtensionSerializer serializer = new TrustedCaIndicationExtensionSerializer(msg);
        byte[] test = serializer.serializeExtensionContent();
        assertArrayEquals(extensionBytes, test);
    }

}
