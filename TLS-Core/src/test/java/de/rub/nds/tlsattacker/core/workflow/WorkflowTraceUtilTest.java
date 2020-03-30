/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2020 Ruhr University Bochum, Paderborn University,
 * and Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.workflow;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.ProtocolMessageType;
import de.rub.nds.tlsattacker.core.protocol.message.AlertMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ClientHelloMessage;
import de.rub.nds.tlsattacker.core.protocol.message.FinishedMessage;
import de.rub.nds.tlsattacker.core.protocol.message.HeartbeatMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ServerHelloMessage;
import de.rub.nds.tlsattacker.core.record.Record;
import de.rub.nds.tlsattacker.core.workflow.action.ReceiveAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendAction;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WorkflowTraceUtilTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private WorkflowTrace trace;
    private Config config;

    private ReceiveAction rcvHeartbeat;
    private ReceiveAction rcvAlertMessage;
    private ReceiveAction rcvServerHello;
    private ReceiveAction rcvFinishedMessage;
    private ReceiveAction rcvMultipleProtocolMessages;
    private ReceiveAction rcvMultipleHandshakeMessages;
    private ReceiveAction rcvMultipleRecords;

    private HeartbeatMessage msgHeartbeatMessageWithLength;
    private ServerHelloMessage msgServerHelloMessageWithCipherSuite;

    private Record recWithLength;

    private SendAction sHeartbeat;
    private SendAction sAlertMessage;
    private SendAction sClientHello;
    private SendAction sFinishedMessage;

    public WorkflowTraceUtilTest() {
    }

    @Before
    public void setUp() {
        config = config.createConfig();
        trace = new WorkflowTrace();

        rcvHeartbeat = new ReceiveAction();
        rcvAlertMessage = new ReceiveAction();
        rcvServerHello = new ReceiveAction();
        rcvFinishedMessage = new ReceiveAction();
        rcvMultipleProtocolMessages = new ReceiveAction();
        rcvMultipleHandshakeMessages = new ReceiveAction();
        rcvMultipleRecords = new ReceiveAction();

        msgHeartbeatMessageWithLength = new HeartbeatMessage();
        msgHeartbeatMessageWithLength.setPayloadLength(42);
        msgServerHelloMessageWithCipherSuite = new ServerHelloMessage();
        msgServerHelloMessageWithCipherSuite.setSelectedCipherSuite(CipherSuite.TLS_AES_128_GCM_SHA256.getByteValue());
        recWithLength = new Record();
        recWithLength.setLength(42);

        rcvHeartbeat.setMessages(new HeartbeatMessage());
        rcvAlertMessage.setMessages(new AlertMessage());
        rcvServerHello.setMessages(new ServerHelloMessage());
        rcvFinishedMessage.setMessages(new FinishedMessage());
        rcvMultipleProtocolMessages.setMessages(new HeartbeatMessage(), new HeartbeatMessage(), msgHeartbeatMessageWithLength);
        rcvMultipleHandshakeMessages.setMessages(new ServerHelloMessage(), new HeartbeatMessage(), msgServerHelloMessageWithCipherSuite);
        rcvMultipleRecords.setRecords(new Record(), new Record(), recWithLength);


        sHeartbeat = new SendAction();
        sAlertMessage = new SendAction();
        sClientHello = new SendAction();
        sFinishedMessage = new SendAction();

        sHeartbeat.setMessages(new HeartbeatMessage());
        sAlertMessage.setMessages(new AlertMessage());
        sClientHello.setMessages(new ClientHelloMessage());
        sFinishedMessage.setMessages(new FinishedMessage());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetLastReceivedMessage() {
        assertNull(WorkflowTraceUtil.getLastReceivedMessage(ProtocolMessageType.HEARTBEAT, trace));

        trace.addTlsAction(rcvMultipleProtocolMessages);

        assertNotSame(rcvMultipleProtocolMessages.getMessages().get(0), WorkflowTraceUtil.getLastReceivedMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertNotSame(rcvMultipleProtocolMessages.getMessages().get(1), WorkflowTraceUtil.getLastReceivedMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertSame(rcvMultipleProtocolMessages.getMessages().get(2), WorkflowTraceUtil.getLastReceivedMessage(ProtocolMessageType.HEARTBEAT, trace));

        assertNull(WorkflowTraceUtil.getLastReceivedMessage(HandshakeMessageType.SERVER_HELLO, trace));

        trace.addTlsAction(rcvMultipleHandshakeMessages);

        assertNotSame(rcvMultipleHandshakeMessages.getMessages().get(0), WorkflowTraceUtil.getLastReceivedMessage(HandshakeMessageType.SERVER_HELLO, trace));
        assertNotSame(rcvMultipleHandshakeMessages.getMessages().get(1), WorkflowTraceUtil.getLastReceivedMessage(HandshakeMessageType.SERVER_HELLO, trace));
        assertSame(rcvMultipleHandshakeMessages.getMessages().get(2), WorkflowTraceUtil.getLastReceivedMessage(HandshakeMessageType.SERVER_HELLO, trace));
    }


    @Test
    public void testDidReceiveMessage() {
        assertFalse(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertFalse(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.ALERT, trace));
        assertFalse(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.SERVER_HELLO, trace));
        assertFalse(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.FINISHED, trace));

        trace.addTlsAction(rcvHeartbeat);

        assertTrue(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertFalse(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.ALERT, trace));
        assertFalse(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.SERVER_HELLO, trace));
        assertFalse(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.FINISHED, trace));

        trace.addTlsAction(rcvAlertMessage);

        assertTrue(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertTrue(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.ALERT, trace));
        assertFalse(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.SERVER_HELLO, trace));
        assertFalse(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.FINISHED, trace));

        trace.addTlsAction(rcvServerHello);

        assertTrue(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertTrue(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.ALERT, trace));
        assertTrue(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.SERVER_HELLO, trace));
        assertFalse(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.FINISHED, trace));

        trace.addTlsAction(rcvFinishedMessage);

        assertTrue(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertTrue(WorkflowTraceUtil.didReceiveMessage(ProtocolMessageType.ALERT, trace));
        assertTrue(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.SERVER_HELLO, trace));
        assertTrue(WorkflowTraceUtil.didReceiveMessage(HandshakeMessageType.FINISHED, trace));
    }

    @Test
    public void testDidSendMessage() {
        assertFalse(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertFalse(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.ALERT, trace));
        assertFalse(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.CLIENT_HELLO, trace));
        assertFalse(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.FINISHED, trace));

        trace.addTlsAction(sHeartbeat);

        assertTrue(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertFalse(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.ALERT, trace));
        assertFalse(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.CLIENT_HELLO, trace));
        assertFalse(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.FINISHED, trace));

        trace.addTlsAction(sAlertMessage);

        assertTrue(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertTrue(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.ALERT, trace));
        assertFalse(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.CLIENT_HELLO, trace));
        assertFalse(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.FINISHED, trace));

        trace.addTlsAction(sClientHello);

        assertTrue(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertTrue(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.ALERT, trace));
        assertTrue(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.CLIENT_HELLO, trace));
        assertFalse(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.FINISHED, trace));

        trace.addTlsAction(sFinishedMessage);

        assertTrue(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.HEARTBEAT, trace));
        assertTrue(WorkflowTraceUtil.didSendMessage(ProtocolMessageType.ALERT, trace));
        assertTrue(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.CLIENT_HELLO, trace));
        assertTrue(WorkflowTraceUtil.didSendMessage(HandshakeMessageType.FINISHED, trace));
    }

    @Test
    public void testGetLastReceivedRecord() {
        assertNull(WorkflowTraceUtil.getLastReceivedRecord(trace));

        trace.addTlsAction(rcvMultipleRecords);

        assertNotSame(rcvMultipleRecords.getRecords().get(0), WorkflowTraceUtil.getLastReceivedRecord(trace));
        assertNotSame(rcvMultipleRecords.getRecords().get(1), WorkflowTraceUtil.getLastReceivedRecord(trace));
        assertSame(rcvMultipleRecords.getRecords().get(2), WorkflowTraceUtil.getLastReceivedRecord(trace));
    }

    private void pwf(String pre, WorkflowTrace trace) {
        LOGGER.info(pre);
        try {
            LOGGER.info(WorkflowTraceSerializer.write(trace));
        } catch (JAXBException | IOException ex) {
            java.util.logging.Logger.getLogger(WorkflowTraceUtilTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void handleDefaultsOfGoodTraceWithDefaultAliasSucceeds() throws JAXBException, IOException {
        InputStream stream = Config.class.getResourceAsStream("/test_good_workflow_trace_defaullt_alias.xml");

        try {
            trace = WorkflowTraceSerializer.read(stream);
        } catch (JAXBException | IOException | XMLStreamException ex) {
            fail();
        }
        assertNotNull(trace);
        pwf("after load:", trace);

        WorkflowTraceNormalizer n = new WorkflowTraceNormalizer();
        n.normalize(trace, config);
        // StringBuilder sb = new
        // StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        // sb.append("<workflowTrace>\n");
        // sb.append("    <Send>\n");
        // sb.append("        <messages>\n");
        // sb.append("            <ClientHello>\n");
        // sb.append("                <extensions>\n");
        // sb.append("                    <ECPointFormat/>\n");
        // sb.append("                    <EllipticCurves/>\n");
        // sb.append("                </extensions>\n");
        // sb.append("            </ClientHello>\n");
        // sb.append("        </messages>\n");
        // sb.append("        <records/>\n");
        // sb.append("    </Send>\n");
        // sb.append("</workflowTrace>\n");
        // String expected = sb.toString();
        String actual = WorkflowTraceSerializer.write(trace);
        LOGGER.info(actual);
        // Assert.assertThat(actual, equalTo(expected));
        //
        // Filter filter =
        // FilterFactory.createWorkflowTraceFilter(FilterType.DEFAULT, config);
        // WorkflowTrace filteredTrace = filter.filteredCopy(trace, config);
        // filteredTrace.setConnections(state.getOriginalWorkflowTrace().getConnections());
        // StringBuilder sb = new
        // StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        // sb.append("<workflowTrace>\n");
        // sb.append("    <Send>\n");
        // sb.append("        <messages>\n");
        // sb.append("            <ClientHello>\n");
        // sb.append("                <extensions>\n");
        // sb.append("                    <ECPointFormat/>\n");
        // sb.append("                    <EllipticCurves/>\n");
        // sb.append("                </extensions>\n");
        // sb.append("            </ClientHello>\n");
        // sb.append("        </messages>\n");
        // sb.append("    </Send>\n");
        // sb.append("</workflowTrace>\n");
        // String expected = sb.toString();
        actual = WorkflowTraceSerializer.write(trace);
        LOGGER.info(actual);
        // Assert.assertThat(actual, equalTo(expected));

    }
}
