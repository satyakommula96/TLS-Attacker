/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import de.rub.nds.tlsattacker.core.constants.AuthzDataFormat;
import de.rub.nds.tlsattacker.core.protocol.message.extension.ServerAuthzExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.ServerAuthzExtensionParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.ServerAuthzExtensionPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.ServerAuthzExtensionSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.io.InputStream;

public class ServerAuthzExtensionHandler extends ExtensionHandler<ServerAuthzExtensionMessage> {

    public ServerAuthzExtensionHandler(TlsContext context) {
        super(context);
    }

    @Override
    public ServerAuthzExtensionParser getParser(InputStream stream) {
        return new ServerAuthzExtensionParser(stream, context.getConfig());
    }

    @Override
    public ServerAuthzExtensionPreparator getPreparator(ServerAuthzExtensionMessage message) {
        return new ServerAuthzExtensionPreparator(context.getChooser(), message, getSerializer(message));
    }

    @Override
    public ServerAuthzExtensionSerializer getSerializer(ServerAuthzExtensionMessage message) {
        return new ServerAuthzExtensionSerializer(message);
    }

    @Override
    public void adjustTLSExtensionContext(ServerAuthzExtensionMessage message) {
        context.setServerAuthzDataFormatList(AuthzDataFormat.byteArrayToList(message.getAuthzFormatList().getValue()));
    }

}
