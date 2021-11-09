/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.exceptions.ParserException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract Parser class which can be used to read a byte array.
 *
 * @param <T>
 *            Type of the Object that should be parsed
 */
public abstract class Parser<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final InputStream stream;

    /**
     * Not so nice...
     */
    private final ByteArrayOutputStream outputStream;

    /**
     * Constructor for the Parser
     *
     * @param stream
     *               The Inputstream to read data drom
     */
    public Parser(InputStream stream) {
        this.stream = stream;
        outputStream = new ByteArrayOutputStream();
    }

    public byte[] getAlreadyParsed() {
        return outputStream.toByteArray();
    }

    /**
     * Parses a number of bytes from the Array and returns them as a byte[].
     *
     * @param  length
     *                Number of bytes to be parsed
     * @return        A subByteArray of according size from the Array
     */
    protected byte[] parseByteArrayField(int length) {
        byte[] data = new byte[length];
        try {
            stream.read(data);
            outputStream.write(data);
        } catch (IOException E) {
            throw new ParserException("Could not parse byteArrayField of length=" + length, E);
        }
        return data;
    }

    /**
     * Parses a number of bytes from the Array and returns them as a int. Throws a ParserException if the number of
     * bytes cannot be parsed. Moves the pointer accordingly.
     *
     * @param  length
     *                Number of bytes to be parsed
     * @return        An integer representation of the subByteArray
     */
    protected int parseIntField(int length) {
        if (length == 0) {
            throw new ParserException("Cannot parse int of size 0");
        }
        return ArrayConverter.bytesToInt(parseByteArrayField(length));
    }

    /**
     * Parses a number of bytes from the Array and returns them as a positive BigInteger. Throws a ParserException if
     * the number of bytes cannot be parsed. Moves the pointer accordingly.
     *
     * @param  length
     *                Number of bytes to be parsed
     * @return        A BigInteger representation of the subByteArray
     */
    protected BigInteger parseBigIntField(int length) {
        if (length == 0) {
            throw new ParserException("Cannot parse BigInt of size 0");
        }
        return new BigInteger(1, parseByteArrayField(length));
    }

    /**
     * Parses a number of bytes from the Array and returns them as a byte. Throws a ParserException if the number of
     * bytes cannot be parsed. Moves the pointer accordingly.
     *
     * @param  length
     *                Number of bytes to be parsed
     * @return        An integer representation of the subByteArray
     */
    protected byte parseByteField(int length) {
        if (length == 0) {
            throw new ParserException("Cannot parse byte of size 0");
        }
        if (length > 1) {
            LOGGER.warn("Parsing byte[] field into a byte of size >1");
        }
        return (byte) ArrayConverter.bytesToInt(parseByteArrayField(length));
    }

    protected String parseStringTill(byte endSequence) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        while (true) {
            byte b = parseByteField(1);
            stream.write(b);
            if (b == endSequence) {
                return stream.toString();
            }
        }
    }

    /**
     * Checks if there are at least count bytes left to read
     *
     * @param  count
     *               Number of bytes to check for
     * @return       True if there are at least count bytes left to read
     */
    protected boolean enoughBytesLeft(int count) {
        return getBytesLeft() >= count;
    }

    protected byte[] parseArrayOrTillEnd(int n) {
        if (n >= 0 && n < getBytesLeft()) {
            return parseByteArrayField(n);
        } else {
            return parseByteArrayField(getBytesLeft());
        }
    }

    public int getBytesLeft() {
        try {
            return stream.available();
        } catch (IOException ex) {
            throw new ParserException("Cannot tell how many bytes are left in inputstream", ex);
        }
    }

    /**
     * Returns the parsed object.
     *
     * @return The parsed object
     */
    public abstract T parse();

    /**
     * TODO: This can break get already parsed - not so nice
     * 
     * @return
     */
    protected InputStream getStream() {
        return stream;
    }

}
