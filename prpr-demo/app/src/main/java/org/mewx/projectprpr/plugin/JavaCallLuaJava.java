package org.mewx.projectprpr.plugin;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

/**
 * This class contains the functions that 'LuaJava' provides.
 *
 * LuaJava provides these functions:
 * - Java run lua codes;
 * - Java call lua functions, with args and returns;
 * - Lua call java functions, with args, 'result' return;
 * Created by MewX on 1/19/2016.
 */
@SuppressWarnings("unused")
public class JavaCallLuaJava {
    private final static String TAG = JavaCallJavaClass.class.getSimpleName();

    static {
        final String libLuaJavaName = "luajava";
        System.loadLibrary(libLuaJavaName);
    }

    LuaState luaState;
    String returnValue;

    /**
     * Construct does the initial job.
     */
    public JavaCallLuaJava() {
        luaState = LuaStateFactory.newLuaState();
        luaState.openLibs();

        if(!bindAllOpenFunctions())
            close(); // fail to bind data
    }

    public void close() {
        luaState.close();
    }

    public LuaState getLuaState() {
        return luaState;
    }

    private boolean bindAllOpenFunctions() {
        JavaFunction javaFunctionTest = new JavaFunction(luaState) {
            @Override
            public int execute() throws LuaException {
                int stackHeight = 1;
                for (int i = stackHeight; i <= luaState.getTop(); i++) {
                    // if in dev mode, this part should check type
                    int type = luaState.type(i);
                    String stype = luaState.typeName(type);

                    String val = null;
                    Object obj = luaState.toJavaObject(i);
                    if (obj != null)
                        val = obj.toString();

                    // For helloLuaJavaCallFromLua()
                    if(val != null)
                        returnValue = val.toUpperCase(); // get return value

                    // For helloLuaJavaCallFromLuaWithReturn()
                    luaState.pushString(returnValue);
                    luaState.setGlobal("result");
                }
                return 0;
            }
        };


        try {
            javaFunctionTest.register("test");
            return true;
        } catch (LuaException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String helloLuaJavaCallFromLua() {
        luaState.LdoString("test(\"test lua\")");
        return returnValue;
    }

    public String helloLuaJavaCallFromLuaWithReturn() {
        luaState.LdoString("test(\"test lua\");a=result;");
        return luaState.getLuaObject("a").toString();
    }

    public String helloLuaJava() {
        luaState.LdoString("a=\"test lua\"");
        return luaState.getLuaObject("a").toString();
    }
}
