package org.uzdiz.visitor;

import org.uzdiz.composite.Vlak;
import org.uzdiz.composite.VozniRed;
import org.uzdiz.composite.EtapaVlaka;

public interface VozniRedVisitor {
    void visit(Vlak vlak);
    void visit(EtapaVlaka etapaVlaka);
    void visit(VozniRed vozniRed);
}
