/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.workflow;

import de.rub.nds.tlsattacker.core.exceptions.SkipActionException;
import de.rub.nds.tlsattacker.core.exceptions.WorkflowExecutionException;
import de.rub.nds.tlsattacker.core.layer.SpecificSendLayerConfiguration;
import de.rub.nds.tlsattacker.core.layer.constant.ImplementedLayers;
import de.rub.nds.tlsattacker.core.state.Context;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.workflow.action.ReceivingAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendingAction;
import de.rub.nds.tlsattacker.core.workflow.action.TlsAction;
import de.rub.nds.tlsattacker.core.workflow.action.executor.WorkflowExecutorType;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DTLSWorkflowExecutor extends WorkflowExecutor {

    private static final Logger LOGGER = LogManager.getLogger();

    public DTLSWorkflowExecutor(State state) {
        super(WorkflowExecutorType.DTLS, state);
    }

    @Override
    public void executeWorkflow() throws WorkflowExecutionException {
        if (config.isWorkflowExecutorShouldOpen()) {
            try {
                initAllLayer();
            } catch (IOException ex) {
                throw new WorkflowExecutionException(
                        "Workflow not executed, could not initialize transport handler: ", ex);
            }
        }
        state.getWorkflowTrace().reset();
        state.setStartTimestamp(System.currentTimeMillis());
        List<TlsAction> tlsActions = state.getWorkflowTrace().getTlsActions();
        int retransmissions = 0;
        int retransmissionActionIndex = 0;
        for (int i = 0; i < tlsActions.size(); i++) {
            if (i != 0
                    && !(tlsActions.get(i) instanceof ReceivingAction)
                    && (tlsActions.get(i - 1) instanceof ReceivingAction)) {
                retransmissionActionIndex = i;
            }
            TlsAction action = tlsActions.get(i);

            if (!action.isExecuted()) {
                LOGGER.trace("Executing regular action {} at index {}", action, index);
                try {
                    this.executeAction(action, state);
                } catch (SkipActionException ex) {
                    continue;
                }
            } else {
                if (action instanceof SendingAction) {
                    executeRetransmission((SendingAction) action);
                } else if (action instanceof ReceivingAction) {
                    action.reset();
                    try {
                        this.executeAction(action, state);
                    } catch (SkipActionException ex) {
                        continue;
                    }
                }
            }

            if ((config.isStopActionsAfterFatal() && isReceivedFatalAlert())) {
                LOGGER.debug(
                        "Skipping all Actions, received FatalAlert, StopActionsAfterFatal active");
                break;
            }
            if ((config.getStopActionsAfterWarning() && isReceivedWarningAlert())) {
                LOGGER.debug(
                        "Skipping all Actions, received Warning Alert, StopActionsAfterWarning active");
                break;
            }
            if ((config.getStopActionsAfterIOException() && isIoException())) {
                LOGGER.debug(
                        "Skipping all Actions, received IO Exception, StopActionsAfterIOException active");
                break;
            }

            if (!action.executedAsPlanned() && action instanceof ReceivingAction) {
                if (config.isStopTraceAfterUnexpected()) {
                    LOGGER.debug("Skipping all Actions, action did not execute as planned.");
                    break;
                } else if (retransmissions == config.getMaxDtlsRetransmissions()) {
                    LOGGER.debug("Hit max retransmissions, stopping workflow");
                    break;
                } else {
                    LOGGER.trace(
                            "Stepping back index to perform retransmission. From index: {}", index);
                    try {
                        performRetransmissions(tlsActions, index);
                    } catch (IOException E) {
                        LOGGER.warn(
                                "IOException occured during retransmission. Stopping workflow.", E);
                        break;
                    }
                    action.reset();
                    retransmissions++;
                }
            } else {
                index++;
            }
        }

        if (config.isFinishWithCloseNotify()) {
            for (Context context : state.getAllContexts()) {
                int currentEpoch = context.getTlsContext().getRecordLayer().getWriteEpoch();
                for (int epoch = currentEpoch; epoch >= 0; epoch--) {
                    context.getTlsContext().getRecordLayer().setWriteEpoch(epoch);
                    sendCloseNotify();
                }
                context.getTlsContext().getRecordLayer().setWriteEpoch(currentEpoch);
            }
        }

        setFinalSocketState();

        closeConnection();
        if (config.isResetWorkflowTracesBeforeSaving()) {
            LOGGER.debug("Resetting WorkflowTrace");
            state.getWorkflowTrace().reset();
        }

        if (getAfterExecutionCallback() != null) {
            LOGGER.debug("Executing AfterExecutionCallback");
            for (TlsContext context : state.getAllTlsContexts()) {
                try {
                    getAfterExecutionCallback().apply(context);
                } catch (Exception ex) {
                    LOGGER.trace("Error during AfterExecutionCallback", ex);
                }
            }
        }
    }

    private void executeRetransmission(SendingAction action) {
        LOGGER.info("Executing retransmission of last sent flight");
        for (String alias : action.getAllSendingAliases()) {
            LOGGER.debug("Retransmitting records for alias {}", alias);
            state.getTlsContext(alias).getRecordLayer().reencrypt(action.getSendRecords());
            state.getTlsContext(alias)
                    .getRecordLayer()
                    .setLayerConfiguration(
                            new SpecificSendLayerConfiguration(
                                    ImplementedLayers.RECORD, action.getSendRecords()));
            try {
                state.getTlsContext(alias).getRecordLayer().sendConfiguration();
            } catch (IOException ex) {
                LOGGER.warn(ex);
                state.getTlsContext().setReceivedTransportHandlerException(true);
            }
        }
    }
}
