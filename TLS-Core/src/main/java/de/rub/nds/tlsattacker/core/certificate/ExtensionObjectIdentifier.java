/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2020 Ruhr University Bochum, Paderborn University,
 * and Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package de.rub.nds.tlsattacker.core.certificate;

public enum ExtensionObjectIdentifier {
    OCSP("1.3.6.1.5.5.7.48.1"),
    CERTIFICATE_AUTHORITY_ISSUER("1.3.6.1.5.5.7.48.2"),
    AUTHORITY_INFO_ACCESS("1.3.6.1.5.5.7.1.1"),
    TLS_FEATURE("1.3.6.1.5.5.7.1.24");

    private final String objectIdentifier;

    /**
     * @param objectIdentifier
     */

    ExtensionObjectIdentifier(final String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }

    public String getOID() {
        return objectIdentifier;
    }
}
