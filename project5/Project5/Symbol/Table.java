package Symbol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Table {
    private final Map<Symbol, Deque<Object>> bindings = new HashMap<Symbol, Deque<Object>>();
    private final Deque<List<Symbol>> scopes = new ArrayDeque<List<Symbol>>();


    public Table() {
        scopes.push(new ArrayList<Symbol>()); 
    }
    public Table put(Symbol key, Object value){
        Deque<Object> stack = bindings.get(key);
        if(stack == null){
            stack = new ArrayDeque<Object>();
            bindings.put(key, stack);
        }
        stack.push(value);
        scopes.peek().add(key);
        return this;
    }
    public Object get(Symbol key){
        Deque<Object> stack = bindings.get(key);
        if (stack == null || stack.isEmpty()) {
            return null;
        } else {
            return stack.peek();
        }
    }
    public boolean inCurrScope(Symbol key){
        return scopes.peek().contains(key);
    }
    public void beginScope(){
        scopes.push(new ArrayList<Symbol>());
    }
    public void endScope(){
        if(scopes.size() <= 1){
            //TODO error throw
        }
        for(Symbol key : scopes.pop()){
            Deque<Object> stack = bindings.get(key);
            stack.pop();
            if(stack.isEmpty()){
                bindings.remove(key);
            }
        }
    }
}