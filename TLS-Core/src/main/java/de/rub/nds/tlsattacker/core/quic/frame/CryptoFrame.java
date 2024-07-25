/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.quic.frame;

import de.rub.nds.modifiablevariable.ModifiableVariableFactory;
import de.rub.nds.modifiablevariable.ModifiableVariableProperty;
import de.rub.nds.modifiablevariable.bytearray.ModifiableByteArray;
import de.rub.nds.modifiablevariable.longint.ModifiableLong;
import de.rub.nds.tlsattacker.core.quic.constants.QuicFrameType;
import de.rub.nds.tlsattacker.core.quic.handler.frame.CryptoFrameHandler;
import de.rub.nds.tlsattacker.core.quic.parser.frame.CryptoFrameParser;
import de.rub.nds.tlsattacker.core.quic.preparator.frame.CryptoFramePreparator;
import de.rub.nds.tlsattacker.core.quic.serializer.frame.CryptoFrameSerializer;
import de.rub.nds.tlsattacker.core.state.quic.QuicContext;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.util.Arrays;

/** A CRYPTO frame (type=0x06) is used to transmit cryptographic handshake messages. */
@XmlRootElement
public class CryptoFrame extends QuicFrame {

    @ModifiableVariableProperty protected ModifiableLong offset;

    @ModifiableVariableProperty protected ModifiableLong length;

    @ModifiableVariableProperty protected ModifiableByteArray cryptoData;

    private int maxFrameLengthConfig;

    public CryptoFrame() {
        super(QuicFrameType.CRYPTO_FRAME);
    }

    public CryptoFrame(int maxFrameLengthConfig) {
        super(QuicFrameType.CRYPTO_FRAME);
        this.maxFrameLengthConfig = maxFrameLengthConfig;
    }

    @Override
    public CryptoFrameHandler getHandler(QuicContext context) {
        return new CryptoFrameHandler(context);
    }

    @Override
    public CryptoFrameSerializer getSerializer(QuicContext context) {
        return new CryptoFrameSerializer(this);
    }

    @Override
    public CryptoFramePreparator getPreparator(QuicContext context) {
        return new CryptoFramePreparator(context.getChooser(), this);
    }

    @Override
    public CryptoFrameParser getParser(QuicContext context, InputStream stream) {
        return new CryptoFrameParser(stream);
    }

    public void setOffset(long offset) {
        this.offset = ModifiableVariableFactory.safelySetValue(this.offset, offset);
    }

    public void setOffset(int offset) {
        this.setOffset((long) offset);
    }

    public void setOffset(ModifiableLong offset) {
        this.offset = offset;
    }

    public ModifiableLong getOffset() {
        return this.offset;
    }

    public void setLength(long length) {
        this.length = ModifiableVariableFactory.safelySetValue(this.length, length);
    }

    public void setLength(int length) {
        this.setLength((long) length);
    }

    public void setLength(ModifiableLong length) {
        this.length = length;
    }

    public ModifiableLong getLength() {
        return this.length;
    }

    public void setCryptoData(byte[] cryptoData) {
        this.cryptoData = ModifiableVariableFactory.safelySetValue(this.cryptoData, cryptoData);
    }

    public void setCryptoData(ModifiableByteArray cryptoData) {
        this.cryptoData = cryptoData;
    }

    public ModifiableByteArray getCryptoData() {
        return this.cryptoData;
    }

    public int getMaxFrameLengthConfig() {
        return maxFrameLengthConfig;
    }

    public void setMaxFrameLengthConfig(int maxFrameLengthConfig) {
        this.maxFrameLengthConfig = maxFrameLengthConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CryptoFrame that = (CryptoFrame) o;

        if ((offset == null) != (that.offset == null)) {
            return false;
        }

        if ((length == null) != (that.offset == null)) {
            return false;
        }

        if (offset != null && !offset.getValue().equals(that.offset.getValue())) {
            return false;
        }
        if (length != null && !length.getValue().equals(that.length.getValue())) {
            return false;
        }
        return Arrays.equals(cryptoData.getValue(), that.cryptoData.getValue());
    }
}
