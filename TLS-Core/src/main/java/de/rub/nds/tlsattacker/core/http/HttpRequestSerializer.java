/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.http;

import java.nio.charset.StandardCharsets;

import de.rub.nds.tlsattacker.core.http.header.HttpHeader;
import de.rub.nds.tlsattacker.core.http.header.serializer.HttpHeaderSerializer;
import de.rub.nds.tlsattacker.core.protocol.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpRequestSerializer extends Serializer<HttpRequestMessage> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final HttpRequestMessage message;

    public HttpRequestSerializer(HttpRequestMessage message) {
        super();
        this.message = message;
    }

    @Override
    protected byte[] serializeBytes() {
        StringBuilder builder = new StringBuilder();
        builder.append(message.getRequestType().getValue()).append(" ").append(message.getRequestPath().getValue())
            .append(" ").append(message.getRequestProtocol().getValue()).append("\r\n");
        for (HttpHeader header : message.getHeader()) {
            HttpHeaderSerializer serializer = new HttpHeaderSerializer(header);
            builder.append(new String(serializer.serialize(), StandardCharsets.ISO_8859_1));
        }
        builder.append("\r\n");
        LOGGER.info(builder.toString());
        appendBytes(builder.toString().getBytes(StandardCharsets.ISO_8859_1));
        return getAlreadySerialized();
    }

}
