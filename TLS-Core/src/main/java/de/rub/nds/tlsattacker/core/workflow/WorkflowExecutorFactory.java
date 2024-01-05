/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.workflow;

import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.workflow.action.executor.WorkflowExecutorType;

public class WorkflowExecutorFactory {

    public static WorkflowExecutor createWorkflowExecutor(WorkflowExecutorType type, State state) {
        switch (type) {
            case DEFAULT:
                return new DefaultWorkflowExecutor(state);
            case THREADED_SERVER:
                return new ThreadedServerWorkflowExecutor(state);
            case DTLS:
                return new DTLSWorkflowExecutor(state);
            case QUIC:
                return new QuicWorkflowExecutor(state);
            default:
                throw new UnsupportedOperationException(type.name() + " not yet implemented");
        }
    }

    private WorkflowExecutorFactory() {}
}
