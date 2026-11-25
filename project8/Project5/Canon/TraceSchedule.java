package Canon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import Temp.Label;
import Tree.*;


public class TraceSchedule {
    public LinkedList<Stm> stms = new LinkedList<Stm>();
    private Map<Label, LinkedList<Stm>> table = new HashMap<Label, LinkedList<Stm>>();

    public TraceSchedule(BasicBlocks b) {
        for (LinkedList<Stm> blk : b.blocks)
            table.put(((LABEL) blk.getFirst()).label, blk);
        for (LinkedList<Stm> blk : b.blocks)
            if (table.containsKey(((LABEL) blk.getFirst()).label)) trace(blk);
        stms.add(new LABEL(b.done));
        table = null;
    }

    private void trace(LinkedList<Stm> b) {
        for (;;) {
            table.remove(((LABEL) b.getFirst()).label);
            stms.addAll(b.subList(0, b.size() - 1));
            Stm last = b.getLast();
            if (last instanceof JUMP) {
                JUMP j = (JUMP) last;
                LinkedList<Stm> target = j.targets.size() == 1 ? table.get(j.targets.getFirst()) : null;
                if (target != null) { b = target; continue; }
                stms.add(last);
                return;
            } else if (last instanceof CJUMP) {
                CJUMP j = (CJUMP) last;
                LinkedList<Stm> t = table.get(j.iftrue);
                LinkedList<Stm> f = table.get(j.iffalse);
                if (f != null) {
                    stms.add(j);
                    b = f;
                } else if (t != null) {
                    stms.add(new CJUMP(CJUMP.notRel(j.relop), j.left, j.right, j.iffalse, j.iftrue));
                    b = t;
                } else {
                    Label ff = new Label();
                    stms.add(new CJUMP(j.relop, j.left, j.right, j.iftrue, ff));
                    stms.add(new LABEL(ff));
                    stms.add(new JUMP(j.iffalse));
                    return;
                }
            } else throw new Error("Bad basic block in TraceSchedule");
        }
    }
}