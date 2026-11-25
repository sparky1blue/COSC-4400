package Canon;

import java.util.LinkedList;
import Temp.Label;
import Tree.*;

public class BasicBlocks {
    public LinkedList<LinkedList<Stm>> blocks = new LinkedList<LinkedList<Stm>>();
    public Label done = new Label();    
    public BasicBlocks(LinkedList<Stm> stms) {
        LinkedList<Stm> cur = null;
        for (Stm s : stms) {
            if (s instanceof LABEL) {
                if (cur != null) {                      
                    cur.add(new JUMP(((LABEL) s).label));
                    blocks.add(cur);
                }
                cur = new LinkedList<Stm>();
                cur.add(s);
            } else {
                if (cur == null) {                     
                    cur = new LinkedList<Stm>();
                    cur.add(new LABEL(new Label()));
                }
                cur.add(s);
                if (s instanceof JUMP || s instanceof CJUMP) {
                    blocks.add(cur);
                    cur = null;
                }
            }
        }
        if (cur != null) {                          
            cur.add(new JUMP(done));
            blocks.add(cur);
        }
    }
}
