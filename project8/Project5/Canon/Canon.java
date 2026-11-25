package Canon;

import java.util.LinkedList;
import Temp.Temp;
import Tree.*;

public class Canon {
    static boolean isNop(Stm a) {
        return a instanceof EXP && ((EXP) a).exp instanceof CONST;
    }
    static Stm seq(Stm a, Stm b) {
        if (isNop(a)) return b;
        if (isNop(b)) return a;
        return new SEQ(a, b);
    }
    static boolean commute(Stm a, Exp b) {
        return isNop(a) || b instanceof NAME || b instanceof CONST;
    }
    static Stm do_stm(Stm s) {
        if (s instanceof SEQ) {
            SEQ q = (SEQ) s;
            return seq(do_stm(q.left), do_stm(q.right));
        }
        if (s instanceof MOVE) {
            MOVE m = (MOVE) s;
            if (m.dst instanceof TEMP && m.src instanceof CALL)
                return reorder_stm(new MoveCall((TEMP) m.dst, (CALL) m.src));
            if (m.dst instanceof ESEQ) {
                ESEQ e = (ESEQ) m.dst;
                return do_stm(new SEQ(e.stm, new MOVE(e.exp, m.src)));
            }
            return reorder_stm(m);
        }
        if (s instanceof EXP) {
            EXP e = (EXP) s;
            if (e.exp instanceof CALL) return reorder_stm(new ExpCall((CALL) e.exp));
            return reorder_stm(e);
        }
        return reorder_stm(s);
    }
    static Stm reorder_stm(Stm s) {
        StmExpList x = reorder(s.kids());
        return seq(x.stm, s.build(x.exps));
    }

    static ESEQ do_exp(Exp e) {
        if (e instanceof ESEQ) {
            ESEQ q = (ESEQ) e;
            Stm stms = do_stm(q.stm);
            ESEQ b = do_exp(q.exp);
            return new ESEQ(seq(stms, b.stm), b.exp);
        }
        return reorder_exp(e);
    }

    static ESEQ reorder_exp(Exp e) {
        StmExpList x = reorder(e.kids());
        return new ESEQ(x.stm, e.build(x.exps));
    }

    static StmExpList reorder(LinkedList<Exp> exps) {
        if (exps.isEmpty()) return new StmExpList(new EXP(new CONST(0)), new LinkedList<Exp>());
        Exp a = exps.getFirst();
        LinkedList<Exp> rest = new LinkedList<Exp>(exps.subList(1, exps.size()));
        if (a instanceof CALL) {
            Temp t = new Temp();
            Exp e = new ESEQ(new MOVE(new TEMP(t), a), new TEMP(t));
            rest.addFirst(e);
            return reorder(rest);
        }
        ESEQ aa = do_exp(a);
        StmExpList bb = reorder(rest);
        if (commute(bb.stm, aa.exp)) {
            bb.exps.addFirst(aa.exp);
            return new StmExpList(seq(aa.stm, bb.stm), bb.exps);
        }
        Temp t = new Temp();
        bb.exps.addFirst(new TEMP(t));
        return new StmExpList(seq(aa.stm, seq(new MOVE(new TEMP(t), aa.exp), bb.stm)), bb.exps);
    }
    static void linear(Stm s, LinkedList<Stm> out) {
        if (s instanceof SEQ) {
            linear(((SEQ) s).left, out);
            linear(((SEQ) s).right, out);
        } else out.add(s);
    }

    public static LinkedList<Stm> linearize(Stm s) {
        LinkedList<Stm> out = new LinkedList<Stm>();
        linear(do_stm(s), out);
        return out;
    }
}

class MoveCall extends Stm {
    TEMP dst;
    CALL src;
    MoveCall(TEMP d, CALL s) { dst = d; src = s; }
    public void accept(IntVisitor v, int d) { throw new Error("MoveCall.accept"); }
    public LinkedList<Exp> kids() { return src.kids(); }
    public Stm build(LinkedList<Exp> kids) { return new MOVE(dst, (CALL) src.build(kids)); }
}

class ExpCall extends Stm {
    CALL call;
    ExpCall(CALL c) { call = c; }
    public void accept(IntVisitor v, int d) { throw new Error("ExpCall.accept"); }
    public LinkedList<Exp> kids() { return call.kids(); }
    public Stm build(LinkedList<Exp> kids) { return new EXP(call.build(kids)); }
}

class StmExpList {
    Stm stm;
    LinkedList<Exp> exps;
    StmExpList(Stm s, LinkedList<Exp> e) { stm = s; exps = e; }
}
