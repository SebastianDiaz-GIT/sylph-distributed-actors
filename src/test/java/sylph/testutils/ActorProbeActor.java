package sylph.testutils;

import sylph.api.Actor;
import sylph.api.ActorContext;

/**
 * Actor que reenvía todos los mensajes a un TestProbe helper.
 * Permite pruebas end-to-end donde la entrega ocurre a través del ActorSystem.
 */
public class ActorProbeActor<M> implements Actor<M> {
    private final TestProbe<M> probe;

    public ActorProbeActor(TestProbe<M> probe) {
        this.probe = probe;
    }

    @Override
    public void receive(M message, ActorContext<M> ctx) {
        probe.receiveFromActor(message);
    }
}

