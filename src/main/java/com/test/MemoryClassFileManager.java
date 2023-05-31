package com.test;

import javax.tools.*;
import java.io.IOException;

public class MemoryClassFileManager extends ForwardingJavaFileManager {

    //private MemoryJavaFileObject memoryJavaFileObject;

    protected MemoryClassFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    // 回调函数
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        //URI uri = URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension);
        MemoryJavaFileObject memoryJavaFileObject = new MemoryJavaFileObject(className, kind);
        //this.classJavaFileObject = new JavaClassObject(className, kind);
        // 存入map中
        CompileStringClass.compileResult.put(className, memoryJavaFileObject);
        return memoryJavaFileObject;
    }

//    public MemoryJavaFileObject getMemoryJavaFileObject(){
//        return memoryJavaFileObject;
//    }


}
