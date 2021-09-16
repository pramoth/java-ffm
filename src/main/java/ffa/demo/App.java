package ffa.demo;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class App {
    public static void main(String[] args) throws Throwable {
        final MemoryAddress printf = CLinker.systemLookup().lookup("printf").get(); // lookup native C printf function from library
        MethodHandle printfMh = CLinker.getInstance().downcallHandle(
                printf,
                MethodType.methodType(int.class, MemoryAddress.class),// C printf represent in Java
                FunctionDescriptor.of(CLinker.C_INT,CLinker.C_POINTER) // C => int printf(char*)
        );

        try (var scope = ResourceScope.newConfinedScope()) {
            var cString = CLinker.toCString("Hello World from C world.\n", scope);
            int len = (int)printfMh.invokeExact(cString.address());
        }
    }
}
