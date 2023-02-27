/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.constants;

import de.rub.nds.x509attacker.constants.X509PublicKeyType;

public enum SignatureAlgorithm {
    ANONYMOUS,
    RSA,
    DSA,
    ECDSA,
    RSA_PSS_RSAE,
    RSA_PSS_PSS,
    ED25519,
    ED448,
    GOSTR34102001("ECGOST3410"),
    GOSTR34102012_256("ECGOST3410-2012-256"),
    GOSTR34102012_512("ECGOST3410-2012-512");

    private final String javaName;

    SignatureAlgorithm() {
        this(null);
    }

    SignatureAlgorithm(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaName() {
        return javaName != null ? javaName : toString();
    }

    public X509PublicKeyType getRequiredCertificateKeyType() {
        switch (this) {
            case RSA:
            case RSA_PSS_PSS:
            case RSA_PSS_RSAE:
                return X509PublicKeyType.RSA;
            case DSA:
                return X509PublicKeyType.DSA;
            case ECDSA:
            case ED25519:
            case ED448:
                return X509PublicKeyType.ECDH_ECDSA;
            case GOSTR34102001:
                return X509PublicKeyType.GOST_R3411_2001;
            case GOSTR34102012_256:
            case GOSTR34102012_512:
                return X509PublicKeyType.GOST_R3411_2012;
            default:
                return null;
        }
    }
}
